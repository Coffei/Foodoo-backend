package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.OrderDao;
import cz.coffei.foodo.data.entities.Order;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;

import javax.ejb.EJBException;
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
@Path("/orders")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class OrderRESTService {

    @Inject
    private Logger log;

    @Inject
    private OrderDao dao;

    @GET
    public List<Order> getAllOrders() {
        return dao.getAllOrders();
    }

    @POST
    public Response createOrder(Order order) {
        log.info("Creating order:");
        log.info(order.toString());

        try {
            dao.createOrder(order);
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
    public Response updateOrder(Order order, @PathParam("id") Long id) {
        Order currentOrder = dao.getOrderById(id);
        if(order.getCustomeremail()!=null) currentOrder.setCustomeremail(order.getCustomeremail());
        if(order.getCustomerName()!=null) currentOrder.setCustomerName(order.getCustomerName());
        if(order.getStatus()!=null) currentOrder.setStatus(order.getStatus());

        log.info("Updating order:");
        log.info(currentOrder.toString());

        try {
            dao.updateOrder(currentOrder);
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
    public Response deleteOrder(@PathParam("id") Long id) {
        Order order = null;
        try {
            order = dao.getOrderById(id);
        } catch (EJBException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        log.info("Deleting order:");
        log.info(order.toString());

        try {
            dao.deleteOrder(order);
            log.info("Deleted!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

}
