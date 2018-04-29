package common;

import java.util.*;

public class StockManagement {
    static HashMap<Ingredient, StockInfo> ingredients = new HashMap<>();
    static HashMap<Dish, StockInfo> dishes = new HashMap<>();

    StockManagement() {}

    StockManagement(
            HashMap<Ingredient, StockInfo> ingredients,
            HashMap<Dish, StockInfo> dishes
    ) {
        StockManagement.ingredients = ingredients;
        StockManagement.dishes = dishes;
    }

    StockInfo getIngredientInfo(Ingredient key) {
        return StockManagement.ingredients.get(key);
    }

    StockInfo getDishInfo(Dish key) {
        return StockManagement.dishes.get(key);
    }

    Set<Ingredient> getIngredients() {
        return StockManagement.ingredients.keySet();
    }

    Set<Dish> getDishes() {
        return StockManagement.dishes.keySet();
    }

    void addDish(Dish dish, StockInfo stockInfo) {
        StockManagement.dishes.put(dish, stockInfo);
    }

    void addIngredient(Ingredient ingredient, StockInfo stockInfo) {
        StockManagement.ingredients.put(ingredient, stockInfo);
    }
}
