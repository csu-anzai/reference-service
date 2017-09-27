package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.repository.ClassificationRepository;
import ch.admin.seco.service.reference.repository.OccupationMappingRepository;
import ch.admin.seco.service.reference.repository.OccupationRepository;
import ch.admin.seco.service.reference.repository.OccupationSynonymRepository;
import ch.admin.seco.service.reference.repository.search.OccupationSynonymSearchRepository;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationSuggestionDto;

/**
 * Service Implementation for managing Occupation.
 */
@Service
@Transactional
public class OccupationServiceImpl implements OccupationService {

    private final Logger log = LoggerFactory.getLogger(OccupationServiceImpl.class);
    private final ApplicationContext applicationContext;
    private final OccupationSynonymRepository occupationSynonymRepository;
    private final OccupationSynonymSearchRepository occupationSynonymSearchRepository;
    private final EntityToSynonymMapper occupationSynonymMapper;
    private final OccupationMappingRepository occupationMappingRepository;
    private final OccupationRepository occupationRepository;
    private final ClassificationRepository classificationRepository;
    private final OccupationSuggestionImpl occupationSuggestion;
    private final Function<OccupationMapping, Optional<Occupation>> occupationMappingToOccupation;
    private OccupationServiceImpl occupationServiceImpl;

    public OccupationServiceImpl(ApplicationContext applicationContext,
        OccupationSynonymRepository occupationSynonymRepository,
        OccupationSynonymSearchRepository occupationSynonymSearchRepository,
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
        occupationSynonymSearchRepository.save(occupationSynonymMapper.toSuggestion(occupationSynonym));
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
    public Optional<Occupation> findOneOccupationByCode(int code) {
        log.debug("Request to get OccupationMapping : code:{}", code);
        return occupationRepository.findOneByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOneOccupationByAvamCode(int avamCode) {
        log.debug("Request to get OccupationMapping : avamCode:{}", avamCode);
        return occupationMappingRepository.findByAvamCode(avamCode)
            .stream()
            .findFirst()
            .flatMap(occupationMappingToOccupation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Occupation> findOneOccupationByX28Code(int x28Code) {
        log.debug("Request to get OccupationMapping : x28Code:{}", x28Code);
        return occupationMappingRepository.findByX28Code(x28Code)
            .flatMap(occupationMappingToOccupation);
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

    @Transactional(readOnly = true)
    @Override
    public OccupationAutocompleteDto suggest(String prefix, Language language, int resultSize) {
        return occupationSuggestion.suggest(prefix, language, resultSize);
    }

    private Stream<Classification> getClassificationsFromOccupatonAsStream(List<OccupationSuggestionDto> occupations) {
        List<Integer> occupationCodes = occupations.stream()
            .map(occupationSuggestionDto -> occupationSuggestionDto.getCode())
            .collect(Collectors.toList());
        return classificationRepository.findAllByOccupationCodes(occupationCodes);
    }

    @PostConstruct
    private void init() {
        occupationServiceImpl = applicationContext.getBean(OccupationServiceImpl.class);
    }
}
