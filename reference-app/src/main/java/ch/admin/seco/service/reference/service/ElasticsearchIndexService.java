package ch.admin.seco.service.reference.service;

import io.micrometer.core.annotation.Timed;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface ElasticsearchIndexService {
    @Async
    @Timed
    @Transactional(readOnly = true)
    void reindexAll();
}
