package common;

import java.util.*;

/**
 * Class that's in charge of all data in the application. By storing it all in one
 * place with a bunch of static variables I don't have to worry about data consistency
 * when updating entries
 */
public class Database {
    public static ArrayList<Supplier> suppliers = new ArrayList<>();
    // Helper variable that makes searching for postcode distance easier
    public static HashMap<String, Long> postcodeDistance = new HashMap<>();
    public static ArrayList<Postcode> postcodes = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    // ordersProcessed contains order that are cooked but not delivered
    // this ArrayList is used as an queue (remove from head and add to tail)
    public static ArrayList<Order> ordersToBeProcessed = new ArrayList<>();
    // ordersToBeProcessed contains orders that are made but not cooked yet
    // this ArrayList is used as an queue (remove from head and add to tail)
    public static ArrayList<Order> ordersProcessed = new ArrayList<>();
    public static ArrayList<Drone> drones = new ArrayList<>();
    public static ArrayList<Staff> staffs = new ArrayList<>();
    public static HashMap<User, ArrayList<DishInfo>> basket = new HashMap<>();
    public static Boolean shouldRestockDish = true;
    public static Boolean shouldRestockIngredient = true;

    /**
     * When Database is first instantiated, it also instantiates an instance
     * of StockManagement to keep track of the dishes and ingredients. Ideally
     * I would've put the content of StockManagement inside Database so everything
     * is in one place but in order to satisfy the spec, they're separately stored.
     */
    public Database() {
        StockManagement stockManagement = new StockManagement();
    }
}
