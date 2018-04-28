package common;

import java.io.*;
import java.net.*;
import java.util.function.Function;

public class Comms {
    private int portNum = 1234;

    // Set up method for StockManagement
    void serverSetup(Function<String, Model> callback) throws IOException {
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
                        connection,
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
    ClientStreams clientSetUp() throws IOException {
        Socket clientSocket = new Socket("localhost", this.portNum);
        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        return new ClientStreams(inputStream, outputStream);
    }

    // Toolbelt method for client to send message to StockManagement
    private void sendMessage(
            Object msg,
            ObjectOutputStream outputStream
    ) throws IOException {
        outputStream.writeObject(msg);
        outputStream.flush();
    }

    // Toolbelt method for client to receive message from StockManagement
    private Object receiveMessage(
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
    private Socket connection;
    private Function<String, Model> logic;

    ClientHandler(
            Socket connection,
            ObjectInputStream inputStream,
            ObjectOutputStream outputStream,
            Function<String, Model> logic
    ) {
        this.connection = connection;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.logic = logic;
    }

    public void run() {
        String received;

        while (true) {
             try {
                 received = (String) inputStream.readObject();
                 outputStream.writeObject(logic.apply(received));
                 outputStream.flush();
             } catch (IOException e) {
                 System.out.println("StockManagement receive I/O Error: " + e);
             } catch (ClassNotFoundException e) {
                 System.out.println("StockManagement receive class not found: " + e);
             }
        }
    }
}

class ClientStreams {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    ClientStreams(
            ObjectInputStream inputStream,
            ObjectOutputStream outputStream
    ) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    ObjectInputStream getInputStream() {
        return this.inputStream;
    }

    ObjectOutputStream getOutputStream() {
        return this.outputStream;
    }
}
