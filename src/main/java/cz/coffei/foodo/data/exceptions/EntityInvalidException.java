package cz.coffei.foodo.data.exceptions;

/**
 * Created by jtrantin on 25.7.15.
 */
public class EntityInvalidException extends Exception {
    public EntityInvalidException(Throwable cause) {
        super(cause);
    }

    public EntityInvalidException() {
    }

    public EntityInvalidException(String message) {
        super(message);
    }

    public EntityInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityInvalidException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
