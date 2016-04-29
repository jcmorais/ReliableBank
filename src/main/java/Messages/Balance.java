package Messages;

import java.io.Serializable;

/**
 * Created by carlosmorais on 07/03/16.
 */
public class Balance implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;
    private long accountId;
    private int amount;

    public Balance(String messageId, long accountId) {
        this.messageId = messageId;
        this.accountId = accountId;
        this.done = false;
        this.response = false;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
}
