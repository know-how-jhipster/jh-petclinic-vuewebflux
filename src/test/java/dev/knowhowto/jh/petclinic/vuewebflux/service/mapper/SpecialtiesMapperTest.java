package dev.knowhowto.jh.petclinic.vuewebflux.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecialtiesMapperTest {

    private SpecialtiesMapper specialtiesMapper;

    @BeforeEach
    public void setUp() {
        specialtiesMapper = new SpecialtiesMapperImpl();
    }
}
