package common;

import java.util.*;

public class Supplier extends Model {
    private String name;
    private double distance;
    private ArrayList<Ingredient> merchandise;

    Supplier(String name, double distance, ArrayList<Ingredient> merchandise) {
        this.name = name;
        this.distance = distance;
        this.merchandise = merchandise;
    }

    public String getName() {
        return this.name;
    }

    void setDistance(double distance) {
        notifyUpdate("distance", this.distance, distance);
        this.distance = distance;
    }

    void addMerchandise(Ingredient newIngredient) {
        ArrayList<Ingredient> oldList = new ArrayList<>(this.merchandise);
        this.merchandise.add(newIngredient);
        notifyUpdate("merchandise", oldList, this.merchandise);
    }
}
