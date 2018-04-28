package client;

import common.*;

import java.io.*;
import java.util.*;

public class Client implements ClientInterface {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Comms myComms;
    private ArrayList<UpdateListener> updateListeners = new ArrayList<>();

    public Client() {
        myComms = new Comms();
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
        } catch (IOException e) {
            System.out.println("Register send message error: " + e);
        }

        return myUser;
    }


    // TODO: Check login fail scenario
    @Override
    public User login(String username, String password) {
        try {
            String[] loginInfo = { username, password };
            myComms.sendMessage(new Message("login", loginInfo), this.outputStream);

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
            return (ArrayList) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get dishes error: " + e);
            return null;
        }
    }

    @Override
    public String getDishDescription(Dish dish) {
        try {
            myComms.sendMessage(new Message("dishDescription", dish), this.outputStream);
            return (String) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get dish description error: " + e);
            return null;
        }
    }

    @Override
    public Number getDishPrice(Dish dish) {
        try {
            myComms.sendMessage(new Message("dishPrice", dish), this.outputStream);
            return (Double) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get dish price error: " + e);
            return null;
        }
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
            return (Double) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Get basket cost error: " + e);
            return null;
        }
    }

    @Override
    public void addDishToBasket(User user, Dish dish, Number quantity) {
        try {
            DishInfo newDish = new DishInfo(user, dish, quantity);
            myComms.sendMessage(new Message("addToBasket", newDish), this.outputStream);
        } catch (IOException e) {
            System.out.println("Add to basket error: " + e);
        }
    }

    @Override
    public void updateDishInBasket(User user, Dish dish, Number quantity) {
        try {
            DishInfo updateDish = new DishInfo(user, dish, quantity);
            myComms.sendMessage(new Message("updateBasket", updateDish), this.outputStream);
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
        try {
            myComms.sendMessage(new Message("isOrderComplete", order), this.outputStream);
            return (Boolean) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Is order complete error: " + e);
            return false;
        }
    }

    @Override
    public String getOrderStatus(Order order) {
        try {
            myComms.sendMessage(new Message("orderStatus", order), this.outputStream);
            return (String) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Order status error: " + e);
            return null;
        }
    }

    @Override
    public Number getOrderCost(Order order) {
        try {
            myComms.sendMessage(new Message("orderCost", order), this.outputStream);
            return (Double) myComms.receiveMessage(this.inputStream);
        } catch (IOException e) {
            System.out.println("Order cost error: " + e);
            return null;
        }
    }

    @Override
    public void cancelOrder(Order order) {
        try {
            myComms.sendMessage(new Message("cancelOrder", order), this.outputStream);
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
