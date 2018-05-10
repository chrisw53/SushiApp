package common;
import java.io.*;
import java.util.*;

// TODO: Implement this inside Client & Server App
public class DataPersistence {
    private ArrayList<String> output = new ArrayList<>();
    private PrintWriter writer;

    public DataPersistence() {
        this.writer = createFile("Backup.txt");
    }

    private void write() {
        supplierOutput();
        postcodeOutput();
        userOutput();
        unprocessedOrderOutput();
        processedOrderOutput();
        dronesOutput();
        staffsOutput();
        stockOutput();
        dishOutput();
        ingredientOutput();

        for (String s : output) {
            writer.println(s);
        }
    }

    private PrintWriter createFile(String path) {
        try {
            File configFile = new File(path);

            return new PrintWriter(new BufferedWriter(new FileWriter(configFile)));

        } catch (IOException e) {
            System.out.println("Error writing to persistent file: " + e);
        }

        return null;
    }

    private void supplierOutput() {
        for (Supplier s : Database.suppliers) {
            String output = "SUPPLIER:" + s.getName() + ":" + s.getDistance();
            this.output.add(output);
        }
    }

    private void postcodeOutput() {
        for (String p : Database.postcodeDistance.keySet()) {
            String output = "POSTCODE:"
                    + p + ":"
                    + Long.toString(Database.postcodeDistance.get(p));
            this.output.add(output);
        }
    }

    private void userOutput() {
        for (User u : Database.users) {
            String output = "USER:"
                    + u.getName() + ":"
                    + u.getPassword() + ":"
                    + u.getAddress() + ":"
                    + u.getPostcode().getName();
            this.output.add(output);
        }
    }

    private void unprocessedOrderOutput() {
        for (Order o : Database.ordersToBeProcessed) {
            String output = "UNPROCESSEDORDER:" + o.getUser().getName() + ":";

            for (Dish d : o.getDish().keySet()) {
                output += Integer.toString(o.getDish().get(d)) + " * " + d.getName() + ",";
            }

            this.output.add(output.substring(0, output.length() - 1));
        }
    }

    private void processedOrderOutput() {
        for (Order o : Database.ordersProcessed) {
            String output = "PROCESSEDORDER:" + o.getUser().getName() + ":";

            for (Dish d : o.getDish().keySet()) {
                output += Integer.toString(o.getDish().get(d)) + " * " + d.getName() + ",";
            }

            this.output.add(output.substring(0, output.length() - 1));
        }
    }

    private void dronesOutput() {
        for (Drone d : Database.drones) {
            String output = "DRONE:" + Integer.toString((int) d.getSpeed());
            this.output.add(output);
        }
    }

    private void staffsOutput() {
        for (Staff s : Database.staffs) {
            String output = "STAFF:" + s.getName();
            this.output.add(output);
        }
    }

    private void stockOutput() {
        for (Ingredient i : StockManagement.ingredients.keySet()) {
            String output = "STOCK:"
                    + i.getName() + ":"
                    + Integer.toString(StockManagement.ingredients.get(i).getQuant());
            this.output.add(output);
        }

        for (Dish d : StockManagement.dishes.keySet()) {
            String output = "STOCK:"
                    + d.getName() + ":"
                    + Integer.toString(StockManagement.dishes.get(d).getQuant());
            this.output.add(output);
        }
    }

    private void ingredientOutput() {
        for (Ingredient i : StockManagement.ingredients.keySet()) {
            String output = "INGREDIENT:"
                    + i.getName() + ":"
                    + i.getUnit() + ":"
                    + i.getSupplier().getName() + ":"
                    + Integer.toString(StockManagement.ingredients.get(i).getThreshold()) + ":"
                    + Integer.toString(StockManagement.ingredients.get(i).getAmountToAdd());

            this.output.add(output);
        }
    }

    private void dishOutput() {
        for (Dish d : StockManagement.dishes.keySet()) {
            String output = "DISH:"
                    + d.getName() + ":"
                    + d.getDescription() + ":"
                    + Integer.toString(d.getPrice()) + ":"
                    + Integer.toString(StockManagement.dishes.get(d).getThreshold()) + ":"
                    + Integer.toString(StockManagement.dishes.get(d).getAmountToAdd()) + ":";

            for (Ingredient i : d.getRecipe().keySet()) {
                output += Integer.toString((int) d.getRecipe().get(i))
                        + " * " + i.getName() + ",";
            }

            this.output.add(output.substring(0, output.length() - 1));
        }
    }
}
