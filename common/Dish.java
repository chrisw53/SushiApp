package common;

import java.io.Serializable;
import java.util.*;

// Implements Serializable so it can be sent via ObjectInputStream and ObjectOutputStream
public class Dish extends Model implements Serializable {
    private String name;
    private String description;
    private int price;
    private Map<Ingredient, Number> recipe;

    public Dish(
            String name,
            String description,
            int price,
            HashMap<Ingredient, Number> recipe
    ) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.recipe = recipe;
    }

    public String getName() {
        return this.name;
    }

    public Map<Ingredient, Number> getRecipe() {
        return this.recipe;
    }

    public void addIngredient(Ingredient key, int quant) {
        HashMap<Ingredient, Number> oldRecipe = new HashMap<>(this.recipe);
        this.recipe.put(key, quant);
        notifyUpdate("recipe", oldRecipe, this.recipe);
    }

    public void deleteIngredient(Ingredient key) {
        HashMap<Ingredient, Number> oldRecipe = new HashMap<>(this.recipe);
        this.recipe.remove(key);
        notifyUpdate("recipe", oldRecipe, this.recipe);
    }

    public void setRecipe(Map<Ingredient, Number> recipe) {
        this.recipe = recipe;
    }

    public int getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }
}