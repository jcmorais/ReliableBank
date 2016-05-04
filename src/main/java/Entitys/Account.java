package Entitys;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by jpp on 18/04/16.
 */

public class Account implements Serializable {
    private long id;
    private int balance;
    private List<Movement> movs;

    public Account(long id, int balance) {
        this.id = id;
        this.balance = balance;
        this.movs = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<Movement> getMovs() {
        List<Movement> res = new ArrayList<>();

        for(Movement mov : movs) {
            res.add(mov);
        }

        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", movs:");

        for(Movement m : this.movs)
            sb.append(">>>"+m.toString());

        sb.append('}');

        return sb.toString();
    }

    public void setMovs(List<Movement> movs) {
        this.movs = movs;
    }
}
