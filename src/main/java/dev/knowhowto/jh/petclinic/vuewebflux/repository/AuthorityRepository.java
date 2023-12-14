package dev.knowhowto.jh.petclinic.vuewebflux.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import dev.knowhowto.jh.petclinic.vuewebflux.domain.Authority;

/**
 * Spring Data R2DBC repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {}
