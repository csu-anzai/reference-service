package ch.admin.seco.service.reference.web.rest;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.OccupationLabelMapping;
import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.service.OccupationLabelService;
import ch.admin.seco.service.reference.service.dto.OccupationLabelAutocompleteDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelDto;
import ch.admin.seco.service.reference.service.dto.OccupationLabelSearchRequestDto;
import ch.admin.seco.service.reference.service.dto.ProfessionCodeDTO;

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
        @RequestParam String prefix, @RequestParam Collection<ProfessionCodeType> types, @RequestParam int resultSize) {
        log.debug("REST request to suggest for a page of OccupationSynonyms for query {}", prefix);
        OccupationLabelAutocompleteDto result = occupationService.suggest(prefix, getLanguage(), types, resultSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/_search/occupations/label/{codeType}")
    @Timed
    public Page<OccupationLabel> searchOccupation(@PathVariable ProfessionCodeType codeType,
        @RequestParam(required = false) String prefix, Pageable pageable) {
        log.debug("REST request to search {} occupations labels with prefix `{}` and page `{}`", codeType, prefix, pageable);
        return occupationService.search(new OccupationLabelSearchRequestDto(codeType, prefix, pageable), getLanguage());
    }

    @GetMapping("/occupations/label/mapped-by/{codeType}/{code}")
    @Timed
    public List<OccupationLabelDto> getOccupationLabelsForClassification(ProfessionCodeDTO professionCode) {
        log.debug("REST request to search occupation belong to classification {}:{}", professionCode.getCodeType(), professionCode.getCode());
        return occupationService.getOccupationLabelsByClassification(professionCode, getLanguage());
    }

    @GetMapping("/occupations/label/{codeType}/{code}")
    @Timed
    public ResponseEntity<Map<String, String>> getOccupationLabels(ProfessionCodeDTO professionCode) {
        return ResponseEntity.ok()
            .headers(createCacheHeader())
            .body(occupationService.getOccupationLabels(professionCode, getLanguage()));
    }

    @GetMapping("/occupations/label/all-languages/{codeType}/{code}")
    @Timed
    public ResponseEntity<Map<String, Map<String, String>>> getOccupationLabelsInAllLanguages(
        ProfessionCodeDTO professionCode) {
        return ResponseEntity.ok()
            .headers(createCacheHeader())
            .body(occupationService.getOccupationLabels(professionCode));
    }

    @GetMapping("/occupations/label/{codeType}/{code}/{classifier}")
    @Timed
    public ResponseEntity<Map<String, String>> getOccupationLabels(ProfessionCodeDTO professionCode,
        @PathVariable String classifier) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.getOccupationLabels(professionCode, getLanguage(), classifier), createCacheHeader());
    }

    @GetMapping("/occupations/label/mapping/{codeType}/{code}")
    @Timed
    public ResponseEntity<OccupationLabelMapping> getOccupationMapping(ProfessionCodeDTO professionCode) {
        return ResponseUtil.wrapOrNotFound(
            occupationService.findOneOccupationMapping(professionCode), createCacheHeader());
    }

    private Language getLanguage() {
        return Language.safeValueOf(getLocale().getLanguage());
    }

    private HttpHeaders createCacheHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic().getHeaderValue());
        return httpHeaders;
    }
}
