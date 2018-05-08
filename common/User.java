package common;

import java.io.Serializable;

public class User extends Model implements Serializable {
    private String username;
    private String password;
    private String address;
    private Postcode postcode;

    public User(
            String username,
            String password,
            String address,
            Postcode postcode
    ) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.postcode = postcode;
    }

    public String getName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAddress() {
        return this.address;
    }

    public Postcode getPostcode() {
        return this.postcode;
    }
}
