package Entitys;

import java.util.List;

/**
 * Created by carlosmorais on 15/02/16.
 */
public interface BankInterface {
    public int getBalance(long accountId);
    long newAccount();
    boolean mov(long accountId, int amount);
    boolean transf(long source, long dest, int amount);
    List<Movement> movList(long id, int n);
}
