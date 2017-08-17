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
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSynonymSearchRepository;
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

    //Occupation synonym constants
    private static final Integer DEFAULT_CODE = 88888888;
    private static final Integer DEFAULT_CODE_2 = 77777777;
    private static final Integer UPDATED_CODE = 99999999;

    private static final Language DEFAULT_LANGUAGE = Language.de;
    private static final Language UPDATED_LANGUAGE = Language.fr;

    private static final String DEFAULT_NAME = "Gärtner";
    private static final String DEFAULT_NAME_2 = "Gärtner/innen und verwandte Berufe";
    private static final String UPDATED_NAME = "Java Informatiker";

    //Occupation mapping constants
    private static final Integer MAPPING_CODE = 10000000;
    private static final Integer MAPPING_X28_CODE = 10000000;
    private static final Integer MAPPING_AVAM_CODE = 10000;

    //Occupation constants
    private static final Integer OCCUPATION_CODE = 10000000;
    private static final Integer OCCUPATION_CLASSIFICATION_CODE = 111;
    private static final String OCCUPATION_LABEL_DE = "Label DE";
    private static final String OCCUPATION_LABEL_FR = "Label FR";
    private static final String OCCUPATION_LABEL_IT = "Label IT";
    private static final String OCCUPATION_LABEL_EN = "Label EN";

    @Autowired
    private OccupationSynonymRepository occupationSynonymRepository;

    @Autowired
    private OccupationService occupationService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private OccupationSynonymSearchRepository occupationSynonymSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private OccupationMappingRepository occupationMappingRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    private MockMvc restOccupationMockMvc;

    private OccupationSynonym occupationSynonym;

    private OccupationMapping occupationMapping;

    private Occupation occupation;

    /**
     * Create an OccupationSynonym entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OccupationSynonym createOccupationSynonymEntity() {
        return new OccupationSynonym()
            .code(DEFAULT_CODE)
            .language(DEFAULT_LANGUAGE)
            .name(DEFAULT_NAME);
    }

    /**
     * Create an OccupationMapping entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OccupationMapping createOccupationMappingEntity() {
        return new OccupationMapping()
            .code(MAPPING_CODE)
            .x28Code(MAPPING_X28_CODE)
            .avamCode(MAPPING_AVAM_CODE);
    }

    private static Occupation createOccupationEntity() {
        return new Occupation()
            .code(OCCUPATION_CODE)
            .classificationCode(OCCUPATION_CLASSIFICATION_CODE)
            .labelDe(OCCUPATION_LABEL_DE)
            .labelFr(OCCUPATION_LABEL_FR)
            .labelIt(OCCUPATION_LABEL_IT)
            .labelEn(OCCUPATION_LABEL_EN);
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
        occupationSynonymSearchRepository.deleteAll();
        occupationSynonym = createOccupationSynonymEntity();
        occupationMapping = createOccupationMappingEntity();
        occupation = createOccupationEntity();
    }

    @Test
    @Transactional
    public void createOccupationSynonym() throws Exception {
        int databaseSizeBeforeCreate = occupationSynonymRepository.findAll().size();

        // Create the OccupationSynonym
        restOccupationMockMvc.perform(post("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isCreated());

        // Validate the OccupationSynonym in the database
        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeCreate + 1);
        OccupationSynonym testOccupationSynonym = occupationSynonymList.get(occupationSynonymList.size() - 1);
        assertThat(testOccupationSynonym.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testOccupationSynonym.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testOccupationSynonym.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the OccupationSynonym in Elasticsearch
        List<ch.admin.seco.service.reference.domain.search.OccupationSynonym> occupationSynonymSynonym = occupationSynonymSearchRepository.findAllByCodeEquals(testOccupationSynonym.getCode());
        assertThat(occupationSynonymSynonym).hasSize(1);
    }

    @Test
    @Transactional
    public void createOccupationSynonymWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = occupationSynonymRepository.findAll().size();

        // Create the OccupationSynonym with an existing ID
        occupationSynonym.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOccupationMockMvc.perform(post("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationSynonymRepository.findAll().size();
        // set the field null
        occupationSynonym.setCode(-1);

        // Create the OccupationSynonym, which fails.

        restOccupationMockMvc.perform(post("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isBadRequest());

        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationSynonymRepository.findAll().size();
        // set the field null
        occupationSynonym.setLanguage(null);

        // Create the OccupationSynonym, which fails.

        restOccupationMockMvc.perform(post("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isBadRequest());

        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = occupationSynonymRepository.findAll().size();
        // set the field null
        occupationSynonym.setName(null);

        // Create the OccupationSynonym, which fails.

        restOccupationMockMvc.perform(post("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isBadRequest());

        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOccupationSynonyms() throws Exception {
        // Initialize the database
        occupationSynonymRepository.deleteAll();
        occupationSynonymRepository.saveAndFlush(occupationSynonym);

        // Get all the occupationList
        restOccupationMockMvc.perform(get("/api/occupations/synonym?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(occupationSynonym.getId().toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getOccupationSynonym() throws Exception {
        // Initialize the database
        occupationSynonymRepository.saveAndFlush(occupationSynonym);

        // Get the occupationSynonym
        restOccupationMockMvc.perform(get("/api/occupations/synonym/{id}", occupationSynonym.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupationSynonym.getId().toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOccupationSynonym() throws Exception {
        // Get the occupationSynonym
        restOccupationMockMvc.perform(get("/api/occupations/synonym/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOccupationSynonym() throws Exception {
        // Initialize the database
        occupationService.save(occupationSynonym);

        int databaseSizeBeforeUpdate = occupationSynonymRepository.findAll().size();

        // Update the occupationSynonym
        OccupationSynonym updatedOccupationSynonym = occupationSynonymRepository.getOne(occupationSynonym.getId());
        updatedOccupationSynonym
            .code(UPDATED_CODE)
            .language(UPDATED_LANGUAGE)
            .name(UPDATED_NAME);

        restOccupationMockMvc.perform(put("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOccupationSynonym)))
            .andExpect(status().isOk());

        // Validate the OccupationSynonym in the database
        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeUpdate);
        OccupationSynonym testOccupationSynonym = occupationSynonymList.get(occupationSynonymList.size() - 1);
        assertThat(testOccupationSynonym.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testOccupationSynonym.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testOccupationSynonym.getName()).isEqualTo(UPDATED_NAME);

        // Validate the OccupationSynonym in Elasticsearch
        List<ch.admin.seco.service.reference.domain.search.OccupationSynonym> occupationSynonymSynonym = occupationSynonymSearchRepository.findAllByCodeEquals(testOccupationSynonym.getCode());
        assertThat(occupationSynonymSynonym).hasSize(1);
    }

    @Test
    @Transactional
    public void updateNonExistingOccupationSynonym() throws Exception {
        int databaseSizeBeforeUpdate = occupationSynonymRepository.findAll().size();

        // Create the OccupationSynonym

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOccupationMockMvc.perform(put("/api/occupations/synonym")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(occupationSynonym)))
            .andExpect(status().isCreated());

        // Validate the OccupationSynonym in the database
        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOccupationSynonym() throws Exception {
        // Initialize the database
        occupationService.save(occupationSynonym);

        int databaseSizeBeforeDelete = occupationSynonymRepository.findAll().size();

        // Get the occupationSynonym
        restOccupationMockMvc.perform(delete("/api/occupations/synonym/{id}", occupationSynonym.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean occupationExistsInEs = occupationSynonymSearchRepository.findById(occupationSynonym.getId()).isPresent();
        assertThat(occupationExistsInEs).isFalse();

        // Validate the database is empty
        List<OccupationSynonym> occupationSynonymList = occupationSynonymRepository.findAll();
        assertThat(occupationSynonymList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOccupationSynonym() throws Exception {
        // Initialize the database
        occupationService.save(occupationSynonym);
        classificationService.save(
            new Classification()
                .code(DEFAULT_CODE_2)
                .name(DEFAULT_NAME_2)
                .language(DEFAULT_LANGUAGE)
        );

        // Search the occupationSynonym
        restOccupationMockMvc.perform(get("/api/_search/occupations/synonym?prefix=Gaert&language=de&responseSize=10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.occupations.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.occupations.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.classifications.[*].code").value(hasItem(DEFAULT_CODE_2)))
            .andExpect(jsonPath("$.classifications.[*].name").value(hasItem(DEFAULT_NAME_2)));
    }

    @Test
    @Transactional
    public void occupationSynonymEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OccupationSynonym.class);
        OccupationSynonym occupationSynonym1 = new OccupationSynonym();
        occupationSynonym1.setId(UUID.randomUUID());
        OccupationSynonym occupationSynonym2 = new OccupationSynonym();
        occupationSynonym2.setId(occupationSynonym1.getId());
        assertThat(occupationSynonym1).isEqualTo(occupationSynonym2);
        occupationSynonym2.setId(UUID.randomUUID());
        assertThat(occupationSynonym1).isNotEqualTo(occupationSynonym2);
        occupationSynonym1.setId(null);
        assertThat(occupationSynonym1).isNotEqualTo(occupationSynonym2);
    }

    @Test
    @Transactional
    public void getAllOccupationMappings() throws Exception {
        // Initialize the database
        occupationMappingRepository.deleteAll();
        occupationMappingRepository.saveAndFlush(occupationMapping);

        // Get all the occupationMappingList
        restOccupationMockMvc.perform(get("/api/occupations/mapping?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(occupationMapping.getId().toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(MAPPING_CODE)))
            .andExpect(jsonPath("$.[*].x28Code").value(hasItem(MAPPING_X28_CODE)))
            .andExpect(jsonPath("$.[*].avamCode").value(hasItem(MAPPING_AVAM_CODE)));
    }

    @Test
    @Transactional
    public void getOccupationMapping() throws Exception {
        // Initialize the database
        occupationMappingRepository.saveAndFlush(occupationMapping);

        // Get the occupationMapping
        restOccupationMockMvc.perform(get("/api/occupations/mapping/{id}", occupationMapping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupationMapping.getId().toString()))
            .andExpect(jsonPath("$.code").value(MAPPING_CODE))
            .andExpect(jsonPath("$.x28Code").value(MAPPING_X28_CODE))
            .andExpect(jsonPath("$.avamCode").value(MAPPING_AVAM_CODE));
    }

    @Test
    @Transactional
    public void getNonExistingOccupationMapping() throws Exception {
        // Get the occupationMapping
        restOccupationMockMvc.perform(get("/api/occupations/mapping/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void occupationMappingEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OccupationMapping.class);
        OccupationMapping occupationMapping1 = new OccupationMapping();
        occupationMapping1.setId(UUID.randomUUID());
        OccupationMapping occupationMapping2 = new OccupationMapping();
        occupationMapping2.setId(occupationMapping1.getId());
        assertThat(occupationMapping1).isEqualTo(occupationMapping2);
        occupationMapping2.setId(UUID.randomUUID());
        assertThat(occupationMapping1).isNotEqualTo(occupationMapping2);
        occupationMapping1.setId(null);
        assertThat(occupationMapping1).isNotEqualTo(occupationMapping2);
    }

    @Test
    @Transactional
    public void getOccupationByCode() throws Exception {
        occupationRepository.saveAndFlush(occupation);

        restOccupationMockMvc.perform(get("/api/occupations?code={code}", occupation.getCode()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupation.getId().toString()))
            .andExpect(jsonPath("$.code").value(OCCUPATION_CODE))
            .andExpect(jsonPath("$.classificationCode").value(OCCUPATION_CLASSIFICATION_CODE))
            .andExpect(jsonPath("$.labelDe").value(OCCUPATION_LABEL_DE))
            .andExpect(jsonPath("$.labelFr").value(OCCUPATION_LABEL_FR))
            .andExpect(jsonPath("$.labelIt").value(OCCUPATION_LABEL_IT))
            .andExpect(jsonPath("$.labelEn").value(OCCUPATION_LABEL_EN));
    }

    @Test
    @Transactional
    public void getNonExistingOccupationByCode() throws Exception {
        restOccupationMockMvc.perform(get("/api/occupations?code={code}", OCCUPATION_CODE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getOccupationByX28Code() throws Exception {
        occupationMappingRepository.save(occupationMapping);
        occupationRepository.saveAndFlush(occupation);

        restOccupationMockMvc.perform(get("/api/occupations?x28Code={x28Code}", MAPPING_X28_CODE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupation.getId().toString()))
            .andExpect(jsonPath("$.code").value(OCCUPATION_CODE))
            .andExpect(jsonPath("$.classificationCode").value(OCCUPATION_CLASSIFICATION_CODE))
            .andExpect(jsonPath("$.labelDe").value(OCCUPATION_LABEL_DE))
            .andExpect(jsonPath("$.labelFr").value(OCCUPATION_LABEL_FR))
            .andExpect(jsonPath("$.labelIt").value(OCCUPATION_LABEL_IT))
            .andExpect(jsonPath("$.labelEn").value(OCCUPATION_LABEL_EN));
    }

    @Test
    @Transactional
    public void getNonExistingOccupationByX28Code() throws Exception {
        occupationMappingRepository.saveAndFlush(occupationMapping);

        restOccupationMockMvc.perform(get("/api/occupations?x28Code={x28Code}", MAPPING_X28_CODE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getOccupationByAvamCode() throws Exception {
        occupationMappingRepository.save(occupationMapping);
        occupationRepository.saveAndFlush(occupation);

        restOccupationMockMvc.perform(get("/api/occupations?avamCode={avamCode}", MAPPING_AVAM_CODE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(occupation.getId().toString()))
            .andExpect(jsonPath("$.code").value(OCCUPATION_CODE))
            .andExpect(jsonPath("$.classificationCode").value(OCCUPATION_CLASSIFICATION_CODE))
            .andExpect(jsonPath("$.labelDe").value(OCCUPATION_LABEL_DE))
            .andExpect(jsonPath("$.labelFr").value(OCCUPATION_LABEL_FR))
            .andExpect(jsonPath("$.labelIt").value(OCCUPATION_LABEL_IT))
            .andExpect(jsonPath("$.labelEn").value(OCCUPATION_LABEL_EN));
    }

    @Test
    @Transactional
    public void getNonExistingOccupationByAvamCode() throws Exception {
        occupationMappingRepository.saveAndFlush(occupationMapping);

        restOccupationMockMvc.perform(get("/api/occupations?avamCode={avamCode}", MAPPING_AVAM_CODE))
            .andExpect(status().isNotFound());
    }
}
