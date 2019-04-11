package ch.admin.seco.service.reference.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;

@Component
public class LifecycleConfiguration {

    private final ElasticsearchIndexService elasticsearchIndexService;

    LifecycleConfiguration(ElasticsearchIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @EventListener
    public void handleContextRefresh(ApplicationReadyEvent event) {
        elasticsearchIndexService.reindexAll();
    }
}
