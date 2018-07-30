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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.ReferenceserviceApp;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.search.CantonSuggestion;
import ch.admin.seco.service.reference.domain.search.LocalitySuggestion;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.repository.CantonRepository;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.search.CantonSearchRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySearchRepository;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the LocalityResource REST controller.
 *
 * @see LocalityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class LocalityResourceIntTest {

    private static final String DEFAULT_CITY = "Bern";
    private static final String UPDATED_CITY = "Embrach";

    private static final String DEFAULT_ZIP_CODE = "3000";
    private static final String UPDATED_ZIP_CODE = "8424";

    private static final Integer DEFAULT_COMMUNAL_CODE = 219;
    private static final Integer UPDATED_COMMUNAL_CODE = 56;

    private static final String DEFAULT_CANTON_CODE = "BE";
    private static final String UPDATED_CANTON_CODE = "ZH";

    private static final String DEFAULT_REGION_CODE = "BE05";
    private static final String UPDATED_REGION_CODE = "ZH08";

    private static final Double DEFAULT_LAT = 47.506D;
    private static final Double UPDATED_LAT = 47.501D;

    private static final Double DEFAULT_LON = 8.803D;
    private static final Double UPDATED_LON = 8.595D;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private CantonRepository cantonRepository;

    @Autowired
    private CantonSearchRepository cantonSearchRepository;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private LocalitySearchRepository localitySynonymSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restLocalityMockMvc;

    private Locality locality;

    private static Locality createLocalityEntity() {
        return new Locality()
                .city(DEFAULT_CITY)
                .zipCode(DEFAULT_ZIP_CODE)
                .communalCode(DEFAULT_COMMUNAL_CODE)
                .cantonCode(DEFAULT_CANTON_CODE)
                .regionCode(DEFAULT_REGION_CODE)
                .geoPoint(new GeoPoint(DEFAULT_LAT, DEFAULT_LON));
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LocalityResource localityResource = new LocalityResource(localityService);
        this.restLocalityMockMvc = MockMvcBuilders.standaloneSetup(localityResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        localitySynonymSearchRepository.deleteAll();
        locality = createLocalityEntity();
    }

    @Test
    @Transactional
    public void createLocality() throws Exception {
        int databaseSizeBeforeCreate = localityRepository.findAll().size();

        // Create the Locality
        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isCreated());

        // Validate the Locality in the database
        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeCreate + 1);
        Locality testLocality = localityList.get(localityList.size() - 1);
        assertThat(testLocality.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testLocality.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(testLocality.getCommunalCode()).isEqualTo(DEFAULT_COMMUNAL_CODE);
        assertThat(testLocality.getCantonCode()).isEqualTo(DEFAULT_CANTON_CODE);
        assertThat(testLocality.getRegionCode()).isEqualTo(DEFAULT_REGION_CODE);
        assertThat(testLocality.getGeoPoint().getLatitude()).isEqualTo(DEFAULT_LAT);
        assertThat(testLocality.getGeoPoint().getLongitude()).isEqualTo(DEFAULT_LON);
        // Validate the Locality in Elasticsearch
        Optional<LocalitySuggestion> localitySynonym = localitySynonymSearchRepository.findById(testLocality.getId());
        assertThat(localitySynonym).isPresent();
    }

    @Test
    @Transactional
    public void createLocalityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = localityRepository.findAll().size();

        // Create the Locality with an existing ID
        locality.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = localityRepository.findAll().size();
        // set the field null
        locality.setCity(null);

        // Create the Locality, which fails.

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isBadRequest());

        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkZipCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = localityRepository.findAll().size();
        // set the field null
        locality.setZipCode(null);

        // Create the Locality, which fails.

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isBadRequest());

        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommunalCodeShouldBeGreaterThanZero() throws Exception {
        int databaseSizeBeforeTest = localityRepository.findAll().size();
        // set the field to 0
        locality.setCommunalCode(0);

        // Create the Locality, which fails.

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isBadRequest());

        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLatitudeIsRequired() throws Exception {
        checkLocalityGeoPoint(new GeoPoint(null, DEFAULT_LON));
    }

    @Test
    @Transactional
    public void checkLongitudeIsRequired() throws Exception {
        checkLocalityGeoPoint(new GeoPoint(DEFAULT_LAT, null));
    }

    @Test
    @Transactional
    public void updateLocality() throws Exception {
        // Initialize the database
        localityService.save(locality);

        int databaseSizeBeforeUpdate = localityRepository.findAll().size();

        // Update the locality
        Locality updatedLocality = localityRepository.getOne(locality.getId());
        updatedLocality
                .city(UPDATED_CITY)
                .zipCode(UPDATED_ZIP_CODE)
                .communalCode(UPDATED_COMMUNAL_CODE)
                .cantonCode(UPDATED_CANTON_CODE)
                .regionCode(UPDATED_REGION_CODE)
                .geoPoint(new GeoPoint(UPDATED_LAT, UPDATED_LON));

        restLocalityMockMvc.perform(put("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedLocality)))
                .andExpect(status().isOk());

        // Validate the Locality in the database
        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeUpdate);
        Locality testLocality = localityList.get(localityList.size() - 1);
        assertThat(testLocality.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testLocality.getZipCode()).isEqualTo(UPDATED_ZIP_CODE);
        assertThat(testLocality.getCommunalCode()).isEqualTo(UPDATED_COMMUNAL_CODE);
        assertThat(testLocality.getCantonCode()).isEqualTo(UPDATED_CANTON_CODE);
        assertThat(testLocality.getRegionCode()).isEqualTo(UPDATED_REGION_CODE);
        assertThat(testLocality.getGeoPoint().getLatitude()).isEqualTo(UPDATED_LAT);
        assertThat(testLocality.getGeoPoint().getLongitude()).isEqualTo(UPDATED_LON);

        // Validate the Locality in Elasticsearch
        Optional<LocalitySuggestion> localitySynonym = localitySynonymSearchRepository.findById(testLocality.getId());
        assertThat(localitySynonym).isPresent();
    }

    @Test
    @Transactional
    public void searchLocality() throws Exception {
        // Initialize the database
        localityService.save(locality);
        cantonSearchRepository.save(new CantonSuggestion()
                .id(UUID.randomUUID())
                .code("BE")
                .name("Canton Bern")
                .cantonSuggestions(Collections.emptySet())
        );

        // Search the locality
        restLocalityMockMvc.perform(get("/api/_search/localities?prefix={prefix}&resultSize={resultSize}", "be", 5))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.localities[*].city").value(hasItem(DEFAULT_CITY)))
                .andExpect(jsonPath("$.localities[*].communalCode").value(hasItem(DEFAULT_COMMUNAL_CODE)))
                .andExpect(jsonPath("$.localities[*].cantonCode").value(hasItem(DEFAULT_CANTON_CODE)))
                .andExpect(jsonPath("$.localities[*].regionCode").value(hasItem(DEFAULT_REGION_CODE)))
                .andExpect(jsonPath("$.localities[*].zipCode").value(hasItem(DEFAULT_ZIP_CODE)))
                .andExpect(jsonPath("$.cantons[*].code").value(hasItem(DEFAULT_CANTON_CODE)));
    }

    @Test
    @Transactional
    public void searchLocalityByZipCode() throws Exception {
        saveLocalities(
                createLocalityEntity().zipCode("3001"),
                createLocalityEntity().city("Ebersecken").zipCode("3002"),
                createLocalityEntity().city("Dachsen").zipCode("4001")
        );

        restLocalityMockMvc.perform(
                get("/api/_search/localities")
                        .param("prefix", "30")
                        .param("resultSize", "10")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.localities[0].zipCode").value("3001"))
                .andExpect(jsonPath("$.localities[1].zipCode").value("3002"));
    }

    @Test
    @Transactional
    public void searchLocalityByZipCodeWithDistinctResult() throws Exception {
        saveLocalities(
                createLocalityEntity().zipCode("3001"),
                createLocalityEntity().zipCode("3002"),
                createLocalityEntity().zipCode("3003"),
                createLocalityEntity().zipCode("3004"),
                createLocalityEntity().city("Zurich").zipCode("3005"),
                createLocalityEntity().city("Lucern").zipCode("3006")
        );

        restLocalityMockMvc.perform(
                get("/api/_search/localities")
                        .param("prefix", "30")
                        .param("resultSize", "10")
                    .param("distinctByLocalityCity", "true")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localities.length()").value("3"))
                .andExpect(jsonPath("$.localities[0].city").value(DEFAULT_CITY))
                .andExpect(jsonPath("$.localities[0].zipCode").isEmpty())
            .andExpect(jsonPath("$.localities[1].city").value("Zurich"))
                .andExpect(jsonPath("$.localities[1].zipCode").isEmpty())
            .andExpect(jsonPath("$.localities[2].city").value("Lucern"))
                .andExpect(jsonPath("$.localities[2].zipCode").isEmpty());
    }

    @Test
    @Transactional
    public void getNonExistingLocality() throws Exception {
        // Get the locality
        restLocalityMockMvc.perform(get("/api/localities/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void searchNearestLocality() throws Exception {
        localityService.save(locality);
        Locality locality2 = new Locality().city(UPDATED_CITY)
                .zipCode(UPDATED_ZIP_CODE)
                .communalCode(UPDATED_COMMUNAL_CODE)
                .cantonCode(UPDATED_CANTON_CODE)
                .regionCode(UPDATED_REGION_CODE)
                .geoPoint(new GeoPoint(UPDATED_LAT, UPDATED_LON));
        localityService.save(locality2);

        MockHttpServletRequestBuilder requestBuilder = get("/api/_search/localities/nearest")
                .param("latitude", UPDATED_LAT.toString())
                .param("longitude", Double.toString(UPDATED_LON - 0.52D));
        restLocalityMockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(locality2.getId().toString()))
                .andExpect(jsonPath("$.city").value(UPDATED_CITY))
                .andExpect(jsonPath("$.zipCode").value(UPDATED_ZIP_CODE))
                .andExpect(jsonPath("$.communalCode").value(UPDATED_COMMUNAL_CODE))
                .andExpect(jsonPath("$.cantonCode").value(UPDATED_CANTON_CODE))
                .andExpect(jsonPath("$.regionCode").value(UPDATED_REGION_CODE))
                .andExpect(jsonPath("$.geoPoint.lat").value(UPDATED_LAT))
                .andExpect(jsonPath("$.geoPoint.lon").value(UPDATED_LON));
    }

    @Test
    @Transactional
    public void updateNonExistingLocality() throws Exception {
        int databaseSizeBeforeUpdate = localityRepository.findAll().size();

        // Create the Locality

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLocalityMockMvc.perform(put("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isCreated());

        // Validate the Locality in the database
        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLocality() throws Exception {
        // Initialize the database
        localityService.save(locality);

        int databaseSizeBeforeDelete = localityRepository.findAll().size();

        // Get the locality
        restLocalityMockMvc.perform(delete("/api/localities/{id}", locality.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean localityExistsInEs = localitySynonymSearchRepository.findById(locality.getId()).isPresent();
        assertThat(localityExistsInEs).isFalse();

        // Validate the database is empty
        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Locality.class);
        Locality locality1 = new Locality();
        locality1.setId(UUID.randomUUID());
        Locality locality2 = new Locality();
        locality2.setId(locality1.getId());
        assertThat(locality1).isEqualTo(locality2);
        locality2.setId(UUID.randomUUID());
        assertThat(locality1).isNotEqualTo(locality2);
        locality1.setId(null);
        assertThat(locality1).isNotEqualTo(locality2);
    }

    @Test
    @Transactional
    public void searchNonExistingNearestLocality() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/_search/localities/nearest")
                .param("latitude", DEFAULT_LAT.toString())
                .param("longitude", DEFAULT_LON.toString());
        restLocalityMockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void countCantons() {
        assertThat(cantonRepository.count()).isEqualTo(26);
    }

    private void saveLocalities(Locality... localities) {
        Arrays.asList(localities).forEach(localityService::save);
    }

    private void checkLocalityGeoPoint(GeoPoint geoPoint) throws Exception {
        int databaseSizeBeforeTest = localityRepository.findAll().size();
        // set the field null
        locality.setGeoPoint(geoPoint);

        // Create the Locality, which fails.

        restLocalityMockMvc.perform(post("/api/localities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(locality)))
                .andExpect(status().isBadRequest());

        List<Locality> localityList = localityRepository.findAll();
        assertThat(localityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void searchByZiCode() throws Exception {
        saveLocalities(
            createLocalityEntity().zipCode("3001"),
            createLocalityEntity().zipCode("3002"),
            createLocalityEntity().zipCode("3002")
        );

        final MockHttpServletRequestBuilder requestBuilder = get("/api/localities")
            .param("zipCode", "3002");
        restLocalityMockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @Transactional
    public void searchByZipCodeAndCitySynonym() throws Exception {
        saveLocalities(
            createLocalityEntity().zipCode("7000").city("Chur"),
            createLocalityEntity().zipCode("7001").city("Chur")
        );

        final MockHttpServletRequestBuilder requestBuilder = get("/api/localities")
            .param("zipCode", "7000")
            .param("city", "Coira");
        restLocalityMockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zipCode").value("7000"))
            .andExpect(jsonPath("$.city").value("Chur"));
    }

    @Test
    public void searchNotExistingLocalityByZipCodeAndCity() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = get("/api/localities")
            .param("zipCode", "7000")
            .param("city", "Chur");
        restLocalityMockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound());
    }
}
