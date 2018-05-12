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
        Configuration initConfig = new Configuration("Backup.txt");
        initConfig.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down, saving data...");
            DataPersistence backup = new DataPersistence();
            backup.write();
            System.out.println("Data saved!");
        }));

        ServerApplication serverWindow = new ServerApplication();
        Server serverInterface = serverWindow.initialise();
        serverWindow.launchGUI(serverInterface);
        ServerCommSetup commSetup = new ServerCommSetup();

    }
}
