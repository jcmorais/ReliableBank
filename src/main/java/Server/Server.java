package Server;

import Entitys.Movement;
import Entitys.Operations;
import Messages.*;
import Utils.ObjectSerializable;
import net.sf.jgcs.*;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

import java.io.*;
import java.util.List;
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
    private Operations operations;
    private boolean readFromClient;
    private boolean recoverMode;
    private String waitFor; //id that onMessage() wants receive and notify
    private String id;

    public Server(int serverId, int opt){
        this.bank = new BankImp(serverId);
        this.id = new java.rmi.dgc.VMID().toString();
        this.operations = new Operations();
        this.recoverMode = false;
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
        if(opt == 1){
            this.startServer();
        }
        else if(opt == 2){
            this.joinServer();
        }
        else if(opt == 3){
            this.recoverServer();
        }
    }


    private void startServer() {
        this.operations = new Operations();
        this.bank.creatDB(); //cria uma BD limpa
        this.readFromClient = true; //pronto a ler pedidos do cliente
    }


    private void joinServer() {
        try {
            //cria a BD limpa
            this.bank.creatDB();
            //pede o estado (TOTAL)
            GetState getState = new GetState(this.getNextId(), null);
            getState.setAllDB(true);
            Message request = dataSession.createMessage();
            byte[] bytes = ObjectSerializable.ObjectToBytes(getState);
            request.setPayload(bytes);
            this.waitFor = getState.getMessageId(); //esta é a resposta ao GetState que quero receber
            this.recoverMode = true; //Server está em recoverMode, não responde a pedidos do Client
            dataSession.multicast(request, new JGroupsService(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recoverServer() {
        try{
            //load da BD
            this.bank.loadDB();
            //pede o estado (INCREMENTAL)
            GetState getState = new GetState(this.getNextId(), this.bank.getLastOpId());
            getState.setIncremental(true);
            Message request = dataSession.createMessage();
            byte[] bytes = ObjectSerializable.ObjectToBytes(getState);
            request.setPayload(bytes);
            this.waitFor = getState.getMessageId(); //esta é a resposta ao estado que quero receber
            this.recoverMode = true; //Server está em recoverMode, não responde a pedidos do Client
            dataSession.multicast(request, new JGroupsService(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNextId(){
        return (this.id+(++this.count));
    }

    public synchronized Object onMessage(Message message) {
        log("Message received "+ (++count) );
        MessageInterface msg = (MessageInterface) ObjectSerializable.BytesToObject(message.getPayload());

        try {
            if(this.readFromClient){
                if((msg instanceof Balance) && (!msg.isResponse())){
                    Balance bal = (Balance) msg;
                    int amount = bank.getBalance(bal.getAccountId());
                    if(amount>0) {
                        bal.setAmount(amount);
                        bal.setDone(true);
                    }
                    bal.setResponse(true);
                    Message reply = dataSession.createMessage();
                    byte[] bytes = ObjectSerializable.ObjectToBytes(bal);
                    reply.setPayload(bytes);
                    dataSession.multicast(reply, new JGroupsService(), null);
                }
                else if((msg instanceof Mov) && (!msg.isResponse())){
                    Mov mov = (Mov) msg;
                    if(bank.mov(mov.getAccountId(), mov.getAmount(), mov.getMessageId()))
                        mov.setDone(true);
                    mov.setResponse(true);
                    Message reply = dataSession.createMessage();
                    byte[] bytes = ObjectSerializable.ObjectToBytes(mov);
                    reply.setPayload(bytes);
                    dataSession.multicast(reply, new JGroupsService() , null);
                    this.operations.addMessage(msg);
                }
                else if ((msg instanceof NewAccount) && (!msg.isResponse())) {
                    NewAccount newAccount = (NewAccount) msg;
                    newAccount.setAccountId(bank.newAccount(newAccount.getMessageId()));
                    newAccount.setDone(true);
                    newAccount.setResponse(true);
                    Message reply = dataSession.createMessage();
                    byte[] bytes = ObjectSerializable.ObjectToBytes(newAccount);
                    reply.setPayload(bytes);
                    dataSession.multicast(reply, new JGroupsService() , null);
                    this.operations.addMessage(msg);
                }
                else if ((msg instanceof Transf) && (!msg.isResponse())) {
                    Transf transf = (Transf) msg;
                    if(bank.transf(transf.getSource(), transf.getDest(), transf.getAmount(), transf.getMessageId())) {
                        transf.setDone(true);
                        this.operations.addMessage(msg);
                    }
                    transf.setResponse(true);
                    Message reply = dataSession.createMessage();
                    byte[] bytes = ObjectSerializable.ObjectToBytes(transf);
                    reply.setPayload(bytes);
                    dataSession.multicast(reply, new JGroupsService() , null);
                }
                else if ((msg instanceof MovList) && (!msg.isResponse())) {
                    MovList movList = (MovList) msg;
                    List<Movement> movs = bank.movList(movList.getAccountId(), movList.getnMovs());
                    if(movs != null) {
                        movList.setMovements(movs);
                        movList.setDone(true);
                    }
                    movList.setResponse(true);
                    Message reply = dataSession.createMessage();
                    byte[] bytes = ObjectSerializable.ObjectToBytes(movList);
                    reply.setPayload(bytes);
                    dataSession.multicast(reply, new JGroupsService() , null);
                }
                else if ((msg instanceof GetState) && (!msg.isResponse())) {
                    GetState getState = (GetState) msg;
                    boolean flag=true;
                    getState.setResponse(true);
                    if(getState.isIncremental()) {
                        if(this.operations.containsOpId(getState.getLastOpId()))
                            getState.setOperations(this.operations.getListSinceId(getState.getLastOpId()));
                        else
                            //este servidor não é capaz de responder ao pedido => ignora a msg
                            flag = false;
                    }
                    else
                        getState.setDataBaseBank(this.bank.getDataBaseBank());
                    if (flag) {
                        log(this.bank.getDataBaseBank().toString());
                        getState.setDone(true);
                        Message reply = dataSession.createMessage();
                        byte[] bytes = ObjectSerializable.ObjectToBytes(getState);
                        reply.setPayload(bytes);
                        dataSession.multicast(reply, new JGroupsService(), null);
                    }
                }
            }
            else if(this.recoverMode){
                if((msg instanceof GetState) && (msg.isResponse() && msg.getMessageId().equals(this.waitFor))) {
                    GetState getState = (GetState) msg;
                    if(getState.isAllDB())
                        this.bank.populateDB(getState.getDataBaseBank());
                    else if(getState.isIncremental())
                        this.applyIncrementaOp(getState.getOperations());
                    //pronto a receber pedidos a partir daqui
                    this.recoverMode = false;
                    this.readFromClient = true;
                    log("=> Recovered!");
                    log(this.bank.getDataBaseBank().toString());
                }
            }
        } catch (GroupException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void applyIncrementaOp(List<MessageInterface> operations) {
        for (MessageInterface msg : operations) {
            log("  =>"+msg.toString());
            if((msg instanceof Mov)){
                Mov mov = (Mov) msg;
                bank.mov(mov.getAccountId(), mov.getAmount(), mov.getMessageId());
            }
            if ((msg instanceof NewAccount) ) {
                NewAccount newAccount = (NewAccount) msg;
                newAccount.setAccountId(bank.newAccount(msg.getMessageId()));
            }
            else if ((msg instanceof Transf) ) {
                Transf transf = (Transf) msg;
                bank.transf(transf.getSource(), transf.getDest(), transf.getAmount(), transf.getMessageId());
            }
        }
    }




    public static void log(String log){
        System.out.println(log);
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        String[] tokens = new String[0];
        String read;
        int id = 0;


        /**
         * O servidor arranca aqui, existem 3 modos (start, join, recover)
         * start id -> inicia o Server com o número 'id' a partir do 0
         * join id -> inicia o Server com o número 'id' e pede o estado completo a outros Servers
         *  recover id -> recupera o Server com o número 'id' e pede eventuais atualizações a outros Servers
         */

        String help = "Hello! How star the Server?\n"+
                "-> start    id    [start first server]\n"+
                "-> join     id    [join to others and receive all state]\n"+
                "-> recover  id    [recover this server and receive incremental state]\n";

        while(flag){
            System.out.println(help);
            read = scanner.nextLine();
            tokens = read.split(" ");

            if(tokens.length >= 1) {
                try {
                    id = Integer.parseInt(tokens[1]);
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
                new Server(id, 1);
                break;
            case "join":
                new Server(id, 2);
                break;
            case "recover":
                new Server(id, 3);
                break;
            default:
                break;
        }


        System.out.println("Start Server...");

        while (true); //never end...
    }


}
