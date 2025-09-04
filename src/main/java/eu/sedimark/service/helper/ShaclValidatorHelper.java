package eu.sedimark.service.helper;

import eu.sedimark.config.OntologyDefinitions;
import eu.sedimark.exception.DltBoothException;
import eu.sedimark.exception.ShaclValidatorException;
import eu.sedimark.model.ShaclValidationResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.Shapes;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ShaclValidatorHelper {

    private final static Logger LOGGER = Logger.getLogger(ShaclValidatorHelper.class.getName());

    /**
     * Validates a JSON-LD String against a SHACL shape loaded from a URL.
     *
     * @param jsonLdString JSON-LD content as a string
     * @param shaclUrl     URL pointing to the SHACL shape (TTL/RDF format)
     * @return ShaclValidationResult with conformance info and detailed report
     */
    public static ShaclValidationResult validate(String jsonLdString, String shaclUrl) {
        // Convert JSON-LD string to InputStream
        InputStream jsonLdStream = new ByteArrayInputStream(jsonLdString.getBytes(StandardCharsets.UTF_8));

        // Load the data model from JSON-LD
        Model dataModel = ModelFactory.createDefaultModel();
        RDFDataMgr.read(dataModel, jsonLdStream, null, RDFLanguages.JSONLD);
        Model shapesModel = ModelFactory.createDefaultModel();

        // Load SHACL shapes from URL with a fallback in case it is not available
        try {
            InputStream shapeStream = URI.create(shaclUrl).toURL().openStream();
            RDFDataMgr.read(shapesModel, shapeStream, Lang.TURTLE);
        } catch (IllegalArgumentException | IOException | RiotException e) {
            LOGGER.warning("Could not load SHACL URL: " + shaclUrl + ". " + e.getMessage() + ". Loading fallback SHACL String.");
            StringReader reader = new StringReader(OntologyDefinitions.FALLBACK_SEDIMARK_SHACL);
            RDFDataMgr.read(shapesModel, reader, null, Lang.TURTLE);
        }

        // Perform SHACL validation
        Shapes shapes = Shapes.parse(shapesModel.getGraph());
        ValidationReport report = ShaclValidator.get().validate(shapes, dataModel.getGraph());

        // Serialize the report to string
        ByteArrayOutputStream reportOut = new ByteArrayOutputStream();
        RDFDataMgr.write(reportOut, report.getModel(), Lang.TTL);

        return new ShaclValidationResult(report.conforms(), reportOut.toString(StandardCharsets.UTF_8));
    }
}