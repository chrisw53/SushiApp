package server;

import common.*;

import java.io.*;
import java.util.*;

public class Server implements ServerInterface{
    // TODO: Implement socket logic
    public static ArrayList<Supplier> suppliers = new ArrayList<>();
    public static HashMap<Postcode, Long> postcodeDistance = new HashMap<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Order> orders = new ArrayList<>();
    public static ArrayList<Drone> drones = new ArrayList<>();
    public static ArrayList<Staff> staffs = new ArrayList<>();
    public static Boolean shouldRestockDish = true;
    public static Boolean shouldRestockIngredient = true;
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    @Override
    public void loadConfiguration(String filename) throws FileNotFoundException {
        Configuration myConfig = new Configuration(filename);
        myConfig.start();

        for (Drone d : drones) {
            Thread t = new Thread(d);
            t.start();
        }

        for (Staff s : staffs) {
            Thread t = new Thread(s);
            t.start();
        }
    }

    @Override
    public void setRestockingIngredientsEnabled(boolean enabled) {
        shouldRestockIngredient = enabled;

        if (enabled) {
            for (Drone d : drones) {
                Thread t = new Thread(d);
                t.start();
            }
        }
    }

    @Override
    public void setRestockingDishesEnabled(boolean enabled) {
        shouldRestockDish = enabled;

        if (enabled) {
            for (Staff s : staffs) {
                Thread t = new Thread(s);
                t.start();
            }
        }
    }

    @Override
    public void setStock(Dish dish, Number stock) {
        StockManagement.dishes.get(dish).setQuant((int) stock);
    }

    @Override
    public void setStock(Ingredient ingredient, Number stock) {
        StockManagement.ingredients.get(ingredient).setQuant((int) stock);
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
        Dish newDish = new Dish(name, description, (int) price, null);
        StockManagement.dishes.put(
                newDish,
                new StockInfo(
                        (int) restockThreshold,
                        (int) restockThreshold,
                        0
                )
        );

        return newDish;
    }

    @Override
    public void removeDish(Dish dish) throws UnableToDeleteException {
        StockManagement.dishes.remove(dish);
    }

    @Override
    public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.addIngredient(ingredient, (int) quantity);
            }
        }
    }

    @Override
    public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.deleteIngredient(ingredient);
            }
        }
    }

    @Override
    public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
        for (Dish d : StockManagement.dishes.keySet()) {
            if (d == dish) {
                d.setRecipe(recipe);
            }
        }
    }

    @Override
    public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
        StockManagement.dishes.get(dish).setRestockLevel(
                (int) restockThreshold,
                (int) restockAmount
        );
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

        return null;
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

        return newIngredient;
    }

    @Override
    public void removeIngredient(Ingredient ingredient) throws UnableToDeleteException {
        StockManagement.ingredients.remove(ingredient);
    }

    @Override
    public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
        StockManagement.ingredients.get(ingredient).setRestockLevel(
                (int) restockThreshold,
                (int) restockAmount
        );
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
        return suppliers;
    }

    @Override
    public Supplier addSupplier(String name, Number distance) {
        Supplier newSupplier = new Supplier(name, (long) distance);
        suppliers.add(newSupplier);

        return newSupplier;
    }

    @Override
    public void removeSupplier(Supplier supplier) throws UnableToDeleteException {
        suppliers.remove(supplier);
    }

    @Override
    public Number getSupplierDistance(Supplier supplier) {
        return supplier.getDistance();
    }

    @Override
    public List<Drone> getDrones() {
        return drones;
    }

    @Override
    public Drone addDrone(Number speed) {
        Drone drone = new Drone((int) speed);
        drones.add(drone);

        return drone;
    }

    @Override
    public void removeDrone(Drone drone) throws UnableToDeleteException {
        // stops all drone threads
        shouldRestockIngredient = false;

        drones.remove(drone);

        // restart the remaining drones
        for (Drone d : drones) {
            Thread t = new Thread(d);
            t.start();
        }
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
        return staffs;
    }

    @Override
    public Staff addStaff(String name) {
        Staff staff = new Staff(name);
        staffs.add(staff);

        return staff;
    }

    @Override
    public void removeStaff(Staff staff) throws UnableToDeleteException {
        staffs.remove(staff);
    }

    @Override
    public String getStaffStatus(Staff staff) {
        return staff.getStatus();
    }

    @Override
    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public void removeOrder(Order order) throws UnableToDeleteException {
        orders.remove(order);
    }

    @Override
    public Number getOrderDistance(Order order) {
        return postcodeDistance.get(order.getUser().getPostcode());
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
        return new ArrayList<>(postcodeDistance.keySet());
    }

    @Override
    public void addPostcode(String code, Number distance) {
        postcodeDistance.put(new Postcode(code), (long) distance);
    }

    @Override
    public void removePostcode(Postcode postcode) throws UnableToDeleteException {
        postcodeDistance.remove(postcode);
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void removeUser(User user) throws UnableToDeleteException {
        users.remove(user);
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
