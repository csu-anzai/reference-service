package ch.admin.seco.service.reference.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static final String CODE_TWO = "SGGG2";
    private static final String EMAIL_TWO = "rav@ktsh.ch";
    private static final String PHONE_TWO = "+41 52 632 70 24";
    private static final String FAX_TWO = "";

    private static final String NAME_EN = "RAV St.Gallen EN";
    private static final String NAME_DE = "RAV St.Gallen DE";
    private static final String CITY_EN = "London EN";
    private static final String CITY_DE = "London DE";
    private static final String STREET_EN = "Berneckerstrasse EN";
    private static final String STREET_DE = "Berneckerstrasse DE";
    private static final String HOUSE_NUMBER = "12";
    private static final String ZIP_CODE = "9000";

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

    private MockMvc restJobCenterMockMvc;

    private JobCenter jobCenterOne;
    private JobCenter jobCenterTwo;

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

        jobCenterOne = buildJobCenter(CODE_ONE, EMAIL_ONE, PHONE_ONE, FAX_ONE, addressEN, addressDE);
        jobCenterTwo = buildJobCenter(CODE_TWO, EMAIL_TWO, PHONE_TWO, FAX_TWO, addressDE);
        jobCenterRepository.saveAll(Arrays.asList(jobCenterOne, jobCenterTwo));
    }

    private static Address buildAddress(String name, String city, String street, Language language) {
        return new Address()
            .name(name)
            .city(city)
            .street(street)
            .houseNumber(HOUSE_NUMBER)
            .zipCode(ZIP_CODE)
            .language(language);
    }

    private static JobCenter buildJobCenter(String code, String email, String phone, String fax, Address... addresses) {
        return new JobCenter()
            .code(code)
            .email(email)
            .phone(phone)
            .fax(fax)
            .addresses(Stream.of(addresses).collect(Collectors.toSet()));
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

    private static MockHttpServletRequestBuilder buildJobCenterSearchByCodeRequest(String code, String language) {
        return get("/api/job-centers")
            .param("code", code)
            .param("language", language);
    }
}
