package common;

import java.util.*;

/**
 * An extension of the Database class. Contains the stock information of the
 * dishes and ingredients
 */
public class StockManagement {
    public static HashMap<Ingredient, StockInfo> ingredients = new HashMap<>();
    public static HashMap<Dish, StockInfo> dishes = new HashMap<>();

    StockManagement() {}
}
