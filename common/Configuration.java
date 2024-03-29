package common;

import java.io.*;
import java.util.*;

public class Configuration {
    private String path;

    /**
     * Constructor that sets the path of the config file
     * @param configFile String representing the path of the config file
     */
    public Configuration(String configFile) {
        this.path = configFile;
    }

    /**
     * Init method for the configuration class to start parsing
     */
    public void start() {
        // These clears the existing configs
        Database.ordersToBeProcessed.clear();
        Database.ordersProcessed.clear();
        Database.basket.clear();
        Database.postcodes.clear();
        Database.suppliers.clear();
        Database.drones.clear();
        Database.staffs.clear();
        Database.users.clear();
        Database.postcodeDistance.clear();
        Database.shouldRestockDish = true;
        Database.shouldRestockIngredient = true;
        StockManagement.dishes.clear();
        StockManagement.ingredients.clear();

        // Establishes a new file in the path provided or override the existing file
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            // parse
            while ((line = br.readLine()) != null) {
                parse(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file name!");
        } catch (IOException e) {
            System.out.println("Config parser IO exception: " + e);
        }
    }

    /**
     * Main parsing logic
     * @param line Each line of the config file
     */
    private void parse(String line) {
        // Split string into type (left of ":") and payload (right of ":")
        String[] content = line.split(":");

        switch (content[0]) {
            case "SUPPLIER":
                Database.suppliers.add(new Supplier(content[1], Long.parseLong(content[2])));
                break;
            case "INGREDIENT":
            {
                Supplier supplier = null;
                // Matches the supplier name to existing suppliers
                for (Supplier s : Database.suppliers) {
                    if (s.getName().equalsIgnoreCase(content[3])) {
                        supplier = s;
                    }
                }

                // Create new Ingredient and stockInfo to be stored
                Ingredient ingredient = new Ingredient(
                        content[1],
                        content[2],
                        supplier
                );
                StockInfo stockInfo = new StockInfo(
                        Integer.parseInt(content[4]),
                        Integer.parseInt(content[5]),
                        0
                );

                StockManagement.ingredients.put(ingredient, stockInfo);
            }
                break;
            case "DISH":
            {
                HashMap<Ingredient, Number> recipe = new HashMap<>();
                String[] recipeDetails = content[6].split(",");

                // Parsing through the recipe section and add them to the recipe hashmap
                for (String detail : recipeDetails) {
                    int quant = Integer.parseInt(detail.split("\\s\\*\\s")[0]);
                    Ingredient ingredient = null;

                    for (Ingredient i : StockManagement.ingredients.keySet()) {
                        if (i.getName().equalsIgnoreCase(detail.split("\\s\\*\\s")[1])) {
                            ingredient = i;
                        }
                    }

                    recipe.put(ingredient, quant);
                }

                // Store the dish and stockInfo. Note, stockInfo always start with quant at 0
                StockManagement.dishes.put(new Dish(
                        content[1],
                        content[2],
                        Integer.parseInt(content[3]),
                        recipe
                ), new StockInfo(
                        Integer.parseInt(content[4]),
                        Integer.parseInt(content[5]),
                        0
                ));
            }
                break;
            case "POSTCODE":
                // This HashMap is so that it's easier to search for postcode distance
                Database.postcodeDistance.put(
                        content[1],
                        Long.parseLong(content[2])
                );
                // This ArrayList is to store the Postcode instances
                Database.postcodes.add(new Postcode(content[1]));
                break;
            case "USER":
                {
                    User user = new User(content[1], content[2], content[3], new Postcode(content[4]));
                    Database.users.add(user);
                    Database.basket.put(user, new ArrayList<>());
                }
                break;
            case "ORDER":
            case "UNPROCESSEDORDER":
            case "PROCESSEDORDER":
            {
                User user = null;
                HashMap<Dish, Integer> orderDetail = new HashMap<>();

                // Identify the user
                for (User u : Database.users) {
                    if (u.getName().equalsIgnoreCase(content[1])) {
                        user = u;
                    }
                }

                String[] individualDishes = content[2].split(",");

                // Parse through the dish section and add each dish to the order
                for (String s : individualDishes) {
                    int quant = Integer.parseInt(s.split("\\s\\*\\s")[0]);
                    Dish dish = null;

                    for (Dish d : StockManagement.dishes.keySet()) {
                        if (d.getName().equalsIgnoreCase(s.split("\\s\\*\\s")[1])) {
                            dish = d;
                        }
                    }

                    orderDetail.put(dish, quant);
                }

                // See Database for the difference between orderProcessed & ordersToBeProcessed
                if (content[0].equalsIgnoreCase("PROCESSEDORDER")) {
                    Database.ordersProcessed.add(new Order(user, orderDetail));
                } else {
                    Database.ordersToBeProcessed.add(new Order(user, orderDetail));
                }
            }
                break;
            case "STOCK":
            {
                boolean matched = false;
                // Identify the Ingredient if it's an ingredient and set its initial stock
                for (Ingredient i : StockManagement.ingredients.keySet()) {
                    if (i.getName().equalsIgnoreCase(content[1])) {
                        StockManagement
                                .ingredients
                                .get(i)
                                .addQuant(Integer.parseInt(content[2]));
                        matched = true;
                    }
                }

                // Otherwise identify the dish and set its initial stock
                if (!matched) {
                    for (Dish d : StockManagement.dishes.keySet()) {
                        if (d.getName().equalsIgnoreCase(content[1])) {
                            StockManagement
                                    .dishes
                                    .get(d)
                                    .addQuant(Integer.parseInt(content[2]));
                        }
                    }
                }
            }
                break;
            case "STAFF":
            {
                Staff staff = new Staff(content[1]);
                Database.staffs.add(staff);
            }
                break;
            case "DRONE":
            {
                Drone drone = new Drone(Integer.parseInt(content[1]));
                Database.drones.add(drone);
            }
                break;
            default:
                break;
        }
    }
}
