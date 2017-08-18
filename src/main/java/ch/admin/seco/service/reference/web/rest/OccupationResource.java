package ch.admin.seco.service.reference.web.rest;

import static java.util.Objects.isNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import ch.admin.seco.service.reference.domain.Language;
import ch.admin.seco.service.reference.domain.Occupation;
import ch.admin.seco.service.reference.domain.OccupationMapping;
import ch.admin.seco.service.reference.domain.OccupationSynonym;
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.web.rest.util.HeaderUtil;
import ch.admin.seco.service.reference.web.rest.util.PaginationUtil;

/**
 * REST controller for managing OccupationSynonym.
 */
@RestController
@RequestMapping("/api")
public class OccupationResource {

    private static final String OCCUPATION_SYNONYM_ENTITY = "occupationSynonym";
    private static final String OCCUPATION_SYNONYM_PATH = "/occupations/synonym";
    private static final String OCCUPATION_MAPPING_PATH = "/occupations/mapping";
    private static final String OCCUPATION_SEARCH_PATH = "/_search" + OCCUPATION_SYNONYM_PATH;

    private final Logger log = LoggerFactory.getLogger(OccupationResource.class);
    private final OccupationService occupationService;

    public OccupationResource(OccupationService occupationService) {
        this.occupationService = occupationService;
    }

    /**
     * POST  /occupations/synonym : Create a new occupationSynonym.
     *
     * @param occupationSynonym the occupationSynonym to create
     * @return the ResponseEntity with status 201 (Created) and with body the new occupationSynonym, or with status 400 (Bad Request) if the occupationSynonym has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping(OCCUPATION_SYNONYM_PATH)
    @Timed
    public ResponseEntity<OccupationSynonym> createOccupationSynonym(@Valid @RequestBody OccupationSynonym occupationSynonym) throws URISyntaxException {
        log.debug("REST request to save OccupationSynonym : {}", occupationSynonym);
        if (occupationSynonym.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(OCCUPATION_SYNONYM_ENTITY, "idexists", "A new occupationSynonym cannot already have an ID")).body(null);
        }
        OccupationSynonym result = occupationService.save(occupationSynonym);
        return ResponseEntity.created(new URI("/api/occupations/synonym/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(OCCUPATION_SYNONYM_ENTITY, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /occupations/synonym : Updates an existing occupationSynonym.
     *
     * @param occupationSynonym the occupationSynonym to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated occupationSynonym,
     * or with status 400 (Bad Request) if the occupationSynonym is not valid,
     * or with status 500 (Internal Server Error) if the occupationSynonym couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping(OCCUPATION_SYNONYM_PATH)
    @Timed
    public ResponseEntity<OccupationSynonym> updateOccupationSynonym(@Valid @RequestBody OccupationSynonym occupationSynonym) throws URISyntaxException {
        log.debug("REST request to update OccupationSynonym : {}", occupationSynonym);

        if (isNull(occupationSynonym.getId())) {
            if (getOccupationSynonymByExternalId(occupationSynonym).isPresent()) {
                return createOccupationSynonym(occupationSynonym);
            }
        }

        OccupationSynonym result = occupationService.save(occupationSynonym);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(OCCUPATION_SYNONYM_ENTITY, occupationSynonym.getId().toString()))
            .body(result);
    }

    @PutMapping("/bulk" + OCCUPATION_SYNONYM_PATH)
    @Timed
    public ResponseEntity<Void> updateOccupationSynonyms(@Valid @RequestBody Collection<OccupationSynonym> occupationSynonyms) throws URISyntaxException {
        log.debug("Request to update OccupationSynonyms : {}", occupationSynonyms);
        occupationService.save(occupationSynonyms);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /occupations/synonym : get all the occupationSynonyms.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of occupationSynonym in body
     */
    @GetMapping(OCCUPATION_SYNONYM_PATH)
    @Timed
    public ResponseEntity<List<OccupationSynonym>> getAllOccupationSynonyms(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of OccupationSynonyms");
        Page<OccupationSynonym> page = occupationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/occupations/synonym");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /occupations/synonym/:id : get the "id" occupationSynonym.
     *
     * @param id the id of the occupation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the occupation, or with status 404 (Not Found)
     */
    @GetMapping(OCCUPATION_SYNONYM_PATH + "/{id}")
    @Timed
    public ResponseEntity<OccupationSynonym> getOccupationSynonym(@PathVariable UUID id) {
        log.debug("REST request to get OccupationSynonym : {}", id);
        Optional<OccupationSynonym> occupation = occupationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(occupation);
    }

    /**
     * DELETE  /occupations/synonym/:id : delete the "id" occupationSynonym.
     *
     * @param id the id of the occupationSynonym to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping(OCCUPATION_SYNONYM_PATH + "/{id}")
    @Timed
    public ResponseEntity<Void> deleteOccupationSynonym(@PathVariable UUID id) {
        log.debug("REST request to delete OccupationSynonym : {}", id);
        occupationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(OCCUPATION_SYNONYM_ENTITY, id.toString())).build();
    }

    /**
     * SEARCH  /_search/occupations/synonym?query=:query : search for the occupationSynonym corresponding
     * to the query.
     *
     * @param prefix       the query of the occupationSynonym search
     * @param language     the language information
     * @param responseSize the responseSize information
     * @return the result of the search
     */
    @GetMapping(OCCUPATION_SEARCH_PATH)
    @Timed
    public ResponseEntity<OccupationAutocompleteDto> searchOccupations(
        @RequestParam String prefix, @RequestParam Language language, @RequestParam int responseSize) {
        log.debug("REST request to search for a page of OccupationSynonyms for query {}", prefix);
        OccupationAutocompleteDto result = occupationService.suggestOccupationSynonyms(prefix, language, responseSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * GET  /occupations/mapping : get all the occupationMappings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of occupationMappings in body
     */
    @GetMapping(OCCUPATION_MAPPING_PATH)
    @Timed
    public ResponseEntity<List<OccupationMapping>> getAllOccupationMappings(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of OccupationMappings");
        Page<OccupationMapping> page = occupationService.findAllOccupationMappings(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/occupations/mapping");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /occupations/mapping/:id : get the "id" occupationMapping.
     *
     * @param id the id of the occupationMapping to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the occupationMapping, or with status 404 (Not Found)
     */
    @GetMapping(OCCUPATION_MAPPING_PATH + "/{id}")
    @Timed
    public ResponseEntity<OccupationMapping> getOccupationMapping(@PathVariable UUID id) {
        log.debug("REST request to get OccupationMapping : {}", id);
        Optional<OccupationMapping> occupationMapping = occupationService.findOneOccupationMapping(id);
        return ResponseUtil.wrapOrNotFound(occupationMapping);
    }

    @GetMapping(value = "/occupations", params = "code")
    @Timed
    public ResponseEntity<Occupation> getOccupationByCode(@RequestParam int code) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationByCode(code));
    }

    @GetMapping(value = "/occupations", params = "avamCode")
    @Timed
    public ResponseEntity<Occupation> getOccupationByAvamCode(@RequestParam int avamCode) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationByAvamCode(avamCode));
    }

    @GetMapping(value = "/occupations", params = "x28Code")
    @Timed
    public ResponseEntity<Occupation> getOccupationByX28Code(@RequestParam int x28Code) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationByX28Code(x28Code));
    }

    private Optional<OccupationSynonym> getOccupationSynonymByExternalId(OccupationSynonym occupationSynonym) {
        return occupationService.findOneOccupationSynonymByExternalId(occupationSynonym.getExternalId())
            .map(item -> {
                occupationSynonym.setId(item.getId());
                return occupationSynonym;
            });
    }
}
