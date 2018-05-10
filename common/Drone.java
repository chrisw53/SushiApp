package common;

public class Drone extends Model implements Runnable {
    private String status = "Idle";
    private int speed;

    public Drone(int speed) {
        this.speed = speed;
    }

    public String getName() {
        return "Drone";
    }

    public void run() {
        while (true) {
            if (!Database.ordersProcessed.isEmpty()) {
                Order order = getLatestOrder();
                System.out.println("Delivering");
                deliverOrder(order.getUser().getPostcode());
                order.setStatus("Delivered");
            }

            if (Database.shouldRestockIngredient) {
                try {
                    Thread.sleep(1000);
                    monitorIngredient();
                } catch (InterruptedException e) {
                    System.out.println("Monitor pause error: " + e);
                }
            }


            if (
                    !Database.shouldRestockIngredient &&
                        Database.ordersProcessed.isEmpty()
                ) {
                synchronized (this) {
                    try {
                        wait();
                        System.out.println("Locked");
                    } catch (InterruptedException e) {
                        System.out.println("Drone wait error: " + e);
                    }
                }
            }
        }
    }

    public String getStatus() {
        return this.status;
    }

    public Number getSpeed() {
        return this.speed;
    }

    private void monitorIngredient() {
        for (Ingredient ingredient : StockManagement.ingredients.keySet()) {
            synchronized (this) {
                StockInfo stockInfo = StockManagement.ingredients.get(ingredient);
                if (stockInfo.getQuant() < stockInfo.getThreshold()) {
                    restockIngredient(ingredient);
                }
            }
        }
    }

    private void restockIngredient(Ingredient ingredient) {
        status = "Restocking";

        long timeToSupplier = ingredient.getSupplier().getDistance() / this.speed;

        try {
            System.out.println(status);
            Thread.sleep(timeToSupplier);
            StockManagement.ingredients.get(ingredient).addQuant();
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Restock ingredient error: " + e);
        }
    }

    private void deliverOrder(Postcode postcode) {
        long deliveryTime = Database.postcodeDistance.get(postcode) / this.speed;
        status = "Delivering";

        try {
            System.out.println(status);
            Thread.sleep(deliveryTime);
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Drone delivery error: " + e);
        }
    }

    public synchronized Order getLatestOrder() {
        if (!Database.ordersProcessed.isEmpty()) {
            Order order = Database.ordersProcessed.get(0);
            Database.ordersProcessed.remove(0);
            return order;
        }

        return null;
    }
}
