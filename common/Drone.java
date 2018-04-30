package common;

import java.util.*;

public class Drone extends Model implements Runnable {
    private String status = "Idle";
    private int speed;
    private ArrayList<Supplier> suppliers;
    private HashMap<Postcode, Long> postcodeDistance;

    public Drone(
            int speed,
            ArrayList<Supplier> suppliers,
            HashMap<Postcode, Long> postcodeDistance
    ) {
        this.speed = speed;
        this.suppliers = suppliers;
        this.postcodeDistance = postcodeDistance;
    }

    public String getName() {
        return "Drone";
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                monitorIngredient();
            } catch (InterruptedException e) {
                System.out.println("Drone monitor error: " + e);
            }
        }
    }

    String getStatus() {
        return this.status;
    }

    private void monitorIngredient() {
        for (Ingredient ingredient : StockManagement.ingredients.keySet()) {
            synchronized (this) {
                StockInfo stockInfo = StockManagement.ingredients.get(ingredient);
                if (stockInfo.getQuant() < stockInfo.getThreshold()) {
                    restockIngredient(ingredient);
                }
            }
        }
    }

    private void restockIngredient(Ingredient ingredient) {
        status = "Restocking";

        long timeToSupplier = ingredient.getSupplier().getDistance() / this.speed;

        try {
            Thread.sleep(timeToSupplier);
            StockManagement.ingredients.get(ingredient).setQuant();
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Restock ingredient error: " + e);
        }
    }

    void deliverOrder(Postcode postcode) {
        long deliveryTime = postcodeDistance.get(postcode) / this.speed;
        status = "Delivering";

        try {
            Thread.sleep(deliveryTime);
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Drone delivery error: " + e);
        }
    }
}
