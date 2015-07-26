package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.MenuItem;
import cz.coffei.foodo.data.entities.MenuItem_;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.util.List;

/**
 * Created by jtrantin on 25.7.15.
 */
@Stateless
public class MenuDao {

    @Inject
    private EntityManager em;

    public void create(MenuItem menu) throws EntityInvalidException {
        if(menu.getId()!=null) throw new EntityInvalidException("entity has non-null ID");

        em.persist(menu);
    }

    public void update(MenuItem menu) throws EntityInvalidException {
        if(menu.getId()==null) throw new EntityInvalidException("entity has no ID");

        em.merge(menu);
    }

    public void delete(MenuItem menu) throws EntityInvalidException {
        if(menu.getId()==null) throw new EntityInvalidException("entity has no ID");
        if(!em.contains(menu)) {
            menu = em.merge(menu);
        }

        em.remove(menu);
    }

    public MenuItem getMenuItem(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MenuItem> query = cb.createQuery(MenuItem.class);
        Root<MenuItem> root = query.from(MenuItem.class);

        query.select(root);
        query.where(cb.equal(root.get(MenuItem_.id), id));

        TypedQuery<MenuItem> typedQuery = em.createQuery(query);

        return typedQuery.getSingleResult();
    }

    /**
     * Retrieves MenuItems in the specified range, inclusive.
     * @param from inclusive from date
     * @param to inclusive to date
     * @return list of menus in the range, order is random
     */
    public List<MenuItem> getMenusItemsInRange(Date from, Date to) {
        if(from.after(to)) throw new IllegalArgumentException("invalid date interval");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MenuItem> query = cb.createQuery(MenuItem.class);
        Root<MenuItem> root = query.from(MenuItem.class);

        query.select(root);
        query.where(
                cb.greaterThanOrEqualTo(root.get(MenuItem_.date), from),
                cb.lessThanOrEqualTo(root.get(MenuItem_.date), to)
        );

        TypedQuery<MenuItem> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }

    public List<MenuItem> getAllMenuItems() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MenuItem> query = cb.createQuery(MenuItem.class);
        Root<MenuItem> root = query.from(MenuItem.class);

        query.select(root);
        TypedQuery<MenuItem> typedQuery = em.createQuery(query);

        return typedQuery.getResultList();
    }
}
