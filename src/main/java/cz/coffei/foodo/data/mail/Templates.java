package cz.coffei.foodo.data.mail;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import cz.coffei.foodo.data.entities.Ingredient;
import cz.coffei.foodo.data.entities.Order;
import cz.coffei.foodo.data.entities.OrderItem;
import cz.coffei.foodo.data.entities.PriceConstant;
import cz.coffei.foodo.data.enums.OrderItemType;
import cz.coffei.foodo.data.util.Properties;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jtrantin on 29.8.15.
 */
public class Templates {
    private static Properties props = Properties.getInstance();

    public static String getTestTemplate(String name, String status, String date) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache template = mf.compile("test.mustache");

        StringWriter writer = new StringWriter();
        HashMap<String, String> scopes = new HashMap<>();
        scopes.put("catname", name);
        scopes.put("status", status);
        scopes.put("date", date);

        template.execute(writer, scopes);

        return writer.toString();
    }

    public static String newOrderHTMLTemplate(Order order, PriceConstant takeawayConstant) {
        return newOrderTemplate(order, takeawayConstant, "orderCreated.html.mustache");
    }

    public static String newOrderTextTemplate(Order order, PriceConstant takeawayConstant) {
        return newOrderTemplate(order, takeawayConstant, "orderCreated.text.mustache");
    }

    public static String cancelledOrderTextTemplate(Order order, PriceConstant takeawayConstant, boolean userInitiated) {
        return cancelledOrderTemplate(order, takeawayConstant, userInitiated, "orderCancelled.text.mustache");
    }

    public static String cancelledOrderHTMLTemplate(Order order, PriceConstant takeawayConstant, boolean userInitiated) {
        return cancelledOrderTemplate(order, takeawayConstant, userInitiated, "orderCancelled.html.mustache");
    }

    public static String finishedOrderTextTemplate(Order order, PriceConstant takeaway) {
        return finishedOrderTemplate(order, takeaway, "orderFinished.text.mustache");
    }

    public static String finishedOrderHTMLTemplate(Order order, PriceConstant takeaway) {
        return finishedOrderTemplate(order, takeaway, "orderFinished.html.mustache");
    }

    private static String finishedOrderTemplate(Order order, PriceConstant takeaway, String templateName) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache template = mf.compile(templateName);
        StringWriter writer = new StringWriter();
        Map<String, Object> scope = convertOrderToTemplateScope(order, takeaway);

        template.execute(writer, scope);

        return writer.toString();
    }

    private static String cancelledOrderTemplate(Order order, PriceConstant takeaway, boolean userInitiated, String templateName) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache template = mf.compile(templateName);
        StringWriter writer = new StringWriter();
        Map<String, Object> scope = convertOrderToTemplateScope(order, takeaway);
        scope.put(userInitiated ? "user_cancelled" : "admin_cancelled", true);

        template.execute(writer, scope);

        return writer.toString();
    }

    private static String newOrderTemplate(Order order, PriceConstant takeawayConstant, String templateName) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache template = mf.compile(templateName);
        StringWriter writer = new StringWriter();
        Map<String, Object> scope = convertOrderToTemplateScope(order, takeawayConstant);

        template.execute(writer, scope);

        return writer.toString();
    }

    private static Map<String, Object> convertOrderToTemplateScope(Order order, PriceConstant takeawayConstant){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        HashMap<String, Object> scope = new HashMap<>();
        scope.put("id", order.getId());
        scope.put("takeaway", order.isTakeaway());
        scope.put("targetTimeSelected", order.getTargetTime()!=null);
        scope.put("price", order.getTotalPrice());
        if(order.getTargetTime()!=null) {
            String targetTime = order.getTargetTime()
                    .atDate(LocalDate.now(ZoneId.of("UTC")))
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of(props.get("clientTimezone")))
                    .format(timeFormatter);
            scope.put("targetTime", targetTime);
        }
        scope.put("details_link", props.get("clientHost") + "/#/order/" + order.getId());
        scope.put("cancel_link", props.get("clientHost") + "/#/order/" + order.getId());
        List<Map<String, Object>> items = new ArrayList<>();
        for(OrderItem item : order.getOrderItems()) {
            Map<String, Object> itemScope = new HashMap<>();
            if(item.getType()== OrderItemType.MENU) {
                itemScope.put("type", "Menu");
                itemScope.put("name", item.getMenu().getName());
                int price = item.getMenu().getPrice() * item.getTimes();
                if(order.isTakeaway()) {
                    price += takeawayConstant.getValue() * item.getTimes();
                }
                itemScope.put("price", price);
            } else if (item.getType()==OrderItemType.CUSTOMSALAD) {
                itemScope.put("type", "Salad");
                itemScope.put("name", item.getIngredients().stream().map(Ingredient::getName).collect(Collectors.joining(", ")));
                int price = item.getIngredients().stream().mapToInt(ingredient -> {
                    if(ingredient.getPrice()!=null) return ingredient.getPrice();

                    return ingredient.getGroup().getPrice();
                }).sum() * item.getTimes();
                if (order.isTakeaway()) {
                    price += takeawayConstant.getValue() * item.getTimes();
                }
                itemScope.put("price", price);
            }
            itemScope.put("times", item.getTimes());

            items.add(itemScope);
        }

        scope.put("items", items);

        return scope;
    }
}
