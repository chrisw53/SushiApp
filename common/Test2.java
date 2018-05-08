package common;

import java.io.*;

public class Test2 {
    public static void main(String[] args) {
        Comms myComms = new Comms();
        ObjectInputStream in;
        ObjectOutputStream out;

        try {
            ClientStreams myStreams = myComms.clientSetUp();
            in = myStreams.getInputStream();
            out = myStreams.getOutputStream();
            myComms.sendMessage(new Message("test"), out);
            System.out.println("test1 ran");
            myComms.sendMessage(new Message("test2"), out);
            System.out.println("test2 ran");
        } catch (IOException e) {
            System.out.println("Client set up error: " + e);
        }


    }
}
