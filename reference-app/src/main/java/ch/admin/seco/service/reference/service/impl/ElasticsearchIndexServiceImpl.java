package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ElasticsearchIndexServiceImpl implements ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexServiceImpl.class);
    private final ElasticsearchLocalityIndexer elasticsearchLocalityIndexer;
    private final ElasticsearchOccupationLabelIndexer elasticsearchOccupationLabelIndexer;
    private final CacheManager cacheManager;

    ElasticsearchIndexServiceImpl(ElasticsearchLocalityIndexer elasticsearchLocalityIndexer,
            ElasticsearchOccupationLabelIndexer elasticsearchOccupationLabelIndexer, CacheManager cacheManager) {

        this.elasticsearchLocalityIndexer = elasticsearchLocalityIndexer;
        this.elasticsearchOccupationLabelIndexer = elasticsearchOccupationLabelIndexer;
        this.cacheManager = cacheManager;
    }

    public void reindexAll() {

        elasticsearchLocalityIndexer.reindexLocalities();
        elasticsearchOccupationLabelIndexer.reindexOccupationLabel();

        evictAllCaches();

        log.info("Elasticsearch: Successfully performed reindexing of reference data.");
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames().stream()
            .map(cacheManager::getCache).filter(Objects::nonNull)
                .forEach(Cache::clear);
    }
}
