package common;

import java.io.*;
import java.net.*;

public class Comms {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket connection;
    private ServerSocket server;

    Comms(int portNum) {
        try {
            server = new ServerSocket(portNum);

            while (true) {
                try {
                    // establishes connection
                    connection = server.accept();

                    // establishes the streams
                    outputStream = new ObjectOutputStream(connection.getOutputStream());
                    outputStream.flush();
                    inputStream = new ObjectInputStream(connection.getInputStream());
                } catch(IOException e){
                    System.out.println("Something's wrong: " + e);
                }
            }
        } catch (IOException e) {
            System.out.println("Comms server establishment failed: " + e);
        }
    }

    private void sendMessage(Object msg) {
        try {
            outputStream.writeObject(msg);
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("Send message error: " + e);
        }
    }

    private Object receiveMessage() {
        Object msg = new Object();

        try {
            msg = inputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("What did you type? " + e);
        } catch (IOException e) {
            System.out.println("Receive message IOException: " + e);
        }

        return msg;
    }
}
