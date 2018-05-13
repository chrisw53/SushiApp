package common;

import java.io.Serializable;
import java.util.*;

/**
 * Class representing a single order by an user
 */
public class Order extends Model implements Serializable {
    private User user;
    private HashMap<Dish, Integer> dishes;
    private Boolean isComplete = false;
    private String status;
    // The UUID is used to uniquely identify different orders
    private UUID uuid = UUID.randomUUID();

    public Order(User user, HashMap<Dish, Integer> dishes) {
        this.user = user;
        this.dishes = dishes;
        this.status = "Processed";
    }

    public UUID getUuid() {
        return this.uuid;
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

    /**
     * Gets the cost of the order by taking the sum of the cost of each
     * dish timed by their quantity
     * @return The cost of the order
     */
    public int getCost() {
        int cost = 0;

        if (!dishes.isEmpty()) {
            for (Dish d : dishes.keySet()) {
                cost += d.getPrice() * dishes.get(d);
            }
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
