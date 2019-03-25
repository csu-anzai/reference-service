package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.LocalityRepository;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.IsAdmin;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;
import ch.admin.seco.service.reference.service.search.LocalitySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Locality.
 */
@Service
@Transactional
public class LocalityServiceImpl implements LocalityService {

    private final Logger log = LoggerFactory.getLogger(LocalityServiceImpl.class);

    private final LocalityRepository localityRepository;
    private final LocalitySearchRepository localitySynonymSearchRepository;
    private final LocalityToSuggestionMapper entityToSynonymMapper;
    private final LocalitySuggestionImpl localitySuggestion;

    public LocalityServiceImpl(LocalityRepository localityRepository,
        LocalitySearchRepository localitySynonymSearchRepository,
        LocalityToSuggestionMapper entityToSynonymMapper,
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
    @IsAdmin
    public Locality save(Locality locality) {
        log.debug("Request to save Locality : {}", locality);
        Locality result = localityRepository.save(locality);
        index(locality);
        return result;
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
    @IsAdmin
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
     *
     * @param localitySearchDTO suggest request parameters
     * @return the result of the suggest
     */
    @Override
    public LocalityAutocompleteDto suggest(LocalitySearchDto localitySearchDTO) {
        return localitySuggestion.suggest(localitySearchDTO);
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
    public List<Locality> findByZipCode(String zipCode) {
        return localityRepository.findByZipCode(zipCode);
    }

    @Override
    public Optional<Locality> findByZipCodeAndCity(String zipCode, String city) {
        return localitySuggestion.findByZipCodeAndCity(zipCode, city);
    }

    private void index(Locality locality) {
        localitySynonymSearchRepository.index(entityToSynonymMapper.toLocalitySuggestion(locality));
    }
}
