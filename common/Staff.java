package common;

import java.util.*;

/**
 * Staff implements Runnable to be able to work with Threads
 */
public class Staff extends Model implements Runnable {
    private String status = "Idle";

    public Staff(String name) {
        this.name = name;
    }

    public void run() {
        while (true) {
            // If shouldRestockDish is true then Staff should constantly monitor dishes
            if (Database.shouldRestockDish) {
                try {
                    Thread.sleep(1000);
                    monitorDishes();
                } catch (InterruptedException e) {
                    System.out.println("Monitor pause error: " + e);
                }
            }

            // If ordersToBeProcessed isn't empty, fulfill the latest order
            if (!Database.ordersToBeProcessed.isEmpty()) {
                Order order = latestOrder();

                for (Dish d : order.getDish().keySet()) {
                    while (StockManagement.dishes.get(d).getQuant() < order.getDish().get(d)) {
                        makeNewDish(d);
                    }

                    // Takes away the dish from the store
                    StockManagement.dishes.get(d).setQuant(-order.getDish().get(d));
                }

                order.setStatus("Completed");
                order.setIsComplete();
                Database.ordersProcessed.add(order);

                // Notify the drones if they're waiting
                for (Drone d : Database.drones) {
                    synchronized (d) {
                        d.notify();
                    }
                }
            }

            /*
                If both ordersToBeProcessed and shouldRestockDish are empty, wait until
                notified by new order coming in
             */
            if (
                Database.ordersToBeProcessed.isEmpty() &&
                !Database.shouldRestockDish
            ) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.out.println("Staff waiting error: " + e);
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return this.status;
    }

    private void monitorDishes() {
        for (Dish dish : StockManagement.dishes.keySet()) {
            // Synchronized access to the StockManagement dishes ArrayList
            synchronized (this) {
                StockInfo stock = StockManagement.dishes.get(dish);
                if (stock.getQuant() < stock.getThreshold()) {
                    makeNewDish(dish);
                }
            }
        }
    }

    // This method doesn't need to synchronized since it's called inside monitorDish
    private void makeNewDish(Dish dish) {
        Random rand = new Random();
        long sleepTimer = rand.nextInt(60000) + 20000;

        try {
            boolean enoughIngredient = true;

            // Check if there're enough ingredient to make the dish at its restock amount
            for (Ingredient i : dish.getRecipe().keySet()) {
                if (
                        (int) dish.getRecipe().get(i)
                                * StockManagement.dishes.get(dish).getAmountToAdd() >
                        StockManagement.ingredients.get(i).getQuant()
                ) {
                    enoughIngredient = false;
                }
            }

            if (enoughIngredient) {
                // Take away ingredients
                for (Ingredient ingredient : dish.getRecipe().keySet()) {
                    int amount = (int) dish.getRecipe().get(ingredient)
                            * StockManagement.dishes.get(dish).getAmountToAdd();
                    StockManagement.ingredients.get(ingredient).addQuant(-amount);
                }

                status = "Cooking";
                System.out.println(status);
                Thread.sleep(sleepTimer);

                // Adds new dish
                StockManagement.dishes.get(dish).addQuant();
                status = "Idle";
            }
        } catch (InterruptedException e) {
            System.out.println("Dish pause error: " + e);
        }
    }

    /**
     * Grabs the oldest order in the ordersProcessed list and remove it from the list
     * @return The oldest order
     */
    private synchronized Order latestOrder() {
        if (!Database.ordersToBeProcessed.isEmpty()) {
            Order order = Database.ordersToBeProcessed.get(0);
            Database.ordersToBeProcessed.remove(0);
            return order;
        }

        return null;
    }
}
