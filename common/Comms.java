package common;

import java.io.*;
import java.net.*;
import java.util.function.Function;

public class Comms {
    private int portNum = 1234;

    /**
     * Setup method for the server socket. Method takes in a callback function which
     * is passed into the client handler class to be used as a switchboard to return
     * the relevant information from the server/database. This is done so that anything
     * stream and socket related is completely contained within the comms class
     * @param callback The server logic function to be called later in client handler.
     *                 Function takes in an Message and returns an object.
     * @throws IOException Exception thrown if something goes wrong
     */
    public void serverSetup(Function<Message, Object> callback) throws IOException {
        // Establishes a new server socket the clients can connect to
        ServerSocket server = new ServerSocket(this.portNum);

        // While true loop make sure the server is always listening for new connections
        // from a socket to the server socket.
        while (true) {
            // Reset the variable representing the incoming connection to null
            Socket connection = null;

            try {
                // Establishes connection
                connection = server.accept();

                // Establishes the streams
                ObjectOutputStream outputStream = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(connection.getInputStream());

                // New thread for incoming connection
                Thread t = new ClientHandler(
                        inputStream,
                        outputStream,
                        callback
                );
                t.start();
            } catch(Exception e){
                // Close the connection if there's an error
                connection.close();
                System.out.println("Something's wrong: " + e);
            }
        }
    }

    /**
     * Method for setting up the client Socket and connecting it to the ServerSocket
     * @return An instance of a container class containing the input and output of the
     * unique client input and output streams
     * @throws IOException Thrown if there's an error connecting to the ServerSocket
     */
    public ClientStreams clientSetUp() throws IOException {
        Socket clientSocket = new Socket("localhost", this.portNum);
        ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

        return new ClientStreams(inputStream, outputStream);
    }

    /**
     * Tool belt method for client to send message to Server
     * @param msg A Message class instance with a type property signifying the action
     *            and a payload for any information needed to perform the action
     * @param outputStream The unique outputStream of the client so the message knows
     *                     where to write to
     * @throws IOException Thrown when there's an error writing to the outputStream
     */
    public void sendMessage(
            Message msg,
            ObjectOutputStream outputStream
    ) throws IOException {
        outputStream.writeObject(msg);
        outputStream.flush();
    }

    /**
     * Toolbelt method for client to receive message from StockManagement
     * @param inputStream The unique inputStream of the client so it knows where to
     *                    read from
     * @return The information read from the inputStream in repsonse to the request
     * @throws IOException Throne when there's an error reading from the inputStream
     */
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

/**
 * Handles threading each client and response to them based on logic callback function
 * passed in from serverSetUp
 */
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

        // The while true loop acts as a server listener function
        while (true) {
             try {
                 received = (Message) inputStream.readObject();
                 Object output = logic.apply(received);
                 outputStream.writeObject(output);
                 outputStream.flush();
             } catch (IOException e) {}
             catch (ClassNotFoundException e) {
                 System.out.println("Receive class not found: " + e);
             }
        }
    }
}
