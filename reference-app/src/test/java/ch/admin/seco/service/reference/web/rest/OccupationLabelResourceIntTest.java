package ch.admin.seco.service.reference.web.rest;

import ch.admin.seco.service.reference.domain.*;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.impl.ElasticsearchOccupationLabelIndexer;
import ch.admin.seco.service.reference.service.search.OccupationLabelSearchRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static ch.admin.seco.service.reference.domain.enums.ProfessionCodeType.*;
import static ch.admin.seco.service.reference.web.rest.TestUtil.doAsAdmin;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OccupationLabelResourceIntTest {

    private static final String URL = "/api/occupations/label";

    private static final String URL_SEARCH = "/api/_search/occupations/label";

    @Autowired
    private MockMvc sut;

    @Autowired
    private OccupationLabelService occupationLabelService;

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

    @Autowired
    private OccupationLabelMappingISCORepository occupationLabelMappingISCORepository;

    @Before
    public void setUp() {
        this.occupationLabelRepository.deleteAll();
        this.occupationLabelMappingRepository.deleteAll();
        this.occupationLabelMappingX28Repository.deleteAll();
        this.occupationLabelMappingISCORepository.deleteAll();

        this.occupationLabelSearchRepository.deleteAll();

        await().until(() -> this.occupationLabelSearchRepository.count() == 0);

        this.occupationLabelMappingRepository.save(
            createOccupationMapping("33302009", "68913", "361", "36102", "Java-Programmierer")
        );
        this.occupationLabelMappingRepository.save(
            createOccupationMapping("33302011", "68904", "361", "36102", "Systemprogrammierer EDV")
        );
        this.occupationLabelMappingX28Repository.save(
            createOccupationLabelMappingX28("68913", "11002714")
        );

        doAsAdmin(()-> this.occupationLabelService.save(createAVAMOccupationLabel("68913", Language.de, 'm', "Java-Programmierer")));
        doAsAdmin(()-> this.occupationLabelService.save(createAVAMOccupationLabel("68904", Language.en, 'm', "Data programmer")));

        doAsAdmin(()-> this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.en, "Javascript Developer")));
        doAsAdmin(()-> this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.de, "Javascript-Entwickler")));
        doAsAdmin(()-> this.occupationLabelService.save(createX28OccupationLabel("11002714", Language.de, "Javascript-Entwicklerin")));

        doAsAdmin(()-> this.occupationLabelService.save(createSBN3OccupationLabel("361", Language.de, "Berufe der Informatik")));

        doAsAdmin(()-> this.occupationLabelService.save(createSBN5OccupationLabel("36102", Language.de, "Programmierer/innen")));

        this.elasticsearchOccupationLabelIndexer.reindexOccupationLabel();

        await().until(() -> this.occupationLabelSearchRepository.count() >= 7);
    }

    @Test
    public void getOccupationByAVAMId() throws Exception {
        // given
        UUID id_AVAM = findByCodeAndType("68913", AVAM);

        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/" + id_AVAM.toString())
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(equalTo(id_AVAM.toString())));

    }

    @Test
    public void getOccupationByX28Id() throws Exception {
        // given
        UUID id_X28 = findByCodeAndType("11002714", X28);

        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/" + id_X28.toString())
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(equalTo(id_X28.toString())));
    }

    @Test
    public void getOccupationBySBN3Id() throws Exception {
        // given
        UUID id_SBN3 = findByCodeAndType("361", SBN3);

        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/" + id_SBN3.toString())
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(equalTo(id_SBN3.toString())));
    }

    @Test
    public void getOccupationBySBN5Id() throws Exception {
        // given
        UUID id_SBN5 = findByCodeAndType("36102", SBN5);

        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/" + id_SBN5.toString())
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(equalTo(id_SBN5.toString())));
    }

    @Test
    public void suggestOccupation_X28() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL_SEARCH + "?prefix=jav&types=x28&types=sbn3&types=sbn5&resultSize=5")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
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
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL_SEARCH + "?prefix=jav&types=avam,sbn3,sbn5&resultSize=5")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
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
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL_SEARCH + "?prefix=java&types=avam,x28,sbn3,sbn5&resultSize=5")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
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
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/x28/11002714")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.*").value(hasItem("Javascript-Entwickler")))
            .andExpect(jsonPath("$.*").value(hasItem("Javascript-Entwicklerin")));
    }

    @Test
    public void getOccupationLabelsByCodeAndTypeAndClassifier() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/avam/68913/m")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.label").value("Java-Programmierer"));
    }

    @Test
    public void getOccupationLabelsByKey() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/sbn5/36102")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andDo(h -> System.out.println(h.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.default").value("Programmierer/innen"));
    }

    @Test
    public void getOccupationMappingByAVAMCode() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/mapping/avam/68913")
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"))
            .andExpect(jsonPath("$.iscoCode").isEmpty());
    }

    @Test
    public void getOccupationMappingByBFSCode() throws Exception {
        // when
        this.occupationLabelMappingISCORepository.save(createOccupationLabelMappingISCO("33302009", "8844"));
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/mapping/bfs/33302009")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"))
            .andExpect(jsonPath("$.iscoCode").value("8844"));
    }

    @Test
    public void getOccupationMappingByX28Code() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/mapping/x28/11002714")
                .locale(Locale.GERMAN)
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.bfsCode").value("33302009"))
            .andExpect(jsonPath("$.avamCode").value("68913"))
            .andExpect(jsonPath("$.sbn3Code").value("361"))
            .andExpect(jsonPath("$.sbn5Code").value("36102"))
            .andExpect(jsonPath("$.description").value("Java-Programmierer"))
            .andExpect(jsonPath("$.iscoCode").isEmpty());
    }

    @Test
    public void getOccupationLabelsForSBN3Code() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/mapped-by/sbn3/361")
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[0].code").value("68904"))
            .andExpect(jsonPath("$.[0].type").value("AVAM"))
            .andExpect(jsonPath("$.[0].labels.default").value("Data programmer"));
    }

    @Test
    public void getOccupationLabelsForSBN5Code() throws Exception {
        // when
        ResultActions resultActions = this.sut.perform(
            MockMvcRequestBuilders.get(URL + "/mapped-by/sbn5/36102")
                .contentType(TestUtil.APPLICATION_JSON_UTF8));

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[0].code").value("68904"))
            .andExpect(jsonPath("$.[0].type").value("AVAM"))
            .andExpect(jsonPath("$.[0].labels.default").value("Data programmer"))
            .andExpect(jsonPath("$.[1].code").value("68913"))
            .andExpect(jsonPath("$.[1].type").value("AVAM"))
            .andExpect(jsonPath("$.[1].labels.default").value("Java-Programmierer"));
    }

    private OccupationLabel createAVAMOccupationLabel(String code, Language lang, char gender, String label) {
        return createOccupationLabel(code, AVAM, lang, String.valueOf(gender), label);
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

    private OccupationLabelMappingISCO createOccupationLabelMappingISCO(String bfsCode, String iscoCode) {
        final OccupationLabelMappingISCO occupationLabelMappingISCO = new OccupationLabelMappingISCO();
        occupationLabelMappingISCO.setBfsCode(bfsCode);
        occupationLabelMappingISCO.setIscoCode(iscoCode);
        return occupationLabelMappingISCO;
    }

    private UUID findByCodeAndType(String code, ProfessionCodeType type) {
        List<OccupationLabel> labels = this.occupationLabelRepository.findByCodeAndTypeAndLanguage(code, type, Language.de);
        if (labels.isEmpty()) {
            return UUID.randomUUID();
        }
        return labels.get(0).getId();
    }
}
