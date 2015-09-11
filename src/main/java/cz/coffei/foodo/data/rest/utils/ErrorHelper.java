package cz.coffei.foodo.data.rest.utils;


import javax.ejb.EJBException;
import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 25.7.15.
 */
public class ErrorHelper {

    private static Logger log = Logger.getLogger("cz.coffei.foodo.data.rest.utils.ErrorHelper");

    static public Response processEJBTransactionFailure(EJBTransactionRolledbackException e) {
        ConstraintViolationException violationException = findExceptionByClass(e, ConstraintViolationException.class);
        if(violationException!=null) {
            return getResponseForConstraintViolation(violationException);
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }



    static public Response getResponseForConstraintViolation(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        Object[] violationEntities = violations.stream().map((violation) -> new ViolationEntity(violation.getPropertyPath(), violation.getConstraintDescriptor().getAnnotation(), violation.getMessage())).toArray();

        return Response.status(Response.Status.BAD_REQUEST).entity(violationEntities).type(MediaType.APPLICATION_JSON_TYPE).build();
    }



    static public <T extends Exception> T findExceptionByClass(Exception e, Class<T> exceptionClass) {
        Exception exception = e;
        while(exception != null) {
            if (exception.getClass().toString().equals(exceptionClass.toString())) return exceptionClass.cast(exception);
            exception = (Exception)exception.getCause();
        }

        return null;
    }


    private static class ViolationEntity {
        private String path;
        private String constraint;
        private String message;

        public ViolationEntity(Path path, Object annotation, String message) {
            this.path = path.toString();
            this.constraint = annotation.toString();
            this.message = message;
        }


        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getConstraint() {
            return constraint;
        }

        public void setConstraint(String constraint) {
            this.constraint = constraint;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


}
