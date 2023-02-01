package org.ujar.jh.petclinic.vuewebflux.service.mapper;

import org.mapstruct.*;
import org.ujar.jh.petclinic.vuewebflux.domain.Vets;
import org.ujar.jh.petclinic.vuewebflux.service.dto.VetsDTO;

/**
 * Mapper for the entity {@link Vets} and its DTO {@link VetsDTO}.
 */
@Mapper(componentModel = "spring")
public interface VetsMapper extends EntityMapper<VetsDTO, Vets> {}
