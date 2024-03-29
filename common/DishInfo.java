package common;

import java.io.Serializable;

/**
 * Container class that contains represents the order information
 * of a single dish
 */
public class DishInfo implements Serializable {
    private User user;
    private Dish dish;
    private Number quant;

    public DishInfo(
            User user,
            Dish dish,
            Number quant
    ) {
        this.user = user;
        this.dish = dish;
        this.quant = quant;
    }

    public User getUser() {
        return this.user;
    }

    public Dish getDish() {
        return this.dish;
    }

    public Number getQuant() {
        return this.quant;
    }

    public void setQuant(Number quant) {
        this.quant = quant;
    }
}
