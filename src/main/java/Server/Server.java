package Server;

import Messages.*;
import Utils.ObjectSerializable;
import net.sf.jgcs.*;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

import javax.xml.crypto.dsig.TransformService;
import java.io.*;
import java.util.Scanner;

/**
 * Created by carlosmorais on 15/02/16.
 */
public class Server implements MessageListener{
    private DataSession dataSession;
    private BankImp bank;
    private JGroupsGroup groupCofig;
    private Protocol protocol;
    private ControlSession controlSession;
    private Service service;

    private int count=0;

    public Server(int serverId, int opt1, int opt2){
        this.bank = new BankImp(serverId);
        try {
            JGroupsProtocolFactory jGroupsProtocolFactory = new JGroupsProtocolFactory();
            service = new JGroupsService();
            groupCofig = new JGroupsGroup("BankSDC");
            protocol = jGroupsProtocolFactory.createProtocol();
            controlSession = protocol.openControlSession(groupCofig);
            dataSession = protocol.openDataSession(groupCofig);
            dataSession.setMessageListener(this);
            controlSession.join();
        } catch (GroupException e) {
            e.printStackTrace();
        }
    }

    public void getState(){
        //tem que saber se é o único

    }

    public synchronized Object onMessage(Message message) {
        log("Message received "+ (++count) );

        MessageInterface msg = (MessageInterface) ObjectSerializable.BytesToObject(message.getPayload());
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        try {
            if((msg instanceof Balance) && (!msg.isResponse())){
                Balance bal = (Balance) msg;
                bal.setAmount(bank.getBalance(bal.getAccountId()));
                bal.setResponse();
                bal.setDone();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(bal);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService(), null);
            }
            else if((msg instanceof Mov) && (!msg.isResponse())){
                Mov mov = (Mov) msg;
                if(bank.mov(mov.getAccountId(), mov.getAmount()))
                    mov.setDone();
                mov.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(mov);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
            }
            else if ((msg instanceof NewAccount) && (!msg.isResponse())) {
                NewAccount newAccount = (NewAccount) msg;
                newAccount.setAccountId(bank.newAccount());
                newAccount.setDone();
                newAccount.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(newAccount);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
            }
            else if ((msg instanceof Transf) && (!msg.isResponse())) {
                Transf transf = (Transf) msg;
                if(bank.transf(transf.getSource(), transf.getDest(), transf.getAmount()))
                    transf.setDone();
                transf.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(transf);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
            }
            else if ((msg instanceof MovList) && (!msg.isResponse())) {
                MovList movList = (MovList) msg;
                movList.setMovements(bank.movList(movList.getAccountId(), movList.getnMovs()));
                movList.setDone();
                movList.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(movList);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
            }

        } catch (GroupException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void log(String log){
        System.out.println(log);
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        String[] tokens = new String[0];
        String read;
        int id = 0;
        Server server;

        String help = "How star the Server?\n"+
                "-> start creat id\n"+
                "-> start load  id\n"+
                "-> join  creat id\n"+
                "-> join  load  id\n";

        while(flag){
            System.out.println(help);
            read = scanner.nextLine();
            tokens = read.split(" ");

            if(tokens.length >= 2) {
                try {
                    id = Integer.parseInt(tokens[2]);
                    flag = false;
                } catch (NumberFormatException e) {
                    System.out.println("wrong...");
                }
            }
            else
                System.out.println("wrong...");
        }

        switch (tokens[0]){
            case "start":
                if(tokens[1].equals("creat"))
                    //creat
                    server = new Server(id, 1, 1);
                else if(tokens[1].equals("load"))
                    //load
                    server = new Server(id, 1, 2);
                break;
            case "join":
                if(tokens[1].equals("creat"))
                    //creat
                    server = new Server(id, 2, 1);
                else if(tokens[1].equals("load"))
                    //load
                    server = new Server(id, 2, 2);
                break;
            default:
                break;
        }


        System.out.println("Start Server...");

        while (true); //never end...
    }
}
