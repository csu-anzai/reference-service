package ch.admin.seco.service.reference.service.impl;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;

@Service
public class ElasticsearchIndexServiceImpl implements ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexServiceImpl.class);
    private final ElasticsearchIndexer elasticsearchIndexer;
    private final ElasticsearchOccupationLabelIndexer elasticsearchOccupationLabelIndexer;

    ElasticsearchIndexServiceImpl(ElasticsearchIndexer elasticsearchIndexServiceTasks,
        ElasticsearchOccupationLabelIndexer elasticsearchOccupationLabelIndexer) {

        this.elasticsearchIndexer = elasticsearchIndexServiceTasks;
        this.elasticsearchOccupationLabelIndexer = elasticsearchOccupationLabelIndexer;
    }

    public void reindexAll() {

        CompletableFuture.allOf(
            CompletableFuture.runAsync(elasticsearchIndexer::reindexLocalityIndex),
            CompletableFuture.runAsync(elasticsearchIndexer::reindexOccupationIndex),
            CompletableFuture.runAsync(elasticsearchOccupationLabelIndexer::reindexOccupationIndex)
        ).join();

        log.info("ReindexAll finished");
    }
}
