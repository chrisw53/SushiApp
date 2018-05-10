package server;

import common.*;

import java.io.IOException;
import java.util.*;

public class ServerCommSetup {
    private HashMap<User, ArrayList<DishInfo>> basket = new HashMap<>();

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
                if (basket.containsKey(info.getUser())) {
                    basket.get(info.getUser()).add(info);
                } else {
                    ArrayList<DishInfo> temp = new ArrayList<>();
                    temp.add(info);
                    basket.put(info.getUser(), temp);
                }
            }
            break;
            case "basket":
            {
                User user = (User) msg.getPayload();
                HashMap<Dish, Integer> temp = new HashMap<>();

                for (DishInfo d : basket.get(user)) {
                    temp.put(d.getDish(), (int) d.getQuant());
                }

                return temp;
            }
            case "basketCost":
            {
                User user = (User) msg.getPayload();
                if (basket.containsKey(user)) {
                    int cost = 0;
                    for (DishInfo d : basket.get(user)) {
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

                for (DishInfo d : basket.get(info.getUser())) {
                    if (d.getDish() == info.getDish()) {
                        d.setQuant(info.getQuant());
                    }
                }
            }
            break;
            case "clearBasket":
            {
                User user = (User) msg.getPayload();
                basket.get(user).clear();
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
                Database.ordersToBeProcessed.remove(msg.getPayload());
                break;
            case "checkout":
            {
                User user = (User) msg.getPayload();
                HashMap<Dish, Integer> dishes = new HashMap<>();
                for (DishInfo d : basket.get(user)) {
                    dishes.put(d.getDish(), (int) d.getQuant());
                }
                basket.remove(user);
                Database.ordersToBeProcessed.add(new Order(user, dishes));

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
