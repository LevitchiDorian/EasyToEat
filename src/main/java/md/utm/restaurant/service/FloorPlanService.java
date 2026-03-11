package md.utm.restaurant.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.domain.Location;
import md.utm.restaurant.domain.Reservation;
import md.utm.restaurant.domain.ReservationTable;
import md.utm.restaurant.domain.RestaurantTable;
import md.utm.restaurant.domain.User;
import md.utm.restaurant.repository.LocationRepository;
import md.utm.restaurant.repository.ReservationTableRepository;
import md.utm.restaurant.repository.RestaurantTableRepository;
import md.utm.restaurant.repository.UserProfileRepository;
import md.utm.restaurant.service.dto.FloorPlanDTO;
import md.utm.restaurant.service.dto.FloorPlanReservationDTO;
import md.utm.restaurant.service.dto.FloorPlanRoomDTO;
import md.utm.restaurant.service.dto.FloorPlanTableDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FloorPlanService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final ReservationTableRepository reservationTableRepository;
    private final UserProfileRepository userProfileRepository;
    private final LocationRepository locationRepository;

    public FloorPlanService(
        RestaurantTableRepository restaurantTableRepository,
        ReservationTableRepository reservationTableRepository,
        UserProfileRepository userProfileRepository,
        LocationRepository locationRepository
    ) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.reservationTableRepository = reservationTableRepository;
        this.userProfileRepository = userProfileRepository;
        this.locationRepository = locationRepository;
    }

    public Optional<FloorPlanDTO> buildForLocation(Long locationId, LocalDate date) {
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            return Optional.empty();
        }
        Location location = locationOpt.get();

        List<RestaurantTable> tables = restaurantTableRepository.findActiveByLocationId(locationId);
        List<ReservationTable> reservations = reservationTableRepository.findActiveByLocationAndDate(locationId, date);

        // Build map: tableId → ReservationTable
        Map<Long, ReservationTable> tableReservationMap = new HashMap<>();
        for (ReservationTable rt : reservations) {
            if (rt.getTable() != null) {
                tableReservationMap.put(rt.getTable().getId(), rt);
            }
        }

        // Group tables by room (preserve insertion order for consistent rendering)
        Map<Long, List<RestaurantTable>> roomTableMap = new LinkedHashMap<>();
        Map<Long, DiningRoom> roomMap = new LinkedHashMap<>();
        for (RestaurantTable t : tables) {
            DiningRoom room = t.getRoom();
            if (room != null) {
                roomTableMap.computeIfAbsent(room.getId(), k -> new ArrayList<>()).add(t);
                roomMap.putIfAbsent(room.getId(), room);
            }
        }

        // Build room DTOs
        List<FloorPlanRoomDTO> roomDTOs = new ArrayList<>();
        for (Map.Entry<Long, List<RestaurantTable>> entry : roomTableMap.entrySet()) {
            DiningRoom room = roomMap.get(entry.getKey());
            List<FloorPlanTableDTO> tableDTOs = new ArrayList<>();
            for (RestaurantTable t : entry.getValue()) {
                FloorPlanTableDTO tDto = mapTable(t, tableReservationMap.get(t.getId()));
                tableDTOs.add(tDto);
            }
            FloorPlanRoomDTO roomDTO = new FloorPlanRoomDTO();
            roomDTO.setId(room.getId());
            roomDTO.setName(room.getName());
            roomDTO.setFloor(room.getFloor());
            roomDTO.setWidthPx(room.getWidthPx());
            roomDTO.setHeightPx(room.getHeightPx());
            roomDTO.setDecorationsJson(room.getDecorationsJson());
            roomDTO.setTables(tableDTOs);
            roomDTOs.add(roomDTO);
        }

        FloorPlanDTO dto = new FloorPlanDTO();
        dto.setLocationId(location.getId());
        dto.setLocationName(location.getName());
        dto.setLocationAddress(location.getAddress());
        dto.setDate(date.toString());
        dto.setRooms(roomDTOs);
        return Optional.of(dto);
    }

    public Optional<FloorPlanDTO> buildForCurrentUser(String login, LocalDate date) {
        return userProfileRepository
            .findByUserLogin(login)
            .filter(up -> up.getLocation() != null)
            .flatMap(up -> buildForLocation(up.getLocation().getId(), date));
    }

    /**
     * Public version: same as buildForLocation but strips reservation details (privacy).
     */
    public Optional<FloorPlanDTO> buildPublicForLocation(Long locationId, LocalDate date) {
        return buildForLocation(locationId, date).map(dto -> {
            dto.getRooms().forEach(room -> room.getTables().forEach(table -> table.setReservation(null)));
            return dto;
        });
    }

    private FloorPlanTableDTO mapTable(RestaurantTable t, ReservationTable rt) {
        FloorPlanTableDTO dto = new FloorPlanTableDTO();
        dto.setId(t.getId());
        dto.setTableNumber(t.getTableNumber());
        dto.setShape(t.getShape() != null ? t.getShape().name() : null);
        dto.setMinCapacity(t.getMinCapacity());
        dto.setMaxCapacity(t.getMaxCapacity());
        dto.setPositionX(t.getPositionX());
        dto.setPositionY(t.getPositionY());
        dto.setWidthPx(t.getWidthPx());
        dto.setHeightPx(t.getHeightPx());
        dto.setRotation(t.getRotation());
        // Reservation overrides AVAILABLE status but not OCCUPIED/OUT_OF_SERVICE
        String baseStatus = t.getStatus() != null ? t.getStatus().name() : "AVAILABLE";
        String effectiveStatus = (rt != null && !"OCCUPIED".equals(baseStatus) && !"OUT_OF_SERVICE".equals(baseStatus))
            ? "RESERVED"
            : baseStatus;
        dto.setStatus(effectiveStatus);
        dto.setIsActive(t.getIsActive());
        dto.setNotes(t.getNotes());
        if (rt != null) {
            dto.setReservation(mapReservation(rt.getReservation()));
        }
        return dto;
    }

    private FloorPlanReservationDTO mapReservation(Reservation r) {
        if (r == null) return null;
        FloorPlanReservationDTO dto = new FloorPlanReservationDTO();
        dto.setId(r.getId());
        dto.setReservationCode(r.getReservationCode());
        dto.setReservationDate(r.getReservationDate() != null ? r.getReservationDate().toString() : null);
        dto.setStartTime(r.getStartTime());
        dto.setEndTime(r.getEndTime());
        dto.setPartySize(r.getPartySize());
        dto.setStatus(r.getStatus() != null ? r.getStatus().name() : null);
        dto.setSpecialRequests(r.getSpecialRequests());
        User client = r.getClient();
        if (client != null) {
            dto.setClientLogin(client.getLogin());
            dto.setClientFirstName(client.getFirstName());
            dto.setClientLastName(client.getLastName());
        }
        return dto;
    }
}
