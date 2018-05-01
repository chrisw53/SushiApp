package common;

import server.Server;

import java.util.*;

// Do Thread t = new Thread(Staff) when starting a new thread
public class Staff extends Model implements Runnable {
    private String status = "Idle";

    public Staff(String name) {
        this.name = name;
    }

    public void run() {
        while (Server.shouldRestockDish) {
            try {
                Thread.sleep(1000);
                monitorDishes();
            } catch (InterruptedException e) {
                System.out.println("Monitor pause error: " + e);
            }
        }
    }

    public String getName() {
        return "Staff";
    }

    public String getStatus() {
        return this.status;
    }

    private void monitorDishes() {
        for (Dish dish : StockManagement.dishes.keySet()) {
            synchronized (this) {
                StockInfo stock = StockManagement.dishes.get(dish);
                if (stock.getQuant() < stock.getThreshold()) {
                    makeNewDish(dish);
                }
            }
        }
    }

    private void makeNewDish(Dish dish) {
        Random rand = new Random();
        long sleepTimer = rand.nextInt(60000) + 20000;

        try {
            status = "Cooking";
            Thread.sleep(sleepTimer);
            // adds new dish
            StockManagement.dishes.get(dish).addQuant();
            // take away ingredients
            for (Ingredient ingredient : dish.getRecipe().keySet()) {
                int amount = (int) dish.getRecipe().get(ingredient)
                        * StockManagement.dishes.get(dish).getAmountToAdd();
                StockManagement.ingredients.get(ingredient).addQuant(-amount);
            }
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Dish pause error: " + e);
        }
    }
}
