package common;

public class StockInfo {
    private int threshold;
    private int amountToAdd;
    private int quant;

    public StockInfo(int threshold, int amountToAdd, int quant) {
        this.threshold = threshold;
        this.amountToAdd = amountToAdd;
        this.quant = quant;
    }

    public void setRestockLevel(int threshold, int amountToAdd) {
        this.threshold = threshold;
        this.amountToAdd = amountToAdd;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getAmountToAdd() {
        return this.amountToAdd;
    }

    public int getQuant() {
        return this.quant;
    }

    void addQuant() {
        this.quant += this.amountToAdd;
    }

    void addQuant(int quant) {
        this.quant += quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
    }
}
