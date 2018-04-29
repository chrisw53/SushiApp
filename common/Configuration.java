package common;

import javafx.geometry.Pos;

import java.io.*;
import java.util.*;

public class Configuration {
    private ArrayList<Supplier> suppliers = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<Postcode, Long> postcodeDistance = new HashMap<>();
    private ArrayList<Order> orders = new ArrayList<>();


    public Configuration(String configFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;

            while ((line = br.readLine()) != null) {

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
            }
                break;
            case "POSTCODE":
                postcodeDistance.put(new Postcode(content[1]), Long.parseLong(content[2]));
                break;
            case "USER":
                break;
            case "STOCK":
                break;
            case "STAFF":
                break;
            case "DRONE":
                break;
            default:
                System.out.println("Empty line");
        }
    }
}
