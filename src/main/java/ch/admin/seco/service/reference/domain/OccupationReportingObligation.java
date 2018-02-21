package ch.admin.seco.service.reference.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.converter.CantonCodesConverter;

@Entity
@Table(name = "occupation_reporting_obligation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OccupationReportingObligation implements Serializable {

    private static final long serialVersionUID = 1231432L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Min(10000)
    @Max(999999)
    @Column(name = "sbn5_code", nullable = false)
    private int sbn5Code;

    @Column(name = "canton_codes")
    @Convert(converter = CantonCodesConverter.class)
    private Set<String> cantonCodes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(int sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public Set<String> getCantonCodes() {
        return cantonCodes;
    }

    public void setCantonCodes(Set<String> cantonCodes) {
        this.cantonCodes = cantonCodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OccupationReportingObligation that = (OccupationReportingObligation) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "OccupationReportingObligation{" +
            "id=" + id +
            ", sbn5Code='" + sbn5Code + '\'' +
            ", cantonCodes=" + cantonCodes +
            '}';
    }
}
