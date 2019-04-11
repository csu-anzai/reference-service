package ch.admin.seco.service.reference.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class GenderNeutralOccupationLabelGeneratorTest {
    private GenderNeutralOccupationLabelGenerator generator = new GenderNeutralOccupationLabelGenerator();

    @Test
    public void mergeWithFemale_IN_ending() {
        assertThat(generator.generate("Informatiker", "Informatikerin")).isEqualTo("Informatiker/in");
        assertThat(generator.generate("Maurer-Verputzer", "Maurerin-Verputzerin")).isEqualTo("Maurer/in-Verputzer/in");
        assertThat(generator.generate("Koch, Commis de Cuisine", "Köchin, Commis de Cuisine")).isEqualTo("Koch/Köchin, Commis de Cuisine");
        assertThat(generator.generate("Schmied und Mechaniker", "Schmiedin und Mechanikerin")).isEqualTo("Schmied/in und Mechaniker/in");
        assertThat(generator.generate("Schmied und Mechaniker", "Schmiedin und Mechanikerin", false)).isEqualTo("Schmied/Schmiedin und Mechaniker/Mechanikerin");
        assertThat(generator.generate("Freiwilliger Helfer", "Freiwillige Helferin")).isEqualTo("Freiwillige/r Helfer/in");
    }

    @Test
    public void mergeWithFemale_NE_ending() {
        assertThat(generator.generate("informaticien", "informaticienne")).isEqualTo("Informaticien/ne");
    }

    @Test
    public void mergeWithFemale_INNEN_ending() {
        assertThat(generator.generate("Obstbauer", "Obstbauerinnen")).isEqualTo("Obstbauer/innen");
    }

    @Test
    public void mergeWith_O_A_ending() {
        assertThat(generator.generate("informatico", "informatica")).isEqualTo("Informatico/a");
    }

    @Test
    public void mergeWith_E_A_ending() {
        assertThat(generator.generate("Pasticciere", "Pasticciera")).isEqualTo("Pasticciere/a");
    }

    @Test
    public void mergeWith_EUR_EUSE_ending() {
        assertThat(generator.generate("Maurer-Chauffeur", "Maurerin-Chauffeuse")).isEqualTo("Maurer/in-Chauffeur/euse");
        assertThat(generator.generate("Maurer, Chauffeur", "Maurerin, Chauffeuse")).isEqualTo("Maurer/in, Chauffeur/euse");
        assertThat(generator.generate("Coiffeur", "Coiffeuse")).isEqualTo("Coiffeur/euse");
    }

    @Test
    public void mergeWithFemale_E_ending() {
        assertThat(generator.generate("Assistant", "Assistante")).isEqualTo("Assistant/e");
        assertThat(generator.generate("Chef", "Cheffe")).isEqualTo("Chef/fe");
    }

    @Test
    public void mergeWith_MANN_FRAU_ending() {
        assertThat(generator.generate("Kaufmann", "Kauffrau")).isEqualTo("Kaufmann/frau");
    }

    @Test
    public void mergeWithNullOrEmptyValues() {
        assertThat(generator.generate(null, null)).isEqualTo("");
        assertThat(generator.generate("Worker", " ")).isEqualTo("Worker");
        assertThat(generator.generate(null, "Worker")).isEqualTo("Worker");
    }

    @Test
    public void mergeWith_ER_ERE_ending() {
        assertThat(generator.generate("Ouvrier", "Ouvrière")).isEqualTo("Ouvrier/ère");
    }

    @Test
    public void mergeWith_ERS_ERES_ending() {
        assertThat(generator.generate("Ouvriers", "Ouvrières")).isEqualTo("Ouvriers/ères");
    }

    @Test
    public void mergeWith_TEUR_TRICE_ending() {
        assertThat(generator.generate("Traducteur", "Traductrice")).isEqualTo("Traducteur/trice");
    }

    @Test
    public void mergeWith_TEURS_TRICES_ending() {
        assertThat(generator.generate("Agriculteurs", "Agricultrices")).isEqualTo("Agriculteurs/trices");
    }

    @Test
    public void mergeWith_RS_SES_ending() {
        assertThat(generator.generate("Éleveurs", "Éleveuses")).isEqualTo("Éleveurs/euses");
    }

    @Test
    public void mergeWith_TORE_TRICE_ending() {
        assertThat(generator.generate("Operatore", "Operatrice")).isEqualTo("Operatore/trice");
    }

    @Test
    public void mergeWith_TORI_TRICI_ending() {
        assertThat(generator.generate("Agricoltori", "Agricoltrici")).isEqualTo("Agricoltori/trici");
    }

    @Test
    public void mergeWithFemale_ESSA_ending() {
        assertThat(generator.generate("Fattore", "Fattoressa")).isEqualTo("Fattore/essa");
    }

    @Test
    public void mergeWithFemale_ESSE_ending() {
        assertThat(generator.generate("Contremaître", "Contremaîtresse")).isEqualTo("Contremaître/esse");
    }

    @Test
    public void mergeWith_E_IN_ending() {
        assertThat(generator.generate("Gehilfe", "Gehilfin")).isEqualTo("Gehilfe/in");
        assertThat(generator.generate("Drucktechnologe", "Drucktechnologin")).isEqualTo("Drucktechnologe/in");
    }
}
