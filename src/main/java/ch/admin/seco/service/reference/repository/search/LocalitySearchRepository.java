package ch.admin.seco.service.reference.repository.search;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.Locality;

/**
 * Spring Data Elasticsearch repository for the Locality entity.
 */
public interface LocalitySearchRepository extends ElasticsearchRepository<Locality, UUID> {
}
