package Messages;

/**
 * Created by carlosmorais on 07/03/16.
 */
public interface MessageInterface{
    public String getMessageId();
    public boolean isResponse();
    boolean isDone();
    void setDone();
    void setResponse();

    public int getState();
}
