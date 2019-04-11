package ch.admin.seco.service.reference.service.search;

import java.util.Set;

public class Suggestions {

    private Set<String> de;

    private Set<String> fr;

    private Set<String> it;

    private Set<String> en;

    public Set<String> getDe() {
        return de;
    }

    public void setDe(Set<String> de) {
        this.de = de;
    }

    public Suggestions de(Set<String> de) {
        this.de = de;
        return this;
    }

    public Set<String> getFr() {
        return fr;
    }

    public void setFr(Set<String> fr) {
        this.fr = fr;
    }

    public Suggestions fr(Set<String> fr) {
        this.fr = fr;
        return this;
    }

    public Set<String> getIt() {
        return it;
    }

    public void setIt(Set<String> it) {
        this.it = it;
    }

    public Suggestions it(Set<String> it) {
        this.it = it;
        return this;
    }

    public Set<String> getEn() {
        return en;
    }

    public void setEn(Set<String> en) {
        this.en = en;
    }

    public Suggestions en(Set<String> en) {
        this.en = en;
        return this;
    }
}
