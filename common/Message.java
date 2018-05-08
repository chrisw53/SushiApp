package common;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;
    private Object payload;

    public Message(
            String type,
            Object payload
    ) {
        this.type = type;
        this.payload = payload;
    }

    public Message(String type) {
        this.type = type;
        this.payload = null;
    }

    public String getType() {
        return this.type;
    }

    public Object getPayload() {
        return this.payload;
    }
}
