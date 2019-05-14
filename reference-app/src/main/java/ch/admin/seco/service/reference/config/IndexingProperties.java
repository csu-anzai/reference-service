package ch.admin.seco.service.reference.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("alv.referenceservice.indexing")
public class IndexingProperties {

    private boolean reindexOnStart = true;

    public boolean isReindexOnStart() {
        return reindexOnStart;
    }

    public void setReindexOnStart(boolean reindexOnStart) {
        this.reindexOnStart = reindexOnStart;
    }
}
