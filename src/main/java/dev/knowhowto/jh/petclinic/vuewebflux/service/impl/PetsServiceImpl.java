package dev.knowhowto.jh.petclinic.vuewebflux.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuewebflux.domain.Pets;
import dev.knowhowto.jh.petclinic.vuewebflux.repository.PetsRepository;
import dev.knowhowto.jh.petclinic.vuewebflux.service.PetsService;
import dev.knowhowto.jh.petclinic.vuewebflux.service.dto.PetsDTO;
import dev.knowhowto.jh.petclinic.vuewebflux.service.mapper.PetsMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Pets}.
 */
@Service
@Transactional
public class PetsServiceImpl implements PetsService {

    private final Logger log = LoggerFactory.getLogger(PetsServiceImpl.class);

    private final PetsRepository petsRepository;

    private final PetsMapper petsMapper;

    public PetsServiceImpl(PetsRepository petsRepository, PetsMapper petsMapper) {
        this.petsRepository = petsRepository;
        this.petsMapper = petsMapper;
    }

    @Override
    public Mono<PetsDTO> save(PetsDTO petsDTO) {
        log.debug("Request to save Pets : {}", petsDTO);
        return petsRepository.save(petsMapper.toEntity(petsDTO)).map(petsMapper::toDto);
    }

    @Override
    public Mono<PetsDTO> update(PetsDTO petsDTO) {
        log.debug("Request to update Pets : {}", petsDTO);
        return petsRepository.save(petsMapper.toEntity(petsDTO)).map(petsMapper::toDto);
    }

    @Override
    public Mono<PetsDTO> partialUpdate(PetsDTO petsDTO) {
        log.debug("Request to partially update Pets : {}", petsDTO);

        return petsRepository
            .findById(petsDTO.getId())
            .map(existingPets -> {
                petsMapper.partialUpdate(existingPets, petsDTO);

                return existingPets;
            })
            .flatMap(petsRepository::save)
            .map(petsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PetsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pets");
        return petsRepository.findAllBy(pageable).map(petsMapper::toDto);
    }

    public Mono<Long> countAll() {
        return petsRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PetsDTO> findOne(Long id) {
        log.debug("Request to get Pets : {}", id);
        return petsRepository.findById(id).map(petsMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Pets : {}", id);
        return petsRepository.deleteById(id);
    }
}
