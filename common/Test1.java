package common;

import java.io.IOException;

public class Test1 {
    public static void main(String[] args) {
        Comms comms = new Comms();
        try {
            comms.serverSetup(Test1::logic);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static Object logic(Message msg) {
        return new Object();
    }
}
