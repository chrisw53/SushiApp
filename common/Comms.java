package common;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.function.Function;

public class Comms {
    private int portNum = 1234;

    // Set up method for StockManagement
    public void serverSetup(Function<Message, Object> callback) throws IOException {
        ServerSocket server = new ServerSocket(this.portNum);

        while (true) {
            Socket connection = null;

            try {
                // establishes connection
                connection = server.accept();

                // establishes the streams
                ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());

                // new thread for incoming connection
                Thread t = new ClientHandler(
                        inputStream,
                        outputStream,
                        callback
                );
                t.start();
            } catch(Exception e){
                connection.close();
                System.out.println("Something's wrong: " + e);
            }
        }
    }

    // When setting up client, run while (true) for receive message
    public ClientStreams clientSetUp() throws IOException {
        Socket clientSocket = new Socket("localhost", this.portNum);
        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        return new ClientStreams(inputStream, outputStream);
    }

    // Toolbelt method for client to send message to StockManagement
    public void sendMessage(
            Message msg,
            ObjectOutputStream outputStream
    ) throws IOException {
        outputStream.writeObject(msg);
        outputStream.flush();
    }

    // Toolbelt method for client to receive message from StockManagement
    public Object receiveMessage(
            ObjectInputStream inputStream
    ) throws IOException {
        Object msg = new Object();

        try {
            msg = inputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("What did you type? " + e);
        }

        return msg;
    }
}

// handles threading each client and response to them based on logic
// provided by StockManagement
class ClientHandler extends Thread {
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Function<Message, Object> logic;

    ClientHandler(
            ObjectInputStream inputStream,
            ObjectOutputStream outputStream,
            Function<Message, Object> logic
    ) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.logic = logic;
    }

    public void run() {
        Message received;

        while (true) {
             try {
                 received = (Message) inputStream.readObject();
                 Object output = logic.apply(received);
                 outputStream.writeObject(output);
                 outputStream.flush();
             } catch (IOException e) {
                 // System.out.println("StockManagement receive I/O Error: " + e);
             } catch (ClassNotFoundException e) {
                 System.out.println("StockManagement receive class not found: " + e);
             }
        }
    }
}
