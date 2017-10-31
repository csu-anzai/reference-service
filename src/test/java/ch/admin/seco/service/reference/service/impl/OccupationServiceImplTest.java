package ch.admin.seco.service.reference.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import ch.admin.seco.service.reference.service.dto.OccupationDto;
import ch.admin.seco.service.reference.service.dto.mapper.OccupationDtoMapper;

@RunWith(MockitoJUnitRunner.class)
public class OccupationServiceImplTest {

    private static final int CODE = 10000000;
    private static final int CODE_2 = 10000001;

    private static final int CLASSIFICATION_CODE = 111;

    private static final int AVAM_CODE = 12345678;

    private static final int X28_CODE = 343234;
    private static final int X28_CODE_2 = 787433;

    private OccupationServiceImpl occupationService;
    private OccupationSuggestionImpl occupationSuggestion;

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private OccupationSynonymRepository occupationSynonymRepository;
    @Mock
    private OccupationSearchRepository occupationSynonymSearchRepository;
    @Mock
    private ElasticsearchTemplate elasticsearchTemplate;
    @Mock
    private EntityToSuggestionMapper occupationSynonymMapper;
    @Mock
    private OccupationMappingRepository occupationMappingRepository;
    @Mock
    private OccupationRepository occupationRepository;

    private OccupationDtoMapper occupationDtoMapper = new OccupationDtoMapper();

    @Mock
    private ClassificationRepository classificationRepository;

    private Occupation occupation;
    private OccupationDto occupationDto;

    @Before
    public void setUp() {
        occupationService = new OccupationServiceImpl(applicationContext,
            occupationSynonymRepository, occupationSynonymSearchRepository,
            occupationSynonymMapper, occupationMappingRepository,
            occupationRepository, occupationSuggestion, occupationDtoMapper);

        occupationSuggestion = new OccupationSuggestionImpl(elasticsearchTemplate, occupationSynonymMapper, classificationRepository);

        occupation = createOccupation(CODE, CLASSIFICATION_CODE);
        occupationDto = createOccupationDto(occupation);
    }

    @Test
    public void shouldReturnEmpty_whenOccupationNotFoundByCode() {
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.empty());

        assertThat(occupationService.findOneOccupationByCode(CODE, Language.de))
            .isEmpty();
    }

    @Test
    public void shouldReturnOccupation_whenOccupationFoundByCode() {
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.of(occupation));

        assertThat(occupationService.findOneOccupationByCode(CODE, Language.de))
            .contains(occupationDto);
    }

    @Test
    public void shouldReturnEmpty_whenOccupationMappingNotFoundByAvamCode() {
        when(occupationMappingRepository.findByAvamCode(AVAM_CODE)).thenReturn(Collections.emptyList());

        assertThat(occupationService.findOneOccupationByAvamCode(AVAM_CODE, Language.de))
            .isEmpty();
    }

    @Test
    public void shouldSearchOccupationByFirstOccupationMapping_whenListOfMappingsFoundByAvamCode() {
        List<OccupationMapping> occupationMappings = Arrays.asList(createOccupationMapping(CODE, AVAM_CODE, X28_CODE),
            createOccupationMapping(CODE_2, AVAM_CODE, X28_CODE_2));
        when(occupationMappingRepository.findByAvamCode(AVAM_CODE)).thenReturn(occupationMappings);
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.ofNullable(occupation));

        assertThat(occupationService.findOneOccupationByAvamCode(AVAM_CODE, Language.de))
            .contains(occupationDto);
    }

    @Test
    public void shouldReturnEmpty_whenMappingNotFoundByX28Code() {
        when(occupationMappingRepository.findByX28Code(X28_CODE)).thenReturn(Optional.empty());

        assertThat(occupationService.findOneOccupationByX28Code(X28_CODE, Language.de))
            .isEmpty();
    }

    @Test
    public void shouldReturnEmpty_whenOccupationNotExistsForMapping() {
        when(occupationMappingRepository.findByX28Code(X28_CODE))
            .thenReturn(Optional.of(createOccupationMapping(CODE, AVAM_CODE, X28_CODE)));
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.empty());

        assertThat(occupationService.findOneOccupationByX28Code(X28_CODE, null))
            .isEmpty();
    }

    @Test
    public void shouldReturnOccupation_whenOccupationExistsForMapping() {
        when(occupationMappingRepository.findByX28Code(X28_CODE))
            .thenReturn(Optional.of(createOccupationMapping(CODE, AVAM_CODE, X28_CODE)));
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.of(occupation));

        assertThat(occupationService.findOneOccupationByX28Code(X28_CODE, null))
            .contains(occupationDto);
    }

    private Occupation createOccupation(int code, int classificationCode) {
        Occupation occupation = new Occupation()
            .code(code)
            .classificationCode(classificationCode);
        occupation.setId(UUID.randomUUID());
        return occupation;
    }

    private OccupationDto createOccupationDto(Occupation occupation) {
        OccupationDto occupationDto = new OccupationDto()
            .id(occupation.getId())
            .code(occupation.getCode())
            .classificationCode(occupation.getClassificationCode());
        return occupationDto;
    }

    private OccupationMapping createOccupationMapping(int code, int avamCode, int x28Code) {
        OccupationMapping occupationMapping = new OccupationMapping()
            .code(code)
            .avamCode(avamCode)
            .x28Code(x28Code);
        occupationMapping.setId(UUID.randomUUID());
        return occupationMapping;
    }
}
