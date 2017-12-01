package ch.admin.seco.service.reference.web.rest;

import static java.lang.Integer.parseInt;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;

/**
 * REST controller for managing OccupationSynonym.
 */
@RestController
@RequestMapping("/api")
public class OccupationLabelResource {

    private final Logger log = LoggerFactory.getLogger(OccupationLabelResource.class);
    private final OccupationLabelService occupationService;

    public OccupationLabelResource(OccupationLabelService occupationService) {
        this.occupationService = occupationService;
    }

    /**
     * SEARCH  /_search/occupations/synonym?query=:query : suggest for the occupationSynonym corresponding
     * to the query.
     *
     * @param prefix          the query of the occupationSynonym suggest
     * @param types include   search only within types code tables
     * @param resultSize      the resultSize information
     * @return the result of the suggest
     */
    @GetMapping("/_search/occupations/label")
    @Timed
    public ResponseEntity<OccupationLabelAutocompleteDto> suggestOccupation(
        @RequestParam String prefix, @RequestParam Collection<String> types, @RequestParam int resultSize) {
        log.debug("REST request to suggest for a page of OccupationSynonyms for query {}", prefix);
        OccupationLabelAutocompleteDto result = occupationService.suggest(prefix, getLanguage(), types, resultSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/occupations/label", params = {"code", "type"})
    @Timed
    public ResponseEntity<Map<String, String>> getOccupationLabelsByCodeAndType(@RequestParam int code, @RequestParam String type) {
        return ResponseEntity.ok()
            .headers(createCacheHeader())
            .body(occupationService.getOccupationLabels(code, type, getLanguage()));
    }

    @GetMapping(value = "/occupations/label", params = {"code", "type", "classifier"})
    @Timed
    public ResponseEntity<Map<String, String>> getOccupationLabelsByCodeAndTypeAndClassifier(@RequestParam int code, @RequestParam String type, @RequestParam String classifier) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.getOccupationLabels(code, type, getLanguage(), classifier), createCacheHeader());
    }

    // /occupations/label/avam:15012
    // /occupations/label/bfs:11101001
    // /occupations/label/x28:11000002:23f1f3e3
    @GetMapping("/occupations/label/{key}")
    @Timed
    public ResponseEntity<Map<String, String>> getOccupationLabelsByKey(@PathVariable String key) {

        String[] parts = key.split(":");
        switch (parts.length) {
            case 2:
                return getOccupationLabelsByCodeAndType(parseInt(parts[1]), parts[0]);
            case 3:
                return getOccupationLabelsByCodeAndTypeAndClassifier(parseInt(parts[1]), parts[0], parts[2]);
            default:
                return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/occupations/label/mapping", params = "avamCode")
    @Timed
    public ResponseEntity<OccupationLabelMapping> getOccupationMappingByAvamCode(@RequestParam int avamCode) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationMappingByAvamCode(avamCode), createCacheHeader());
    }

    @GetMapping(value = "/occupations/label/mapping", params = "bfsCode")
    @Timed
    public ResponseEntity<OccupationLabelMapping> getOccupationMappingByBFSCode(@RequestParam int bfsCode) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationMappingByBfsCode(bfsCode), createCacheHeader());
    }

    @GetMapping(value = "/occupations/label/mapping", params = "x28Code")
    @Timed
    public ResponseEntity<OccupationLabelMapping> getOccupationMappingByX28Code(@RequestParam int x28Code) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationMappingByX28Code(x28Code), createCacheHeader());
    }

    private Language getLanguage() {
        return Language.safeValueOf(getLocale().getLanguage());
    }

    private HttpHeaders createCacheHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES).cachePublic().getHeaderValue());
        return httpHeaders;
    }
}
