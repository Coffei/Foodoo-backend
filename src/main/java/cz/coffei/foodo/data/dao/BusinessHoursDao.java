package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.BusinessHours;
import cz.coffei.foodo.data.entities.BusinessHours_;
import cz.coffei.foodo.data.enums.BusinessHoursType;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by jtrantin on 8.8.15.
 */
@Stateless
public class BusinessHoursDao {

    @Inject
    private EntityManager em;

    public BusinessHours getHours(BusinessHoursType type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BusinessHours> query = cb.createQuery(BusinessHours.class);

        Root<BusinessHours> root = query.from(BusinessHours.class);
        query.select(root);
        query.where(cb.equal(root.get(BusinessHours_.type), type));

        List<BusinessHours> results = em.createQuery(query).getResultList();
        return (results.isEmpty() ? null : results.get(0));
    }

    public void setHours(BusinessHours hours) {
        BusinessHours currentHours = getHours(hours.getType());
        if(currentHours!=null) {
            currentHours.setStartTime(hours.getStartTime());;
            currentHours.setEndTime(hours.getEndTime());;
            em.merge(currentHours);
        } else {
            em.persist(hours);
        }
    }
}
