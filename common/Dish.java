package common;

import java.util.*;

public class Dish extends Model {
    private String description;
    private double price;
    private HashMap<Ingredient, Integer> recipe;

    Dish(
            String name,
            String description,
            double price,
            HashMap<Ingredient, Integer> recipe
        ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.recipe = recipe;
    }

    public String getName() {
        return this.name;
    }

    HashMap<Ingredient, Integer> getRecipe() {
        return this.recipe;
    }

    void deleteIngredient(Ingredient key) {
        HashMap<Ingredient, Integer> oldRecipe = new HashMap<>(this.recipe);
        this.recipe.remove(key);
        notifyUpdate("recipe", oldRecipe, this.recipe);
    }

    void changeIngredientAmount(Ingredient key, int amount) {
        HashMap<Ingredient, Integer> oldRecipe = new HashMap<>(this.recipe);
        this.recipe.replace(key, amount);
        notifyUpdate("recipe", oldRecipe, this.recipe);
    }

    double getPrice() {
        return this.price;
    }

    void setPrice(double price) {
        notifyUpdate("price", this.price, price);
        this.price = price;
    }

    String getDescription() {
        return this.description;
    }

    void setDescription(String description) {
        notifyUpdate("description", this.description, description);
        this.description = description;
    }
}
