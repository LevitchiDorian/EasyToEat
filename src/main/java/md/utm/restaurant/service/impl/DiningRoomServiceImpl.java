package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.DiningRoom;
import md.utm.restaurant.repository.DiningRoomRepository;
import md.utm.restaurant.service.DiningRoomService;
import md.utm.restaurant.service.dto.DiningRoomDTO;
import md.utm.restaurant.service.mapper.DiningRoomMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.DiningRoom}.
 */
@Service
@Transactional
public class DiningRoomServiceImpl implements DiningRoomService {

    private static final Logger LOG = LoggerFactory.getLogger(DiningRoomServiceImpl.class);

    private final DiningRoomRepository diningRoomRepository;

    private final DiningRoomMapper diningRoomMapper;

    public DiningRoomServiceImpl(DiningRoomRepository diningRoomRepository, DiningRoomMapper diningRoomMapper) {
        this.diningRoomRepository = diningRoomRepository;
        this.diningRoomMapper = diningRoomMapper;
    }

    @Override
    public DiningRoomDTO save(DiningRoomDTO diningRoomDTO) {
        LOG.debug("Request to save DiningRoom : {}", diningRoomDTO);
        DiningRoom diningRoom = diningRoomMapper.toEntity(diningRoomDTO);
        diningRoom = diningRoomRepository.save(diningRoom);
        return diningRoomMapper.toDto(diningRoom);
    }

    @Override
    public DiningRoomDTO update(DiningRoomDTO diningRoomDTO) {
        LOG.debug("Request to update DiningRoom : {}", diningRoomDTO);
        DiningRoom diningRoom = diningRoomMapper.toEntity(diningRoomDTO);
        diningRoom = diningRoomRepository.save(diningRoom);
        return diningRoomMapper.toDto(diningRoom);
    }

    @Override
    public Optional<DiningRoomDTO> partialUpdate(DiningRoomDTO diningRoomDTO) {
        LOG.debug("Request to partially update DiningRoom : {}", diningRoomDTO);

        return diningRoomRepository
            .findById(diningRoomDTO.getId())
            .map(existingDiningRoom -> {
                diningRoomMapper.partialUpdate(existingDiningRoom, diningRoomDTO);

                return existingDiningRoom;
            })
            .map(diningRoomRepository::save)
            .map(diningRoomMapper::toDto);
    }

    public Page<DiningRoomDTO> findAllWithEagerRelationships(Pageable pageable) {
        return diningRoomRepository.findAllWithEagerRelationships(pageable).map(diningRoomMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DiningRoomDTO> findOne(Long id) {
        LOG.debug("Request to get DiningRoom : {}", id);
        return diningRoomRepository.findOneWithEagerRelationships(id).map(diningRoomMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DiningRoom : {}", id);
        diningRoomRepository.deleteById(id);
    }
}
