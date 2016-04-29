package Server;

import DAO.BankDAO;
import Entitys.Account;
import Entitys.BankInterface;
import Entitys.Movement;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by carlosmorais on 15/02/16.
 */
public class BankImp implements BankInterface {
    private BankDAO bankDAO;

    public BankImp(int serverId) {
        try {
            this.bankDAO = new BankDAO(serverId);
            this.bankDAO.creatDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getBalance(long accountId) {
        try {
            return this.bankDAO.getBalance(accountId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; //problem!
    }

    @Override
    public synchronized long newAccount() {
        try {
            return this.bankDAO.newAccount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public synchronized boolean mov(long id, int amount) {
        try {
            int balance = this.bankDAO.getBalance(id);
            if( (amount<0) && ((balance+amount) >= 0)) {
                this.bankDAO.withdraw(id, amount);
                return true;
            }
            else if(amount>0){
                this.bankDAO.deposit(id, amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
        //TODO: Throw exception account not found??
    }


    @Override
    public boolean transf(long source, long dest, int amount) {
        try {
            int balanceSource = this.bankDAO.getBalance(source);
            if((balanceSource-amount) >= 0) {
                this.bankDAO.withdraw(source, -amount);
                this.bankDAO.deposit(dest, amount);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
        //TODO: Throw exception account not found??
    }

    @Override
    public  List<Movement> movList(long id, int n) {
        try {
            return this.bankDAO.getMovs(id, n);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
        //TODO: Throw exception account not found??
    }

    public Set<Account> getAccounts() {
        // TODO: 27/04/16 logo se vê 
        return null;
    }


}
