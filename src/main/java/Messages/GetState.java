package Messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlosmorais on 27/04/16.
 */
public class GetState implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;

    private int stateId;

    //id da última operação feita com sucesso pelo Servidor
    private String lastOpId;

    private List<MessageInterface> operations;

    public GetState(String messageId, String lastOpId, int stateId) {
        this.messageId = messageId;
        this.lastOpId = lastOpId;
        this.stateId = stateId;
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
    public void setDone() {
        this.done = true;
    }

    @Override
    public void setResponse() { this.response = true; }

    public int getState() {
        return this.stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
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
}
