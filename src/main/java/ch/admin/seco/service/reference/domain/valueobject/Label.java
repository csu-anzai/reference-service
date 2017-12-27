package ch.admin.seco.service.reference.domain.valueobject;

public class Label {

    private String classifier;
    private String label;

    public Label(String type, String code) {
        this.classifier = type;
        this.label = code;
    }

    Label() {
    }

    public String getClassifier() {
        return classifier;
    }

    public String getLabel() {
        return label;
    }
}
