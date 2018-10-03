package ch.admin.seco.service.reference.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.ReferenceserviceApp;
import ch.admin.seco.service.reference.domain.JobCenter;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.valueobject.Address;
import ch.admin.seco.service.reference.repository.JobCenterRepository;
import ch.admin.seco.service.reference.service.JobCenterService;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class JobCenterResourceIntTest {

    private static final String CODE_ONE = "SGGG1";
    private static final String EMAIL_ONE = "info.ravobu@sg.ch";
    private static final String PHONE_ONE = "+41 58 229 93 93";
    private static final String FAX_ONE = "41 58 229 93 83";
    private static final String POSTAL_CODE_ONE = "1000";

    private static final String CODE_TWO = "SGGG2";
    private static final String EMAIL_TWO = "rav@ktsh.ch";
    private static final String PHONE_TWO = "+41 52 632 70 24";
    private static final String FAX_TWO = "";
    private static final String POSTAL_CODE_TWO = "9999";

    private static final String CODE_THREE = "SGGG3";
    private static final String EMAIL_THREE = "rav@ktsh.ch";
    private static final String PHONE_THREE = "+41 52 632 70 24";
    private static final String FAX_THREE = "";
    private static final String POSTAL_CODE_THREE = "9998";

    private static final String NAME_EN = "RAV St.Gallen EN";
    private static final String NAME_DE = "RAV St.Gallen DE";
    private static final String CITY_EN = "London EN";
    private static final String CITY_DE = "London DE";
    private static final String STREET_EN = "Berneckerstrasse EN";
    private static final String STREET_DE = "Berneckerstrasse DE";
    private static final String HOUSE_NUMBER = "12";
    private static final String ZIP_CODE = "9000";

    private static final String CODE_TO_UPDATE = "JC11";
    private static final String CODE_TO_CREATE = "JC01";
    private static final String POSTAL_CODE_FOUR = "PO04";
    private static final String POSTAL_CODE_FIVE = "PO05";
    private static final String POSTAL_CODE_SIX = "PO6";
    private static final String POSTAL_CODE_SEVEN = "PO7";
    private static final String POSTAL_CODE_EIGHT = "PO8";

    @Autowired
    private JobCenterRepository jobCenterRepository;

    @Autowired
    private JobCenterService jobCenterService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc restJobCenterMockMvc;

    private JobCenter jobCenterOne;
    private JobCenter jobCenterTwo;
    private JobCenter jobCenterThree;

    private static Address buildAddress(String name, String city, String street, Language language) {
        return new Address()
            .name(name)
            .city(city)
            .street(street)
            .houseNumber(HOUSE_NUMBER)
            .zipCode(ZIP_CODE)
            .language(language);
    }

    private static JobCenter buildJobCenter(String code, String email, String phone, String fax, String postalCode, Address... addresses) {
        return new JobCenter()
            .code(code)
            .email(email)
            .phone(phone)
            .fax(fax)
            .postalCodes(Sets.newHashSet(postalCode))
            .addresses(Stream.of(addresses).collect(Collectors.toSet()));
    }

    private static MockHttpServletRequestBuilder buildJobCenterSearchByCodeRequest(String code, String language) {
        return get("/api/job-centers/" + code)
            .param("language", language);
    }

    private static MockHttpServletRequestBuilder buildJobCenterSearchByLocationRequest(String countryCode,
        String postalCode, String language) {
        return get("/api/job-centers/by-location")
            .param("countryCode", countryCode)
            .param("postalCode", postalCode)
            .param("language", language);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobCenterResource localityResource = new JobCenterResource(jobCenterService);
        this.restJobCenterMockMvc = MockMvcBuilders.standaloneSetup(localityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        Address addressEN = buildAddress(NAME_EN, CITY_EN, STREET_EN, Language.en);
        Address addressDE = buildAddress(NAME_DE, CITY_DE, STREET_DE, Language.de);

        jobCenterOne = buildJobCenter(CODE_ONE, EMAIL_ONE, PHONE_ONE, FAX_ONE, POSTAL_CODE_ONE, addressEN, addressDE);
        jobCenterTwo = buildJobCenter(CODE_TWO, EMAIL_TWO, PHONE_TWO, FAX_TWO, POSTAL_CODE_TWO, addressDE);
        jobCenterThree = buildJobCenter(CODE_THREE, EMAIL_THREE, PHONE_THREE, FAX_THREE, POSTAL_CODE_THREE, addressDE);
        jobCenterRepository.saveAll(Arrays.asList(jobCenterOne, jobCenterTwo, jobCenterThree));
    }

    @Test
    @Transactional
    public void searchNonExistingJobCenterByCode() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByCodeRequest("TEST_CODE", "de"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void searchJobCenterByCodeAndLanguage() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByCodeRequest(CODE_ONE, "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCenterOne.getId().toString()))
            .andExpect(jsonPath("$.code").value(jobCenterOne.getCode()))
            .andExpect(jsonPath("$.email").value(jobCenterOne.getEmail()))
            .andExpect(jsonPath("$.phone").value(jobCenterOne.getPhone()))
            .andExpect(jsonPath("$.fax").value(jobCenterOne.getFax()))
            .andExpect(jsonPath("$.address.name").value(NAME_EN))
            .andExpect(jsonPath("$.address.city").value(CITY_EN))
            .andExpect(jsonPath("$.address.street").value(STREET_EN));
    }

    @Test
    @Transactional
    public void searchJobCenterByLocation() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByLocationRequest("CH", POSTAL_CODE_ONE, "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCenterOne.getId().toString()))
            .andExpect(jsonPath("$.code").value(jobCenterOne.getCode()))
            .andExpect(jsonPath("$.email").value(jobCenterOne.getEmail()))
            .andExpect(jsonPath("$.phone").value(jobCenterOne.getPhone()))
            .andExpect(jsonPath("$.fax").value(jobCenterOne.getFax()))
            .andExpect(jsonPath("$.address.name").value(NAME_EN))
            .andExpect(jsonPath("$.address.city").value(CITY_EN))
            .andExpect(jsonPath("$.address.street").value(STREET_EN));
    }

    @Test
    @Transactional
    public void searchJobCenterByLocationOutsideCH() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByLocationRequest("IT", POSTAL_CODE_TWO, "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCenterTwo.getId().toString()))
            .andExpect(jsonPath("$.code").value(jobCenterTwo.getCode()))
            .andExpect(jsonPath("$.email").value(jobCenterTwo.getEmail()))
            .andExpect(jsonPath("$.phone").value(jobCenterTwo.getPhone()))
            .andExpect(jsonPath("$.fax").value(jobCenterTwo.getFax()))
            .andExpect(jsonPath("$.address.name").value(NAME_DE))
            .andExpect(jsonPath("$.address.city").value(CITY_DE))
            .andExpect(jsonPath("$.address.street").value(STREET_DE));
    }

    @Test
    @Transactional
    public void searchJobCenterByLocationOutsideEU() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByLocationRequest("VI", POSTAL_CODE_THREE, "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCenterThree.getId().toString()))
            .andExpect(jsonPath("$.code").value(jobCenterThree.getCode()))
            .andExpect(jsonPath("$.email").value(jobCenterThree.getEmail()))
            .andExpect(jsonPath("$.phone").value(jobCenterThree.getPhone()))
            .andExpect(jsonPath("$.fax").value(jobCenterThree.getFax()))
            .andExpect(jsonPath("$.address.name").value(NAME_DE))
            .andExpect(jsonPath("$.address.city").value(CITY_DE))
            .andExpect(jsonPath("$.address.street").value(STREET_DE));
    }

    @Test
    @Transactional
    public void searchJobCenterByCodeWithFallbackToGermanLanguage() throws Exception {
        restJobCenterMockMvc.perform(buildJobCenterSearchByCodeRequest(CODE_TWO, "en"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobCenterTwo.getId().toString()))
            .andExpect(jsonPath("$.code").value(jobCenterTwo.getCode()))
            .andExpect(jsonPath("$.email").value(jobCenterTwo.getEmail()))
            .andExpect(jsonPath("$.phone").value(jobCenterTwo.getPhone()))
            .andExpect(jsonPath("$.fax").value(jobCenterTwo.getFax()))
            .andExpect(jsonPath("$.address.name").value(NAME_DE))
            .andExpect(jsonPath("$.address.city").value(CITY_DE))
            .andExpect(jsonPath("$.address.street").value(STREET_DE));
    }

    @Test
    @Transactional
    public void searchAllJobCenters() throws Exception {
        restJobCenterMockMvc.perform(get("/api/job-centers")
            .param("language", "en"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void createJobCenter() throws Exception {
        // GIVEN
        Address addressEN = buildAddress(NAME_EN, CITY_EN, STREET_EN, Language.en);
        JobCenter jobCenterToCreate = buildJobCenter(CODE_TO_CREATE, EMAIL_ONE, PHONE_ONE, FAX_ONE, null, addressEN, addressEN);
        addPostalCodeJobCenterMapping(POSTAL_CODE_FOUR, CODE_TO_CREATE);
        addPostalCodeJobCenterMapping(POSTAL_CODE_FIVE, CODE_TO_CREATE);

        // WHEN
        restJobCenterMockMvc.perform(patch("/api/job-centers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobCenterToCreate)))
            .andExpect(status().isOk());

        // THEN
        Optional<JobCenter> jobCenter = this.jobCenterRepository.findOneByCode(CODE_TO_CREATE);
        assertThat(jobCenter).isPresent();
        assertThat(jobCenter.get().getPostalCodes()).containsExactlyInAnyOrder(POSTAL_CODE_FOUR, POSTAL_CODE_FIVE);

    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void updateJobCenter() throws Exception {
        // GIVEN
        Address addressEN = buildAddress(NAME_EN, CITY_EN, STREET_EN, Language.en);
        JobCenter newJobcenter = buildJobCenter(CODE_TO_UPDATE, EMAIL_ONE, PHONE_ONE, FAX_ONE, POSTAL_CODE_SIX, addressEN, addressEN);
        this.jobCenterRepository.save(newJobcenter);

        addPostalCodeJobCenterMapping(POSTAL_CODE_SEVEN, CODE_TO_UPDATE);
        addPostalCodeJobCenterMapping(POSTAL_CODE_EIGHT, CODE_TO_UPDATE);
        JobCenter jobCenterToUpdate = buildJobCenter(CODE_TO_UPDATE, EMAIL_ONE, PHONE_ONE, FAX_ONE, POSTAL_CODE_SIX, addressEN, addressEN);

        // WHEN
        restJobCenterMockMvc.perform(patch("/api/job-centers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobCenterToUpdate)))
            .andExpect(status().isOk());

        // THEN
        Optional<JobCenter> jobCenter = this.jobCenterRepository.findOneByCode(CODE_TO_UPDATE);
        assertThat(jobCenter).isPresent();
        assertThat(jobCenter.get().getPostalCodes()).containsExactlyInAnyOrder(POSTAL_CODE_SEVEN, POSTAL_CODE_EIGHT);
    }

    private void addPostalCodeJobCenterMapping(String postalCode, String jobCenterCode) {
        this.jdbcTemplate.update("insert into postal_code_job_center_mapping (postal_code, job_center_code) values(?, ?)", postalCode, jobCenterCode);
    }
}
