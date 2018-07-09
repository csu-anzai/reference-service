package ch.admin.seco.service.reference.web.rest;

import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.AVAM;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.BFS;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.SBN3;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.SBN5;
import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.X28;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ch.admin.seco.service.reference.ReferenceserviceApp;
import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.OccupationLabelMappingX28;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.repository.OccupationLabelMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationLabelMappingX28Repository;
import ch.admin.seco.service.reference.repository.OccupationLabelRepository;
import ch.admin.seco.service.reference.repository.search.OccupationLabelSearchRepository;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.impl.ElasticsearchOccupationLabelIndexer;
import ch.admin.seco.service.reference.web.rest.errors.ExceptionTranslator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReferenceserviceApp.class)
public class OccupationLabelResourceIntTest {

    private MockMvc sut;

    @Autowired
    private OccupationLabelService occupationLabelService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private FormattingConversionService formattingConversionService;

    @Autowired
    private OccupationLabelRepository occupationLabelRepository;

    @Autowired
    private OccupationLabelMappingRepository occupationLabelMappingRepository;

    @Autowired
    private OccupationLabelMappingX28Repository occupationLabelMappingX28Repository;

    @Autowired
    private OccupationLabelSearchRepository occupationLabelSearchRepository;

    @Autowired
    private ElasticsearchOccupationLabelIndexer elasticsearchOccupationLabelIndexer;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OccupationLabelResource occupationLabelResource = new OccupationLabelResource(occupationLabelService);

        this.sut = MockMvcBuilders.standaloneSetup(occupationLabelResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .setConversionService(formattingConversionService).build();
    }

    @Before
    public void initTest() {
        this.occupationLabelRepository.deleteAll();
        this.occupationLabelMappingRepository.deleteAll();
        this.occupationLabelMappingX28Repository.deleteAll();
        this.occupationLabelSearchRepository.deleteAll();

        this.occupationLabelMappingRepository.save(
            //                         bfs     avam  sbn3  sbn5
            createOccupationMapping("33302009", "68913", "361", "36102", "Java-Programmierer")
        );
        this.occupationLabelMappingRepository.save(
            createOccupationMapping("33302011", "68904", "361", "36102", "Systemprogrammierer EDV")
        );
        this.occupationLabelMappingX28Repository.save(
            //                              avam      x28
            createOccupationLabelMappingX28("68913", "11002714")
        );
        this.occupationLabelService.save(createAvamOccupationLabel("68913", Language.de, 'm', "Java-Programmierer"));
        this.occupationLabelService.save(createAvamOccupationLabel("68913", Language.en, 'm', "Java-Programmer"));
        this.occupationLabelService.save(createAvamOccupationLabel("68904", Language.en, 'm', "Data programmer"));
        this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.en, "Javascript Developer"));
        this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.de, "Javascript-Entwickler"));
        this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.de, "Javascript-Entwicklerin"));
        this.occupationLabelService.save(createSBN5OccupationLabel("36102", Language.de, "Programmierer/innen"));
        this.occupationLabelService.save(createSBN3OccupationLabel("361", Language.de, "Berufe der Informatik"));

        this.elasticsearchOccupationLabelIndexer.reindexOccupationLabel();
    }

    @Test
    public void suggestOccupation_X28() throws Exception {
        sut.perform(get("/api/_search/occupations/label?prefix=jav&types=x28&types=sbn3&types=sbn5&resultSize=5").locale(Locale.GERMAN))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.occupations").value(hasSize(2)))
            .andExpect(jsonPath("$.occupations[*].code").value(hasItem("11002714")))
            .andExpect(jsonPath("$.occupations[*].type").value(hasItem("X28")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Javascript-Entwickler")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Javascript-Entwicklerin")))
            .andExpect(jsonPath("$.classifications").value(hasSize(2)))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("361")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN3")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Berufe der Informatik")))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("36102")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN5")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Programmierer/innen")));
    }

    @Test
    public void suggestOccupation_AVAM() throws Exception {
        sut.perform(get("/api/_search/occupations/label?prefix=jav&types=avam,sbn3,sbn5&resultSize=5").locale(Locale.GERMAN))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.occupations").value(hasSize(1)))
            .andExpect(jsonPath("$.occupations[*].code").value(hasItem("68913")))
            .andExpect(jsonPath("$.occupations[*].type").value(hasItem("AVAM")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Java-Programmierer")))
            .andExpect(jsonPath("$.occupations[*].classifier").value(hasItem("default")))
            .andExpect(jsonPath("$.classifications").value(hasSize(2)))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("361")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN3")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Berufe der Informatik")))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("36102")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN5")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Programmierer/innen")));
    }

    @Test
    public void suggestOccupation_X28_AVAM() throws Exception {
        sut.perform(get("/api/_search/occupations/label?prefix=java&types=avam,x28,sbn3,sbn5&resultSize=5").locale(Locale.GERMAN))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.occupations").value(hasSize(3)))
            .andExpect(jsonPath("$.occupations[*].code").value(hasItem("68913")))
            .andExpect(jsonPath("$.occupations[*].type").value(hasItem("AVAM")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Java-Programmierer")))
            .andExpect(jsonPath("$.occupations[*].classifier").value(hasItem("default")))
            .andExpect(jsonPath("$.occupations[*].code").value(hasItem("11002714")))
            .andExpect(jsonPath("$.occupations[*].type").value(hasItem("X28")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Javascript-Entwickler")))
            .andExpect(jsonPath("$.occupations[*].label").value(hasItem("Javascript-Entwicklerin")))
            .andExpect(jsonPath("$.classifications").value(hasSize(2)))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("361")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN3")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Berufe der Informatik")))
            .andExpect(jsonPath("$.classifications[*].code").value(hasItem("36102")))
            .andExpect(jsonPath("$.classifications[*].type").value(hasItem("SBN5")))
            .andExpect(jsonPath("$.classifications[*].label").value(hasItem("Programmierer/innen")));
    }

    @Test
    public void getOccupationLabelsByCodeAndType_X28() throws Exception {
        sut.perform(get("/api/occupations/label/x28/11002714").locale(Locale.GERMAN))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.*").value(hasItem("Javascript-Entwickler")))
            .andExpect(jsonPath("$.*").value(hasItem("Javascript-Entwicklerin")));
    }

    @Test
    public void getOccupationLabelsByCodeAndTypeAndClassifier() throws Exception {
        sut.perform(get("/api/occupations/label/avam/68913/m").locale(Locale.GERMAN))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.label").value("Java-Programmierer"));
    }

    @Test
    public void getOccupationLabelsByKey() throws Exception {
        sut.perform(get("/api/occupations/label/sbn5/36102").locale(Locale.GERMAN))
            .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.default").value("Programmierer/innen"));
    }

    @Test
    public void getOccupationMappingByAvamCode() throws Exception {
        sut.perform(get("/api/occupations/label/mapping/avam/68913"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"));
    }

    @Test
    public void getOccupationLabelsMappingByAvamCode() throws Exception {
        sut.perform(get("/api/occupations/label/mapping/all-languages/avam/68913"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"))
            .andExpect(jsonPath("$.labelDe").value("Java-Programmierer"))
            .andExpect(jsonPath("$.labelEn").value("Java-Programmer"));
    }

    @Test
    public void getOccupationMappinByBFSCode() throws Exception {
        sut.perform(get("/api/occupations/label/mapping/bfs/33302009"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"));
    }

    @Test
    public void getOccupationMappingByX28Code() throws Exception {
        sut.perform(get("/api/occupations/label/mapping/x28/11002714"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"));
    }

    @Test
    public void getOccupationLabelsForSBN3Code() throws Exception {
        sut.perform(get("/api/occupations/label/mapped-by/sbn3/361"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[0].code").value("68904"))
            .andExpect(jsonPath("$.[0].type").value("AVAM"))
            .andExpect(jsonPath("$.[0].labels.default").value("Data programmer"));
    }

    @Test
    public void getOccupationLabelsForSBN5Code() throws Exception {
        sut.perform(get("/api/occupations/label/mapped-by/sbn5/36102"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[0].code").value("68904"))
            .andExpect(jsonPath("$.[0].type").value("AVAM"))
            .andExpect(jsonPath("$.[0].labels.default").value("Data programmer"))
            .andExpect(jsonPath("$.[1].code").value("68913"))
            .andExpect(jsonPath("$.[1].type").value("AVAM"))
            .andExpect(jsonPath("$.[1].labels.default").value("Java-Programmer"));
    }

    private OccupationLabel createAvamOccupationLabel(String code, Language lang, char gender, String label) {
        return createOccupationLabel(code, AVAM, lang, String.valueOf(gender), label);
    }

    private OccupationLabel createBFSOccupationLabel(String code, Language lang, char gender, String label) {
        return createOccupationLabel(code, BFS, lang, String.valueOf(gender), label);
    }

    private OccupationLabel createSBN3OccupationLabel(String code, Language lang, String label) {
        return createOccupationLabel(code, SBN3, lang, "default", label);
    }

    private OccupationLabel createSBN5OccupationLabel(String code, Language lang, String label) {
        return createOccupationLabel(code, SBN5, lang, "default", label);
    }

    private OccupationLabel createX28OccupationLabel(String code, Language lang, String label) {
        return createOccupationLabel(code, X28, lang, RandomStringUtils.randomAlphanumeric(10), label);
    }


    private OccupationLabel createOccupationLabel(String code, ProfessionCodeType type, Language lang, String classifier, String label) {
        return new OccupationLabel()
            .id(UUID.randomUUID())
            .code(code)
            .type(type)
            .language(lang)
            .classifier(classifier)
            .label(label);
    }

    private OccupationLabelMappingX28 createOccupationLabelMappingX28(String avamCode, String x28Code) {
        OccupationLabelMappingX28 mapping = new OccupationLabelMappingX28();
        mapping.setAvamCode(avamCode);
        mapping.setX28Code(x28Code);

        return mapping;
    }

    private OccupationLabelMapping createOccupationMapping(String bfsCode, String avamCode, String sbn3Code, String sbn5Code, String description) {
        OccupationLabelMapping mapping = new OccupationLabelMapping();
        mapping.setId(UUID.randomUUID());
        mapping.setBfsCode(bfsCode);
        mapping.setAvamCode(avamCode);
        mapping.setSbn3Code(sbn3Code);
        mapping.setSbn5Code(sbn5Code);
        mapping.setDescription(description);

        return mapping;
    }
}
