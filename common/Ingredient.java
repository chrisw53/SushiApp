package common;

import java.io.Serializable;

public class Ingredient extends Model implements Serializable {
    private Supplier supplier;
    private String unit;

    public Ingredient(String name, String unit, Supplier supplier) {
        this.name = name;
        this.unit = unit;
        this.supplier = supplier;
    }

    public String getName() {
        return this.name;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public String getUnit() {
        return this.unit;
    }
}
