package common;

// Implements Runnable so it can work with Threading
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
            /*
                If the orderProcessed list is not empty, that means some orders
                are cooked and are ready to be delivered. Therefore grab the latest
                one and deliver it
            */
            if (!Database.ordersProcessed.isEmpty()) {
                Order order = latestOrder();
                deliverOrder(order.getUser().getPostcode().getName());
                order.setStatus("Delivered");
            }

            /*
                If restock ingredient is true, check whether an ingredient is below
                the restock threshold
             */
            if (Database.shouldRestockIngredient) {
                try {
                    Thread.sleep(1000);
                    monitorIngredient();
                } catch (InterruptedException e) {
                    System.out.println("Monitor pause error: " + e);
                }
            }


            /*
                If both ordersProcessed is empty and shouldRestockIngredient is false,
                put the drone thread in wait. It'll be notified when either a new order
                is added to ordersProcessed or if shouldRestockIngredient becomes true
             */
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

    /**
     * Checks whether any ingredient is falling below the restock threshold.
     * The process of checking the ingredient is synchronized to preserve data
     * consistency across all drones.
     */
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

    /**
     * If an ingredient's stock is below the restock threshold, this method is called
     * from monitorIngredient to restock it. Because this is only called inside the
     * synchronized block of the monitorIngredient method, there's no need to also
     * synchronize this method even though it is making change to a shared access
     * database.
     * @param ingredient The ingredient that needs to be restocked
     */
    private void restockIngredient(Ingredient ingredient) {
        status = "Restocking";

        long timeToSupplier = ingredient.getSupplier().getDistance() / this.speed;

        try {
            // Times 1000 to put it in seconds
            Thread.sleep(timeToSupplier * 1000);
            StockManagement.ingredients.get(ingredient).addQuant();
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Restock ingredient error: " + e);
        }
    }

    /**
     * Delivers the order to the postcode provided
     * @param postcode postcode to deliver the order to
     */
    private void deliverOrder(String postcode) {
        long deliveryTime = Database.postcodeDistance.get(postcode) / this.speed;
        status = "Delivering";

        try {
            Thread.sleep(deliveryTime * 1000);
            status = "Idle";
        } catch (InterruptedException e) {
            System.out.println("Drone delivery error: " + e);
        }
    }

    /**
     * Grabs the oldest order in the ordersProcessed list and remove it from the list
     * @return The oldest order
     */
    private synchronized Order latestOrder() {
        if (!Database.ordersProcessed.isEmpty()) {
            Order order = Database.ordersProcessed.get(0);
            Database.ordersProcessed.remove(0);
            return order;
        }

        return null;
    }
}
