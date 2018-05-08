package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Test3 {
    public static void main(String[] args) {
        Comms myComms = new Comms();
        ObjectInputStream in;
        ObjectOutputStream out;

        try {
            ClientStreams myStreams = myComms.clientSetUp();
            in = myStreams.getInputStream();
            out = myStreams.getOutputStream();
            myComms.sendMessage(new Message("test3"), out);
            System.out.println("test3 ran");
            myComms.sendMessage(new Message("test4"), out);
            System.out.println("test4 ran");
        } catch (IOException e) {
            System.out.println("Client set up error: " + e);
        }


    }
}
