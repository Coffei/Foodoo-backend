package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.MenuDao;
import cz.coffei.foodo.data.entities.MenuItem;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;
import sun.security.provider.certpath.OCSPResponse;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 25.7.15.
 */
@Path("/menus")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class MenuRESTService {

    @Inject
    private MenuDao dao;

    @Inject
    private Logger log;

    @GET
    @Path("/range/{from}/{to}")
    public Response getMenusForRange(@PathParam("from") Date from, @PathParam("to") Date to) {
        try {
            List<MenuItem> menus = dao.getMenusItemsInRange(from, to);
            return Response.ok().entity(menus).build();
        } catch (EJBException e) {
            IllegalArgumentException illegalArgumentException = ErrorHelper.findExceptionByClass(e, IllegalArgumentException.class);
            if(illegalArgumentException!=null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("invalid range").build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    public Response createMenu(MenuItem menu) {
        log.info("Creating new menu:");
        log.info(menu.toString());

        try {
            dao.create(menu);
            log.info("Created!");
            return Response.created(null).build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateMenu(MenuItem menu, @PathParam("id") Long id) {
        menu.setId(id);

        log.info("Updating menu:");
        log.info(menu.toString());

        try {
            dao.update(menu);
            log.info("Updated!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteMenu(@PathParam("id") Long id) {
        MenuItem menu = new MenuItem();
        menu.setId(id);

        log.info("Deleting menu:");
        log.info(menu.toString());

        try {
            dao.delete(menu);
            log.info("Deleted!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }




}
