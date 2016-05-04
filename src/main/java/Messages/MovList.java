package Messages;


import Entitys.Movement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jpp on 18/04/16.
 */
public class MovList implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;
    private long accountId;
    private int nMovs;
    private List<Movement> movements;

    private int stateId;

    public MovList(String messageId, long accountId, int nMovs) {
        this.messageId = messageId;
        this.accountId = accountId;
        this.nMovs = nMovs;
        this.done = false;
        this.response = false;
        this.movements = new ArrayList<>();
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

    public long getAccountId() {
        return accountId;
    }

    public int getnMovs() {
        return nMovs;
    }

    public List<Movement> getMovements() {
        return this.movements;
    }

    public void setMovements(List<Movement> movList) {
        this.movements=movList;
    }

    public int getState() {
        return this.stateId;
    }
}
