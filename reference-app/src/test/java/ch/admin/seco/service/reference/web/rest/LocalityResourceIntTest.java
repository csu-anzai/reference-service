package ch.admin.seco.service.reference.web.rest;

import ch.admin.seco.service.reference.domain.CantonRepository;
import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.LocalityRepository;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.impl.ElasticsearchLocalityIndexer;
import ch.admin.seco.service.reference.service.search.CantonSearchRepository;
import ch.admin.seco.service.reference.service.search.CantonSuggestion;
import ch.admin.seco.service.reference.service.search.LocalitySearchRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ch.admin.seco.service.reference.web.rest.TestUtil.doAsAdmin;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LocalityResource REST controller.
 *
 * @see LocalityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LocalityResourceIntTest {

    private static final String URL = "/api/localities";

    private static final String URL_SEARCH = "/api/_search/localities";

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
    private LocalitySearchRepository localitySearchRepository;

    @Autowired
    private ElasticsearchLocalityIndexer elasticsearchLocalityIndexer;

    @Autowired
    private MockMvc mockMvc;

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
    public void setUp() {
        this.localityRepository.deleteAll();
        this.cantonRepository.deleteAll();
        this.localitySearchRepository.deleteAll();
        this.cantonSearchRepository.deleteAll();

        this.elasticsearchLocalityIndexer.reindexLocalities();

        locality = createLocalityEntity();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void createLocality() throws Exception {
        // given
        ResultActions post = post(locality, URL);
        post.andExpect(status().isCreated());

        // when
        List<Locality> localityList = this.localityRepository.findAll();

        // then
        assertThat(localityList).hasSize(1);
        Locality testLocality = localityList.get(0);
        assertThat(testLocality.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testLocality.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(testLocality.getCommunalCode()).isEqualTo(DEFAULT_COMMUNAL_CODE);
        assertThat(testLocality.getCantonCode()).isEqualTo(DEFAULT_CANTON_CODE);
        assertThat(testLocality.getRegionCode()).isEqualTo(DEFAULT_REGION_CODE);
        assertThat(testLocality.getGeoPoint().getLatitude()).isEqualTo(DEFAULT_LAT);
        assertThat(testLocality.getGeoPoint().getLongitude()).isEqualTo(DEFAULT_LON);

        await().until(() -> this.localitySearchRepository.findById(testLocality.getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void checkCityIsRequired() throws Exception {
        // given
        locality.setCity(null);

        // when
        ResultActions post = post(locality, URL);
        List<Locality> localityList = this.localityRepository.findAll();

        // then
        post.andExpect(status().isBadRequest());
        assertThat(localityList).hasSize(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void checkZipCodeIsRequired() throws Exception {
        // given
        locality.setZipCode(null);

        // when
        ResultActions post = post(locality, URL);
        List<Locality> localityList = this.localityRepository.findAll();

        // then
        post.andExpect(status().isBadRequest());
        assertThat(localityList).hasSize(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void checkCommunalCodeShouldBeGreaterThanZero() throws Exception {
        // given
        locality.setCommunalCode(0);

        // when
        ResultActions post = post(locality, URL);
        List<Locality> localityList = this.localityRepository.findAll();

        // then
        post.andExpect(status().isBadRequest());
        assertThat(localityList).hasSize(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void checkLatitudeIsRequired() throws Exception {
        // given
        locality.setGeoPoint(new GeoPoint(null, DEFAULT_LON));

        // when
        ResultActions post = post(locality, URL);
        List<Locality> localityList = localityRepository.findAll();

        // then
        post.andExpect(status().isBadRequest());
        assertThat(localityList).hasSize(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void checkLongitudeIsRequired() throws Exception {
        // given
        locality.setGeoPoint(new GeoPoint(DEFAULT_LAT, null));

        // when
        ResultActions post = post(locality, URL);
        List<Locality> localityList = localityRepository.findAll();

        // then
        post.andExpect(status().isBadRequest());
        assertThat(localityList).hasSize(0);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateLocality() throws Exception {
        // given
        this.localityRepository.save(locality);

        Locality updatedLocality = this.localityRepository.getOne(locality.getId());
        updatedLocality
            .city(UPDATED_CITY)
            .zipCode(UPDATED_ZIP_CODE)
            .communalCode(UPDATED_COMMUNAL_CODE)
            .cantonCode(UPDATED_CANTON_CODE)
            .regionCode(UPDATED_REGION_CODE)
            .geoPoint(new GeoPoint(UPDATED_LAT, UPDATED_LON));

        // when
        ResultActions put = put(updatedLocality, URL);
        List<Locality> localityList = this.localityRepository.findAll();

        // then
        put.andExpect(status().isOk());

        assertThat(localityList).hasSize(1);
        Locality testLocality = localityList.get(0);
        assertThat(testLocality.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testLocality.getZipCode()).isEqualTo(UPDATED_ZIP_CODE);
        assertThat(testLocality.getCommunalCode()).isEqualTo(UPDATED_COMMUNAL_CODE);
        assertThat(testLocality.getCantonCode()).isEqualTo(UPDATED_CANTON_CODE);
        assertThat(testLocality.getRegionCode()).isEqualTo(UPDATED_REGION_CODE);
        assertThat(testLocality.getGeoPoint().getLatitude()).isEqualTo(UPDATED_LAT);
        assertThat(testLocality.getGeoPoint().getLongitude()).isEqualTo(UPDATED_LON);

        await().until(() -> this.localitySearchRepository.findById(testLocality.getId()).isPresent());
    }

    @Test
    public void searchLocality() throws Exception {
        // given
        doAsAdmin(() -> this.localityService.save(locality));
        this.cantonSearchRepository.save(new CantonSuggestion()
            .id(UUID.randomUUID())
            .code("BE")
            .name("Canton Bern")
            .cantonSuggestions(Collections.emptySet())
        );

        // when
        ResultActions resultActions = this.mockMvc.perform(
            get(URL_SEARCH + "?prefix={prefix}&resultSize={resultSize}", "be", 5)
        );

        // then
        resultActions
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
    public void searchLocalityByZipCode() throws Exception {
        // given
        saveLocalitiesAsAdmin(
            createLocalityEntity().zipCode("3001"),
            createLocalityEntity().city("Ebersecken").zipCode("3002"),
            createLocalityEntity().city("Dachsen").zipCode("4001")
        );

        // when
        ResultActions resultActions = this.mockMvc.perform(
            get(URL_SEARCH)
                .param("prefix", "30")
                .param("resultSize", "10")
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.localities[0].zipCode").value("3001"))
            .andExpect(jsonPath("$.localities[1].zipCode").value("3002"));
    }

    @Test
    public void searchLocalityByZipCodeWithDistinctResult() throws Exception {
        // given
        saveLocalitiesAsAdmin(
            createLocalityEntity().zipCode("3001").geoPoint(null),
            createLocalityEntity().zipCode("3002").geoPoint(null),
            createLocalityEntity().zipCode("3003").geoPoint(new GeoPoint(DEFAULT_LAT, DEFAULT_LON)),
            createLocalityEntity().zipCode("3004").geoPoint(null),
            createLocalityEntity().city("Zurich").zipCode("3005"),
            createLocalityEntity().city("Lucern").zipCode("3006")
        );

        // when
        ResultActions resultActions = this.mockMvc.perform(
            get(URL_SEARCH)
                .param("prefix", "30")
                .param("resultSize", "10")
                .param("distinctByLocalityCity", "true")
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.localities.length()").value("3"))
            .andExpect(jsonPath("$.localities[0].city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.localities[0].zipCode").value("3003"))
            .andExpect(jsonPath("$.localities[0].geoPoint.lat").value(DEFAULT_LAT))
            .andExpect(jsonPath("$.localities[0].geoPoint.lon").value(DEFAULT_LON))
            .andExpect(jsonPath("$.localities[1].city").value("Zurich"))
            .andExpect(jsonPath("$.localities[1].zipCode").value("3005"))
            .andExpect(jsonPath("$.localities[2].city").value("Lucern"))
            .andExpect(jsonPath("$.localities[2].zipCode").value("3006"));
    }

    @Test
    public void getExistingLocalityById() throws Exception {
        // given
        Locality savedLocality = this.localityRepository.save(locality);

        // when
        ResultActions resultActions = this.mockMvc.perform(
            get(URL + "/{id}", savedLocality.getId())
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(savedLocality.getId().toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.zipCode").value(DEFAULT_ZIP_CODE))
            .andExpect(jsonPath("$.communalCode").value(DEFAULT_COMMUNAL_CODE))
            .andExpect(jsonPath("$.cantonCode").value(DEFAULT_CANTON_CODE))
            .andExpect(jsonPath("$.regionCode").value(DEFAULT_REGION_CODE))
            .andExpect(jsonPath("$.geoPoint.lat").value(DEFAULT_LAT))
            .andExpect(jsonPath("$.geoPoint.lon").value(DEFAULT_LON));
    }

    @Test
    public void getNonExistingLocality() throws Exception {
        // when
        ResultActions resultActions = this.mockMvc.perform(
            get(URL + "/{id}", UUID.randomUUID())
        );

        // then
        resultActions
            .andExpect(status().isNotFound());
    }

    @Test
    public void searchNearestLocality() throws Exception {
        // given
        doAsAdmin(() -> this.localityService.save(locality));
        Locality locality2 = new Locality().city(UPDATED_CITY)
                .zipCode(UPDATED_ZIP_CODE)
                .communalCode(UPDATED_COMMUNAL_CODE)
                .cantonCode(UPDATED_CANTON_CODE)
                .regionCode(UPDATED_REGION_CODE)
                .geoPoint(new GeoPoint(UPDATED_LAT, UPDATED_LON));
        doAsAdmin(() -> this.localityService.save(locality2));

        // when
        ResultActions resultActions = this.mockMvc.perform(get(URL_SEARCH + "/nearest")
            .param("latitude", UPDATED_LAT.toString())
            .param("longitude", Double.toString(UPDATED_LON - 0.52D))
        );

        // then
        resultActions
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
    @WithMockUser(roles = "ADMIN")
    public void deleteLocality() throws Exception {
        // given
        doAsAdmin(() -> this.localityService.save(locality));

        // when
        ResultActions resultActions = this.mockMvc.perform(
            delete(URL + "/{id}", locality.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
        );

        // then
        resultActions
            .andExpect(status().isOk());
        assertThat(this.localityRepository.findById(locality.getId())).isNotPresent();
        await().until(() -> !this.localitySearchRepository.findById(locality.getId()).isPresent());
    }

    @Test
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
    public void searchNonExistingNearestLocality() throws Exception {
        // when
        ResultActions resultActions = this.mockMvc.perform(get(URL_SEARCH + "/nearest")
            .param("latitude", DEFAULT_LAT.toString())
            .param("longitude", DEFAULT_LON.toString())
        );

        // then
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    public void countCantons() {
        await().until(() -> this.cantonSearchRepository.count() >= 26);
    }

    @Test
    public void searchByZipCode() throws Exception {
        saveLocalitiesAsAdmin(
            createLocalityEntity().zipCode("3001"),
            createLocalityEntity().zipCode("3002"),
            createLocalityEntity().zipCode("3002")
        );

        final MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("zipCode", "3002");
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void searchByZipCodeAndCitySynonym() throws Exception {
        saveLocalitiesAsAdmin(
            createLocalityEntity().zipCode("7000").city("Chur"),
            createLocalityEntity().zipCode("7001").city("Chur")
        );

        final MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("zipCode", "7000")
            .param("city", "Coira");
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zipCode").value("7000"))
            .andExpect(jsonPath("$.city").value("Chur"));
    }

    @Test
    public void searchNotExistingLocalityByZipCodeAndCity() throws Exception {
        final MockHttpServletRequestBuilder requestBuilder = get(URL)
            .param("zipCode", "7000")
            .param("city", "Chur");
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNotFound());
    }

    private void saveLocalitiesAsAdmin(Locality... localities) {
        doAsAdmin(() -> Arrays.asList(localities).forEach(this.localityService::save));
    }

    private ResultActions post(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
            MockMvcRequestBuilders.post(urlTemplate)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }

    private ResultActions put(Object request, String urlTemplate) throws Exception {
        return this.mockMvc.perform(
            MockMvcRequestBuilders.put(urlTemplate)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(request))
        );
    }
}
