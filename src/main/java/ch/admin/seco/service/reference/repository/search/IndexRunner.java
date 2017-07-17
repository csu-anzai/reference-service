package ch.admin.seco.service.reference.repository.search;

import ch.admin.seco.service.reference.service.ElasticsearchIndexService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class IndexRunner implements CommandLineRunner {

    private final ElasticsearchIndexService elasticsearchIndexService;

    IndexRunner(ElasticsearchIndexService elasticsearchIndexService) {
        this.elasticsearchIndexService = elasticsearchIndexService;
    }

    @Override
    public void run(String... args) throws Exception {
        elasticsearchIndexService.reindexAll();
    }
}
