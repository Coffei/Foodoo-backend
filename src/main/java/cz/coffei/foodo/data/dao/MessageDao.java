package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.Message;
import cz.coffei.foodo.data.entities.Message_;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Objects;

/**
 * Created by jtrantin on 11.9.15.
 */
@Stateless
public class MessageDao {

    @Inject
    private EntityManager em;

    public Message getMessageByType(String type) {
        Objects.requireNonNull(type);
        if(type.isEmpty()) throw new IllegalArgumentException("empty type");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Message> query = cb.createQuery(Message.class);

        Root<Message> root = query.from(Message.class);
        query.select(root).where(cb.equal(root.get(Message_.type), type));

        TypedQuery<Message> typedQuery = em.createQuery(query);

        return typedQuery.getSingleResult();
    }

    public void createMessage(Message msg) {
        em.persist(msg);
    }

    public void updateMessage(Message msg) {
        em.merge(msg);
    }

    public void deleteMessage(Message msg) {
        if(!em.contains(msg)) {
            msg = em.merge(msg);
        }

        em.remove(msg);
    }
}
