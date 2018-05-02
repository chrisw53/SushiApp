import common.Database;
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
        ServerApplication server = new ServerApplication();
        server.launchGUI(server.initialise());
    }
}
