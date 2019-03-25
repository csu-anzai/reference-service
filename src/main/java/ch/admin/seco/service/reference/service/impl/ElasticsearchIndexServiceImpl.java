package ch.admin.seco.service.reference.service.impl;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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

        CompletableFuture.allOf(
                CompletableFuture.runAsync(elasticsearchLocalityIndexer::reindexLocalities),
                CompletableFuture.runAsync(elasticsearchOccupationLabelIndexer::reindexOccupationLabel)
        ).join();

        evictAllCaches();

        log.info("ReindexAll finished");
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames().stream()
            .map(cacheManager::getCache).filter(Objects::nonNull)
                .forEach(Cache::clear);
    }
}
