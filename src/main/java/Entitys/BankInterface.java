package Entitys;

import java.util.List;

/**
 * Created by carlosmorais on 15/02/16.
 */
public interface BankInterface {
    int getBalance(long accountId);
    long newAccount(String opId);
    boolean mov(long accountId, int amount, String opId);
    boolean transf(long source, long dest, int amount, String opId);
    List<Movement> movList(long id, int n);
}
