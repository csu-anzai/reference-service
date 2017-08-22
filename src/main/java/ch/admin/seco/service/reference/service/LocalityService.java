package ch.admin.seco.service.reference.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.dto.LocalitySuggestionDto;

/**
 * Service Interface for managing Locality.
 */
public interface LocalityService {

    /**
     * Save a locality.
     *
     * @param locality the entity to save
     * @return the persisted entity
     */
    Locality save(Locality locality);

    /**
     *  Get all the localities.
     *
     *  @return the list of entities
     */
    List<Locality> findAll();

    /**
     *  Get the "id" locality.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Optional<Locality> findOne(UUID id);

    /**
     *  Delete the "id" locality.
     *
     *  @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Search for the locality corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<Locality> search(String query);

    /**
     * Search for the nearest locality to the geo point.
     *
     * @param geoPoint the geo point from which to search
     * @return nearest entity to geo point
     */
    Optional<Locality> searchNearestLocality(GeoPoint geoPoint);

    List<LocalitySuggestionDto> suggestLocalities(String prefix, int resultSize);
}
