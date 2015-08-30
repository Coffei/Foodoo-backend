package cz.coffei.foodo.data.util;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.logging.Logger;

/**
 * Created by jtrantin on 25.7.15.
 */
public class Resources {

    @Produces
    @PersistenceContext
    private EntityManager em;

    @Resource(mappedName = "java:jboss/mail/gmail")
    @Produces
    Session emailSession;

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
