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
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import ch.admin.seco.service.reference.domain.converter.CantonCodesConverter;

@Entity
@Table(name = "reporting_obligation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReportingObligation implements Serializable {

    private static final long serialVersionUID = 1231432L;

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUIDGenerator")
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    private UUID id;

    @NotNull
    @Column(name = "sbn5_code")
    private String sbn5Code;

    @Column(name = "canton_codes")
    @Convert(converter = CantonCodesConverter.class)
    private Set<String> cantonCodes;

    public boolean isAppliedForSwissOrCanton(String cantonCode) {
        if (StringUtils.isEmpty(cantonCode)) {
            return isAppliedForSwiss();
        } else {
            return isAppliedForSwiss()
                || getCantonCodes().stream().anyMatch(code -> code.equalsIgnoreCase(cantonCode));
        }
    }

    public boolean isAppliedForSwiss() {
        return CollectionUtils.isEmpty(getCantonCodes());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSbn5Code() {
        return sbn5Code;
    }

    public void setSbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
    }

    public ReportingObligation sbn5Code(String sbn5Code) {
        this.sbn5Code = sbn5Code;
        return this;
    }

    public Set<String> getCantonCodes() {
        return cantonCodes;
    }

    public void setCantonCodes(Set<String> cantonCodes) {
        this.cantonCodes = cantonCodes;
    }

    public ReportingObligation cantonCodes(Set<String> cantonCodes) {
        this.cantonCodes = cantonCodes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReportingObligation that = (ReportingObligation) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return "ReportingObligation{" +
            "id=" + id +
            ", sbn5Code='" + sbn5Code + '\'' +
            ", cantonCodes=" + cantonCodes +
            ", appliedForSwiss=" + isAppliedForSwiss() +
            '}';
    }
}
