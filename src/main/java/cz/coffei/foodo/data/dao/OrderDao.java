package cz.coffei.foodo.data.dao;

import com.sun.corba.se.spi.protocol.InitialServerRequestDispatcher;
import cz.coffei.foodo.data.entities.*;
import cz.coffei.foodo.data.enums.OrderItemType;
import cz.coffei.foodo.data.enums.OrderStatus;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by jtrantin on 26.7.15.
 */
@Stateless
public class OrderDao {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private MenuDao menuDao;

    @Inject
    private IngredientGroupDao groupDao;

    @Inject
    private IngredientDao ingredientDao;

    @Inject
    private PriceConstantDao priceConstantDao;

    public void createOrder(Order order) throws EntityInvalidException {
        Integer takeawayPrice = priceConstantDao.getPriceConstant("takeaway", 0).getValue();
        Integer price = 0;
        for(OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);

            if(orderItem.getType()== OrderItemType.MENU) {
                if(orderItem.getMenu()==null || orderItem.getId()==null) throw new EntityInvalidException("type MENU specified but no menu present");
                MenuItem menu = menuDao.getMenuItem(orderItem.getMenu().getId());
                price += menu.getPrice() * (orderItem.getTimes() != null ? orderItem.getTimes() : 1);
            } else { // custom salad
                if(orderItem.getIngredients()==null) throw new EntityInvalidException("type SALAD specified but no ingredients present");
                List<Ingredient> ingredients = fetchAllIngredients(orderItem.getIngredients());

                // get all required groups & check ingredients are selected properly
                List<IngredientGroup> requiredGroups = groupDao.getAllRequiredGroups();
                for (IngredientGroup group : requiredGroups) {
                    if(ingredients.stream().noneMatch((ingredient -> ingredient.getGroup().equals(group))))
                        throw new EntityInvalidException("No ingredients from group " + group.getName() + " are selected.");
                }

                // check whether groups that do not allow more ingredients are not misused
                Stream<IngredientGroup> mappedGroups = ingredients.stream().map(Ingredient::getGroup).filter(group -> !group.isAllowMore());
                List<IngredientGroup> usedGroups = ingredients.stream().map(Ingredient::getGroup).filter(group -> !group.isAllowMore()).distinct().collect(Collectors.toList());
                for(IngredientGroup group : usedGroups) {
                    if(mappedGroups.filter(filterGroup -> filterGroup.equals(group)).count() > 1)
                        throw new EntityInvalidException("More ingredients from group " + group.getName() + " selected even though it's forbidden");
                }

                for (Ingredient ingredient : ingredients) {
                    if (ingredient.getPrice() != null) {
                        price += ingredient.getPrice() * (orderItem.getTimes() != null ? orderItem.getTimes() : 1);
                    } else {
                        price += ingredient.getGroup().getPrice() * (orderItem.getTimes() != null ? orderItem.getTimes() : 1);
                    }
                }
            }
        }
        if (order.isTakeaway()) {
            int portionCount = order.getOrderItems().stream().mapToInt(orderItem -> orderItem.getTimes() != null ? orderItem.getTimes() : 1).sum();
            price += portionCount * takeawayPrice;
        }

        order.setStatus(OrderStatus.NEW);
        int offset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
        order.setCreated(new Timestamp(System.currentTimeMillis() - offset));
        order.setTotalPrice(price);
        em.persist(order);
    }

    public void updateOrder(Order order) throws EntityInvalidException {
        if(order.getId()==null) throw new EntityInvalidException("entity has no id");

        em.merge(order);
    }

    public List<Order> getAllOrders() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);

        Root<Order> root = query.from(Order.class);
        query.select(root);

        TypedQuery<Order> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public Order getOrderById(Long id) {
        if(id==null) throw new IllegalArgumentException("entity has no id");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);

        Root<Order> root = query.from(Order.class);
        query.select(root);
        query.where(cb.equal(root.get(Order_.id), id));

        TypedQuery<Order> typedQuery = em.createQuery(query);
        return typedQuery.getSingleResult();
    }

    public void deleteOrder(Order order) throws EntityInvalidException {
        if(order.getId()==null) throw new EntityInvalidException("entity has no id");
        if (!em.contains(order)) {
            order = em.merge(order);
        }

        em.remove(order);
    }

    private List<Ingredient> fetchAllIngredients(List<Ingredient> ingredients) {
        List<Ingredient> result = ingredients.stream().mapToLong((Ingredient::getId))
                .mapToObj(ingredientDao::getIngredientById)
                .collect(Collectors.toList());

        return result;
    }

}