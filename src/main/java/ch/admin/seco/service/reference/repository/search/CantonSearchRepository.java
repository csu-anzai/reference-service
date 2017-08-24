package ch.admin.seco.service.reference.repository.search;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.Canton;

/**
 * Spring Data Elasticsearch repository for the Canton entity.
 */
public interface CantonSearchRepository extends ElasticsearchRepository<Canton, UUID> {
}
