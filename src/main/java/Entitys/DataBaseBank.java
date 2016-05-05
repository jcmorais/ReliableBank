package Entitys;

import java.io.Serializable;
import java.util.List;

/**
 * Created by carlosmorais on 04/05/16.
 */

//para passar o estado da BD
public class DataBaseBank implements Serializable {
    private List<Account> accounts;
    private List<Movement> movements;
    //dbInfo
    private int idAccount;
    private int idMov;
    private String lastOpId;

    public DataBaseBank() { }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public int getIdAccount() {
        return idAccount;
    }

    public int getIdMov() {
        return idMov;
    }

    public String getLastOpId() {
        return lastOpId;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void setLastOpId(String lastOpId) {
        this.lastOpId = lastOpId;
    }

    public void setIdMov(int idMov) {
        this.idMov = idMov;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("++++++++++++ DataBase Bank ++++++++++++\n\n");
        sb.append("Info => idAccount: "+this.idAccount+", idMov: "+this.idMov+" lastOpId: "+this.lastOpId+";\n\n");
        sb.append("Accounts\n");
        for(Account a: this.accounts)
            sb.append("  ->"+a.toString()+"\n");
        sb.append("\nAll Movements\n");
        for(Movement a: this.movements)
            sb.append("  ->"+a.toString()+" : \n");
        sb.append("\n\n");
        return sb.toString();
    }
}
