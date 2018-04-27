package common;

public class Ingredient extends Model {
    private String name;

    Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
