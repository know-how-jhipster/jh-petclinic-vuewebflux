package org.ujar.jh.petclinic.vuewebflux.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.ujar.jh.petclinic.vuewebflux.domain.Types} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypesDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 80)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypesDTO)) {
            return false;
        }

        TypesDTO typesDTO = (TypesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, typesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypesDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
