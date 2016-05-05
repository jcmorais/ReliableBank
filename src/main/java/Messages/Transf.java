package Messages;

import java.io.Serializable;

/**
 * Created by jpp on 18/04/16.
 */
public class Transf implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;
    private int source;
    private int dest;
    private int amount;

    public Transf(String messageId, int source, int dest, int amount) {
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
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public void setResponse(boolean response) {
        this.response = response;
    }

    public int getSource() { return this.source; }

    public int getDest() { return this.dest; }

    public int getAmount() {
        return this.amount;
    }
}
