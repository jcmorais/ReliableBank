package Entitys;

import java.io.Serializable;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class Movement implements Serializable {
    private String type;
    private int amount;

    public Movement(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
