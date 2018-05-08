package common;

public class Supplier extends Model {
    private long distance;

    public Supplier(String name, long distance) {
        this.name = name;
        this.distance = distance;
    }

    public String getName() {
        return this.name;
    }

    public long getDistance() {
        return this.distance;
    }

    void setDistance(long distance) {
        notifyUpdate("distance", this.distance, distance);
        this.distance = distance;
    }
}
