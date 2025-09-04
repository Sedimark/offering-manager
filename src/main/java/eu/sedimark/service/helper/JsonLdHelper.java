package eu.sedimark.service.helper;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import eu.sedimark.config.OntologyDefinitions;
import eu.sedimark.exception.TechnicalException;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class JsonLdHelper {

    private final static Logger LOGGER = Logger.getLogger(JsonLdHelper.class.getSimpleName());

    public enum ContextPreset {
        FULL, SELFLISTING
    }

    private static final Map<String, String> FULL_PREFIXES = Map.ofEntries(
            Map.entry(OntologyDefinitions.SEDI_PREFIX, OntologyDefinitions.SEDI_URI),
            Map.entry(OntologyDefinitions.DCT_PREFIX, OntologyDefinitions.DCT_URI),
            Map.entry(OntologyDefinitions.ODRL_PREFIX, OntologyDefinitions.ODRL_URI),
            Map.entry(OntologyDefinitions.OWL_PREFIX, OntologyDefinitions.OWL_URI),
            Map.entry(OntologyDefinitions.RDF_PREFIX, OntologyDefinitions.RDF_URI),
            Map.entry(OntologyDefinitions.XML_PREFIX, OntologyDefinitions.XML_URI),
            Map.entry(OntologyDefinitions.XSD_PREFIX, OntologyDefinitions.XSD_URI),
            Map.entry(OntologyDefinitions.RDFS_PREFIX, OntologyDefinitions.RDFS_URI),
            Map.entry(OntologyDefinitions.DCAT_PREFIX, OntologyDefinitions.DCAT_URI),
            Map.entry(OntologyDefinitions.SCHEMA_PREFIX, OntologyDefinitions.SCHEMA_URI)
    );

    private static final Map<String, String> SELFLISTING_PREFIXES = Map.of(
            OntologyDefinitions.SEDI_PREFIX, OntologyDefinitions.SEDI_URI,
            OntologyDefinitions.XSD_PREFIX, OntologyDefinitions.XSD_URI,
            OntologyDefinitions.SCHEMA_PREFIX, OntologyDefinitions.SCHEMA_URI
    );

    public static Map<String, Object> buildSedimarkContext(ContextPreset preset) {
        Map<String, String> prefixes;

        if (Objects.requireNonNull(preset) == ContextPreset.SELFLISTING) {
            prefixes = SELFLISTING_PREFIXES;
        } else {
            prefixes = FULL_PREFIXES;
        }

        return buildSedimarkContext(prefixes);
    }

    public static Map<String, Object> buildSedimarkContext() {
        return buildSedimarkContext(FULL_PREFIXES);
    }

    public static Map<String, Object> buildSedimarkContext(Map<String, String> prefixes) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("@vocab", OntologyDefinitions.VOCAB_URI);

        context.putAll(prefixes);

        Map<String, Object> contextWrapper = new LinkedHashMap<>();
        contextWrapper.put("@context", context);
        return contextWrapper;
    }

    public static String buildSedimarkContextString() {
        try {
            return JsonUtils.toPrettyString(buildSedimarkContext());
        } catch (IOException e) {
            throw new TechnicalException("Error while building Sedimark Context", e);
        }
    }

    public static String buildSedimarkContextString(ContextPreset preset) {
        try {
            return JsonUtils.toPrettyString(buildSedimarkContext(preset));
        } catch (IOException e) {
            throw new TechnicalException("Error while building Sedimark Context", e);
        }
    }

    public static String buildSedimarkContextString(Map<String, String> prefixes) {
        try {
            return JsonUtils.toPrettyString(buildSedimarkContext(prefixes));
        } catch (IOException e) {
            throw new TechnicalException("Error while building Sedimark Context", e);
        }
    }

    public static Object expand(String jsonLd) {
        // we should extract @context and use it later to compact it back
        try {
            Object jsonLdObj = JsonUtils.fromString(jsonLd);
            return JsonLdProcessor.expand(jsonLdObj, new JsonLdOptions());
        } catch (IOException e) {
            throw new TechnicalException("Error expanding JSON-LD", e);
        }
    }

    public static Object compact(String jsonLd)  {
        return compact(jsonLd, buildSedimarkContextString());
    }

    public static Object compact(String jsonLd, String contextString) {
        try {
            Object context = JsonUtils.fromString(contextString);
            Object jsonLdObj = JsonUtils.fromString(jsonLd);
            return JsonLdProcessor.compact(jsonLdObj, context, new JsonLdOptions());
        } catch (IOException e) {
            throw new TechnicalException("Error compacting JSON-LD", e);
        }
    }

    public static Object flatten(String jsonLd) {
        return flatten(jsonLd, buildSedimarkContextString());
    }

    public static Object flatten(String jsonLd, String contextString) {
        try {
            Object context = JsonUtils.fromString(contextString);
            Object jsonLdObj = JsonUtils.fromString(jsonLd);
            return JsonLdProcessor.flatten(jsonLdObj, context, new JsonLdOptions());
        } catch (IOException e) {
            throw new TechnicalException("Error flattening JSON-LD", e);
        }
    }

    public static String toJsonString(Object jsonObject) {
        return toJsonString(jsonObject, true);
    }

    public static String toJsonString(Object jsonObject, boolean pretty) {
        try {
            return pretty
                    ? JsonUtils.toPrettyString(jsonObject)
                    : JsonUtils.toString(jsonObject);
        } catch (IOException e) {
            throw new TechnicalException("Error serializing object to JSON", e);
        }
    }
}
