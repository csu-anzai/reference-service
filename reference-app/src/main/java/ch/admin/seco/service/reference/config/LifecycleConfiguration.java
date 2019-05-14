package ch.admin.seco.service.reference.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;

@Component
@EnableConfigurationProperties(IndexingProperties.class)
public class LifecycleConfiguration {

    private final ElasticsearchIndexService elasticsearchIndexService;

    private final IndexingProperties indexingProperties;

    LifecycleConfiguration(ElasticsearchIndexService elasticsearchIndexService, IndexingProperties indexingProperties) {
        this.elasticsearchIndexService = elasticsearchIndexService;
        this.indexingProperties = indexingProperties;
    }

    @EventListener
    public void handleContextRefresh(ApplicationReadyEvent event) {
        if (this.indexingProperties.isReindexOnStart()) {
            elasticsearchIndexService.reindexAll();
        }
    }
}
