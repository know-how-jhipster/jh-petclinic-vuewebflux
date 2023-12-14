package dev.knowhowto.jh.petclinic.vuewebflux.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.knowhowto.jh.petclinic.vuewebflux.domain.Types;
import dev.knowhowto.jh.petclinic.vuewebflux.repository.TypesRepository;
import dev.knowhowto.jh.petclinic.vuewebflux.service.TypesService;
import dev.knowhowto.jh.petclinic.vuewebflux.service.dto.TypesDTO;
import dev.knowhowto.jh.petclinic.vuewebflux.service.mapper.TypesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Types}.
 */
@Service
@Transactional
public class TypesServiceImpl implements TypesService {

    private final Logger log = LoggerFactory.getLogger(TypesServiceImpl.class);

    private final TypesRepository typesRepository;

    private final TypesMapper typesMapper;

    public TypesServiceImpl(TypesRepository typesRepository, TypesMapper typesMapper) {
        this.typesRepository = typesRepository;
        this.typesMapper = typesMapper;
    }

    @Override
    public Mono<TypesDTO> save(TypesDTO typesDTO) {
        log.debug("Request to save Types : {}", typesDTO);
        return typesRepository.save(typesMapper.toEntity(typesDTO)).map(typesMapper::toDto);
    }

    @Override
    public Mono<TypesDTO> update(TypesDTO typesDTO) {
        log.debug("Request to update Types : {}", typesDTO);
        return typesRepository.save(typesMapper.toEntity(typesDTO)).map(typesMapper::toDto);
    }

    @Override
    public Mono<TypesDTO> partialUpdate(TypesDTO typesDTO) {
        log.debug("Request to partially update Types : {}", typesDTO);

        return typesRepository
            .findById(typesDTO.getId())
            .map(existingTypes -> {
                typesMapper.partialUpdate(existingTypes, typesDTO);

                return existingTypes;
            })
            .flatMap(typesRepository::save)
            .map(typesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TypesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Types");
        return typesRepository.findAllBy(pageable).map(typesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return typesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TypesDTO> findOne(Long id) {
        log.debug("Request to get Types : {}", id);
        return typesRepository.findById(id).map(typesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Types : {}", id);
        return typesRepository.deleteById(id);
    }
}
