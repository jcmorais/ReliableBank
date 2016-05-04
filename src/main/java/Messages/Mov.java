package Messages;

import java.io.Serializable;

/**
 * Created by carlosmorais on 07/03/16.
 */
public class Mov implements Serializable, MessageInterface {
    private String messageId;
    private boolean done;
    private boolean response;
    private long accountId;
    private int amount;

    private int stateId;

    public Mov(String messageId, long accountId, int amount) {
        this.messageId = messageId;
        this.amount = amount;
        this.accountId = accountId;
        this.done = false;
        this.response = false;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getAmount() {
        return amount;
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
}
