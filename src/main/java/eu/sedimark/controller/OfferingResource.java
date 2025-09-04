package eu.sedimark.controller;

import eu.sedimark.model.NftAddressResponse;
import eu.sedimark.model.offering.Offering;
import eu.sedimark.model.offering.PaginatedSelfListingResult;
import eu.sedimark.service.OfferingService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.logging.Logger;

@Path("/offerings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OfferingResource {

    private final Logger LOGGER;

    private final OfferingService offeringService = OfferingService.getInstance();

    public OfferingResource() {
        this.LOGGER = Logger.getLogger(this.getClass().getName());
    }

    @Context
    private UriInfo uriInfo; // Injecting UriInfo

    @POST
    public Response createOffering(String offeringDescription) {
        LOGGER.info("Registering offering: " + offeringDescription);
        Offering newOffering = offeringService.registerOffering(offeringDescription);
        LOGGER.info("Offering created successfully with ID: " + newOffering.getId() +
                ", Hash: " + offeringService.getOfferingHashFromOfferingId(newOffering.getId()));
        return Response.status(Response.Status.CREATED).entity(offeringService.getOfferingString(newOffering)).build();
    }

    @GET
    @Path("/{offeringId}")
    public Response getOffering(@PathParam("offeringId") String offeringId) {
        LOGGER.info("Obtaining offering with id: " + offeringId);
        Offering offering;
        try {
            offering = offeringService.getOfferingByShortId(offeringId);
        } catch (NotFoundException e) {
            LOGGER.warning("Offering with ID: " + offeringId + " not found.");
            return notFoundResponse();
        }
        LOGGER.info("Offering with ID: " + offeringId + " retrieved successfully.");
        return Response.ok(offeringService.getOfferingString(offering)).build();
    }

    @GET
    @Path("/{offeringId}/nftaddress")
    public Response getOfferingNftAddress(@PathParam("offeringId") String offeringId) {
        LOGGER.info("Obtaining NFT address from offering with id: " + offeringId);

        String nftAddress = offeringService.getNftAddressFromOfferingShortId(offeringId);

        if (nftAddress == null) {
            LOGGER.warning("Offering with ID: " + offeringId + " not found.");
            return notFoundResponse();
        }
        LOGGER.info("NFT Address: " + nftAddress + " Offering with ID: " + offeringId + " retrieved successfully.");
        NftAddressResponse nftAddressResponse = new NftAddressResponse(nftAddress);
        return Response.ok(nftAddressResponse).build();
    }

    @GET
    public Response listOfferings(@QueryParam("size") @DefaultValue("-1") int size,
                                  @QueryParam("page") @DefaultValue("0") int page) {
        if (size == -1) {
            LOGGER.info("Listing default self-listing with IDs from all available offerings.");
        } else {
            LOGGER.info("Listing offerings with pagination params: size=" + size + ", page=" + page);
        }

        PaginatedSelfListingResult<String> paginatedSelfListingResult = offeringService.createSelfListing(page, size);

        return Response.ok(paginatedSelfListingResult.getData())
                .header("X-Total-Count", paginatedSelfListingResult.getTotalCount())
                .build();
    }

    @PUT
    @Path("/{offeringId}")
    public Response updateOffering(@PathParam("offeringId") String offeringId, String updatedOffering) {
        LOGGER.info("Updating (actually removing and creating a new one) offering with ID: " + offeringId);
        try {
            offeringService.removeOfferingByShortId(offeringId);
            Offering newOffering = offeringService.registerOffering(updatedOffering);
            LOGGER.info("Offering with ID: " + offeringId + " updated successfully to new ID: " + newOffering.getId());
            return Response.status(Response.Status.CREATED).entity(offeringService.getOfferingString(newOffering)).build();
        } catch (NotFoundException e) {
            LOGGER.warning("Failed to update offering with ID: " + offeringId + ". Not found.");
            return notFoundResponse();
        }
    }

    @DELETE
    @Path("/{offeringId}")
    public Response deleteOffering(@PathParam("offeringId") String offeringId) {
        LOGGER.info("Attempting to delete offering with ID: " + offeringId);
        try {
            offeringService.removeOfferingByShortId(offeringId);
            LOGGER.info("Offering with ID: " + offeringId + " deleted successfully.");
            return Response.noContent().build();
        } catch (NotFoundException e) {
            LOGGER.warning("Offering with ID: " + offeringId + " not found. Deletion failed.");
            return notFoundResponse();
        }
    }

    private Response notFoundResponse() {
        LOGGER.warning("Returning 404 NOT FOUND response.");
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    private Response badRequestResponse() {
        LOGGER.warning("Returning 400 BAD REQUEST response.");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}