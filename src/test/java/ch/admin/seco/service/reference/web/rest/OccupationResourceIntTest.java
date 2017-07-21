package ch.admin.seco.service.reference.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.ReferenceserviceApp;
import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import ch.admin.seco.service.reference.service.ClassificationService;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the OccupationResource REST controller.
 *
 * @see OccupationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class OccupationResourceIntTest {

    private static final Integer DEFAULT_CODE = 88888888;
    private static final Integer DEFAULT_CODE_2 = 77777777;
    private static final Integer UPDATED_CODE = 99999999;

    private static final Language DEFAULT_LANGUAGE = Language.de;
    private static final Language UPDATED_LANGUAGE = Language.fr;

    private static final String DEFAULT_NAME = "Gärtner";
    private static final String DEFAULT_NAME_2 = "Gärtner/innen und verwandte Berufe";
    private static final String UPDATED_NAME = "Java Informatiker";

    @Autowired
    private OccupationRepository occupationRepository;

    @Autowired
    private OccupationService occupationService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private OccupationSearchRepository occupationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOccupationMockMvc;

    private Occupation occupation;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Occupation createEntity(EntityManager em) {
        Occupation occupation = new Occupation()
            .code(DEFAULT_CODE)
            .language(DEFAULT_LANGUAGE)
            .name(DEFAULT_NAME);
        return occupation;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OccupationResource occupationResource = new OccupationResource(occupationService);
        this.restOccupationMockMvc = MockMvcBuilders.standaloneSetup(occupationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        occupationSearchRepository.deleteAll();
        occupation = createEntity(em);
    }

    @Test
    @Transactional
    public void createOccupation() throws Exception {
        int databaseSizeBeforeCreate = occupationRepository.findAll().size();

        // Create the Occupation
        restOccupationMockMvc.perform(post("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isCreated());

        // Validate the Occupation in the database
        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeCreate + 1);
        Occupation testOccupation = occupationList.get(occupationList.size() - 1);
        assertThat(testOccupation.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOccupation.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testOccupation.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Occupation in Elasticsearch
        List<OccupationSynonym> occupationSynonym = occupationSearchRepository.findAllByCodeEquals(testOccupation.getCode());
        assertThat(occupationSynonym).hasSize(1);
    }

    @Test
    @Transactional
    public void createOccupationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = occupationRepository.findAll().size();

        // Create the Occupation with an existing ID
        occupation.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOccupationMockMvc.perform(post("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationRepository.findAll().size();
        // set the field null
        occupation.setCode(-1);

        // Create the Occupation, which fails.

        restOccupationMockMvc.perform(post("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isBadRequest());

        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationRepository.findAll().size();
        // set the field null
        occupation.setLanguage(null);

        // Create the Occupation, which fails.

        restOccupationMockMvc.perform(post("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isBadRequest());

        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationRepository.findAll().size();
        // set the field null
        occupation.setName(null);

        // Create the Occupation, which fails.

        restOccupationMockMvc.perform(post("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isBadRequest());

        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOccupations() throws Exception {
        // Initialize the database
        occupationRepository.deleteAll();
        occupationRepository.saveAndFlush(occupation);

        // Get all the occupationList
        restOccupationMockMvc.perform(get("/api/occupations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(occupation.getId().toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getOccupation() throws Exception {
        // Initialize the database
        occupationRepository.saveAndFlush(occupation);

        // Get the occupation
        restOccupationMockMvc.perform(get("/api/occupations/{id}", occupation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupation.getId().toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOccupation() throws Exception {
        // Get the occupation
        restOccupationMockMvc.perform(get("/api/occupations/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOccupation() throws Exception {
        // Initialize the database
        occupationService.save(occupation);

        int databaseSizeBeforeUpdate = occupationRepository.findAll().size();

        // Update the occupation
        Occupation updatedOccupation = occupationRepository.getOne(occupation.getId());
        updatedOccupation
            .code(UPDATED_CODE)
            .language(UPDATED_LANGUAGE)
            .name(UPDATED_NAME);

        restOccupationMockMvc.perform(put("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOccupation)))
            .andExpect(status().isOk());

        // Validate the Occupation in the database
        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeUpdate);
        Occupation testOccupation = occupationList.get(occupationList.size() - 1);
        assertThat(testOccupation.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOccupation.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testOccupation.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Occupation in Elasticsearch
        List<OccupationSynonym> occupationSynonym = occupationSearchRepository.findAllByCodeEquals(testOccupation.getCode());
        assertThat(occupationSynonym).hasSize(1);
    }

    @Test
    @Transactional
    public void updateNonExistingOccupation() throws Exception {
        int databaseSizeBeforeUpdate = occupationRepository.findAll().size();

        // Create the Occupation

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOccupationMockMvc.perform(put("/api/occupations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupation)))
            .andExpect(status().isCreated());

        // Validate the Occupation in the database
        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOccupation() throws Exception {
        // Initialize the database
        occupationService.save(occupation);

        int databaseSizeBeforeDelete = occupationRepository.findAll().size();

        // Get the occupation
        restOccupationMockMvc.perform(delete("/api/occupations/{id}", occupation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean occupationExistsInEs = occupationSearchRepository.findById(occupation.getId()).isPresent();
        assertThat(occupationExistsInEs).isFalse();

        // Validate the database is empty
        List<Occupation> occupationList = occupationRepository.findAll();
        assertThat(occupationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOccupation() throws Exception {
        // Initialize the database
        occupationService.save(occupation);
        classificationService.save(
            new Classification()
                .code(DEFAULT_CODE_2)
                .name(DEFAULT_NAME_2)
                .language(DEFAULT_LANGUAGE)
        );

        // Search the occupation
        restOccupationMockMvc.perform(get("/api/_search/occupations?prefix=Gaert&language=de&responseSize=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.occupations.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.occupations.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.classifications.[*].code").value(hasItem(DEFAULT_CODE_2)))
            .andExpect(jsonPath("$.classifications.[*].name").value(hasItem(DEFAULT_NAME_2)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Occupation.class);
        Occupation occupation1 = new Occupation();
        occupation1.setId(UUID.randomUUID());
        Occupation occupation2 = new Occupation();
        occupation2.setId(occupation1.getId());
        assertThat(occupation1).isEqualTo(occupation2);
        occupation2.setId(UUID.randomUUID());
        assertThat(occupation1).isNotEqualTo(occupation2);
        occupation1.setId(null);
        assertThat(occupation1).isNotEqualTo(occupation2);
    }
}
