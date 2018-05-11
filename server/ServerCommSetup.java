package server;

import common.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

public class ServerCommSetup {
    public ServerCommSetup() {
        try {
            Comms myComms = new Comms();
            myComms.serverSetup(this::serverLogic);
        } catch (IOException e) {
            System.out.println("Server setup error: " + e);
        }
    }

    private Object serverLogic(Message msg) {
        switch (msg.getType()) {
            case "register":
                Database.users.add((User) msg.getPayload());
                Database.basket.put((User) msg.getPayload(), new ArrayList<>());
                break;
            case "login":
            {
                String username = ((String[]) msg.getPayload())[0];
                String password = ((String[]) msg.getPayload())[1];

                for (User u : Database.users) {
                    if (u.getName().equals(username) && u.getPassword().equals(password)) {
                        return u;
                    }
                }
            }
            break;
            case "dishes":
                return new ArrayList<>(StockManagement.dishes.keySet());
            case "postcodes":
                return Database.postcodes;
            case "addToBasket":
            {
                DishInfo info = (DishInfo) msg.getPayload();
                if (Database.basket.containsKey(info.getUser())) {
                    Database.basket.get(info.getUser()).add(info);
                } else {
                    ArrayList<DishInfo> temp = new ArrayList<>();
                    temp.add(info);
                    Database.basket.put(info.getUser(), temp);
                }
            }
            break;
            case "basket":
            {
                User user = null;
                HashMap<Dish, Number> temp = new HashMap<>();

                for (User u : Database.basket.keySet()) {
                    if (u.getName().equalsIgnoreCase(((User) msg.getPayload()).getName())) {
                        user = u;
                    }
                }

                for (DishInfo d : Database.basket.get(user)) {
                    temp.put(d.getDish(), d.getQuant());
                }

                return temp;
            }
            case "basketCost":
            {
                User user = (User) msg.getPayload();
                if (Database.basket.containsKey(user)) {
                    int cost = 0;
                    for (DishInfo d : Database.basket.get(user)) {
                        cost += (d.getDish().getPrice() * (int) d.getQuant());
                    }
                    return cost;
                } else {
                    return 0;
                }
            }
            case "updateBasket":
            {
                DishInfo info = (DishInfo) msg.getPayload();
                User user = null;

                for (User u : Database.basket.keySet()) {
                    if (u.getName().equalsIgnoreCase(info.getUser().getName())) {
                        user = u;
                    }
                }

                for (DishInfo d : Database.basket.get(user)) {
                    if (d.getDish().getName().equalsIgnoreCase(info.getDish().getName())) {
                        d.setQuant(info.getQuant());
                    }
                }
            }
            break;
            case "clearBasket":
            {
                User user = (User) msg.getPayload();
                Database.basket.get(user).clear();
            }
            break;
            case "orders":
            {
                ArrayList<Order> userOrder = new ArrayList<>();
                User user = (User) msg.getPayload();

                for (Order o : Database.ordersToBeProcessed) {
                    if (o.getUser() == user) {
                        userOrder.add(o);
                    }
                }

                for (Order o : Database.ordersProcessed) {
                    if (o.getUser() == user) {
                        userOrder.add(o);
                    }
                }

                return userOrder;
            }
            case "cancelOrder":
                Order order = null;

                for (Order o : Database.ordersToBeProcessed) {
                    if (o.getUuid().equals(((Order) msg.getPayload()).getUuid())) {
                        order = o;
                    }
                }

                Database.ordersToBeProcessed.remove(order);
                break;
            case "checkout":
            {
                User user = (User) msg.getPayload();
                HashMap<Dish, Integer> dishes = new HashMap<>();
                for (DishInfo d : Database.basket.get(user)) {
                    dishes.put(d.getDish(), (int) d.getQuant());
                }
                Database.basket.remove(user);
                Order newOrder = new Order(user, dishes);
                Database.ordersToBeProcessed.add(newOrder);

                for (Staff s : Database.staffs) {
                    synchronized (s) {
                        s.notify();
                    }
                }
            }
            break;
            default:
                return null;
        }

        // Placeholder return statement
        return null;
    }
}
