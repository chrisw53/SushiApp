package common;

import java.util.*;

public class StockManagement {
    public static HashMap<Ingredient, StockInfo> ingredients = new HashMap<>();
    public static HashMap<Dish, StockInfo> dishes = new HashMap<>();

    public StockManagement() {}

    StockManagement(
            HashMap<Ingredient, StockInfo> ingredients,
            HashMap<Dish, StockInfo> dishes
    ) {
        StockManagement.ingredients = ingredients;
        StockManagement.dishes = dishes;
    }
}
