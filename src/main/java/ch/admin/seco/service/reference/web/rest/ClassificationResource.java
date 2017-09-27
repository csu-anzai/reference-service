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

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;
import ch.admin.seco.service.reference.service.ClassificationService;
import ch.admin.seco.service.reference.web.rest.util.HeaderUtil;
import ch.admin.seco.service.reference.web.rest.util.PaginationUtil;

/**
 * REST controller for managing Classification.
 */
@RestController
@RequestMapping("/api")
public class ClassificationResource {

    private static final String ENTITY_NAME = "classification";
    private final Logger log = LoggerFactory.getLogger(ClassificationResource.class);
    private final ClassificationService classificationService;

    public ClassificationResource(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    /**
     * POST  /classifications : Create a new classification.
     *
     * @param classification the classification to create
     * @return the ResponseEntity with status 201 (Created) and with body the new classification, or with status 400 (Bad Request) if the classification has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/classifications")
    @Timed
    public ResponseEntity<ch.admin.seco.service.reference.domain.Classification> createClassification(@Valid @RequestBody ch.admin.seco.service.reference.domain.Classification classification) throws URISyntaxException {
        log.debug("REST request to save Classification : {}", classification);
        if (classification.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new classification cannot already have an ID")).body(null);
        }
        ch.admin.seco.service.reference.domain.Classification result = classificationService.save(classification);
        return ResponseEntity.created(new URI("/api/classifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /classifications : Updates an existing classification.
     *
     * @param classification the classification to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated classification,
     * or with status 400 (Bad Request) if the classification is not valid,
     * or with status 500 (Internal Server Error) if the classification couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/classifications")
    @Timed
    public ResponseEntity<ch.admin.seco.service.reference.domain.Classification> updateClassification(@Valid @RequestBody ch.admin.seco.service.reference.domain.Classification classification) throws URISyntaxException {
        log.debug("REST request to update Classification : {}", classification);
        if (classification.getId() == null) {
            return createClassification(classification);
        }
        ch.admin.seco.service.reference.domain.Classification result = classificationService.save(classification);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, classification.getId().toString()))
            .body(result);
    }

    /**
     * GET  /classifications : get all the classifications.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of classifications in body
     */
    @GetMapping("/classifications")
    @Timed
    public ResponseEntity<List<Classification>> getAllClassifications(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Classification");
        Page<Classification> page = classificationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/classifications");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * GET  /classifications/:id : get the "id" classification.
     *
     * @param id the id of the classification to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the classification, or with status 404 (Not Found)
     */
    @GetMapping("/classifications/{id}")
    @Timed
    public ResponseEntity<ch.admin.seco.service.reference.domain.Classification> getClassification(@PathVariable UUID id) {
        log.debug("REST request to get Classification : {}", id);
        Optional<ch.admin.seco.service.reference.domain.Classification> classification = classificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(classification);
    }

    /**
     * DELETE  /classifications/:id : delete the "id" classification.
     *
     * @param id the id of the classification to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/classifications/{id}")
    @Timed
    public ResponseEntity<Void> deleteClassification(@PathVariable UUID id) {
        log.debug("REST request to delete Classification : {}", id);
        classificationService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/classifications?query=:query : suggest for the classification corresponding
     * to the query.
     *
     * @param query the query of the classification suggest
     * @return the result of the suggest
     */
    @GetMapping("/_search/classifications")
    @Timed
    public List<ClassificationSuggestion> suggestClassification(@RequestParam String query) {
        log.debug("REST request to suggest Classifications for query {}", query);
        return classificationService.search(query);
    }

}
