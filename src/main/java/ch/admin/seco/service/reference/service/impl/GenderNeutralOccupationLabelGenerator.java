package ch.admin.seco.service.reference.service.impl;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.capitalize;

import java.util.StringTokenizer;

import org.springframework.stereotype.Component;

@Component
public class GenderNeutralOccupationLabelGenerator {

    private static final String TITLE_SEPARATOR = "/";

    /*
     * Combines a male and female job title into a gender neutral title
     *
     * The titles can be shortened where appropriate:
     *
     * Informatiker + Informatikerin -> Informatiker/in
     * informatico + informatica -> informatico/a
     * informaticien + informaticienne -> informaticien/ne
     * coiffeur + coiffeuse -> coiffeur/euse, similar traducteur/trice, infirmier/ière
     * Koch, Commis de Cuisine + Köchin, Commis de Cuisine -> Koch / Köchin, Commis de Cuisine
     *
     * null safe
     */
    public String generate(String maleOccupationLabel, String femaleOccupationLabel) {
        return generate(maleOccupationLabel, femaleOccupationLabel, true);
    }

    public String generate(String maleOccupationLabel, String femaleOccupationLabel, boolean doMergeLabels) {
        String sanitizedMaleLabel = sanitizeLabel(maleOccupationLabel);
        String sanitizedFemaleLabel = sanitizeLabel(femaleOccupationLabel);

        if (sanitizedMaleLabel.isEmpty() || sanitizedFemaleLabel.isEmpty()) {
            return !sanitizedMaleLabel.isEmpty() ? sanitizedMaleLabel : sanitizedFemaleLabel;
        } else if (sanitizedMaleLabel.equals(sanitizedFemaleLabel)) {
            return sanitizedMaleLabel;
        }

        StringTokenizer maleTokens = tokenize(sanitizedMaleLabel);
        StringTokenizer femaleTokens = tokenize(sanitizedFemaleLabel);

        if (maleTokens.countTokens() != femaleTokens.countTokens()) {
            return String.format("%s %s %s", sanitizedMaleLabel, TITLE_SEPARATOR, sanitizedFemaleLabel);
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (maleTokens.hasMoreTokens()) {
            String maleToken = maleTokens.nextToken();
            String femaleToken = femaleTokens.nextToken();
            if (maleToken.equals(femaleToken)) {
                stringBuilder.append(maleToken);
            } else if (maleToken.matches("[ -]") && femaleToken.matches("[ -]")) {
                stringBuilder.append(maleToken);
            } else if (doMergeLabels) {
                mergeLabels(stringBuilder, maleToken, femaleToken);
            } else {
                stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append(femaleToken);
            }
        }
        return stringBuilder.toString();
    }

    private void mergeLabels(StringBuilder stringBuilder, String maleToken, String femaleToken) {
        if (femaleToken.equalsIgnoreCase(maleToken + "ne")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("ne");
        } else if (femaleToken.equalsIgnoreCase(maleToken + "e")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("e");
        } else if (femaleToken.equalsIgnoreCase(maleToken + maleToken.charAt(maleToken.length() - 1) + "e")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append(maleToken.charAt(maleToken.length() - 1)).append("e");
        } else if (femaleToken.equalsIgnoreCase(maleToken + "in")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("in");
        } else if (matchEnding(maleToken, femaleToken, "e", "in")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("in");
        } else if (femaleToken.equalsIgnoreCase(maleToken + "innen")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("innen");
        } else if (femaleToken.equalsIgnoreCase(maleToken + "ssa")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("essa");
        } else if (femaleToken.equalsIgnoreCase(maleToken + "sse")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("esse");
        } else if (matchEnding(maleToken, femaleToken, "o", "a")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("a");
        } else if (matchEnding(maleToken, femaleToken, "e", "a")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("a");
        } else if (matchEnding(maleToken, femaleToken, "mann", "frau")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("frau");
        } else if (matchEnding(maleToken, femaleToken, "r", "se")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("euse");
        } else if (matchEnding(maleToken, femaleToken, "rs", "ses")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("euses");
        } else if (matchEnding(maleToken, femaleToken, "eur", "rice")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("trice");
        } else if (matchEnding(maleToken, femaleToken, "eurs", "rices")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("trices");
        } else if (matchEnding(maleToken, femaleToken, "tore", "trice")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("trice");
        } else if (matchEnding(maleToken, femaleToken, "tori", "trici")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("trici");
        } else if (matchEnding(maleToken, femaleToken, "er", "ère")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("ère");
        } else if (matchEnding(maleToken, femaleToken, "ers", "ères")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("ères");
        } else if (matchEnding(maleToken, femaleToken, "oge", "ogie")) {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append("ogie");
        } else if (maleToken.equalsIgnoreCase(femaleToken + "r")) {
            stringBuilder.append(femaleToken).append(TITLE_SEPARATOR).append("r");
        } else {
            stringBuilder.append(maleToken).append(TITLE_SEPARATOR).append(femaleToken);
        }
    }

    private boolean matchEnding(String maleToken, String femaleToken, String maleEnding, String femaleEnding) {
        return maleToken.endsWith(maleEnding) && femaleToken.equalsIgnoreCase(maleToken.substring(0, maleToken.length() - maleEnding.length()) + femaleEnding);
    }

    private StringTokenizer tokenize(String label) {
        return new StringTokenizer(label, ", /-()", true);
    }

    private String sanitizeLabel(String label) {
        return isNull(label) ? "" : capitalize(label.trim());
    }

}
