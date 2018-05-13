package common;

import java.io.Serializable;

/**
 * Container class that's used to send requests from
 * client to the server. Type is used to identify the purpose
 * of the request and payload carries the supporting data
 */
public class Message implements Serializable {
    private String type;
    private Object payload;

    // Messages can be established with a type and payload
    public Message(
            String type,
            Object payload
    ) {
        this.type = type;
        this.payload = payload;
    }

    // Message can also be established with only a type
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
