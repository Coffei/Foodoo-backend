package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.PriceConstant;
import cz.coffei.foodo.data.entities.PriceConstant_;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by jtrantin on 26.7.15.
 */
@Stateless
public class PriceConstantDao {

    @Inject
    private EntityManager em;

    public PriceConstant getPriceConstant(String name, Integer defaultValue) {
        if(name==null || name.isEmpty()) throw new IllegalArgumentException("empty name");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PriceConstant> query = cb.createQuery(PriceConstant.class);

        Root<PriceConstant> root = query.from(PriceConstant.class);
        query.select(root);
        query.where(cb.equal(cb.lower(root.get(PriceConstant_.name)), name.toLowerCase()));

        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            if(defaultValue!=null) {
                return new PriceConstant(name, defaultValue);
            }
            throw e;
        }
    }

    public PriceConstant getPriceConstant(String name) {
        return this.getPriceConstant(name, null);
    }

    public void setConstant(PriceConstant constant) {
        em.merge(constant);
    }

    public void deleteconstant(PriceConstant constant) {
        if(!em.contains(constant)) {
            constant = em.merge(constant);
        }

        em.remove(constant);
    }
}
