package common;

import java.util.*;

public class Staff extends Thread {
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                monitorDishes();
            } catch (InterruptedException e) {
                System.out.println("Monitor pause error: " + e);
            }
        }
    }

    private void monitorDishes() {
        for (Dish dish : StockManagement.dishes.keySet()) {
            synchronized (this) {
                StockManagement.StockInfo stock = StockManagement.dishes.get(dish);
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
            Thread.sleep(sleepTimer);
            // adds new dish
            StockManagement.dishes.get(dish).setQuant();
            // take away ingredients
            for (Ingredient ingredient : dish.getRecipe().keySet()) {
                double amount = dish.getRecipe().get(ingredient)
                        * StockManagement.dishes.get(dish).getAmountToAdd();
                StockManagement.ingredients.get(ingredient).setQuant(amount);
            }
        } catch (InterruptedException e) {
            System.out.println("Dish pause error: " + e);
        } finally {
            monitorDishes();
        }
    }
}
