package eu.sedimark.model;

import lombok.Getter;

/**
 * Holds the result of SHACL validation.
 */
public class ShaclValidationResult {
    private final boolean conforms;
    @Getter
    private final String report;

    public ShaclValidationResult(boolean conforms, String report) {
        this.conforms = conforms;
        this.report = report;
    }

    public boolean conforms() {
        return conforms;
    }

    @Override
    public String toString() {
        return "Conforms: " + conforms + "\nReport:\n" + report;
    }
}
