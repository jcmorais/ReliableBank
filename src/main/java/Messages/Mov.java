package Messages;

import java.io.Serializable;

/**
 * Created by carlosmorais on 07/03/16.
 */
public class Mov implements Serializable, MessageInterface {
    private String messageId;
    private boolean done;
    private boolean response;
    private int accountId;
    private int amount;

    public Mov(String messageId, int accountId, int amount) {
        this.messageId = messageId;
        this.amount = amount;
        this.accountId = accountId;
        this.done = false;
        this.response = false;
    }

    public int getAccountId() {
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
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public void setResponse(boolean response) {
        this.response = response;
    }

}
