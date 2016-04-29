package Messages;

/**
 * Created by carlosmorais on 27/04/16.
 */
public class State implements MessageInterface {
    private String messageId;
    private boolean done;
    private boolean response;

    public State(String messageId) {
        this.messageId = messageId;
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
