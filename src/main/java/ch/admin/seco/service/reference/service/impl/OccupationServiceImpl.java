package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSearchRepository;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationDto;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationServiceImpl implements OccupationService {

    private final Logger log = LoggerFactory.getLogger(OccupationServiceImpl.class);
    private final ApplicationContext applicationContext;
    private final OccupationSynonymRepository occupationSynonymRepository;
    private final OccupationSearchRepository occupationSynonymSearchRepository;
    private final EntityToSynonymMapper occupationSynonymMapper;
    private final OccupationMappingRepository occupationMappingRepository;
    private final OccupationRepository occupationRepository;
    private final ClassificationRepository classificationRepository;
    private final OccupationSuggestionImpl occupationSuggestion;
    private final Function<OccupationMapping, Optional<Occupation>> occupationMappingToOccupation;
    private OccupationServiceImpl occupationServiceImpl;

    public OccupationServiceImpl(ApplicationContext applicationContext,
        OccupationSynonymRepository occupationSynonymRepository,
        OccupationSearchRepository occupationSynonymSearchRepository,
        EntityToSynonymMapper occupationSynonymMapper,
        OccupationMappingRepository occupationMappingRepository,
        OccupationRepository occupationRepository,
        ClassificationRepository classificationRepository, OccupationSuggestionImpl occupationSuggestion) {

        this.applicationContext = applicationContext;
        this.occupationSynonymRepository = occupationSynonymRepository;
        this.occupationSynonymSearchRepository = occupationSynonymSearchRepository;
        this.occupationSynonymMapper = occupationSynonymMapper;
        this.occupationMappingRepository = occupationMappingRepository;
        this.occupationRepository = occupationRepository;
        this.classificationRepository = classificationRepository;

        this.occupationMappingToOccupation = mapping -> occupationRepository.findOneByCode(mapping.getCode());
        this.occupationSuggestion = occupationSuggestion;
    }

    /**
     * Save a occupationSynonym.
     *
     * @param occupationSynonym the entity to save
     * @return the persisted entity
     */
    @Override
    public OccupationSynonym save(OccupationSynonym occupationSynonym) {
        log.debug("Request to save OccupationSynonym : {}", occupationSynonym);
        OccupationSynonym result = occupationSynonymRepository.save(occupationSynonym);
        occupationSynonymSearchRepository.save(occupationSynonymMapper.toOccupationSuggestion(occupationSynonym));
        return result;
    }

    /**
     * Get all the occupationSynonyms.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OccupationSynonym> findAll(Pageable pageable) {
        log.debug("Request to get all OccupationSynonyms");
        return occupationSynonymRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationSynonym> findOne(UUID id) {
        log.debug("Request to get OccupationSynonym : {}", id);
        return occupationSynonymRepository.findById(id);
    }

    /**
     * Delete the occupationSynonym by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete OccupationSynonym : {}", id);
        occupationSynonymRepository.findById(id).ifPresent(
            occupation -> {
                occupationSynonymRepository.delete(occupation);
                occupationSynonymSearchRepository.deleteAllByCodeEquals(occupation.getCode());
            }
        );
    }

    /**
     * Get one occupationMapping by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationMapping> findOneOccupationMapping(UUID id) {
        log.debug("Request to get OccupationMapping : {}", id);
        return occupationMappingRepository.findById(id);
    }

    /**
     * Get all the occupationMappings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OccupationMapping> findAllOccupationMappings(Pageable pageable) {
        log.debug("Request to get all OccupationMappings");
        return occupationMappingRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationDto> findOneOccupationByCode(int code, Language language) {
        log.debug("Request to get OccupationMapping : code:{}", code);
        return occupationRepository.findOneByCode(code)
            .map(toOccupationDto(language));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationDto> findOneOccupationByAvamCode(int avamCode, Language language) {
        log.debug("Request to get OccupationMapping : avamCode:{}", avamCode);
        return occupationMappingRepository.findByAvamCode(avamCode)
            .stream()
            .findFirst()
            .flatMap(occupationMappingToOccupation)
            .map(toOccupationDto(language));

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationDto> findOneOccupationByX28Code(int x28Code, Language language) {
        log.debug("Request to get OccupationMapping : x28Code:{}", x28Code);
        return occupationMappingRepository.findByX28Code(x28Code)
            .flatMap(occupationMappingToOccupation)
            .map(toOccupationDto(language));
    }

    @Transactional(readOnly = true)
    @Override
    public OccupationAutocompleteDto suggest(String prefix, Language language, boolean includeSynonyms, int resultSize) {
        return occupationSuggestion.suggest(prefix, language, includeSynonyms, resultSize);
    }

    /**
     * Get one occupationSynonym by id.
     *
     * @param externalId the externalId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OccupationSynonym> findOneOccupationSynonymByExternalId(int externalId) {
        log.debug("Request to get OccupationSynonym : {}", externalId);
        return occupationSynonymRepository.findByExternalId(externalId);
    }

    @Override
    public List<OccupationSynonym> save(Collection<OccupationSynonym> occupationSynonyms) {
        occupationSynonyms.stream()
            .filter(item -> isNull(item.getId()))
            .forEach(occupationSynonym -> {
                occupationSynonymRepository.findByExternalId(occupationSynonym.getExternalId())
                    .ifPresent(item ->
                        occupationSynonym.setId(item.getId())
                    );

            });
        return occupationSynonymRepository.saveAll(occupationSynonyms);
    }

    @Override
    public List<Occupation> saveOccupations(Collection<Occupation> occupations) {
        occupations.stream()
            .filter(item -> isNull(item.getId()))
            .forEach(occupation -> {
                occupationRepository.findOneByCode(occupation.getCode())
                    .ifPresent(item ->
                        occupation.setId(item.getId())
                    );

            });
        return occupationRepository.saveAll(occupations);
    }

    private Function<Occupation, OccupationDto> toOccupationDto(Language language) {
        return occupation -> new OccupationDto()
            .id(occupation.getId())
            .code(occupation.getCode())
            .classificationCode(occupation.getClassificationCode())
            .labels(occupation.getMaleLabels().get(language), occupation.getFemaleLabels().get(language));
    }

    @PostConstruct
    private void init() {
        occupationServiceImpl = applicationContext.getBean(OccupationServiceImpl.class);
    }
}
