package common;

public class Order extends Model {
    private int quant;
    private Dish dish;

    Order(int quant, Dish dish) {
        this.quant = quant;
        this.dish = dish;
    }

    public String getName() {
        return this.dish.getName();
    }

    int getQuant() {
        return this.quant;
    }

    Dish getDish() {
        return this.dish;
    }
}
