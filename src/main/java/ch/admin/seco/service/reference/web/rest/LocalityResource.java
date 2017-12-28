package ch.admin.seco.service.reference.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.Locality;
import ch.admin.seco.service.reference.domain.valueobject.GeoPoint;
import ch.admin.seco.service.reference.service.LocalityService;
import ch.admin.seco.service.reference.service.dto.LocalityAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.LocalitySearchDto;
import ch.admin.seco.service.reference.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Locality.
 */
@RestController
@RequestMapping("/api")
public class LocalityResource {

    private static final String ENTITY_NAME = "locality";
    private final Logger log = LoggerFactory.getLogger(LocalityResource.class);
    private final LocalityService localityService;

    public LocalityResource(LocalityService localityService) {
        this.localityService = localityService;
    }

    /**
     * POST  /localities : Create a new locality.
     *
     * @param locality the locality to create
     * @return the ResponseEntity with status 201 (Created) and with body the new locality, or with status 400 (Bad Request) if the locality has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/localities")
    @Timed
    public ResponseEntity<Locality> createLocality(@Valid @RequestBody Locality locality) throws URISyntaxException {
        log.debug("REST request to save Locality : {}", locality);
        if (locality.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new locality cannot already have an ID")).body(null);
        }
        Locality result = localityService.save(locality);
        return ResponseEntity.created(new URI("/api/localities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /localities : Updates an existing locality.
     *
     * @param locality the locality to saveOccupations
     * @return the ResponseEntity with status 200 (OK) and with body the updated locality,
     * or with status 400 (Bad Request) if the locality is not valid,
     * or with status 500 (Internal Server Error) if the locality couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/localities")
    @Timed
    public ResponseEntity<Locality> updateLocality(@Valid @RequestBody Locality locality) throws URISyntaxException {
        log.debug("REST request to saveOccupations Locality : {}", locality);
        if (locality.getId() == null) {
            return createLocality(locality);
        }
        Locality result = localityService.save(locality);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, locality.getId().toString()))
            .body(result);
    }

    /**
     * GET  /localities : get all the localities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of localities in body
     */
    @GetMapping("/localities")
    @Timed
    public List<Locality> getAllLocalities() {
        log.debug("REST request to get all Localities");
        return localityService.findAll();
    }

    /**
     * GET  /localities/:id : get the "id" locality.
     *
     * @param id the uuid of the locality to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the locality, or with status 404 (Not Found)
     */
    @GetMapping("/localities/{id}")
    @Timed
    public ResponseEntity<Locality> getLocality(@PathVariable UUID id) {
        log.debug("REST request to get Locality : {}", id);
        return ResponseUtil.wrapOrNotFound(localityService.findOne(id));
    }

    /**
     * DELETE  /localities/:id : delete the "id" locality.
     *
     * @param id the uuid of the locality to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/localities/{id}")
    @Timed
    public ResponseEntity<Void> deleteLocality(@PathVariable UUID id) {
        log.debug("REST request to delete Locality : {}", id);
        localityService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/localities?prefix=:prefix&resultSize=:resultSize&distinctByLocalityCity=:distinctByLocalityCity :
     * suggest for the locality corresponding to the prefix and limit result by resultSize.
     *
     * @param prefix the prefix of the locality suggest
     * @param resultSize the resultSize information
     * @param distinctByLocalityCity indicates is localities should be distinct by city if true
     *                               otherwise distinct by city + zipCode
     * @return the result of the suggest
     */
    @GetMapping("/_search/localities")
    @Timed
    public LocalityAutocompleteDto suggestLocality(@RequestParam String prefix, @RequestParam int resultSize,
        @RequestParam(defaultValue = "false") boolean distinctByLocalityCity) {
        log.debug("REST request to suggest Localities for prefix {}, resultSize {}", prefix, resultSize);
        return localityService.suggest(new LocalitySearchDto(prefix, resultSize, distinctByLocalityCity));
    }

    /**
     * SEARCH  /_search/localities/nearest : suggest for the nearest
     * locality to corresponding coordinates.
     *
     * @param latitude the latitude for which obtain nearest locality
     * @param longitude the longitude for which obtain nearest locality
     * @return the ResponseEntity with status 200 (OK) and with body the locality, or with status 404 (Not Found)
     */
    @GetMapping("/_search/localities/nearest")
    @Timed
    public ResponseEntity<Locality> searchNearestLocality(@RequestParam Double latitude, @RequestParam Double longitude) {
        log.debug("REST request to suggest Locality nearest to geo point (latitude={}, longitude={})", latitude, longitude);
        return ResponseUtil.wrapOrNotFound(localityService.findNearestLocality(new GeoPoint(latitude, longitude)));
    }

    @GetMapping(value = "/localities", params = "zipCode")
    @Timed
    public List<Locality> findLocalitiesByZipCode(@RequestParam String zipCode) {
        log.debug("REST request to suggest Locality by zipCode({})", zipCode);
        return localityService.findByZipCode(zipCode);
    }
}
