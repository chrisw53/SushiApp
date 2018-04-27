package common;

import java.util.*;

public class StockManagement {
    class StockInfo {
        private int threshold;
        private int amountToAdd;
        private double quant;

        StockInfo(int threshold, int amountToAdd, double quant) {
            this.threshold = threshold;
            this.amountToAdd = amountToAdd;
            this.quant = quant;
        }

        int getThreshold() {
            return this.threshold;
        }

        int getAmountToAdd() {
            return this.amountToAdd;
        }

        double getQuant() {
            return this.quant;
        }

        void setQuant() {
            this.quant += this.amountToAdd;
        }

        void setQuant(double minus) {
            this.quant -= minus;
        }
    }

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
