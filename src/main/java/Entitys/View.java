package Entitys;

import Messages.MessageInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class View {
    private int state;
    private HashMap<Integer, List<MessageInterface> > operations;

    public View() {
        this.state = 1;
        this.operations = new HashMap<>();
    }


    public int getState() {
        return state;
    }

    public void addMessage(MessageInterface msg){
        if(this.operations.containsKey(this.state))
            this.operations.get(this.state).add(msg);
        else{
            this.operations.put(this.state, new ArrayList<MessageInterface>());
            this.operations.get(this.state).add(msg);
        }
    }


    public List<MessageInterface> getListSinceId(String id, int stateEnd){
        ArrayList<MessageInterface> res = new ArrayList<>();
        boolean add = false;
        if(id == null)
            add = true;
        for (int i = 1; i<stateEnd; i++ ){
            if(this.operations.containsKey(i)){
                for (MessageInterface operation : this.operations.get(i)) {
                    //procura até encontrat o id, depois de encontrar vai adicionar tudo!!
                    if(add)
                        res.add(operation);
                    if(!add && operation.getMessageId().equals(id))
                        add = true;
                }
            }
        }
        return res;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void updateState() {
        this.state++;
    }
}
