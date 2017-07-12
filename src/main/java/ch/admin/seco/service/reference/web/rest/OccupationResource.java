package ch.admin.seco.service.reference.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
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
import ch.admin.seco.service.reference.service.OccupationService;
import ch.admin.seco.service.reference.service.dto.OccupationAutocompleteDto;
import ch.admin.seco.service.reference.web.rest.util.HeaderUtil;
import ch.admin.seco.service.reference.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Occupation.
 */
@RestController
@RequestMapping("/api")
public class OccupationResource {

    private static final String ENTITY_NAME = "occupation";
    private final Logger log = LoggerFactory.getLogger(OccupationResource.class);
    private final OccupationService occupationService;

    public OccupationResource(OccupationService occupationService) {
        this.occupationService = occupationService;
    }

    /**
     * POST  /occupations : Create a new occupation.
     *
     * @param occupation the occupation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new occupation, or with status 400 (Bad Request) if the occupation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/occupations")
    @Timed
    public ResponseEntity<Occupation> createOccupation(@Valid @RequestBody Occupation occupation) throws URISyntaxException {
        log.debug("REST request to save Occupation : {}", occupation);
        if (occupation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new occupation cannot already have an ID")).body(null);
        }
        Occupation result = occupationService.save(occupation);
        return ResponseEntity.created(new URI("/api/occupations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /occupations : Updates an existing occupation.
     *
     * @param occupation the occupation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated occupation,
     * or with status 400 (Bad Request) if the occupation is not valid,
     * or with status 500 (Internal Server Error) if the occupation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/occupations")
    @Timed
    public ResponseEntity<Occupation> updateOccupation(@Valid @RequestBody Occupation occupation) throws URISyntaxException {
        log.debug("REST request to update Occupation : {}", occupation);
        if (occupation.getId() == null) {
            return createOccupation(occupation);
        }
        Occupation result = occupationService.save(occupation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, occupation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /occupations : get all the occupations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of occupations in body
     */
    @GetMapping("/occupations")
    @Timed
    public ResponseEntity<List<Occupation>> getAllOccupations(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Occupations");
        Page<Occupation> page = occupationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/occupations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /occupations/:id : get the "id" occupation.
     *
     * @param id the id of the occupation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the occupation, or with status 404 (Not Found)
     */
    @GetMapping("/occupations/{id}")
    @Timed
    public ResponseEntity<Occupation> getOccupation(@PathVariable UUID id) {
        log.debug("REST request to get Occupation : {}", id);
        Optional<Occupation> occupation = occupationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(occupation);
    }

    /**
     * DELETE  /occupations/:id : delete the "id" occupation.
     *
     * @param id the id of the occupation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/occupations/{id}")
    @Timed
    public ResponseEntity<Void> deleteOccupation(@PathVariable UUID id) {
        log.debug("REST request to delete Occupation : {}", id);
        occupationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/occupations?query=:query : search for the occupation corresponding
     * to the query.
     *
     * @param prefix       the query of the occupation search
     * @param language     the language information
     * @param responseSize the responseSize information
     * @return the result of the search
     */
    @GetMapping("/_search/occupations")
    @Timed
    public ResponseEntity<OccupationAutocompleteDto> searchOccupations(
        @RequestParam String prefix, @RequestParam Language language, @RequestParam int responseSize) {
        log.debug("REST request to search for a page of Occupations for query {}", prefix);
        OccupationAutocompleteDto result = occupationService.suggestOccupations(prefix, language, responseSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
