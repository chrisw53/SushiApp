package common;

import java.io.*;
import java.util.*;

public class Configuration {
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<Postcode, Long> postcodeDistance = new HashMap<>();
    private ArrayList<Order> orders = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();

    public Configuration(String configFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                content.add(line);
            }

            // parser
            for (String s : content) {
                parse(s);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Wrong file name!");
        } catch (IOException e) {
            System.out.println("Config parser IO exception: " + e);
        }
    }

    public ArrayList<Supplier> getSuppliers() {
        return this.suppliers;
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public HashMap<Postcode, Long> getPostcodeDistance() {
        return this.postcodeDistance;
    }

    public ArrayList<Order> getOrders() {
        return this.orders;
    }

    private void parse(String line) {
        String[] content = line.split(":");

        switch (content[0]) {
            case "SUPPLIER":
                suppliers.add(new Supplier(content[1], Long.parseLong(content[2])));
                break;
            case "INGREDIENT":
            {
                Supplier supplier = null;
                for (Supplier s : this.suppliers) {
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
                HashMap<Ingredient, Integer> receipe = new HashMap<>();
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
                postcodeDistance.put(new Postcode(content[1]), Long.parseLong(content[2]));
                break;
            case "USER":
                users.add(new User(
                        content[1],
                        content[2],
                        content[3],
                        new Postcode(content[4])
                ));
                break;
            case "ORDER":
            {
                User user = null;
                HashMap<Dish, Integer> orderDetail = new HashMap<>();

                for (User u : users) {
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

                orders.add(new Order(user, orderDetail));
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
                                .setQuant(Integer.parseInt(content[2]));
                        matched = true;
                    }
                }

                if (!matched) {
                    for (Dish d : StockManagement.dishes.keySet()) {
                        if (d.getName().equalsIgnoreCase(content[1])) {
                            StockManagement
                                    .dishes
                                    .get(d)
                                    .setQuant(Integer.parseInt(content[2]));
                        }
                    }
                }
            }
                break;
            case "STAFF":
            {
                Thread t = new Thread(new Staff(content[1]));
                t.start();
            }
                break;
            case "DRONE":
            {
                Thread t = new Thread(new Drone(
                        Integer.parseInt(content[1]),
                        suppliers,
                        postcodeDistance
                ));
                t.start();
            }
                break;
            default:
                break;
        }
    }

    /*
    public static void main(String[] args) {
        Configuration test = new Configuration("src/common/ConfigurationTest.txt");

        System.out.println("\nSuppliers\n");
        for (Supplier s : test.getSuppliers()) {
            System.out.println("Name: " + s.getName());
            System.out.println("Distance: " + s.getDistance());
        }

        System.out.println("\nUsers\n");
        for (User u : test.getUsers()) {
            System.out.println("Name: " + u.getName());
            System.out.println("Address: " + u.getAddress());
            System.out.println("Password: " + u.getPassword());
            System.out.println("Postcode: " + u.getPostcode());
        }

        System.out.println("\nPostcodes\n");
        for (Postcode p : test.getPostcodeDistance().keySet()) {
            System.out.println("Postcode: " + p.getName());
            System.out.println("Distance: " + test.getPostcodeDistance().get(p));
        }
    }
    */
}
