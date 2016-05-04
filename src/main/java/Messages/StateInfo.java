package Messages;

import java.io.Serializable;

/**
 * Created by carlosmorais on 27/04/16.
 */
public class StateInfo implements MessageInterface, Serializable {
    private String messageId;
    private boolean done;
    private boolean response;

    private int state;
    private boolean newState;

    //pedido que é feito num servidor que pretente entrar no "grupo"
    //provoca uma alteração no "estado" dos servidores
    private boolean serverRequest;


    public StateInfo(String messageId) {
        this.messageId = messageId;
        this.newState = false;
        this.done = false;
        this.response = false;
        this.serverRequest = false;
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
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isServerRequest() {
        return serverRequest;
    }

    public void setServerRequest(){
        this.serverRequest = true;
    }

    public boolean isNewState() {
        return newState;
    }

    public void setNewState() {
        this.newState = true;
    }
}
