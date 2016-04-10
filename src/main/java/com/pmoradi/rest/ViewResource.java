package com.pmoradi.rest;

import com.pmoradi.essentials.EntryUtil;
import com.pmoradi.rest.entries.*;
import com.pmoradi.security.RequestInterval;
import com.pmoradi.system.Facade;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class ViewResource {

    @Inject
    private Facade logic;

    @POST
    @RequestInterval(5000)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("view/all")
    public Response viewAll(ViewEntry in) throws IOException {
        EntryUtil.shrink(in);

        if(in.getGroupName().isEmpty()){
            return Response.status(Status.FORBIDDEN).entity(new GenericMessage("Group can not be empty")).build();
        } else if(in.getGroupName().equals("default")) {
            return Response.status(Status.FORBIDDEN).entity(new GenericMessage("Can not fetch data from the default group")).build();
        }

        GroupEntry groupEntry = logic.getGroupData(in.getGroupName(), in.getPassword());
        if(groupEntry == null) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("Group and password mismatch.")).build();
        } else {
            return Response.ok(groupEntry).build();
        }
    }

    @GET
    @Path("{url}/view")
    public Response viewSingle(@PathParam("url") String urlName) throws IOException {

        UrlEntry urlData = logic.getUrlData("default", urlName.trim().toLowerCase());

        if(urlData == null) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("URL not found.")).build();
        } else {
            URLInfoEntry entry = new URLInfoEntry();
            entry.setLink(urlData.getLink());
            entry.setUrl(urlData.getUrlName());
            entry.setGroup("default");
            entry.setClicks(urlData.getClicks());

            return Response.ok(entry).build();
        }
    }

    @GET
    @Path("{group}/{url}/view")
    public Response viewSingle(@PathParam("group") String groupName,
                               @PathParam("url") String urlName) throws IOException {

        UrlEntry urlData = logic.getUrlData(groupName.toLowerCase(), urlName.toLowerCase());
        if(urlData == null) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("URL not found.")).build();
        } else {
            URLInfoEntry entry = new URLInfoEntry();
            entry.setLink(urlData.getLink());
            entry.setUrl(urlData.getUrlName());
            entry.setGroup(groupName);

            return Response.ok(entry).build();
        }
    }
}
