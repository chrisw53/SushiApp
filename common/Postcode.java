package common;

public class Postcode extends Model {
    private String postcode;

    Postcode(String postcode) {
        this.postcode = postcode;
    }

    public String getName() {
        return this.postcode;
    }
}
