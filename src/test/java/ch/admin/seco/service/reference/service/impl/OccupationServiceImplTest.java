package ch.admin.seco.service.reference.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSynonymSearchRepository;

@RunWith(MockitoJUnitRunner.class)
public class OccupationServiceImplTest {

    private static final int CODE = 10000000;
    private static final int CODE_2 = 10000001;

    private static final int CLASSIFICATION_CODE = 111;

    private static final int AVAM_CODE = 12345678;

    private static final int X28_CODE = 343234;
    private static final int X28_CODE_2 = 787433;

    private OccupationServiceImpl occupationService;

    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private OccupationSynonymRepository occupationSynonymRepository;
    @Mock
    private OccupationSynonymSearchRepository occupationSynonymSearchRepository;
    @Mock
    private ElasticsearchTemplate elasticsearchTemplate;
    @Mock
    private EntityToSynonymMapper occupationSynonymMapper;
    @Mock
    private OccupationMappingRepository occupationMappingRepository;
    @Mock
    private OccupationRepository occupationRepository;

    private Occupation occupation;

    @Before
    public void setUp() {
        occupationService = new OccupationServiceImpl(applicationContext,
            occupationSynonymRepository, occupationSynonymSearchRepository,
            elasticsearchTemplate, occupationSynonymMapper, occupationMappingRepository,
            occupationRepository);
        occupation = createOccupation(CODE, CLASSIFICATION_CODE);
    }

    private static Occupation createOccupation(int code, int classificationCode) {
        Occupation occupation = new Occupation()
            .code(code)
            .classificationCode(classificationCode);
        occupation.setId(UUID.randomUUID());
        return occupation;
    }

    private static OccupationMapping createOccupationMapping(int code, int avamCode, int x28Code) {
        OccupationMapping occupationMapping = new OccupationMapping()
            .code(code)
            .avamCode(avamCode)
            .x28Code(x28Code);
        occupationMapping.setId(UUID.randomUUID());
        return occupationMapping;
    }

    @Test
    public void shouldReturnEmpty_whenOccupationNotFoundByCode() {
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.empty());

        checkResultIsEmpty(() -> occupationService.findOneOccupationByCode(CODE));
    }

    @Test
    public void shouldReturnOccupation_whenOccupationFoundByCode() {
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.of(occupation));

        checkResultIsNotEmpty(() -> occupationService.findOneOccupationByCode(CODE), occupation);
    }

    @Test
    public void shouldReturnEmpty_whenOccupationMappingNotFoundByAvamCode() {
        when(occupationMappingRepository.findByAvamCode(AVAM_CODE)).thenReturn(Collections.emptyList());

        checkResultIsEmpty(() -> occupationService.findOneOccupationByAvamCode(AVAM_CODE));
    }

    @Test
    public void shouldSearchOccupationByFirstOccupationMapping_whenListOfMappingsFoundByAvamCode() {
        List<OccupationMapping> occupationMappings = Arrays.asList(createOccupationMapping(CODE, AVAM_CODE, X28_CODE),
            createOccupationMapping(CODE_2, AVAM_CODE, X28_CODE_2));
        when(occupationMappingRepository.findByAvamCode(AVAM_CODE)).thenReturn(occupationMappings);
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.ofNullable(occupation));

        checkResultIsNotEmpty(() -> occupationService.findOneOccupationByAvamCode(AVAM_CODE), occupation);
    }

    @Test
    public void shouldReturnEmpty_whenMappingNotFoundByX28Code() {
        when(occupationMappingRepository.findByX28Code(X28_CODE)).thenReturn(Optional.empty());

        checkResultIsEmpty(() -> occupationService.findOneOccupationByX28Code(X28_CODE));
    }

    @Test
    public void shouldReturnEmpty_whenOccupationNotExistsForMapping() {
        when(occupationMappingRepository.findByX28Code(X28_CODE))
            .thenReturn(Optional.of(createOccupationMapping(CODE, AVAM_CODE, X28_CODE)));
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.empty());

        checkResultIsEmpty(() -> occupationService.findOneOccupationByX28Code(X28_CODE));
    }

    @Test
    public void shouldReturnOccupation_whenOccupationExistsForMapping() {
        when(occupationMappingRepository.findByX28Code(X28_CODE))
            .thenReturn(Optional.of(createOccupationMapping(CODE, AVAM_CODE, X28_CODE)));
        when(occupationRepository.findOneByCode(CODE)).thenReturn(Optional.of(occupation));

        checkResultIsNotEmpty(() -> occupationService.findOneOccupationByX28Code(X28_CODE), occupation);
    }

    private void checkResultIsEmpty(Supplier<Optional<Occupation>> supplier) {
        Optional<Occupation> result = supplier.get();

        assertThat(result).isEmpty();
    }

    private void checkResultIsNotEmpty(Supplier<Optional<Occupation>> supplier, Occupation expectedResult) {
        Optional<Occupation> result = supplier.get();

        assertThat(result).isNotEmpty();
        assertThat(result).contains(expectedResult);
    }
}
