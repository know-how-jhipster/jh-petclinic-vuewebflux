package org.ujar.jh.petclinic.vuewebflux.service.mapper;

import org.mapstruct.*;
import org.ujar.jh.petclinic.vuewebflux.domain.Owners;
import org.ujar.jh.petclinic.vuewebflux.domain.Pets;
import org.ujar.jh.petclinic.vuewebflux.domain.Types;
import org.ujar.jh.petclinic.vuewebflux.service.dto.OwnersDTO;
import org.ujar.jh.petclinic.vuewebflux.service.dto.PetsDTO;
import org.ujar.jh.petclinic.vuewebflux.service.dto.TypesDTO;

/**
 * Mapper for the entity {@link Pets} and its DTO {@link PetsDTO}.
 */
@Mapper(componentModel = "spring")
public interface PetsMapper extends EntityMapper<PetsDTO, Pets> {
    @Mapping(target = "type", source = "type", qualifiedByName = "typesId")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownersId")
    PetsDTO toDto(Pets s);

    @Named("typesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TypesDTO toDtoTypesId(Types types);

    @Named("ownersId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OwnersDTO toDtoOwnersId(Owners owners);
}
