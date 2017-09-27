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
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.domain.valueobject.Labels;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.search.ClassificationSearchRepository;
import ch.admin.seco.service.reference.service.ClassificationService;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the ClassificationResource REST controller.
 *
 * @see ClassificationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class ClassificationResourceIntTest {

    private static final Integer DEFAULT_CODE = 100;
    private static final Integer UPDATED_CODE = 101;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Language DEFAULT_LANGUAGE = Language.de;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private ClassificationSearchRepository classificationSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClassificationMockMvc;

    private ch.admin.seco.service.reference.domain.Classification classification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ch.admin.seco.service.reference.domain.Classification createEntity(EntityManager em) {
        ch.admin.seco.service.reference.domain.Classification classification = new ch.admin.seco.service.reference.domain.Classification()
            .code(DEFAULT_CODE)
            .labels(new Labels().de(DEFAULT_NAME));
        return classification;
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClassificationResource classificationResource = new ClassificationResource(classificationService);
        this.restClassificationMockMvc = MockMvcBuilders.standaloneSetup(classificationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        classificationSearchRepository.deleteAll();
        classification = createEntity(em);
    }

    @Test
    @Transactional
    public void createClassification() throws Exception {
        int databaseSizeBeforeCreate = classificationRepository.findAll().size();

        // Create the Classification
        restClassificationMockMvc.perform(post("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isCreated());

        // Validate the Classification in the database
        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeCreate + 1);
        ch.admin.seco.service.reference.domain.Classification testClassification = classificationList.get(classificationList.size() - 1);
        assertThat(testClassification.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testClassification.getLabels().getDe()).isEqualTo(DEFAULT_NAME);

        // Validate the Classification in Elasticsearch
        List<ClassificationSuggestion> classificationSynonymList = classificationSearchRepository
            .findAllByCodeEquals(testClassification.getCode());
        assertThat(classificationSynonymList).hasSize(1);
    }

    @Test
    @Transactional
    public void createClassificationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = classificationRepository.findAll().size();

        // Create the Classification with an existing ID
        classification.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassificationMockMvc.perform(post("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isBadRequest());

        // Validate the Classification in the database
        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = classificationRepository.findAll().size();
        // set the field null
        classification.setCode(-1);

        // Create the Classification, which fails.

        restClassificationMockMvc.perform(post("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isBadRequest());

        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = classificationRepository.findAll().size();
        // set the field null
        classification.setLabels(null);

        // Create the Classification, which fails.

        restClassificationMockMvc.perform(post("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isBadRequest());

        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClassifications() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        // Get all the classificationList
        restClassificationMockMvc.perform(get("/api/classifications?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classification.getId().toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].labels.de").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getClassification() throws Exception {
        // Initialize the database
        classificationRepository.saveAndFlush(classification);

        // Get the classification
        restClassificationMockMvc.perform(get("/api/classifications/{id}", classification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(classification.getId().toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.labels.de").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClassification() throws Exception {
        // Get the classification
        restClassificationMockMvc.perform(get("/api/classifications/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClassification() throws Exception {
        // Initialize the database
        classificationService.save(classification);

        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();

        // Update the classification
        ch.admin.seco.service.reference.domain.Classification updatedClassification = classificationRepository.getOne(classification.getId());
        updatedClassification
            .code(UPDATED_CODE)
            .labels(new Labels().de(UPDATED_NAME));

        restClassificationMockMvc.perform(put("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedClassification)))
            .andExpect(status().isOk());

        // Validate the Classification in the database
        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate);
        ch.admin.seco.service.reference.domain.Classification testClassification = classificationList.get(classificationList.size() - 1);
        assertThat(testClassification.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testClassification.getLabels().getDe()).isEqualTo(UPDATED_NAME);

        // Validate the Classification in Elasticsearch
        List<ClassificationSuggestion> classificationSynonymList = classificationSearchRepository
            .findAllByCodeEquals(testClassification.getCode());
        assertThat(classificationSynonymList).hasSize(1);
    }

    @Test
    @Transactional
    public void updateNonExistingClassification() throws Exception {
        int databaseSizeBeforeUpdate = classificationRepository.findAll().size();

        // Create the Classification

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClassificationMockMvc.perform(put("/api/classifications")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(classification)))
            .andExpect(status().isCreated());

        // Validate the Classification in the database
        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteClassification() throws Exception {
        // Initialize the database
        classificationService.save(classification);

        int databaseSizeBeforeDelete = classificationRepository.findAll().size();

        // Get the classification
        restClassificationMockMvc.perform(delete("/api/classifications/{id}", classification.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean classificationExistsInEs = classificationSearchRepository.findById(classification.getId()).isPresent();
        assertThat(classificationExistsInEs).isFalse();

        // Validate the database is empty
        List<ch.admin.seco.service.reference.domain.Classification> classificationList = classificationRepository.findAll();
        assertThat(classificationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchClassification() throws Exception {
        // Initialize the database
        classificationService.save(classification);

        // Search the classification
        restClassificationMockMvc.perform(get("/api/_search/classifications?query=id:" + classification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classification.getId().toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].labels.de").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ch.admin.seco.service.reference.domain.Classification.class);
        ch.admin.seco.service.reference.domain.Classification classification1 = new ch.admin.seco.service.reference.domain.Classification();
        classification1.setId(UUID.randomUUID());
        ch.admin.seco.service.reference.domain.Classification classification2 = new ch.admin.seco.service.reference.domain.Classification();
        classification2.setId(classification1.getId());
        assertThat(classification1).isEqualTo(classification2);
        classification2.setId(UUID.randomUUID());
        assertThat(classification1).isNotEqualTo(classification2);
        classification1.setId(null);
        assertThat(classification1).isNotEqualTo(classification2);
    }
}
