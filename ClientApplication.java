import client.*;

public class ClientApplication {
    private Client initialise() {
        return new Client();
    }

    private void launchGUI(ClientInterface client) {
        ClientWindow clientWindow = new ClientWindow(client);
    }

    public static void main(String[] args) {
        ClientApplication newClient = new ClientApplication();

        newClient.launchGUI(newClient.initialise());
    }
}
