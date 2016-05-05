package Entitys;

import java.util.List;

/**
 * Created by carlosmorais on 15/02/16.
 */
public interface BankInterface {
    int getBalance(int accountId);
    int newAccount(String opId);
    boolean mov(int accountId, int amount, String opId);
    boolean transf(int source, int dest, int amount, String opId);
    List<Movement> movList(int id, int n);
}
