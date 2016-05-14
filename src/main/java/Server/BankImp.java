package Server;

import DAO.BankDAO;
import Entitys.BankInterface;
import Entitys.DataBaseBank;
import Entitys.Movement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by carlosmorais on 15/02/16.
 */
public class BankImp implements BankInterface {
    private BankDAO bankDAO;
    private static String DB_PREFIX = "bank";
    private static String PROTOCOL = "jdbc:derby:";
    private String DB_NAME;
    private Connection connection;

    public BankImp(int serverId) {
            this.bankDAO = new BankDAO();
            this.DB_NAME = DB_PREFIX+serverId;
    }

    public void creatDB(){
        try {
            this.connection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true");
            this.connection.setAutoCommit(false);
            this.bankDAO.creatDB(this.connection);
            log("Connected to and created database "+DB_NAME);
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void loadDB() {
        try {
            this.connection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";");
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getBalance(int accountId) {
        try {
            if(this.bankDAO.hasAccount(connection, accountId))
                return this.bankDAO.getBalance(this.connection, accountId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; //problem!
    }

    @Override
    public synchronized int newAccount(String opId) {
        int id = -1;
        try {
            id = this.bankDAO.newAccount(this.connection);
            this.bankDAO.updateLastOpId(this.connection, opId);
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public synchronized boolean mov(int id, int amount, String opId) {
        boolean ok = false;
        try {
            if(!this.bankDAO.hasAccount(connection, id))
                return false; //invalid account!

            int balance = this.bankDAO.getBalance(this.connection, id);
            if( (amount<0) && ((balance+amount) >= 0)) {
                this.bankDAO.mov(this.connection, id, amount);
                this.bankDAO.movement(this.connection, id, "WITHDRAW", amount);
                ok =  true;
            }
            else if(amount>0){
                this.bankDAO.mov(this.connection, id, amount);
                this.bankDAO.movement(this.connection, id, "DEPOSIT", amount);
                ok = true;
            }
            this.bankDAO.updateLastOpId(this.connection, opId);
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return ok;
    }


    @Override
    public boolean transf(int source, int dest, int amount, String opId) {
        try {
            if(!this.bankDAO.hasAccount(connection, source))
                return false; //invalid account!
            if(!this.bankDAO.hasAccount(connection, dest))
                return false; //invalid account!

            int balanceSource = this.bankDAO.getBalance(this.connection, source);
            if((balanceSource-amount) >= 0) {
                this.bankDAO.mov(this.connection, source, -amount);
                this.bankDAO.movement(this.connection, source, "TRANSF", -amount);
                this.bankDAO.mov(this.connection, dest, amount);
                this.bankDAO.movement(this.connection, dest, "TRANSF", amount);
                this.bankDAO.updateLastOpId(this.connection, opId);
                this.connection.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public  List<Movement> movList(int id, int n) {
        try {
            if(!this.bankDAO.hasAccount(connection, id))
                return null;
            return this.bankDAO.getMovs(this.connection, id, n);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLastOpId() {
        try {
            return this.bankDAO.getLastOpId(this.connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void log(String s){
        System.out.println(s);
    }

    public void populateDB(DataBaseBank dataBaseBank) {
        try {
            this.bankDAO.populateDB(this.connection, dataBaseBank);
            this.connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public DataBaseBank getDataBaseBank() {
        return this.bankDAO.getDataBaseBank(this.connection);
    }
}
