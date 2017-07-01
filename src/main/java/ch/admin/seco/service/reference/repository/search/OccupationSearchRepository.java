package ch.admin.seco.service.reference.repository.search;

import ch.admin.seco.service.reference.domain.Occupation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for the Occupation entity.
 */
public interface OccupationSearchRepository extends ElasticsearchRepository<Occupation, UUID> {
}
