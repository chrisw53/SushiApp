package common;

public class Ingredient extends Model {
    private Supplier supplier;
    private String unit;

    Ingredient(String name, String unit, Supplier supplier) {
        this.name = name;
        this.unit = unit;
        this.supplier = supplier;
    }

    public String getName() {
        return this.name;
    }

    Supplier getSupplier() {
        return this.supplier;
    }

    String getUnit() {
        return this.unit;
    }
}
