package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.IngredientGroup;
import cz.coffei.foodo.data.entities.IngredientGroup_;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by jtrantin on 26.7.15.
 */
@Stateless
public class IngredientGroupDao {

    @Inject
    private EntityManager em;

    public void create(IngredientGroup group) throws EntityInvalidException {
        if(group.getId()!=null) throw new EntityInvalidException("entity ha non-null ID");

        em.persist(group);
    }

    public void update(IngredientGroup group) throws EntityInvalidException {
        if(group.getId()==null) throw new EntityInvalidException("entity has no ID");
        try {
            IngredientGroup oldGroup = getGroupById(group.getId());
            group.setIngredients(oldGroup.getIngredients());
        } catch (NoResultException e) {
            throw new EntityInvalidException("entity has invalid ID", e);
        }

        em.merge(group);
    }

    public void delete(IngredientGroup group) throws EntityInvalidException {
        if(group.getId()==null) throw new EntityInvalidException("entity has no ID");

        if(!em.contains(group)) {
            group = em.merge(group);
        }

        em.remove(group);
    }

    public IngredientGroup getGroupById(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IngredientGroup> query = cb.createQuery(IngredientGroup.class);

        Root<IngredientGroup> root = query.from(IngredientGroup.class);
        query.select(root);
        query.where(cb.equal(root.get(IngredientGroup_.id), id));

        TypedQuery<IngredientGroup> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult();
    }

    public List<IngredientGroup> getAllGroups() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IngredientGroup> query = cb.createQuery(IngredientGroup.class);

        Root<IngredientGroup> root = query.from(IngredientGroup.class);
        query.select(root);

        return em.createQuery(query).getResultList();
    }

    public List<IngredientGroup> getAllRequiredGroups() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IngredientGroup> query = cb.createQuery(IngredientGroup.class);

        Root<IngredientGroup> root = query.from(IngredientGroup.class);
        query.select(root);
        query.where(cb.equal(root.get(IngredientGroup_.required), true));

        return em.createQuery(query).getResultList();
    }

    public IngredientGroup fetchCollections(IngredientGroup group) {
        if(!em.contains(group)) {
            group = em.merge(group);
        }
        group.getIngredients().size();

        return group;
    }
}
