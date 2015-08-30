package cz.coffei.foodo.data.rest;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.coffei.foodo.data.dao.BusinessHoursDao;
import cz.coffei.foodo.data.entities.BusinessHours;
import cz.coffei.foodo.data.enums.BusinessHoursType;
import cz.coffei.foodo.data.rest.conversions.LocalTimeSerializer;
import cz.coffei.foodo.data.rest.utils.ErrorHelper;
import javax.ejb.EJBTransactionRolledbackException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Time;
import java.time.LocalTime;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 8.8.15.
 */
@Path("/hours")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
@RequestScoped
public class BusinessHoursRESTService {

    @Inject
    private BusinessHoursDao dao;

    @Inject
    private Logger log;

    @Path("/{type}")
    @GET
    public Response getHours(@PathParam("type") String type) {


        BusinessHoursType hoursType;
        try {
            hoursType = BusinessHoursType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(404).build();
        }

        BusinessHours hours = dao.getHours(hoursType);
        if(hours!=null) {
            return Response.ok(hours).build();
        } else {
            return Response.noContent().build();
        }
    }

    @Path("/{type}")
    @POST
    public Response setHours(@PathParam("type") String type, BusinessHours hours) {
        BusinessHoursType hoursType;
        try {
            hoursType = BusinessHoursType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.status(404).build();
        }

        hours.setType(hoursType);
        try {
            dao.setHours(hours);
            return Response.ok().build();
        } catch (EJBTransactionRolledbackException e) {
            return ErrorHelper.processEJBTransactionFailure(e);
        }
    }
}

