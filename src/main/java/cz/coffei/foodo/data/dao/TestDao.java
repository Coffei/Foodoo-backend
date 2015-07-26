package cz.coffei.foodo.data.dao;

import cz.coffei.foodo.data.entities.*;
import cz.coffei.foodo.data.enums.OrderItemType;
import cz.coffei.foodo.data.exceptions.EntityInvalidException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jtrantin on 26.7.15.
 */
@Stateless
public class TestDao {

    @Inject
    private IngredientDao ingredientDao;

    @Inject
    private IngredientGroupDao groupDao;

    @Inject
    private PriceConstantDao priceConstantDao;

    @Inject
    private OrderDao orderDao;

    public void createTestEntities() throws EntityInvalidException {
        priceConstantDao.setConstant(new PriceConstant("takeaway", 10));

        IngredientGroup greens = new IngredientGroup();
        greens.setName("Greens");
        greens.setPrice(40);
        greens.setRequired(true);
        greens.setAllowMore(false);

        IngredientGroup cheeses = new IngredientGroup();
        cheeses.setName("Cheese");
        cheeses.setPrice(19);
        cheeses.setRequired(false);
        cheeses.setAllowMore(true);

        groupDao.create(greens);
        groupDao.create(cheeses);

        Ingredient leafs = new Ingredient();
        leafs.setName("Tree leaves");
        leafs.setPrice(10);
        leafs.setDescription("Fresh tree leaves gathered just this morning. A budget option.");
        leafs.setGroup(greens);

        Ingredient rucola = new Ingredient();
        rucola.setName("Rucola");
        rucola.setGroup(greens);

        Ingredient parmesan = new Ingredient();
        parmesan.setName("Parmesan");
        parmesan.setGroup(cheeses);
        parmesan.setDescription("Good ol' classic.");

        Ingredient gold = new Ingredient();
        gold.setName("Gold cheese");
        gold.setPrice(100);
        gold.setGroup(cheeses);
        gold.setDescription("Made from gold! Inedible but expensive.");

        ingredientDao.create(leafs);
        ingredientDao.create(rucola);
        ingredientDao.create(parmesan);
        ingredientDao.create(gold);


        Order order = new Order();
        order.setCustomerName("Jonáš Trantina");
        order.setTakeaway(true);

        OrderItem item = new OrderItem();
        item.setType(OrderItemType.CUSTOMSALAD);
        item.setTimes(2);
        List<Ingredient> ingredients = new ArrayList<>(3);
        ingredients.add(rucola);
        ingredients.add(gold);
        item.setIngredients(ingredients);

        order.setOrderItems(Collections.singletonList(item));
        orderDao.createOrder(order);

    }
}
