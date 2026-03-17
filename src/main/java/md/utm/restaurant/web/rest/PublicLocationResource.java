package md.utm.restaurant.web.rest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import md.utm.restaurant.domain.LocationHours;
import md.utm.restaurant.repository.LocationHoursRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public (no-auth) endpoint for location business-hours status.
 * GET /api/public/locations/{id}/hours-status
 */
@RestController
@RequestMapping("/api/public/locations")
public class PublicLocationResource {

    private final LocationHoursRepository locationHoursRepository;

    public PublicLocationResource(LocationHoursRepository locationHoursRepository) {
        this.locationHoursRepository = locationHoursRepository;
    }

    @GetMapping("/{id}/hours-status")
    public ResponseEntity<HoursStatusDTO> getHoursStatus(@PathVariable Long id) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        String nowHHMM = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        LocationHours lh = locationHoursRepository.findByLocationIdAndDay(id, today).orElse(null);

        if (lh == null) {
            // No hours configured — assume open
            return ResponseEntity.ok(new HoursStatusDTO(true, null, null));
        }

        if (Boolean.TRUE.equals(lh.getIsClosed())) {
            return ResponseEntity.ok(new HoursStatusDTO(false, lh.getOpenTime(), lh.getCloseTime()));
        }

        String openTime = lh.getOpenTime();
        String closeTime = lh.getCloseTime();
        boolean isOpen = openTime != null && closeTime != null && nowHHMM.compareTo(openTime) >= 0 && nowHHMM.compareTo(closeTime) < 0;

        return ResponseEntity.ok(new HoursStatusDTO(isOpen, openTime, closeTime));
    }

    public record HoursStatusDTO(boolean isOpen, String openTime, String closeTime) {}
}
