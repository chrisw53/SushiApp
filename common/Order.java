package common;

import java.util.*;

public class Order extends Model {
    private User user;
    private HashMap<Dish, Integer> dishes;
    private Boolean isComplete = false;
    private String status;

    Order(User user, HashMap<Dish, Integer> dishes) {
        this.user = user;
        this.dishes = dishes;
        this.status = "Processed";
    }

    public String getName() {
        return this.user.getName();
    }

    public HashMap<Dish, Integer> getDish() {
        return this.dishes;
    }

    public void setIsComplete() {
        this.isComplete = !this.isComplete;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
