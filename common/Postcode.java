package common;

import java.io.Serializable;

public class Postcode extends Model implements Serializable {
    private String postcode;

    public Postcode(String postcode) {
        this.postcode = postcode;
    }

    public String getName() {
        return this.postcode;
    }
}
