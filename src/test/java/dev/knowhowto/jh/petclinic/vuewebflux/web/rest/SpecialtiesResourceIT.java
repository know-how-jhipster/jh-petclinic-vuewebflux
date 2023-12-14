package dev.knowhowto.jh.petclinic.vuewebflux.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import dev.knowhowto.jh.petclinic.vuewebflux.IntegrationTest;
import dev.knowhowto.jh.petclinic.vuewebflux.domain.Specialties;
import dev.knowhowto.jh.petclinic.vuewebflux.repository.EntityManager;
import dev.knowhowto.jh.petclinic.vuewebflux.repository.SpecialtiesRepository;
import dev.knowhowto.jh.petclinic.vuewebflux.service.SpecialtiesService;
import dev.knowhowto.jh.petclinic.vuewebflux.service.dto.SpecialtiesDTO;
import dev.knowhowto.jh.petclinic.vuewebflux.service.mapper.SpecialtiesMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link SpecialtiesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SpecialtiesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/specialties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpecialtiesRepository specialtiesRepository;

    @Mock
    private SpecialtiesRepository specialtiesRepositoryMock;

    @Autowired
    private SpecialtiesMapper specialtiesMapper;

    @Mock
    private SpecialtiesService specialtiesServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Specialties specialties;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specialties createEntity(EntityManager em) {
        Specialties specialties = new Specialties().name(DEFAULT_NAME);
        return specialties;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specialties createUpdatedEntity(EntityManager em) {
        Specialties specialties = new Specialties().name(UPDATED_NAME);
        return specialties;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_specialties__vet").block();
            em.deleteAll(Specialties.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        specialties = createEntity(em);
    }

    @Test
    void createSpecialties() throws Exception {
        int databaseSizeBeforeCreate = specialtiesRepository.findAll().collectList().block().size();
        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeCreate + 1);
        Specialties testSpecialties = specialtiesList.get(specialtiesList.size() - 1);
        assertThat(testSpecialties.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createSpecialtiesWithExistingId() throws Exception {
        // Create the Specialties with an existing ID
        specialties.setId(1L);
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        int databaseSizeBeforeCreate = specialtiesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = specialtiesRepository.findAll().collectList().block().size();
        // set the field null
        specialties.setName(null);

        // Create the Specialties, which fails.
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSpecialtiesAsStream() {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        List<Specialties> specialtiesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SpecialtiesDTO.class)
            .getResponseBody()
            .map(specialtiesMapper::toEntity)
            .filter(specialties::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(specialtiesList).isNotNull();
        assertThat(specialtiesList).hasSize(1);
        Specialties testSpecialties = specialtiesList.get(0);
        assertThat(testSpecialties.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void getAllSpecialties() {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        // Get all the specialtiesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(specialties.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpecialtiesWithEagerRelationshipsIsEnabled() {
        when(specialtiesServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(specialtiesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpecialtiesWithEagerRelationshipsIsNotEnabled() {
        when(specialtiesServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(specialtiesRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getSpecialties() {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        // Get the specialties
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, specialties.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(specialties.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingSpecialties() {
        // Get the specialties
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSpecialties() throws Exception {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();

        // Update the specialties
        Specialties updatedSpecialties = specialtiesRepository.findById(specialties.getId()).block();
        updatedSpecialties.name(UPDATED_NAME);
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(updatedSpecialties);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, specialtiesDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
        Specialties testSpecialties = specialtiesList.get(specialtiesList.size() - 1);
        assertThat(testSpecialties.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, specialtiesDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSpecialtiesWithPatch() throws Exception {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();

        // Update the specialties using partial update
        Specialties partialUpdatedSpecialties = new Specialties();
        partialUpdatedSpecialties.setId(specialties.getId());

        partialUpdatedSpecialties.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSpecialties.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecialties))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
        Specialties testSpecialties = specialtiesList.get(specialtiesList.size() - 1);
        assertThat(testSpecialties.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateSpecialtiesWithPatch() throws Exception {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();

        // Update the specialties using partial update
        Specialties partialUpdatedSpecialties = new Specialties();
        partialUpdatedSpecialties.setId(specialties.getId());

        partialUpdatedSpecialties.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSpecialties.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecialties))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
        Specialties testSpecialties = specialtiesList.get(specialtiesList.size() - 1);
        assertThat(testSpecialties.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, specialtiesDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSpecialties() throws Exception {
        int databaseSizeBeforeUpdate = specialtiesRepository.findAll().collectList().block().size();
        specialties.setId(count.incrementAndGet());

        // Create the Specialties
        SpecialtiesDTO specialtiesDTO = specialtiesMapper.toDto(specialties);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(specialtiesDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Specialties in the database
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSpecialties() {
        // Initialize the database
        specialtiesRepository.save(specialties).block();

        int databaseSizeBeforeDelete = specialtiesRepository.findAll().collectList().block().size();

        // Delete the specialties
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, specialties.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Specialties> specialtiesList = specialtiesRepository.findAll().collectList().block();
        assertThat(specialtiesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
