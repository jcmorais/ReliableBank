package Client;

import Entitys.Movement;
import Messages.*;
import Entitys.BankInterface;
import Utils.ObjectSerializable;
import net.sf.jgcs.*;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

import java.io.IOException;
import java.util.List;

/**
 * Created by carlosmorais on 15/02/16.
 */
public class BankStub implements BankInterface, MessageListener {
    private int count;
    private String id;
    private DataSession dataSession;
    private JGroupsGroup groupCofig;
    private Protocol protocol;
    private ControlSession controlSession;

    MessageInterface msgReply;
    private String waitFor; //id that onMessage() wants receive and notify

    public BankStub() {
        this.count = 0;
        this.id = new java.rmi.dgc.VMID().toString();
        this.msgReply = null;
        this.waitFor = null;
        try {
            JGroupsProtocolFactory jGroupsProtocolFactory = new JGroupsProtocolFactory();
            groupCofig = new JGroupsGroup("BankSDC");
            protocol = jGroupsProtocolFactory.createProtocol();
            controlSession = protocol.openControlSession(groupCofig);
            dataSession = protocol.openDataSession(groupCofig);
            dataSession.setMessageListener(this);
            controlSession.join();
        } catch (ClosedSessionException e) {
            e.printStackTrace();
        } catch (GroupException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean mov(int accountId, int amount, String opId) {
        MessageInterface msg = new Mov(this.getNextId(), accountId ,amount);
        Mov msgRes = (Mov) this.sendAndRequest(msg);
        return msgRes.isDone();
    }

    @Override
    public synchronized int getBalance(int accountId) {
        MessageInterface msg = new Balance(this.getNextId(), accountId);
        Balance msgRes = (Balance) this.sendAndRequest(msg);
        if(msgRes.isDone())
            return msgRes.getAmount();
        else
            return -1;
    }

    @Override
    public int newAccount(String opId) {
        MessageInterface msg = new NewAccount(this.getNextId());
        NewAccount msgRes = (NewAccount) this.sendAndRequest(msg);
        return msgRes.getAccountId();
    }

    @Override
    public boolean transf(int source, int dest, int amount, String opId) {
        MessageInterface msg = new Transf(this.getNextId(), source, dest, amount);
        Transf msgRes = (Transf) this.sendAndRequest(msg);
        return msgRes.isDone();
    }

    @Override
    public List<Movement> movList(int id, int n) {
        MessageInterface msg = new MovList(this.getNextId(), id, n);
        MovList msgRes = (MovList) this.sendAndRequest(msg);
        if(msgRes.isDone())
            return msgRes.getMovements();
        else
            return null;
    }

    public String getNextId(){
        return (this.id+(++this.count));
    }

    public synchronized MessageInterface sendAndRequest(MessageInterface msg){
        Message request = null;
        try {
            request = dataSession.createMessage();
            byte[] bytes = ObjectSerializable.ObjectToBytes(msg);
            request.setPayload(bytes);
            dataSession.multicast(request, new JGroupsService(), null);
            this.waitFor = msg.getMessageId();
            this.msgReply = null;
            while (this.msgReply == null)
                wait();
            return this.msgReply;
        } catch (GroupException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            this.msgReply = null;
        }
        return null;
    }

    @Override
    public synchronized Object onMessage(Message message) {
        MessageInterface msg = (MessageInterface) ObjectSerializable.BytesToObject(message.getPayload());

        if( msg.isResponse() && msg.getMessageId().equals(this.waitFor)){
            this.msgReply = msg;
            this.waitFor=null;
            notify();
        }
        return null;
    }
}
