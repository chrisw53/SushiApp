package common;

import java.io.Serializable;

/**
 * Container class that contains the stock information of an ingredient
 * or a dish
 */
public class StockInfo implements Serializable {
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

    // Synchronized due to possibility of concurrent access
    public synchronized int getQuant() {
        return this.quant;
    }

    // Synchronized due to possibility of concurrent access
    synchronized void addQuant() {
        this.quant += this.amountToAdd;
    }

    // Synchronized due to possibility of concurrent access
    synchronized void addQuant(int quant) {
        this.quant += quant;
    }

    // Synchronized due to possibility of concurrent access
    public synchronized void setQuant(int quant) {
        this.quant = quant;
    }
}
