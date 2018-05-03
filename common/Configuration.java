package common;

import java.io.*;
import java.util.*;

public class Configuration {
    private String path;

    public Configuration(String configFile) {
        this.path = configFile;
    }

    public void start() {
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

    private void parse(String line) {
        String[] content = line.split(":");

        switch (content[0]) {
            case "SUPPLIER":
                Database.suppliers.add(new Supplier(content[1], Long.parseLong(content[2])));
                break;
            case "INGREDIENT":
            {
                Supplier supplier = null;
                for (Supplier s : Database.suppliers) {
                    if (s.getName().equalsIgnoreCase(content[3])) {
                        supplier = s;
                    }
                }

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
                HashMap<Ingredient, Number> receipe = new HashMap<>();
                String[] receipeDetails = content[6].split(",");

                for (String detail : receipeDetails) {
                    int quant = Integer.parseInt(detail.split(" * ")[0]);
                    Ingredient ingredient = null;

                    for (Ingredient i : StockManagement.ingredients.keySet()) {
                        if (i.getName().equalsIgnoreCase(detail.split(" * ")[1])) {
                            ingredient = i;
                        }
                    }

                    receipe.put(ingredient, quant);
                }

                StockManagement.dishes.put(new Dish(
                        content[1],
                        content[2],
                        Integer.parseInt(content[3]),
                        receipe
                ), new StockInfo(
                        Integer.parseInt(content[4]),
                        Integer.parseInt(content[5]),
                        0
                ));
            }
                break;
            case "POSTCODE":
                Database.postcodeDistance.put(
                        new Postcode(content[1]),
                        Long.parseLong(content[2])
                );
                break;
            case "USER":
                Database.users.add(new User(
                        content[1],
                        content[2],
                        content[3],
                        new Postcode(content[4])
                ));
                break;
            case "ORDER":
            case "UNPROCESSEDORDER":
            case "PROCESSEDORDER":
            {
                User user = null;
                HashMap<Dish, Integer> orderDetail = new HashMap<>();

                for (User u : Database.users) {
                    if (u.getName().equalsIgnoreCase(content[1])) {
                        user = u;
                    }
                }

                String[] individualDishes = content[2].split(",");

                for (String s : individualDishes) {
                    int quant = Integer.parseInt(s.split(" * ")[0]);
                    Dish dish = null;

                    for (Dish d : StockManagement.dishes.keySet()) {
                        if (d.getName().equalsIgnoreCase(s.split(" * ")[1])) {
                            dish = d;
                        }
                    }

                    orderDetail.put(dish, quant);
                }

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
                for (Ingredient i : StockManagement.ingredients.keySet()) {
                    if (i.getName().equalsIgnoreCase(content[1])) {
                        StockManagement
                                .ingredients
                                .get(i)
                                .addQuant(Integer.parseInt(content[2]));
                        matched = true;
                    }
                }

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
