package common;

public class StockInfo {
    private int threshold;
    private int amountToAdd;
    private double quant;

    StockInfo(int threshold, int amountToAdd, double quant) {
        this.threshold = threshold;
        this.amountToAdd = amountToAdd;
        this.quant = quant;
    }

    int getThreshold() {
        return this.threshold;
    }

    int getAmountToAdd() {
        return this.amountToAdd;
    }

    double getQuant() {
        return this.quant;
    }

    void setQuant() {
        this.quant += this.amountToAdd;
    }

    void setQuant(double minus) {
        this.quant -= minus;
    }
}
