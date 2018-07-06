package ch.admin.seco.service.reference.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.ReferenceserviceApp;
import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.ReportingObligation;
import ch.admin.seco.service.reference.repository.OccupationLabelMappingRepository;
import ch.admin.seco.service.reference.repository.ReportingObligationRepository;
import ch.admin.seco.service.reference.service.ReportingObligationService;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class ReportingObligationResourceIntTest {

    private MockMvc mockMvc;

    @Autowired
    private ReportingObligationService reportingObligationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private FormattingConversionService formattingConversionService;

    @Autowired
    private ReportingObligationRepository reportingObligationRepository;

    @Autowired
    private OccupationLabelMappingRepository occupationMappingRepository;

    @Before
    public void setUp() {
        ReportingObligationResource reportingObligationResource =
            new ReportingObligationResource(reportingObligationService);

        this.mockMvc = MockMvcBuilders.standaloneSetup(reportingObligationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(formattingConversionService)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    @Transactional
    public void shouldReturnFalseWhenCheckForSwissAndReportingObligationNotFoundBySbn5Code() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456"),
            new ReportingObligation().sbn5Code("23975")
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by/sbn5/11503"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(false))
            .andExpect(jsonPath("$.professionCode.code").value("11503"));
    }

    @Test
    @Transactional
    public void shouldReturnTrueWhenCheckForSwissAndReportingObligationFoundBySbn5Code() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456"),
            new ReportingObligation().sbn5Code("23975")
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by/sbn5/23975"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(true))
            .andExpect(jsonPath("$.professionCode.code").value("23975"));
    }

    @Test
    @Transactional
    public void shouldReturnFalseWhenCheckForSwissAndReportingObligationAppliedForCanton() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "ZH", "BE")))
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by/sbn5/56456"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(false))
            .andExpect(jsonPath("$.professionCode.code").value("56456"))
            .andExpect(jsonPath("$.cantons").value(Matchers.containsInAnyOrder("AG", "ZH", "BE")));
    }

    @Test
    @Transactional
    public void shouldReturnNotFoundWhenCheckForSwissAndSbn5CodeNotFoundByAvamCode() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456"),
            new ReportingObligation().sbn5Code("23975")
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by/avam/555"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void shouldReturnTrueWhenCheckForSwissAndMapAvamToSbn5CodeAndReportingObligationExists() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
        );
        saveOccupationLabelMapping();

        mockMvc.perform(get("/api/reporting-obligations/check-by/avam/55123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(true))
            .andExpect(jsonPath("$.professionCode.code").value("56456"));
    }


    private void saveOccupationLabelMapping() {
        OccupationLabelMapping occupationLabelMapping = new OccupationLabelMapping()
            .bfsCode("10000000")
            .avamCode("55123")
            .sbn3Code("345")
            .sbn5Code("56456")
            .description("Test");
        occupationMappingRepository.save(occupationLabelMapping);
    }

    @Test
    @Transactional
    public void shouldReturnTrueWhenCheckForCantonAndReportingObligationAppliedForSwiss() throws Exception {
        saveReportingObligations(new ReportingObligation().sbn5Code("56456"));

        mockMvc.perform(get("/api/reporting-obligations/check-by-canton/sbn5/56456")
            .param("cantonCode", "SO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(true))
            .andExpect(jsonPath("$.professionCode.code").value("56456"));
    }

    @Test
    @Transactional
    public void shouldReturnTrueWhenCheckForCantonAndReportingObligationMatchedByCantonCode() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "SO", "BE")))
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by-canton/sbn5/56456")
            .param("cantonCode", "SO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(true))
            .andExpect(jsonPath("$.professionCode.code").value("56456"))
            .andExpect(jsonPath("$.cantons").value(Matchers.containsInAnyOrder("AG", "SO", "BE")));
    }

    @Test
    @Transactional
    public void shouldReturnFalseWhenCheckForCantonAndReportingObligationNotMatchedByCantonCode() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "ZH", "BE")))
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by-canton/sbn5/56456")
            .param("cantonCode", "SO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(false))
            .andExpect(jsonPath("$.professionCode.code").value("56456"))
            .andExpect(jsonPath("$.cantons").value(Matchers.containsInAnyOrder("AG", "ZH", "BE")));
    }

    @Test
    @Transactional
    public void shouldReturnTrueWhenCheckWithoutCantonAndReportingObligationAppliedForSwiss() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(Collections.emptySet())
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by-canton/sbn5/56456"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(true))
            .andExpect(jsonPath("$.professionCode.code").value("56456"))
            .andExpect(jsonPath("$.cantons").value(Matchers.empty()));
    }

    @Test
    @Transactional
    public void shouldReturnFalseWhenCheckWithoutCantonAndReportingObligationAppliedForCantons() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "ZH", "BE")))
        );

        mockMvc.perform(get("/api/reporting-obligations/check-by-canton/sbn5/56456"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasReportingObligation").value(false))
            .andExpect(jsonPath("$.professionCode.code").value("56456"))
            .andExpect(jsonPath("$.cantons").value(Matchers.containsInAnyOrder("AG", "ZH", "BE")));
    }

    @Test
    @Transactional
    public void shouldReturnReportingObligationsForCantonCode() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "ZH", "BE"))),
            new ReportingObligation().sbn5Code("23975")
                .cantonCodes(new HashSet<>(Arrays.asList("Ti", "SO", "ZH"))),
            new ReportingObligation().sbn5Code("11503")
        );

        mockMvc.perform(get("/api/reporting-obligations")
            .param("cantonCode", "SO"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].sbn5Code").value("23975"))
            .andExpect(jsonPath("$.length()").value("1"));
    }

    @Test
    @Transactional
    public void shouldReturnReportingObligationsForSwiss() throws Exception {
        saveReportingObligations(
            new ReportingObligation().sbn5Code("23975"),
            new ReportingObligation().sbn5Code("56456")
                .cantonCodes(new HashSet<>(Arrays.asList("AG", "ZH", "BE"))),
            new ReportingObligation().sbn5Code("11503")
        );

        mockMvc.perform(get("/api/reporting-obligations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[?(@.sbn5Code == 23975)]").exists())
            .andExpect(jsonPath("$.[?(@.sbn5Code == 11503)]").exists())
            .andExpect(jsonPath("$.length()").value("2"));
    }

    private void saveReportingObligations(ReportingObligation... reportingObligations) {
        reportingObligationRepository.saveAll(Arrays.asList(reportingObligations));
    }
}
