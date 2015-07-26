package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.IngredientDao;
import cz.coffei.foodo.data.entities.Ingredient;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;
import jdk.nashorn.internal.runtime.options.LoggingOption;

import javax.ejb.EJBTransactionRequiredException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 26.7.15.
 */
@Path("/ingredients")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class IngredientRESTService {

    @Inject
    private Logger log;

    @Inject
    private IngredientDao dao;

    @GET
    public List<Ingredient> getAllIngredients() {
        return dao.getAllIngredients();
    }

    @PUT
    @Path("/{id}")
    public Response updateIngredient(Ingredient ingredient, @PathParam("id") Long id) {
        ingredient.setId(id);

        log.info("Updating ingredient:");
        log.info(ingredient.toString());

        try {
            dao.update(ingredient);
            log.info("Updated!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch(EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteIngredient(@PathParam("id") Long id) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);

        log.info("Deleting ingredient:");
        log.info(ingredient.toString());

        try {
            dao.delete(ingredient);
            log.info("Deleted!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }
}
