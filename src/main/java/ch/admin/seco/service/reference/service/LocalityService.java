package ch.admin.seco.service.reference.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;

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
     * Search for the locality corresponding
     * to the prefix and limit result by resultSize.
     *
     * @param localitySearchDTO suggest request parameters
     * @return the result of the suggest
     */
    LocalityAutocompleteDto suggest(LocalitySearchDto localitySearchDTO);

    /**
     * Search for the nearest locality to the geo point.
     *
     * @param geoPoint the geo point from which to suggest
     * @return nearest entity to geo point
     */
    Optional<Locality> findNearestLocality(GeoPoint geoPoint);

    List<Locality> findByZipCode(String zipCode);
}
