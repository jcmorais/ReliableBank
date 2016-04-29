package DAO;

import Entitys.Movement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class BankDAO {
    // TODO: 28/04/16 "bank" + identificador único -> passado como argumento?
    private static String DB_PREFIX = "bank";
    private static String PROTOCOL = "jdbc:derby:";

    private String DB_NAME;

    private Connection connection;

    public BankDAO(int id) throws SQLException {
        this.DB_NAME = DB_PREFIX+id;
    }


    public void loadDB(){
        try {
            this.connection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void creatDB(){
        try {
            this.connection = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true");
            log("Connected to and created database "+DB_NAME);
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
            st.executeUpdate("create table dbInfo(idAccount int, idMov int)");
            st.executeUpdate("create table movement(id int, idAccount int, type varchar(30), amount int)");
            log("tables created");


            st.executeUpdate("insert into dbInfo VALUES (1000,1000)");
            log("Info nserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public long newAccount() throws SQLException {
        long id = this.getNextAccountId();
        String updateStatement = "INSERT INTO accounts VALUES (?, 0)";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1, (int) id);
        statement.execute();
        statement.close();
        return id;
    }


    public int getBalance(long id) throws SQLException {
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

    public void deposit(long id, int amount) throws SQLException {
        String updateStatement = "UPDATE accounts SET balance = balance + ? WHERE id = ? ";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1,amount);
        statement.setInt(2, (int) id);
        statement.execute();
        statement.close();
        this.movement(id, "crédito", amount);
    }

    public void withdraw(long id, int amount) throws SQLException {
        String updateStatement = "UPDATE accounts SET balance = balance + ? WHERE id = ? ";
        PreparedStatement statement = connection.prepareStatement(updateStatement);
        statement.setInt(1,amount);
        statement.setInt(2, (int) id);
        statement.execute();
        statement.close();
        this.movement(id, "débito", amount);
    }


    // TODO: 28/04/16 passar a receber como arg o objeto Movement, com mais detalhes para serem persitidos...
    public void movement(long accountId, String type, int amount) throws SQLException {
        String updateStatement = "INSERT INTO movement VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(updateStatement);

        statement.setInt(1, this.getNextMovId());
        statement.setInt(2, (int) accountId);
        statement.setString(3, type);
        statement.setInt(4, amount);
        statement.execute();
        statement.close();
    }


    public List<Movement> getMovs(long accountId, int n) throws SQLException {
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


    public int getNextAccountId() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT idAccount from dbInfo");
        rs.next();
        int id = rs.getInt(1);
        st.executeUpdate("UPDATE dbInfo SET idAccount = "+(id+1));
        return id;
    }

    public int getNextMovId() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT idMov from dbInfo");
        rs.next();
        int id = rs.getInt(1);
        st.executeUpdate("UPDATE dbInfo SET idMov = "+(id+1));
        return id;
    }

    private void log(String s){
        System.out.println(s);
    }

    public static void main(String[] args) throws SQLException {
        BankDAO dao = new BankDAO(1);


        dao.creatDB();
        //dao.loadDB();


        /*
        System.out.println(dao.getNextMovId());
        System.out.println(dao.getNextMovId());
        System.out.println(dao.getNextMovId());
        */

        dao.deposit(1000, 500);
        dao.deposit(1001, 500);
        dao.withdraw(1000, 100);
        dao.withdraw(1001, 40);
        dao.withdraw(1000, 40);



        System.out.println("1000: "+dao.getBalance(1000));
        List<Movement> res = dao.getMovs(1000, 4);
        for(Movement m : res)
            System.out.println(m.toString());


        System.out.println("1001: "+dao.getBalance(1001));
        res = dao.getMovs(1001, 1);
        for(Movement m : res)
            System.out.println(m.toString());

    }
}
