package common;

import java.util.*;

public class Database {
    public static ArrayList<Supplier> suppliers = new ArrayList<>();
    public static HashMap<String, Long> postcodeDistance = new HashMap<>();
    public static ArrayList<Postcode> postcodes = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Order> ordersToBeProcessed = new ArrayList<>();
    public static ArrayList<Order> ordersProcessed = new ArrayList<>();
    public static ArrayList<Drone> drones = new ArrayList<>();
    public static ArrayList<Staff> staffs = new ArrayList<>();
    public static HashMap<User, ArrayList<DishInfo>> basket = new HashMap<>();
    public static Boolean shouldRestockDish = true;
    public static Boolean shouldRestockIngredient = true;

    public Database() {
        StockManagement stockManagement = new StockManagement();
    }
}
