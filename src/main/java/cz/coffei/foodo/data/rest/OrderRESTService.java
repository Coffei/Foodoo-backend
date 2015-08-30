package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.OrderDao;
import cz.coffei.foodo.data.dao.PriceConstantDao;
import cz.coffei.foodo.data.entities.Order;
import cz.coffei.foodo.data.entities.PriceConstant;
import cz.coffei.foodo.data.enums.OrderStatus;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.mail.MailSender;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by jtrantin on 26.7.15.
 */
@Path("/orders")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
@RequestScoped
public class OrderRESTService {

    private int MAX_MIN_CANCEL = 15;

    @Inject
    private Logger log;

    @Inject
    private OrderDao dao;

    @Inject
    private PriceConstantDao priceConstantDao;

    @Inject
    private MailSender mailSender;

    @GET
    @Path("/status/{statuses}")
    public Response getOrdersByStatus(@PathParam("statuses") String statuses) {
        List<OrderStatus> allowedStatuses = Collections.emptyList();
        try {
            Stream<OrderStatus> orderStatusStream = Stream.of(statuses.split(",")).map(OrderStatus::valueOf);
            allowedStatuses = orderStatusStream.collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid order status").build();
        }

        List<Order> orders = dao.getOrdersByStatuses(allowedStatuses);
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{id}")
    public Response findOrderById(@PathParam("id") Long id) {
        try {
            Order order = dao.getOrderById(id);
            return Response.ok(order).build();
        } catch (EJBException e) {
            NoResultException nre = ErrorHelper.findExceptionByClass(e, NoResultException.class);
            if (nre != null) {
                return Response.noContent().build();
            }

            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    public Response createOrder(Order order) {
        log.info("Creating order:");
        log.info(order.toString());

        try {
            order = dao.createOrder(order);
            PriceConstant takeaway = priceConstantDao.getPriceConstant("takeaway", 0);
            try {
                mailSender.sendNewOrderEmail(order, takeaway);
            } catch (MessagingException e) {
                log.warning("Cannot send email for new order " + order.getId());
            }
            log.info("Created!");
            return Response.created(null).entity(order.getId()).build();
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
        boolean statusChanged = !currentOrder.getStatus().equals(order.getStatus());
        if (order.getCustomeremail() != null) currentOrder.setCustomeremail(order.getCustomeremail());
        if (order.getCustomerName() != null) currentOrder.setCustomerName(order.getCustomerName());
        if (order.getStatus() != null) currentOrder.setStatus(order.getStatus());

        log.info("Updating order:");
        log.info(currentOrder.toString());

        try {
            dao.updateOrder(currentOrder);
            PriceConstant takeaway = priceConstantDao.getPriceConstant("takeaway", 0);
            try {
                mailSender.sendStatusChangedEmail(currentOrder, takeaway, false);
            } catch (MessagingException e) {
                log.warning("Cannot send email for order status change: " + currentOrder.getId());
                log.warning(e.toString());
            }
            log.info("Updated!");
            return Response.ok().build();
        } catch (EntityInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    /**
     * Method used for cancelling the order by customer
     * @param id identificator of the order to cancel
     * @return
     */
    @POST
    @Path(("/{id}/cancel"))
    public Response cancelOrder(@PathParam("id") Long id) {
        Order currentOrder = dao.getOrderById(id);


        Duration duration = Duration.between(currentOrder.getCreated().toLocalDateTime().atZone(ZoneId.of("UTC")), ZonedDateTime.now(ZoneId.of("UTC")));
        log.info(currentOrder.getCreated().toString());
        log.info(duration.toString());
        if (duration.compareTo(Duration.ofMinutes(MAX_MIN_CANCEL)) > 0) {
            // it is more than 15 minutes from creation
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        currentOrder.setStatus(OrderStatus.CANCELLED);
        try {
            dao.updateOrder(currentOrder);
            PriceConstant takeaway = priceConstantDao.getPriceConstant("takeaway", 0);
            try {
                mailSender.sendStatusChangedEmail(currentOrder, takeaway, true);
            } catch (MessagingException e) {
                log.warning("Cannot send email for order cancellation: " + currentOrder.getId());
                log.warning(e.toString());
            }
            log.info("Order cancelled: "  + currentOrder.getId());
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
