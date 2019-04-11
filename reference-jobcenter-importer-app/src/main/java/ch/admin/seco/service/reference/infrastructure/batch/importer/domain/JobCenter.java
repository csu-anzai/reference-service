package ch.admin.seco.service.reference.infrastructure.batch.importer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The persistent class for the AUX_AAMT_V database table.
 */
@Entity
@Table(name = "AUX_AAMT_V")
public class JobCenter implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Code")
    private String code;

    @Column(name = "NAME_DE")
    private String nameDe;

    @Column(name = "NAME_FR")
    private String nameFr;

    @Column(name = "NAME_IT")
    private String nameIt;

    @Column(name = "STRASSE_DE")
    private String strasseDe;

    @Column(name = "STRASSE_FR")
    private String strasseFr;

    @Column(name = "STRASSE_IT")
    private String strasseIt;

    @Column(name = "HAUS_NR")
    private String hausNr;

    @Column(name = "PLZ")
    private String plz;

    @Column(name = "ORT_DE")
    private String ortDe;

    @Column(name = "ORT_FR")
    private String ortFr;

    @Column(name = "ORT_IT")
    private String ortIt;

    @Column(name = "TELEFON")
    private String telefon;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "EMAIL")
    private String email;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the nameDe
     */
    public String getNameDe() {
        return nameDe;
    }

    /**
     * @param nameDe the nameDe to set
     */
    public void setNameDe(String nameDe) {
        this.nameDe = nameDe;
    }

    /**
     * @return the nameFr
     */
    public String getNameFr() {
        return nameFr;
    }

    /**
     * @param nameFr the nameFr to set
     */
    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    /**
     * @return the nameIt
     */
    public String getNameIt() {
        return nameIt;
    }

    /**
     * @param nameIt the nameIt to set
     */
    public void setNameIt(String nameIt) {
        this.nameIt = nameIt;
    }

    /**
     * @return the strasseDe
     */
    public String getStrasseDe() {
        return strasseDe;
    }

    /**
     * @param strasseDe the strasseDe to set
     */
    public void setStrasseDe(String strasseDe) {
        this.strasseDe = strasseDe;
    }

    /**
     * @return the strasseFr
     */
    public String getStrasseFr() {
        return strasseFr;
    }

    /**
     * @param strasseFr the strasseFr to set
     */
    public void setStrasseFr(String strasseFr) {
        this.strasseFr = strasseFr;
    }

    /**
     * @return the strasseIt
     */
    public String getStrasseIt() {
        return strasseIt;
    }

    /**
     * @param strasseIt the strasseIt to set
     */
    public void setStrasseIt(String strasseIt) {
        this.strasseIt = strasseIt;
    }

    /**
     * @return the hausNr
     */
    public String getHausNr() {
        return hausNr;
    }

    /**
     * @param hausNr the hausNr to set
     */
    public void setHausNr(String hausNr) {
        this.hausNr = hausNr;
    }

    /**
     * @return the plz
     */
    public String getPlz() {
        return plz;
    }

    /**
     * @param plz the plz to set
     */
    public void setPlz(String plz) {
        this.plz = plz;
    }

    /**
     * @return the ortDe
     */
    public String getOrtDe() {
        return ortDe;
    }

    /**
     * @param ortDe the ortDe to set
     */
    public void setOrtDe(String ortDe) {
        this.ortDe = ortDe;
    }

    /**
     * @return the ortFr
     */
    public String getOrtFr() {
        return ortFr;
    }

    /**
     * @param ortFr the ortFr to set
     */
    public void setOrtFr(String ortFr) {
        this.ortFr = ortFr;
    }

    /**
     * @return the ortIt
     */
    public String getOrtIt() {
        return ortIt;
    }

    /**
     * @param ortIt the ortIt to set
     */
    public void setOrtIt(String ortIt) {
        this.ortIt = ortIt;
    }

    /**
     * @return the telefon
     */
    public String getTelefon() {
        return telefon;
    }

    /**
     * @param telefon the telefon to set
     */
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
