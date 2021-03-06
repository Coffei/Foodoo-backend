package cz.coffei.foodo.data.rest;

import cz.coffei.foodo.data.dao.TestDao;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;
import cz.coffei.foodo.data.mail.MailSender;
import cz.coffei.foodo.data.mail.Templates;
import cz.coffei.foodo.data.util.Properties;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 25.7.15.
 */
@Path("/test")
public class TestService {

    @Inject
    private TestDao dao;

    @Inject
    private Logger log;

    @Inject
    private MailSender sender;

    @GET
    @Produces("text/html")
    public String getTestPage() {
        return "<html><head><script src=\"https://code.jquery.com/jquery-2.1.4.js\"></script></head><body>Do your testing here.</body></html>";
    }

    @GET
    @Path("/createentities")
    public Response createTestEntities() {
        try {
            dao.createTestEntities();
            return Response.ok().build();
        } catch (Exception e) {
            log.warning(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/properties/{name}")
    public Response getProperty(@PathParam("name") String key) {
        Properties props = Properties.getInstance();
        return Response.ok(props.get(key)).build();
    }

}
