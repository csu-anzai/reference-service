package ch.admin.seco.service.reference.domain.valueobject;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

import ch.admin.seco.service.reference.domain.Language;

@Embeddable
public class Labels {

    @Size(min = 2, max = 255)
    @Column(name = "label_de")
    private String de;

    @Size(min = 2, max = 255)
    @Column(name = "label_fr")
    private String fr;

    @Size(min = 2, max = 255)
    @Column(name = "label_it")
    private String it;

    @Size(min = 2, max = 255)
    @Column(name = "label_en")
    private String en;

    public String getDe() {
        return de;
    }

    public void setDe(String de) {
        this.de = de;
    }

    public Labels de(String de) {
        this.de = de;
        return this;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    public Labels fr(String fr) {
        this.fr = fr;
        return this;
    }

    public String getIt() {
        return it;
    }

    public void setIt(String it) {
        this.it = it;
    }

    public Labels it(String it) {
        this.it = it;
        return this;
    }


    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public Labels en(String en) {
        this.en = en;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(de, fr, it, en);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Labels label = (Labels) o;
        return Objects.equals(de, label.de) &&
            Objects.equals(fr, label.fr) &&
            Objects.equals(it, label.it) &&
            Objects.equals(en, label.en);
    }

    @Override
    public String toString() {
        return "Label{" +
            "de='" + de + '\'' +
            ", fr='" + fr + '\'' +
            ", it='" + it + '\'' +
            ", en='" + en + '\'' +
            '}';
    }

    public String get(Language language) {
        switch (language) {
            case de:
                return de;
            case fr:
                return fr;
            case it:
                return it;
            case en:
                return en;
            default:
                return de;
        }
    }
}
