package eu.sedimark.config;

import eu.sedimark.exception.GlobalExceptionMapper;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import eu.sedimark.controller.OfferingResource;
import org.glassfish.jersey.server.wadl.internal.WadlResource;

import java.util.logging.Logger;


@ApplicationPath("/")
public class ApplicationConfig extends ResourceConfig {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public ApplicationConfig() {
        Config.initialize();

        register(OfferingResource.class);
        register(WadlResource.class);
        register(GlobalExceptionMapper.class);
        register(JacksonFeature.class);
    }

}
