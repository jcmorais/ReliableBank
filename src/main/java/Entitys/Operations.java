package Entitys;

import Messages.MessageInterface;

import java.util.*;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class Operations {
    /**
     * Onde são armezenadas todas as operações que vão ser feitas no Server
     * Apenas são guardadas operações de escrita (que provocam uma alteração ao estado)
     */
    private List<MessageInterface> operations;
    private Set<String> ids;


    public Operations() {
        this.operations = new ArrayList<>();
        this.ids = new HashSet<>();
    }

    public void addMessage(MessageInterface msg){
        this.operations.add(msg);
        this.ids.add(msg.getMessageId());
    }

    public List<MessageInterface> getListSinceId(String id){
        ArrayList<MessageInterface> res = new ArrayList<>();
        boolean add = false;
        if(id == null)
            add=true;
        for(MessageInterface msg : this.operations){
            if(add)
                res.add(msg);
            if(!add && msg.getMessageId().equals(id))
                add = true;
        }
        return res;
    }

    public boolean containsOpId(String opId){
        return this.ids.contains(opId);
    }
}
