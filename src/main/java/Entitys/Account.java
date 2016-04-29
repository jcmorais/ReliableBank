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
    private List<String> movs;

    public Account(long id) {
        this.id = id;
        this.balance = 0;
        this.movs = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<String> getMovs() {
        List<String> res = new ArrayList<>();

        for(String mov : movs) {
            res.add(mov);
        }

        return res;
    }

    public void deposit(boolean transf, int val) {
        balance += val;
        if(transf)
            movs.add("Transf:" + val);
        else
            movs.add("Deposit:" + val);
    }

    public boolean withdraw(boolean transf, int val) {
        if(balance+val<0) return false;

        balance += val;
        if(transf)
            movs.add("Transf:" + val);
        else
            movs.add("Withdraw:" + abs(val));

        return true;
    }
}
