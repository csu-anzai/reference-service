package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * A Canton.
 */
@Entity
@Table(name = "canton")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "localities", type = "canton")
@Mapping(mappingPath = "config/elasticsearch/mappings/canton.json")
@Setting(settingPath = "config/elasticsearch/settings/folding-analyzer.json")
public class Canton implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Size(max = 50)
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull
    @Size(max = 2)
    @Column(name = "code", length = 2, nullable = false, unique = true)
    private String code;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Canton id(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Canton name(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Canton code(String code) {
        this.code = code;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Canton canton = (Canton) o;
        if (canton.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), canton.getId());
    }

    @Override
    public String toString() {
        return "Canton{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
