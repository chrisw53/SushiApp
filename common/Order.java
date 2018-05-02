package common;

import java.util.*;

public class Order extends Model {
    private User user;
    private HashMap<Dish, Integer> dishes;
    private Boolean isComplete = false;
    private String status;

    public Order(User user, HashMap<Dish, Integer> dishes) {
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

    public User getUser() {
        return this.user;
    }

    public int getCost() {
        int cost = 0;
        for (Dish d : dishes.keySet()) {
            cost += d.getPrice() * dishes.get(d);
        }

        return cost;
    }

    public Boolean getIsComplete() {
        return this.isComplete;
    }

    void setIsComplete() {
        this.isComplete = true;
    }

    void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
