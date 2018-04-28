package common;

import java.io.*;

public class ClientStreams {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    ClientStreams(
            ObjectInputStream inputStream,
            ObjectOutputStream outputStream
    ) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public ObjectInputStream getInputStream() {
        return this.inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return this.outputStream;
    }
}