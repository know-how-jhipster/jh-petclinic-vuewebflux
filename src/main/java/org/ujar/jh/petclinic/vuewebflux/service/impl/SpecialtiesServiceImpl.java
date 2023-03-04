package org.ujar.jh.petclinic.vuewebflux.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ujar.jh.petclinic.vuewebflux.domain.Specialties;
import org.ujar.jh.petclinic.vuewebflux.repository.SpecialtiesRepository;
import org.ujar.jh.petclinic.vuewebflux.service.SpecialtiesService;
import org.ujar.jh.petclinic.vuewebflux.service.dto.SpecialtiesDTO;
import org.ujar.jh.petclinic.vuewebflux.service.mapper.SpecialtiesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Specialties}.
 */
@Service
@Transactional
public class SpecialtiesServiceImpl implements SpecialtiesService {

    private final Logger log = LoggerFactory.getLogger(SpecialtiesServiceImpl.class);

    private final SpecialtiesRepository specialtiesRepository;

    private final SpecialtiesMapper specialtiesMapper;

    public SpecialtiesServiceImpl(SpecialtiesRepository specialtiesRepository, SpecialtiesMapper specialtiesMapper) {
        this.specialtiesRepository = specialtiesRepository;
        this.specialtiesMapper = specialtiesMapper;
    }

    @Override
    public Mono<SpecialtiesDTO> save(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to save Specialties : {}", specialtiesDTO);
        return specialtiesRepository.save(specialtiesMapper.toEntity(specialtiesDTO)).map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<SpecialtiesDTO> update(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to update Specialties : {}", specialtiesDTO);
        return specialtiesRepository.save(specialtiesMapper.toEntity(specialtiesDTO)).map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<SpecialtiesDTO> partialUpdate(SpecialtiesDTO specialtiesDTO) {
        log.debug("Request to partially update Specialties : {}", specialtiesDTO);

        return specialtiesRepository
            .findById(specialtiesDTO.getId())
            .map(existingSpecialties -> {
                specialtiesMapper.partialUpdate(existingSpecialties, specialtiesDTO);

                return existingSpecialties;
            })
            .flatMap(specialtiesRepository::save)
            .map(specialtiesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SpecialtiesDTO> findAll() {
        log.debug("Request to get all Specialties");
        return specialtiesRepository.findAll().map(specialtiesMapper::toDto);
    }

    public Flux<SpecialtiesDTO> findAllWithEagerRelationships(Pageable pageable) {
        return specialtiesRepository.findAllWithEagerRelationships(pageable).map(specialtiesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return specialtiesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SpecialtiesDTO> findOne(Long id) {
        log.debug("Request to get Specialties : {}", id);
        return specialtiesRepository.findOneWithEagerRelationships(id).map(specialtiesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Specialties : {}", id);
        return specialtiesRepository.deleteById(id);
    }
}
