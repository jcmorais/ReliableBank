package Entitys;

import java.io.Serializable;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class Movement implements Serializable {
    private int id;
    private int idAccount;
    private String type;
    private int amount;

    public Movement(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public Movement(int id, int idAccount, String type, int amount) {
        this.id = id;
        this.idAccount = idAccount;
        this.type = type;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public int getIdAccount() {
        return idAccount;
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
