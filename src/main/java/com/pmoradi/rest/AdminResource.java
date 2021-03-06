package com.pmoradi.rest;

import com.pmoradi.rest.entries.GenericMessage;
import com.pmoradi.rest.entries.GroupEntry;
import com.pmoradi.rest.entries.UserEntry;
import com.pmoradi.security.Guarded;
import com.pmoradi.security.Role;
import com.pmoradi.system.AdminFacade;

import javax.inject.Inject;
import javax.security.auth.login.CredentialException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    private AdminFacade adminFacade;

    @GET
    @Guarded(Role.TRUSTED)
    @Path("group/{groupName}/view")
    public Response viewGroup(@PathParam("groupName") String groupName) {
        try {
            GroupEntry data = adminFacade.sneakPeek(groupName);
            return Response.ok(data).build();
        } catch (NotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("Group '" + groupName + "' was not found.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }

    @DELETE
    @Guarded(Role.MAINTAINER)
    @Path("group/{groupName}/delete")
    public Response deleteGroup(@PathParam("groupName") String groupName) {
        if(groupName.equals("default")) {
            return Response.status(Status.FORBIDDEN).entity(new GenericMessage("The default group can not be removed.")).build();
        }

        try {
            adminFacade.deleteGroup(groupName);
            return Response.ok(new GenericMessage("Group '" + groupName + "' was successfully removed!")).build();
        } catch (NotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("Can't delete a group that doesn't exist.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }

    @DELETE
    @Guarded(Role.MAINTAINER)
    @Path("url/{groupName}/{alias}/delete")
    public Response deleteUrl(@PathParam("groupName") String groupName,
                              @PathParam("alias") String alias) {
        try {
            adminFacade.deleteURL(groupName, alias);
            return Response.ok(new GenericMessage("Alias '" + alias + "' from group '" + groupName + "' was successfully removed!")).build();
        } catch (NotFoundException e) {
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("Alias or group doesn't exist.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }

    @POST
    @Guarded(Role.ADMINISTRATOR)
    @Path("user/add")
    public Response addUser(UserEntry user) {
        try {
            adminFacade.addUser(user.getUsername(), user.getPassword(), user.getRole());
            return Response.ok(new GenericMessage("User '" + user.getUsername() + "' was successfully added to the database!")).build();
        } catch (CredentialException e) {
            return Response.status(Status.FORBIDDEN).entity(new GenericMessage(e.getMessage())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }

    @POST
    @Guarded(Role.ADMINISTRATOR)
    @Path("user/role")
    public Response changeRole(UserEntry user) {
        try {
            adminFacade.changeRole(user.getUsername(), user.getRole());
            return Response.ok(new GenericMessage("User '" + user.getUsername() + "' changed role to " + user.getRole() + "!")).build();
        } catch (NotFoundException e){
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("The given user doesn't exist.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }

    @POST
    @Guarded(Role.ADMINISTRATOR)
    @Path("user/delete")
    public Response removeUser(UserEntry user) {
        try {
            adminFacade.removeUser(user.getUsername());
            return Response.ok(new GenericMessage("User '" + user.getUsername() + "' successfully removed!")).build();
        } catch (NotFoundException e){
            return Response.status(Status.NOT_FOUND).entity(new GenericMessage("The given user doesn't exist.")).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericMessage(e.getClass().getName() + ": " + e.getMessage())).build();
        }
    }
}