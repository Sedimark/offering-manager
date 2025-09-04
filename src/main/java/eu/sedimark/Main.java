package eu.sedimark;

import eu.sedimark.config.ApplicationConfig;
import eu.sedimark.config.Config;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;

import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static HttpServer startServer(String baseURL, ApplicationConfig config) {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURL), config);
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        String containerURI = Config.getContainerEndpoint();

        final HttpServer server = startServer(containerURI, config);
        LOGGER.info(String.format("Offering Manager server started at %s with public access at %s (status: %s)",
                containerURI, Config.getExternalEndpoint() , server.isStarted()));
    }
}