package Messages;

import java.io.Serializable;

/**
 * Created by jpp on 18/04/16.
 */
public class NewAccount implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private long accountId;
    private boolean response;

    public NewAccount(String messageId) {
        this.messageId = messageId;
        this.done = false;
        this.accountId = -1;
        this.done = false;
        this.response = false;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
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
