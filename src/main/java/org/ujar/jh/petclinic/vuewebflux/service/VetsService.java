package org.ujar.jh.petclinic.vuewebflux.service;

import org.springframework.data.domain.Pageable;
import org.ujar.jh.petclinic.vuewebflux.service.dto.VetsDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link org.ujar.jh.petclinic.vuewebflux.domain.Vets}.
 */
public interface VetsService {
    /**
     * Save a vets.
     *
     * @param vetsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<VetsDTO> save(VetsDTO vetsDTO);

    /**
     * Updates a vets.
     *
     * @param vetsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<VetsDTO> update(VetsDTO vetsDTO);

    /**
     * Partially updates a vets.
     *
     * @param vetsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<VetsDTO> partialUpdate(VetsDTO vetsDTO);

    /**
     * Get all the vets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<VetsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of vets available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" vets.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<VetsDTO> findOne(Long id);

    /**
     * Delete the "id" vets.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
