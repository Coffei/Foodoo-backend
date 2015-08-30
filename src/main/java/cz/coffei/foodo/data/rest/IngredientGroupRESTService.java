package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.IngredientDao;
import cz.coffei.foodo.data.dao.IngredientGroupDao;
import cz.coffei.foodo.data.entities.Ingredient;
import cz.coffei.foodo.data.entities.IngredientGroup;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 26.7.15.
 */
@Path("/groups")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
@RequestScoped
public class IngredientGroupRESTService {

    @Inject
    private Logger log;

    @Inject
    private IngredientGroupDao dao;

    @Inject
    private IngredientDao ingredientDao;

    @GET
    public List<IngredientGroup> getAllGroups() {
        return dao.getAllGroups();
    }

    // Groups section
    @GET
    @Path("/{id}")
    public Response getGroup(@PathParam("id") Long id) {
        try {
            IngredientGroup group = dao.getGroupById(id);
            return Response.ok().entity(group).build();
        } catch (EJBException e) {
            NoResultException noResultEx = ErrorHelper.findExceptionByClass(e, NoResultException.class);
            if(noResultEx!=null) {
                return Response.noContent().build();
            }

            return Response.serverError().build();
        }
    }

    @POST
    public Response createGroup(IngredientGroup group) {
        log.info("Creating group:");
        log.info(group.toString());

        try {
            dao.create(group);
            log.info("Created!");
            return Response.created(null).build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch(EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateGroup(IngredientGroup group, @PathParam("id") Long id) {
        group.setId(id);

        log.info("Updating group");
        log.info(group.toString());

        try {
            dao.update(group);
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
    public Response deleteGroup(@PathParam("id") Long id) {
        //try to fetch group
        IngredientGroup group;
        try {
            group = dao.getGroupById(id);
            group = dao.fetchCollections(group);
        } catch (EJBException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info("Deleting group: ");
        log.info(group.toString());

        try {
            dao.delete(group);
            log.info("Deleted!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch(EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }


    // Ingredients section
    @GET
    @Path("/{id}/ingredients")
    public List<Ingredient> getIngredientsByGroup(@PathParam("id") Long id) {
        IngredientGroup group = new IngredientGroup();
        group.setId(id);

        return ingredientDao.getIngredientsInGroup(group);
    }

    @POST
    @Path("/{id}/ingredients")
    public Response createIngredient(Ingredient ingredient, @PathParam("id") Long groupId) {
        IngredientGroup group = new IngredientGroup();
        group.setId(groupId);

        ingredient.setGroup(group);

        log.info("Creating ingredient:");
        log.info(ingredient.toString());

        try {
            ingredientDao.create(ingredient);
            log.info("Created!");
            return Response.created(null).build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }
}
