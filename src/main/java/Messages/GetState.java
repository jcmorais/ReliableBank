package Messages;

import Entitys.DataBaseBank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosmorais on 27/04/16.
 */

/**
 * Operação utilizada para um Server pedir atualização de estado a outros Servers
 */
public class GetState implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;

    private boolean incremental; //pedido de atualização INCREMENTAL
    private boolean allDB; //pedido de atualização TOTAL

    //incremental
    //id da última operação feita com sucesso pelo Servidor, pretende-se receber todas as operações a partir daqui
    private String lastOpId;
    private List<MessageInterface> operations;

    //allDb
    private DataBaseBank dataBaseBank;

    public GetState(String messageId, String lastOpId) {
        this.messageId = messageId;
        this.lastOpId = lastOpId;
        this.incremental = false;
        this.allDB = false;
        this.dataBaseBank = null;
        this.operations = null;
    }

    @Override
    public String getMessageId() { return this.messageId; }

    @Override
    public boolean isResponse() { return this.response; }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getLastOpId() {
        return lastOpId;
    }

    public List<MessageInterface> getOperations() {
        return operations;
    }

    public void setOperations(List<MessageInterface> operations) {
        this.operations = operations;
    }

    public boolean isAllDB() {
        return allDB;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setAllDB(boolean allDB) {
        this.allDB = allDB;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public DataBaseBank getDataBaseBank() {
        return dataBaseBank;
    }

    public void setDataBaseBank(DataBaseBank dataBaseBank) {
        this.dataBaseBank = dataBaseBank;
    }
}
