package common;

import java.util.*;

// Do Thread t = new Thread(Staff) when starting a new thread
public class Staff extends Model implements Runnable {
    private String status = "Idle";

    public Staff(String name) {
        this.name = name;
    }

    public void run() {
        while (true) {
            if (Database.shouldRestockDish) {
                try {
                    Thread.sleep(1000);
                    monitorDishes();
                } catch (InterruptedException e) {
                    System.out.println("Monitor pause error: " + e);
                }
            }

            if (!Database.ordersToBeProcessed.isEmpty()) {
                Order order = latestOrder();

                for (Dish d : order.getDish().keySet()) {
                    while (StockManagement.dishes.get(d).getQuant() < order.getDish().get(d)) {
                        makeNewDish(d);
                    }
                }

                order.setStatus("Completed");
                order.setIsComplete();
                Database.ordersProcessed.add(order);

                for (Drone d : Database.drones) {
                    synchronized (d) {
                        d.notify();
                    }
                }
            }

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
                // take away ingredients
                for (Ingredient ingredient : dish.getRecipe().keySet()) {
                    int amount = (int) dish.getRecipe().get(ingredient)
                            * StockManagement.dishes.get(dish).getAmountToAdd();
                    System.out.println("Took away this much: " + amount);
                    StockManagement.ingredients.get(ingredient).addQuant(-amount);
                }

                status = "Cooking";
                System.out.println(status);
                Thread.sleep(sleepTimer);

                // adds new dish
                StockManagement.dishes.get(dish).addQuant();
                status = "Idle";
            }
        } catch (InterruptedException e) {
            System.out.println("Dish pause error: " + e);
        }
    }

    private synchronized Order latestOrder() {
        if (!Database.ordersToBeProcessed.isEmpty()) {
            System.out.println("Grabbing Latest Order");
            Order order = Database.ordersToBeProcessed.get(0);
            Database.ordersToBeProcessed.remove(0);
            return order;
        }

        return null;
    }
}
