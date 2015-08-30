package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.PriceConstantDao;
import cz.coffei.foodo.data.entities.PriceConstant;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by jtrantin on 8.8.15.
 */
@Path("/constants")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
@RequestScoped
public class PriceConstantRESTService {

    @Inject
    private PriceConstantDao dao;

    @POST
    @Path("/{name}")
    public Response setConstant(@PathParam("name") String name, PriceConstant constant) {
        constant.setName(name);
        try {
            dao.setConstant(constant);
            return Response.ok().build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }

    }

    @GET
    @Path("/{name}")
    public Response getConstant(@PathParam("name") String name) {
        try {
            PriceConstant constant = dao.getPriceConstant(name);
            return Response.ok(constant).build();
        } catch (EJBException e) {
            NoResultException ex = ErrorHelper.findExceptionByClass(e, NoResultException.class);
            if(ex!=null) {
                return Response.noContent().build();
            }

            return Response.serverError().build();
        }
    }
}
