import common.*;
import server.*;

public class ServerApplication {
    private Server initialise() {
        // Instantiate the database
        Database database = new Database();

        return new Server();
    }

    private void launchGUI(ServerInterface server) {
        ServerWindow serverWindow = new ServerWindow(server);
    }

    public static void main(String[] args) {
        // Loads up any backup if there are any
        Configuration initConfig = new Configuration("Backup.txt");
        initConfig.start();

        // Set up a listener for shutdown and implement DataPersistence for it
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down, saving data...");
            DataPersistence backup = new DataPersistence();
            backup.write();
            System.out.println("Data saved!");
        }));

        ServerApplication serverWindow = new ServerApplication();
        Server serverInterface = serverWindow.initialise();
        serverWindow.launchGUI(serverInterface);

        // Set up the serverComm last because it's a blocking while true loop
        ServerCommSetup commSetup = new ServerCommSetup();

    }
}
