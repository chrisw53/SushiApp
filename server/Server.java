package server;

import common.*;

import java.io.*;
import java.util.*;

public class Server implements ServerInterface{
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        Configuration myConfig = new Configuration(filename);
        myConfig.start();

        for (Drone d : Database.drones) {
            Thread t = new Thread(d);
            t.start();
        }

        for (Staff s : Database.staffs) {
            Thread t = new Thread(s);
            t.start();
        }

        notifyUpdate();
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        Database.shouldRestockIngredient = enabled;

        if (enabled) {
            for (Drone d : Database.drones) {
                synchronized (d) {
                    d.notify();
                }
            }
        }
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        Database.shouldRestockDish = enabled;

        if (enabled) {
            for (Staff s : Database.staffs) {
                synchronized (s) {
                    s.notify();
                }
            }
        }
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        StockManagement.dishes.get(dish).setQuant((int) stock);
        notifyUpdate();
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        StockManagement.ingredients.get(ingredient).setQuant((int) stock);
        notifyUpdate();
    }

    @Override
    public List<Dish> getDishes() {
        return new ArrayList<>(StockManagement.dishes.keySet());
    }

    @Override
    public Dish addDish(
            String name,
            String description,
            Number price,
            Number restockThreshold,
            Number restockAmount
    ) {
        Dish newDish = new Dish(name, description, (int) price, new HashMap<>());
        StockManagement.dishes.put(
                newDish,
                new StockInfo(
                        (int) restockThreshold,
                        (int) restockThreshold,
                        0
                )
        );
        notifyUpdate();

        return newDish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        StockManagement.dishes.remove(dish);
        notifyUpdate();
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.addIngredient(ingredient, (int) quantity);
            }
        }
        notifyUpdate();
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.deleteIngredient(ingredient);
            }
        }
        notifyUpdate();
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.setRecipe(recipe);
            } else {
                dish.setRecipe(recipe);
            }
        }
        notifyUpdate();
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        StockManagement.dishes.get(dish).setRestockLevel(
                (int) restockThreshold,
                (int) restockAmount
        );
        notifyUpdate();
    }

    @Override
    public Number getRestockThreshold(Dish dish) {
        return StockManagement.dishes.get(dish).getThreshold();
    }

    @Override
    public Number getRestockAmount(Dish dish) {
        return StockManagement.dishes.get(dish).getAmountToAdd();
    }

    @Override
    public Map<Ingredient, Number> getRecipe(Dish dish) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                return d.getRecipe();
            }
        }

        return new HashMap<>();
    }

    @Override
    public Map<Dish, Number> getDishStockLevels() {
        HashMap<Dish, Number> stockLevel = new HashMap<>();

        for (Dish d : StockManagement.dishes.keySet()) {
            stockLevel.put(d, StockManagement.dishes.get(d).getQuant());
        }

        return stockLevel;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return new ArrayList<>(StockManagement.ingredients.keySet());
    }

    @Override
    public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold, Number restockAmount) {
        Ingredient newIngredient = new Ingredient(
                name,
                unit,
                supplier
        );

        StockManagement.ingredients.put(
                newIngredient,
                new StockInfo(
                        (int) restockThreshold,
                        (int) restockAmount,
                        0
                )
        );

        notifyUpdate();

        return newIngredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        StockManagement.ingredients.remove(ingredient);
        notifyUpdate();
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        StockManagement.ingredients.get(ingredient).setRestockLevel(
                (int) restockThreshold,
                (int) restockAmount
        );
        notifyUpdate();
    }

    @Override
    public Number getRestockThreshold(Ingredient ingredient) {
        return StockManagement.ingredients.get(ingredient).getThreshold();
    }

    @Override
    public Number getRestockAmount(Ingredient ingredient) {
        return StockManagement.ingredients.get(ingredient).getAmountToAdd();
    }

    @Override
    public Map<Ingredient, Number> getIngredientStockLevels() {
        HashMap<Ingredient, Number> stockLevel = new HashMap<>();

        for (Ingredient i : StockManagement.ingredients.keySet()) {
            stockLevel.put(i, StockManagement.ingredients.get(i).getQuant());
        }

        return stockLevel;
    }

    @Override
    public List<Supplier> getSuppliers() {
        return Database.suppliers;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier newSupplier = new Supplier(name, distance.longValue());
        Database.suppliers.add(newSupplier);

        notifyUpdate();
        return newSupplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        Database.suppliers.remove(supplier);
        notifyUpdate();
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    @Override
    public List<Drone> getDrones() {
        return Database.drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone((int) speed);
        Database.drones.add(drone);

        if (Database.shouldRestockIngredient) {
            Thread t = new Thread(drone);
            t.start();
        }

        notifyUpdate();

        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        // stops all drone threads
        Database.shouldRestockIngredient = false;

        Database.drones.remove(drone);

        // restart the remaining drones
        for (Drone d : Database.drones) {
            Thread t = new Thread(d);
            t.start();
        }

        notifyUpdate();
    }

    @Override
    public Number getDroneSpeed(Drone drone) {
        return drone.getSpeed();
    }

    @Override
    public String getDroneStatus(Drone drone) {
        return drone.getStatus();
    }

    @Override
    public List<Staff> getStaff() {
        return Database.staffs;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staff = new Staff(name);
        Database.staffs.add(staff);

        if (Database.shouldRestockDish) {
            Thread t = new Thread(staff);
            t.start();
        }

        notifyUpdate();

        return staff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        Database.staffs.remove(staff);
        notifyUpdate();
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public List<Order> getOrders() {
        ArrayList<Order> orders = new ArrayList<>(Database.ordersToBeProcessed);
        orders.addAll(Database.ordersProcessed);

        return orders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        Database.ordersToBeProcessed.remove(order);
        notifyUpdate();
    }

    @Override
    public Number getOrderDistance(Order order) {
        return Database.postcodeDistance.get(order.getUser().getPostcode());
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getIsComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public List<Postcode> getPostcodes() {
        return new ArrayList<>(Database.postcodeDistance.keySet());
    }

    @Override
    public void addPostcode(String code, Number distance) {
        Database.postcodeDistance.put(new Postcode(code), distance.longValue());
        notifyUpdate();
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        Database.postcodeDistance.remove(postcode);
        notifyUpdate();
    }

    @Override
    public List<User> getUsers() {
        return Database.users;
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        Database.users.remove(user);
        notifyUpdate();
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        for(UpdateListener listener : updateListeners) {
            listener.updated(new UpdateEvent());
        }
    }
}
