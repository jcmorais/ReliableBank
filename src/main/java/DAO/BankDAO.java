package DAO;

import Entitys.Account;
import Entitys.DataBaseBank;
import Entitys.Movement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class BankDAO {


    public BankDAO() { }


    public void creatDB(Connection connection){
        try {
            Statement st = connection.createStatement();
            //apaga o que existe
            try {
                st.executeUpdate("DROP TABLE accounts");
                st.executeUpdate("DROP TABLE dbInfo");
                st.executeUpdate("DROP TABLE movement");
            }
            catch (SQLSyntaxErrorException sqle){ }

            //cria tabelas para dados
            st.executeUpdate("create table accounts (id int PRIMARY KEY, balance int)");
            st.executeUpdate("create table dbInfo(idAccount int, idMov int, lastOp varchar(100))");
            st.executeUpdate("create table movement(id int, idAccount int, type varchar(30), amount int)");
            log("tables created");

            st.executeUpdate("insert into dbInfo VALUES (1000, 1000, null)");
            log("Info inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public long newAccount(Connection connection) throws SQLException {
        long id = this.getNextAccountId(connection);
        String updateStatement = "INSERT INTO accounts VALUES (?, 0)";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1, (int) id);
        statement.execute();
        statement.close();
        return id;
    }


    public int getBalance(Connection connection, long id) throws SQLException {
        String st = "SELECT balance FROM accounts WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(st);
        statement.setInt(1, (int) id);
        int balance=0;
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            balance = rs.getInt(1);
        }
        statement.close();
        return balance;
    }

    public void mov(Connection connection, long id, int amount) throws SQLException {
        String updateStatement = "UPDATE accounts SET balance = balance + ? WHERE id = ? ";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1,amount);
        statement.setInt(2, (int) id);
        statement.execute();
        statement.close();
    }
    

    public void updateLastOpId(Connection connection, String opId) throws SQLException {
        String updateStatement = "UPDATE dbInfo SET lastOp = ? ";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setString(1, opId);
        statement.execute();
        statement.close();
    }


    public String getLastOpId(Connection connection) throws SQLException {
        String st = "SELECT lastOp FROM dbInfo";
        PreparedStatement statement = connection.prepareStatement(st);
        String id=null;
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            id = rs.getString(1);
        }
        statement.close();
        return id;
    }

    public int getIdAccount(Connection connection) throws SQLException {
        String st = "SELECT idAccount FROM dbInfo";
        PreparedStatement statement = connection.prepareStatement(st);
        int id=-1;
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            id = rs.getInt(1);
        }
        statement.close();
        return id;
    }

    public int getIdMov(Connection connection) throws SQLException {
        String st = "SELECT idMov FROM dbInfo";
        PreparedStatement statement = connection.prepareStatement(st);
        int id=-1;
        ResultSet rs = statement.executeQuery();
        if(rs.next()){
            id = rs.getInt(1);
        }
        statement.close();
        return id;
    }


    // TODO: 28/04/16 passar a receber como arg o objeto Movement, com mais detalhes para serem persitidos...
    public void movement(Connection connection, long accountId, String type, int amount) throws SQLException {
        String updateStatement = "INSERT INTO movement VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(updateStatement);

        statement.setInt(1, this.getNextMovId(connection));
        statement.setInt(2, (int) accountId);
        statement.setString(3, type);
        statement.setInt(4, amount);
        statement.execute();
        statement.close();
    }


    public List<Movement> getMovs(Connection connection, long accountId, int n) throws SQLException {
        ArrayList<Movement> res = new ArrayList<>();
        String updateStatement = "SELECT * FROM movement WHERE idAccount = ? order by id desc FETCH FIRST ? ROWS ONLY";
        // order by id desc limit N;
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1,(int) accountId);
        statement.setInt(2, n);
        ResultSet rs = statement.executeQuery();

        while(rs.next()){
            res.add(new Movement(rs.getString(3), rs.getInt(4)));
        }

        statement.close();
        return res;
    }


    public int getNextAccountId(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT idAccount from dbInfo");
        rs.next();
        int id = rs.getInt(1);
        st.executeUpdate("UPDATE dbInfo SET idAccount = "+(id+1));
        return id;
    }

    public int getNextMovId(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT idMov from dbInfo");
        rs.next();
        int id = rs.getInt(1);
        st.executeUpdate("UPDATE dbInfo SET idMov = "+(id+1));
        return id;
    }


    public List<Account> getAccounts(Connection connection) throws SQLException {
        List<Account> accounts = new ArrayList<Account>();
        String stAccount = "SELECT * FROM accounts";
        PreparedStatement statement = connection.prepareStatement(stAccount);
        ResultSet rs = statement.executeQuery();

        while(rs.next()){
            Account account = new Account(rs.getLong(1), rs.getInt(2));
            account.setMovs(this.getMovs(connection, account.getId(), 10000));
            accounts.add(account);
        }

        statement.close();
        return accounts;
    }


    private void log(String s){
        System.out.println(s);
    }

    public List<Movement> getMovements(Connection connection) throws SQLException {
        ArrayList<Movement> res = new ArrayList<>();
        String updateStatement = "SELECT * FROM movement";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        ResultSet rs = statement.executeQuery();
        while(rs.next()){
            res.add(new Movement(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4)));
        }
        statement.close();
        return res;
    }

    public DataBaseBank getDataBaseBank(Connection connection) {
        DataBaseBank db = new DataBaseBank();
        try {
            db.setAccounts(this.getAccounts(connection));
            db.setMovements(this.getMovements(connection));
            db.setIdAccount(this.getIdAccount(connection));
            db.setLastOpId(this.getLastOpId(connection));
            db.setIdMov(this.getIdMov(connection));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return db;
    }

    public void populateDB(Connection connection, DataBaseBank dataBaseBank) throws SQLException {
        boolean first=true;

        //Accounts
        if(dataBaseBank.getAccounts().size()>0) {
            StringBuilder sbSt = new StringBuilder();
            sbSt.append("INSERT INTO accounts VALUES ");
            for (Account account : dataBaseBank.getAccounts()) {
                if (first) {
                    first = false;
                } else
                    sbSt.append(" , ");
                sbSt.append("(" + account.getId() + " , " + account.getBalance() + " )");
            }
            String updateStatement = sbSt.toString();
            PreparedStatement statement = connection.prepareStatement(updateStatement);
            statement.execute();
            statement.close();
        }

        //Movements
        if(dataBaseBank.getMovements().size()>0) {
            first = true;
            StringBuilder sbSt = new StringBuilder();
            sbSt.append("INSERT INTO movement VALUES ");
            for (Movement m : dataBaseBank.getMovements()) {
                if (first) {
                    first = false;
                } else
                    sbSt.append(" , ");
                sbSt.append("(" + m.getId() + " , " + m.getIdAccount() + " , '" + m.getType() + "' , " + m.getAmount() + " )");
            }
            String updateStatement = sbSt.toString();
            PreparedStatement statement = connection.prepareStatement(updateStatement);
            statement.execute();
            statement.close();
        }

        //dbInfo
        String updateStatement = "UPDATE dbInfo SET idAccount = ?, idMov = ?, lastOp = ? ";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1, dataBaseBank.getIdAccount());
        statement.setInt(2, dataBaseBank.getIdMov());
        statement.setString(3, dataBaseBank.getLastOpId());
        statement.execute();
        statement.close();
    }
}
