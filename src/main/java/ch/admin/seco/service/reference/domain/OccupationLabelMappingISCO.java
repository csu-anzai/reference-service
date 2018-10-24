package ch.admin.seco.service.reference.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "occupation_label_mapping_isco")
public class OccupationLabelMappingISCO {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NaturalId
    @NotNull
    @Column(name = "bfs_code", nullable = false, unique = true)
    private String bfsCode;

    @NotNull
    @Column(name = "isco_code", nullable = false)
    private String iscoCode;

    public OccupationLabelMappingISCO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBfsCode() {
        return bfsCode;
    }

    public void setBfsCode(String bfsCode) {
        this.bfsCode = bfsCode;
    }

    public String getIscoCode() {
        return iscoCode;
    }

    public void setIscoCode(String iscoCode) {
        this.iscoCode = iscoCode;
    }
}
