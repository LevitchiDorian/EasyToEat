package md.utm.restaurant.service.impl;

import java.util.Optional;
import md.utm.restaurant.domain.WaitingList;
import md.utm.restaurant.repository.WaitingListRepository;
import md.utm.restaurant.service.WaitingListService;
import md.utm.restaurant.service.dto.WaitingListDTO;
import md.utm.restaurant.service.mapper.WaitingListMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link md.utm.restaurant.domain.WaitingList}.
 */
@Service
@Transactional
public class WaitingListServiceImpl implements WaitingListService {

    private static final Logger LOG = LoggerFactory.getLogger(WaitingListServiceImpl.class);

    private final WaitingListRepository waitingListRepository;

    private final WaitingListMapper waitingListMapper;

    public WaitingListServiceImpl(WaitingListRepository waitingListRepository, WaitingListMapper waitingListMapper) {
        this.waitingListRepository = waitingListRepository;
        this.waitingListMapper = waitingListMapper;
    }

    @Override
    public WaitingListDTO save(WaitingListDTO waitingListDTO) {
        LOG.debug("Request to save WaitingList : {}", waitingListDTO);
        WaitingList waitingList = waitingListMapper.toEntity(waitingListDTO);
        waitingList = waitingListRepository.save(waitingList);
        return waitingListMapper.toDto(waitingList);
    }

    @Override
    public WaitingListDTO update(WaitingListDTO waitingListDTO) {
        LOG.debug("Request to update WaitingList : {}", waitingListDTO);
        WaitingList waitingList = waitingListMapper.toEntity(waitingListDTO);
        waitingList = waitingListRepository.save(waitingList);
        return waitingListMapper.toDto(waitingList);
    }

    @Override
    public Optional<WaitingListDTO> partialUpdate(WaitingListDTO waitingListDTO) {
        LOG.debug("Request to partially update WaitingList : {}", waitingListDTO);

        return waitingListRepository
            .findById(waitingListDTO.getId())
            .map(existingWaitingList -> {
                waitingListMapper.partialUpdate(existingWaitingList, waitingListDTO);

                return existingWaitingList;
            })
            .map(waitingListRepository::save)
            .map(waitingListMapper::toDto);
    }

    public Page<WaitingListDTO> findAllWithEagerRelationships(Pageable pageable) {
        return waitingListRepository.findAllWithEagerRelationships(pageable).map(waitingListMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WaitingListDTO> findOne(Long id) {
        LOG.debug("Request to get WaitingList : {}", id);
        return waitingListRepository.findOneWithEagerRelationships(id).map(waitingListMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete WaitingList : {}", id);
        waitingListRepository.deleteById(id);
    }
}
