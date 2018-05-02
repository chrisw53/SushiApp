import server.*;

public class ServerApplication {
    private Server initialise() {
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
