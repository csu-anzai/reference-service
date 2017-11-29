package ch.admin.seco.service.reference.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.repository.LocalityRepository;
import ch.admin.seco.service.reference.repository.search.LocalitySearchRepository;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalityServiceImpl implements LocalityService {

    private final Logger log = LoggerFactory.getLogger(LocalityServiceImpl.class);

    private final LocalityRepository localityRepository;
    private final LocalitySearchRepository localitySynonymSearchRepository;
    private final EntityToSuggestionMapper entityToSynonymMapper;
    private final LocalitySuggestionImpl localitySuggestion;

    public LocalityServiceImpl(LocalityRepository localityRepository,
        LocalitySearchRepository localitySynonymSearchRepository,
        EntityToSuggestionMapper entityToSynonymMapper,
        LocalitySuggestionImpl localitySuggestion) {

        this.localityRepository = localityRepository;
        this.localitySynonymSearchRepository = localitySynonymSearchRepository;
        this.entityToSynonymMapper = entityToSynonymMapper;
        this.localitySuggestion = localitySuggestion;
    }

    /**
     * Save a locality.
     *
     * @param locality the entity to save
     * @return the persisted entity
     */
    @Override
    public Locality save(Locality locality) {
        log.debug("Request to save Locality : {}", locality);
        Locality result = localityRepository.save(locality);
        index(locality);
        return result;
    }

    /**
     *  Get all the localities.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Locality> findAll() {
        log.debug("Request to get all Localities");
        return localityRepository.findAll();
    }

    /**
     *  Get one locality by id.
     *
     *  @param id the uuid of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Locality> findOne(UUID id) {
        log.debug("Request to get Locality : {}", id);
        return localityRepository.findById(id);
    }

    /**
     *  Delete the  locality by id.
     *
     *  @param id the uuid of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Locality : {}", id);
        localityRepository.findById(id).ifPresent(
            locality -> {
                localityRepository.delete(locality);
                localitySynonymSearchRepository.deleteById(locality.getId());
            });
    }

    /**
     * Search for the locality corresponding
     * to the prefix and limit result by resultSize.
     *
     * @param prefix the prefix of the locality suggest
     * @param resultSize the response size information
     * @return the result of the suggest
     */
    @Override
    public LocalityAutocompleteDto suggest(String prefix, int resultSize) {
        return localitySuggestion.suggest(prefix, resultSize);
    }

    /**
     * Search for the nearest locality to the geo point.
     *
     * @param geoPoint the geo point from which to suggest
     * @return nearest entity to geo point
     */
    @Override
    public Optional<Locality> findNearestLocality(GeoPoint geoPoint) {
        return localitySuggestion.findNearestLocality(geoPoint);
    }

    @Override
    public List<Locality> findOneByZipCode(String zipCode) {
        return localityRepository.findByZipCode(zipCode);
    }

    @Async
    public void index(Locality locality) {
        localitySynonymSearchRepository.index(entityToSynonymMapper.toLocalitySuggestion(locality));
    }
}
