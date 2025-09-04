package eu.sedimark.config;

import com.github.jsonldjava.utils.JsonUtils;
import eu.sedimark.exception.TechnicalException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class OntologyDefinitions {

    public static final String CONNECTOR_VOCAB = "https://w3id.org/edc/v0.0.1/ns/";

    public static final String SEDI_PREFIX = "sedi";
    public static final String SEDI_URI = "https://w3id.org/sedimark/ontology#";

    public static final String DCT_PREFIX = "dct";
    public static final String DCT_URI = "http://purl.org/dc/terms/";

    public static final String ODRL_PREFIX = "odrl";
    public static final String ODRL_URI = "http://www.w3.org/ns/odrl/2/";

    public static final String OWL_PREFIX = "owl";
    public static final String OWL_URI = "http://www.w3.org/2002/07/owl#";

    public static final String RDF_PREFIX = "rdf";
    public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    public static final String XML_PREFIX = "xml";
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";

    public static final String XSD_PREFIX = "xsd";
    public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema#";

    public static final String RDFS_PREFIX = "rdfs";
    public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";

    public static final String DCAT_PREFIX = "dcat";
    public static final String DCAT_URI = "http://www.w3.org/ns/dcat#";

    public static final String SCHEMA_PREFIX = "schema";
    public static final String SCHEMA_URI = "https://schema.org/";

    public static final String VOCAB_URI = "https://w3id.org/sedimark/vocab/";

    public static final Set<String> ASSET_TYPES = Set.of(
            "https://w3id.org/sedimark/ontology#Asset",
            "https://w3id.org/sedimark/ontology#DataAsset",
            "https://w3id.org/sedimark/ontology#AIModelAsset",
            "https://w3id.org/sedimark/ontology#ServiceAsset",
            "https://w3id.org/sedimark/ontology#OtherAsset",
            "Asset",
            "DataAsset",
            "AIModelAsset",
            "ServiceAsset",
            "OtherAsset",
            "sedi:Asset",
            "sedi:DataAsset",
            "sedi:AIModelAsset",
            "sedi:ServiceAsset",
            "sedi:OtherAsset"
    );

    public static final Set<String> OFFERING_TYPES = Set.of(
            "https://w3id.org/sedimark/ontology#Offering",
            "Offering",
            "sedi:Offering"
    );

    public static final Set<String> ASSET_PROVISION_TYPES = Set.of(
            "https://w3id.org/sedimark/ontology#AssetProvision",
            "AssetProvision",
            "sedi:AssetProvision"
    );

    public static final Set<String> OFFERING_CONTRACT_TYPES = Set.of(
            "https://w3id.org/sedimark/ontology#OfferingContract",
            "OfferingContract",
            "sedi:OfferingContract"
    );
    public static final Set<String> ODRL_PERMISSION_KEYS = Set.of(
            "permission",
            "odrl:permission",
            "http://www.w3.org/ns/odrl/2/permission"
    );

    public static final Set<String> ODRL_OBLIGATION_KEYS = Set.of(
            "obligation",
            "odrl:obligation",
            "http://www.w3.org/ns/odrl/2/obligation"
    );

    public static final Set<String> ODRL_PROHIBITION_KEYS = Set.of(
            "prohibition",
            "odrl:prohibition",
            "http://www.w3.org/ns/odrl/2/prohibition"
    );

    public static final String SHACL_URL = "https://raw.githubusercontent.com/Sedimark/ontology/refs/heads/main/shacl/offering-manager.ttl";

    public static final String FALLBACK_SEDIMARK_SHACL=
    """
            @prefix sh: <http://www.w3.org/ns/shacl#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
            @prefix owl: <http://www.w3.org/2002/07/owl#> .
            @prefix skos: <http://www.w3.org/2004/02/skos/core#> .
            @prefix sedimark: <https://w3id.org/sedimark/ontology#> .
            @prefix dcterms: <http://purl.org/dc/terms/> .
            @prefix dcat: <http://www.w3.org/ns/dcat#> .
            @prefix odrl: <http://www.w3.org/ns/odrl/2/> .
            @prefix schema: <https://schema.org/> .
            @prefix foaf: <http://xmlns.com/foaf/0.1/> .
            @prefix prov: <http://www.w3.org/ns/prov#> .
            
            # Document-level checks to ensure required instances exist
            sedimark:DocumentRequirementsShape
                a sh:NodeShape ;
                sh:target [
                    a sh:SPARQLTarget ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT DISTINCT ?this WHERE { ?this ?p ?o }
                        LIMIT 1
                    ""\" ;
                ] ;
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "At least one sedimark:Offering instance must exist" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT $this
                        WHERE {
                            FILTER NOT EXISTS { ?offeringOld a sedimark:Offering }
                        }
                    ""\"
                ] ;
            
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "At least one sedimark:OfferingContract instance must exist" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT $this
                        WHERE {
                            FILTER NOT EXISTS { ?contract a sedimark:OfferingContract }
                        }
                    ""\"
                ] .
            
            # Offering shape
            sedimark:OfferingShape
                a sh:NodeShape ;
                sh:targetClass sedimark:Offering ;
               \s
                # Title property
                sh:property [
                    sh:path dcterms:title ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Offering must have exactly one dcterms:title with xsd:string value" ;
                ] ;
               \s
                # Description property
                sh:property [
                    sh:path dcterms:description ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Offering must have exactly one dcterms:description with xsd:string value" ;
                ] ;
               \s
                # Theme taxonomy property
                sh:property [
                    sh:path dcat:themeTaxonomy ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Offering must have at least one dcat:themeTaxonomy with skos:ConceptScheme value" ;
                ] ;
               \s
                # hasAsset property
                sh:property [
                    sh:path sedimark:hasAsset ;
                    sh:or (
                        [ sh:class sedimark:Asset ]
                        [ sh:class sedimark:DataAsset ]
                        [ sh:class sedimark:AIModelAsset ]
                        [ sh:class sedimark:ServiceAsset ]
                        [ sh:class sedimark:OtherAsset ]
                    ) ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Offering must have at least one of the following asset value: Asset, DataAsset, AIModelAsset, ServiceAsset, OtherAsset" ;
                ] ;
               \s
                # isListedBy property
                sh:property [
                    sh:path sedimark:isListedBy ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Offering must have exactly one sedimark:isListedBy with a sedimark:Self-Listing id" ;
                ] ;
               \s
                # hasOfferingContract property
                sh:property [
                    sh:path sedimark:hasOfferingContract ;
                    sh:class sedimark:OfferingContract ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Offering must have at least one sedimark:hasOfferingContract with sedimark:OfferingContract value" ;
                ] ;
               \s
                # publisher property
                sh:property [
                    sh:path dcterms:publisher ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "dcterms:publisher must refer to a sedimark:Participant id" ;
                ] ;
            
                # License property
                sh:property [
                    sh:path dcterms:license ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Offering must have exactly one dcterms:license with xsd:string value" ;
                ] .
            
            # Assets shape
            sedimark:AssetShape
                a sh:NodeShape ;
                # This shape applies to all listed classes
                sh:targetClass sedimark:Asset ;
                sh:targetClass sedimark:DataAsset ;
                sh:targetClass sedimark:AIModelAsset ;
                sh:targetClass sedimark:ServiceAsset ;
                sh:targetClass sedimark:OtherAsset ;
               \s
                # offeredBy property
                sh:property [
                    sh:path sedimark:offeredBy ;
                    sh:class sedimark:Offering ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Asset must have at least one sedimark:offeredBy with sedimark:Offering value" ;
                ] ;
               \s
                # isProvidedBy property
                sh:property [
                    sh:path sedimark:isProvidedBy ;
                    sh:class sedimark:AssetProvision ;
                    sh:minCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Asset must have at least one sedimark:isProvidedBy with sedimark:AssetProvision value" ;
                ] ;
               \s
                # Theme property
                sh:property [
                    sh:path dcat:theme ;
                    sh:minCount 1 ;
                    sh:message "sedimark:Asset must have at least one dcat:theme with skos:Concept id" ;
                ] ;
               \s
                # Title property
                sh:property [
                    sh:path dcterms:title ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Asset must have exactly one dcterms:title with xsd:string value" ;
                ] ;
               \s
                # Description property
                sh:property [
                    sh:path dcterms:description ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Asset must have exactly one dcterms:description with xsd:string value" ;
                ] ;
               \s
                # Issued property
                sh:property [
                    sh:path dcterms:issued ;
                    sh:datatype xsd:dateTime ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Asset must have exactly one dcterms:issued with xsd:dateTime value" ;
                ] ;
               \s
                # Keyword property
                sh:property [
                    sh:path dcat:keyword ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:message "sedimark:Asset must have at least one dcat:keyword with xsd:string value" ;
                ] ;
               \s
                # Spatial property
                sh:property [
                    sh:path dcterms:spatial ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Asset must have exactly one dcterms:spatial with dcterms:Location id" ;
                ] ;
               \s
                # Optional generatedBy property
                sh:property [
                    sh:path prov:generatedBy ;
                    sh:datatype xsd:dateTime ;
                    sh:message "sedimark:Asset can have prov:generatedBy properties with xsd:dateTime values" ;
                ] ;
               \s
                # Optional isVersionOf property
                sh:property [
                    sh:path dcat:isVersionOf ;
                    sh:datatype xsd:dateTime ;
                    sh:message "sedimark:Asset can have dcat:isVersionOf properties with xsd:dateTime values" ;
                ] .
            
            # Self-Listing shape
            sedimark:SelfListingShape
                a sh:NodeShape ;
                sh:targetClass sedimark:Self-Listing ;
               \s
                # belongsTo property
                sh:property [
                    sh:path sedimark:belongsTo ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:nodeKind sh:IRI ;
                    sh:message "sedimark:Self-Listing must have exactly one sedimark:belongsTo with sedimark:Participant value" ;
                ] .
               \s
                # Title property removed
               \s
                # Description property removed
               
               \s
                # Issued property removed
               
               \s
                # Modified property removed

               \s
                # Landing page property removed
               
            
            # Participant shape
            sedimark:ParticipantShape
                a sh:NodeShape ;
                sh:targetClass sedimark:Participant ;
               \s
                # accountId property
                sh:property [
                    sh:path schema:accountId ;
                    sh:datatype xsd:string ;
                    sh:minCount 1 ;
                    sh:maxCount 1 ;
                    sh:message "sedimark:Participant must have exactly one schema:accountId with xsd:string value" ;
                ] .
               \s
                # email property removed
                
            
            # OfferingContract shape
            sedimark:OfferingContractShape
                a sh:NodeShape ;
                sh:targetClass sedimark:OfferingContract .
               \s
                # permission property
                # sh:property [
                #     sh:path odrl:permission ;
                #     sh:minCount 1 ;
                #     sh:message "sedimark:OfferingContract must have at least one odrl:permission" ;
                # ] ;
               \s
                # duty property
                # sh:property [
                #     sh:path odrl:duty ;
                #     sh:minCount 1 ;
                #     sh:message "sedimark:OfferingContract must have at least one odrl:duty" ;
                # ] ;
               \s
                # obligation property
                # sh:property [
                #     sh:path odrl:obligation ;
                #     sh:minCount 1 ;
                #     sh:message "sedimark:OfferingContract must have at least one odrl:obligation" ;
                # ] .
            
            # AssetProvision shape - validate that for each Asset there's at least one AssetProvision
            sedimark:AssetProvisionRequirementShape
                a sh:NodeShape ;
                sh:targetClass sedimark:Asset ;
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Each sedimark:Asset must have at least one sedimark:AssetProvision instance" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT $this
                        WHERE {
                            $this a sedimark:Asset .
                            FILTER NOT EXISTS {
                                $this sedimark:isProvidedBy ?provision .
                                ?provision a sedimark:AssetProvision .
                            }
                        }
                    ""\"
                ] .
            
            # Validate that referenced instances exist
            sedimark:InstanceExistenceShape
                a sh:NodeShape ;
               \s
                # Check if referenced Offering instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:Offering must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:offeredBy ?value }\s
                            FILTER NOT EXISTS { ?value a sedimark:Offering }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced Asset instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:Asset must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:hasAsset ?value }
                            FILTER NOT EXISTS { ?value a sedimark:Asset }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced Self-Listing instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:Self-Listing must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:isListedBy ?value }
                            FILTER NOT EXISTS { ?value a sedimark:Self-Listing }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced AssetProvision instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:AssetProvision must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:isProvidedBy ?value }
                            FILTER NOT EXISTS { ?value a sedimark:AssetProvision }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced AssetQuality instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:AssetQuality must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:hasAssetQuality ?value }
                            FILTER NOT EXISTS { ?value a sedimark:AssetQuality }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced OfferingContract instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:OfferingContract must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:hasOfferingContract ?value }
                            FILTER NOT EXISTS { ?value a sedimark:OfferingContract }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced Participant instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced sedimark:Participant must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this sedimark:belongsTo ?value }
                            FILTER NOT EXISTS { ?value a sedimark:Participant }
                        }
                    ""\"
                ] ;
               \s
                # Check if referenced ConceptScheme instances exist
                sh:sparql [
                    a sh:SPARQLConstraint ;
                    sh:message "Referenced skos:ConceptScheme must exist in the document" ;
                    sh:select ""\"
                    PREFIX sedimark: <https://w3id.org/sedimark/ontology#>
                    PREFIX dcat: <http://www.w3.org/ns/dcat#>
                    PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
                        SELECT ?this ?value
                        WHERE {
                            { ?this dcat:themeTaxonomy ?value }
                            FILTER NOT EXISTS { ?value a skos:ConceptScheme }
                        }
                    ""\"
                ] .
    """;
}
