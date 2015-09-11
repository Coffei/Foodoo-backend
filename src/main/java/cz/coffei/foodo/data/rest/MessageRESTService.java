package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.MessageDao;
import cz.coffei.foodo.data.entities.Message;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;

import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by jtrantin on 11.9.15.
 */
@Path("/messages")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
@RequestScoped
public class MessageRESTService {

    @Inject
    private MessageDao messageDao;

    @Path("/{type}")
    @GET
    public Response getMessageByType(@PathParam("type") String type) {
        if(type==null || type.isEmpty()) return Response.noContent().build();

        try {
            Message msg = messageDao.getMessageByType(type);
            if(msg!=null) return Response.ok(msg).build();
            else return Response.noContent().build();
        } catch (EJBException e) {
            if(ErrorHelper.findExceptionByClass(e, NoResultException.class) != null) {
                return Response.noContent().build();
            }
        }

        return Response.serverError().build();
    }

    @Path("/{type}")
    @POST
    public Response createMessage(Message msg, @PathParam("type") String type) {
        if(msg == null || type==null || type.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();

        msg.setType(type);

        try {
            messageDao.createMessage(msg);
            return Response.created(null).build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }

    @Path("/{type}")
    @PUT
    public Response updateMessage(Message msg, @PathParam("type") String type) {
        if(msg== null || type==null || type.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();

        try {
            Message currentMsg = messageDao.getMessageByType(type);
            currentMsg.setContent(msg.getContent());
            messageDao.updateMessage(currentMsg);
            return Response.ok().build();
        } catch(EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        } catch (EJBException e) {
            if (ErrorHelper.findExceptionByClass(e, NoResultException.class) != null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        return Response.serverError().build();
    }

    @Path("/{type}")
    @DELETE
    public Response deleteMessage(@PathParam("type") String type) {
        if(type==null || type.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();

        try {
            Message msg = messageDao.getMessageByType(type);
            messageDao.deleteMessage(msg);
            return Response.ok().build();
        } catch (EJBException e) {
            if (ErrorHelper.findExceptionByClass(e, NoResultException.class) != null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
