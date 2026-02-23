package md.utm.restaurant.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import md.utm.restaurant.domain.ReservationTable;
import md.utm.restaurant.repository.ReservationTableRepository;
import md.utm.restaurant.service.ReservationTableService;
import md.utm.restaurant.service.dto.ReservationTableDTO;
import md.utm.restaurant.service.mapper.ReservationTableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.ReservationTable}.
 */
@Service
@Transactional
public class ReservationTableServiceImpl implements ReservationTableService {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationTableServiceImpl.class);

    private final ReservationTableRepository reservationTableRepository;

    private final ReservationTableMapper reservationTableMapper;

    public ReservationTableServiceImpl(
        ReservationTableRepository reservationTableRepository,
        ReservationTableMapper reservationTableMapper
    ) {
        this.reservationTableRepository = reservationTableRepository;
        this.reservationTableMapper = reservationTableMapper;
    }

    @Override
    public ReservationTableDTO save(ReservationTableDTO reservationTableDTO) {
        LOG.debug("Request to save ReservationTable : {}", reservationTableDTO);
        ReservationTable reservationTable = reservationTableMapper.toEntity(reservationTableDTO);
        reservationTable = reservationTableRepository.save(reservationTable);
        return reservationTableMapper.toDto(reservationTable);
    }

    @Override
    public ReservationTableDTO update(ReservationTableDTO reservationTableDTO) {
        LOG.debug("Request to update ReservationTable : {}", reservationTableDTO);
        ReservationTable reservationTable = reservationTableMapper.toEntity(reservationTableDTO);
        reservationTable = reservationTableRepository.save(reservationTable);
        return reservationTableMapper.toDto(reservationTable);
    }

    @Override
    public Optional<ReservationTableDTO> partialUpdate(ReservationTableDTO reservationTableDTO) {
        LOG.debug("Request to partially update ReservationTable : {}", reservationTableDTO);

        return reservationTableRepository
            .findById(reservationTableDTO.getId())
            .map(existingReservationTable -> {
                reservationTableMapper.partialUpdate(existingReservationTable, reservationTableDTO);

                return existingReservationTable;
            })
            .map(reservationTableRepository::save)
            .map(reservationTableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationTableDTO> findAll() {
        LOG.debug("Request to get all ReservationTables");
        return reservationTableRepository
            .findAll()
            .stream()
            .map(reservationTableMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ReservationTableDTO> findAllWithEagerRelationships(Pageable pageable) {
        return reservationTableRepository.findAllWithEagerRelationships(pageable).map(reservationTableMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationTableDTO> findOne(Long id) {
        LOG.debug("Request to get ReservationTable : {}", id);
        return reservationTableRepository.findOneWithEagerRelationships(id).map(reservationTableMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ReservationTable : {}", id);
        reservationTableRepository.deleteById(id);
    }
}
