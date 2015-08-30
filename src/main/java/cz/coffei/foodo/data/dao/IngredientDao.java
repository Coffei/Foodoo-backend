package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.Ingredient;
import cz.coffei.foodo.data.entities.IngredientGroup;
import cz.coffei.foodo.data.entities.Ingredient_;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by jtrantin on 26.7.15.
 */
@Stateless
public class IngredientDao {

    @Inject
    private EntityManager em;

    public void create(Ingredient ingredient) throws EntityInvalidException {
        if(ingredient.getId()!=null) throw new EntityInvalidException("entity has non-null ID");

        em.persist(ingredient);
    }

    public void update(Ingredient ingredient) throws EntityInvalidException {
        if(ingredient.getId()==null) throw new EntityInvalidException("entity has no ID");

        em.merge(ingredient);
    }


    public void delete(Ingredient ingredient) throws EntityInvalidException {
        if(ingredient.getId()==null || ingredient.getGroup()==null) throw new EntityInvalidException("entity has no ID");

        IngredientGroup group = null;
        if(!em.contains(ingredient)) {
            group = em.merge(ingredient.getGroup());
        }

        group.getIngredients().remove(ingredient); // triggers removal of ingredient
    }

    public List<Ingredient> getAllIngredients() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingredient> query = cb.createQuery(Ingredient.class);

        Root<Ingredient> root = query.from(Ingredient.class);
        query.select(root);

        TypedQuery<Ingredient> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<Ingredient> getIngredientsInGroup(IngredientGroup group) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingredient> query = cb.createQuery(Ingredient.class);

        Root<Ingredient> root = query.from(Ingredient.class);
        query.select(root);
        query.where(cb.equal(root.get(Ingredient_.group), group));

        TypedQuery<Ingredient> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public Ingredient getIngredientById(Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ingredient> query = cb.createQuery(Ingredient.class);

        Root<Ingredient> root = query.from(Ingredient.class);
        query.select(root);
        query.where(cb.equal(root.get(Ingredient_.id), id));

        TypedQuery<Ingredient> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult();
    }




}
