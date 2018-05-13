package client;

import common.*;

import java.io.*;
import java.util.*;

public class Client implements ClientInterface {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Comms myComms = new Comms();
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    /**
     * The constructor runs the clientSetup method in the comms toolbelt
     * and stores the unique input and output streams of the client in the
     * client class variables
     */
    public Client() {
        try {
            ClientStreams myStreams = myComms.clientSetUp();
            inputStream = myStreams.getInputStream();
            outputStream = myStreams.getOutputStream();
        } catch (IOException e) {
            System.out.println("Client set up error: " + e);
        }
    }

    @Override
    public User register(String username, String password, String address, Postcode postcode) {
        User myUser = new User(username, password, address, postcode);
        Message msg = new Message("register", myUser);

        try {
            myComms.sendMessage(msg, this.outputStream);
            // This is to clear the null returned in the inputStream pipeline
            myComms.receiveMessage(inputStream);
        } catch (IOException e) {
            System.out.println("Register send message error: " + e);
        }

        return myUser;
    }

    //
    @Override
    public User login(String username, String password) {
        try {
            String[] loginInfo = { username, password };
            myComms.sendMessage(new Message("login", loginInfo), this.outputStream);

            // Blocking until the user is passed back from the server
            return (User) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Login error: " + e);
            return null;
        }
    }

    @Override
    public List<Postcode> getPostcodes() {
        try {
            myComms.sendMessage(new Message("postcodes"), this.outputStream);
            return (ArrayList) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get postcode error: " + e);
            return null;
        }
    }

    @Override
    public List<Dish> getDishes() {
        try {
            myComms.sendMessage(new Message("dishes"), this.outputStream);
            return (ArrayList<Dish>) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get dishes error: " + e);
            return null;
        }
    }

    @Override
    public String getDishDescription(Dish dish) {
        return dish.getDescription();
    }

    @Override
    public Number getDishPrice(Dish dish) {
        return dish.getPrice();
    }

    @Override
    public Map<Dish, Number> getBasket(User user) {
        try {
            myComms.sendMessage(new Message("basket", user), this.outputStream);
            return (HashMap) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get basket error: " + e);
            return null;
        }
    }

    @Override
    public Number getBasketCost(User user) {
        try {
            myComms.sendMessage(new Message("basketCost", user), this.outputStream);
            return (Integer) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get basket cost error: " + e);
            return null;
        }
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        try {
            // Gathers all the information about the dish into a container class to send as the
            // payload of the Message
            DishInfo newDish = new DishInfo(user, dish, quantity);
            myComms.sendMessage(new Message("addToBasket", newDish), this.outputStream);
            myComms.receiveMessage(inputStream);
        } catch (IOException e) {
            System.out.println("Add to basket error: " + e);
        }
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        try {
            // Gathers all the information about the dish into a container class to send as the
            // payload of the Message
            DishInfo updateDish = new DishInfo(user, dish, quantity);
            myComms.sendMessage(new Message("updateBasket", updateDish), this.outputStream);
            myComms.receiveMessage(inputStream);
        } catch (IOException e) {
            System.out.println("Update basket error: " + e);
        }
    }

    @Override
    public Order checkoutBasket(User user) {
        try {
            myComms.sendMessage(new Message("checkout", user), this.outputStream);
            return (Order) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Check out error: " + e);
            return null;
        }
    }

    @Override
    public void clearBasket(User user) {
        try {
            myComms.sendMessage(new Message("clearBasket", user), this.outputStream);
            myComms.receiveMessage(inputStream);
            notifyUpdate();
        } catch (IOException e) {
            System.out.println("Clear basket error: " + e);
        }
    }

    @Override
    public List<Order> getOrders(User user) {
        try {
            myComms.sendMessage(new Message("orders", user), this.outputStream);
            return (ArrayList) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get orders error: " + e);
            return null;
        }
    }

    @Override
    public boolean isOrderComplete(Order order) {
        return order.getIsComplete();
    }

    @Override
    public String getOrderStatus(Order order) {
        return order.getStatus();
    }

    @Override
    public Number getOrderCost(Order order) {
        return order.getCost();
    }

    @Override
    public void cancelOrder(Order order) {
        try {
            myComms.sendMessage(new Message("cancelOrder", order), this.outputStream);
            myComms.receiveMessage(inputStream);
            notifyUpdate();
        } catch (IOException e) {
            System.out.println("Cancel order error: " + e);
        }
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    @Override
    public void notifyUpdate() {
        for(UpdateListener listener : updateListeners) {
            listener.updated(new UpdateEvent());
        }
    }
}
