package dev.knowhowto.jh.petclinic.vuewebflux.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuewebflux.domain.Owners;
import dev.knowhowto.jh.petclinic.vuewebflux.repository.OwnersRepository;
import dev.knowhowto.jh.petclinic.vuewebflux.service.OwnersService;
import dev.knowhowto.jh.petclinic.vuewebflux.service.dto.OwnersDTO;
import dev.knowhowto.jh.petclinic.vuewebflux.service.mapper.OwnersMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Owners}.
 */
@Service
@Transactional
public class OwnersServiceImpl implements OwnersService {

    private final Logger log = LoggerFactory.getLogger(OwnersServiceImpl.class);

    private final OwnersRepository ownersRepository;

    private final OwnersMapper ownersMapper;

    public OwnersServiceImpl(OwnersRepository ownersRepository, OwnersMapper ownersMapper) {
        this.ownersRepository = ownersRepository;
        this.ownersMapper = ownersMapper;
    }

    @Override
    public Mono<OwnersDTO> save(OwnersDTO ownersDTO) {
        log.debug("Request to save Owners : {}", ownersDTO);
        return ownersRepository.save(ownersMapper.toEntity(ownersDTO)).map(ownersMapper::toDto);
    }

    @Override
    public Mono<OwnersDTO> update(OwnersDTO ownersDTO) {
        log.debug("Request to update Owners : {}", ownersDTO);
        return ownersRepository.save(ownersMapper.toEntity(ownersDTO)).map(ownersMapper::toDto);
    }

    @Override
    public Mono<OwnersDTO> partialUpdate(OwnersDTO ownersDTO) {
        log.debug("Request to partially update Owners : {}", ownersDTO);

        return ownersRepository
            .findById(ownersDTO.getId())
            .map(existingOwners -> {
                ownersMapper.partialUpdate(existingOwners, ownersDTO);

                return existingOwners;
            })
            .flatMap(ownersRepository::save)
            .map(ownersMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OwnersDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Owners");
        return ownersRepository.findAllBy(pageable).map(ownersMapper::toDto);
    }

    public Mono<Long> countAll() {
        return ownersRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OwnersDTO> findOne(Long id) {
        log.debug("Request to get Owners : {}", id);
        return ownersRepository.findById(id).map(ownersMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Owners : {}", id);
        return ownersRepository.deleteById(id);
    }
}
