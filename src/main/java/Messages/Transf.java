package Messages;

import java.io.Serializable;

/**
 * Created by jpp on 18/04/16.
 */
public class Transf implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;
    private long source;
    private long dest;
    private int amount;

    public Transf(String messageId, long source, long dest, int amount) {
        this.messageId = messageId;
        this.source = source;
        this.dest = dest;
        this.amount = amount;
        this.done = false;
        this.response = false;
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

    public long getSource() { return this.source; }

    public long getDest() { return this.dest; }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
