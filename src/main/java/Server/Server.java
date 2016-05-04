package Server;

import Entitys.Account;
import Entitys.View;
import Messages.*;
import Utils.ObjectSerializable;
import net.sf.jgcs.*;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

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

    private View view;
    private boolean readFromClient;
    private boolean recoverMode;

    MessageInterface msgReply;
    private String waitFor; //id that onMessage() wants receive and notify
    private String id;

    public Server(int serverId, int opt1, int opt2){
        this.bank = new BankImp(serverId);
        this.id = new java.rmi.dgc.VMID().toString();
        this.view = new View();
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
        if(opt1 == 1 && opt2 == 1){
            this.startServer();
        }
        else if(opt1 == 1 && opt2 == 2){
            this.loadServer();
        }
        else if(opt1 == 2 && opt2 == 1){
            this.joinServer();
        }
        else if(opt1 == 2 && opt2 == 2){
            this.recoverServer();
        }
    }

    private void loadServer() {
        //lê a BD e esta pronto a receber pedidos???
    }

    private void joinServer() {
        //cria a BD limpa
        //pede estado
        //pede BD
        //proto a receber pedidos
    }

    private void recoverServer() {
        //load da BD
        this.bank.loadDB();
        //StateInfo
        StateInfo stateInfo = new StateInfo(this.getNextId());
        stateInfo.setServerRequest();
        StateInfo msgRes = (StateInfo) this.sendAndRequest(stateInfo);
        log("receive the state:"+msgRes.getState());
        //Todos os elementos vão receber esta menagem, a partir daqui estamos num novo estado
        this.view.setState(msgRes.getState());
        //já pode ler os pedidos do cliente
        this.recoverMode = true;
        this.readFromClient = true;
        // TODO: 02/05/16 é preciso colecionar as mensagens...

        //getState a partir de determinada Op
        GetState getState = new GetState(this.getNextId(), this.bank.getLastOpId(), this.view.getState());
        GetState msgSt = (GetState) this.sendAndRequest(getState);
        log("receive operations");

        //aplica as operações
        for (MessageInterface msg : msgSt.getOperations()) {
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
        this.recoverMode=false;
        //notify();
        log("recovered!");
        for (Account account : this.bank.getAccounts()) {
            log(account.toString());
        }
    }

    private void startServer() {
        this.view = new View(); //cria uma view limpa
        this.bank.creatDB(); //cria uma BD limpa
        this.readFromClient = true; //começa a ler do cliente
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


    public synchronized Object onMessage(Message message) {
        log("Message received "+ (++count) );
        MessageInterface msg = (MessageInterface) ObjectSerializable.BytesToObject(message.getPayload());
        /*
        while(this.recoverMode) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        try {
            if(this.readFromClient){
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
                if(bank.mov(mov.getAccountId(), mov.getAmount(), mov.getMessageId()))
                    mov.setDone();
                mov.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(mov);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
                this.view.addMessage(msg);
            }
            else if ((msg instanceof NewAccount) && (!msg.isResponse())) {
                NewAccount newAccount = (NewAccount) msg;
                newAccount.setAccountId(bank.newAccount(newAccount.getMessageId()));
                newAccount.setDone();
                newAccount.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(newAccount);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
                this.view.addMessage(msg);
            }
            else if ((msg instanceof Transf) && (!msg.isResponse())) {
                Transf transf = (Transf) msg;
                if(bank.transf(transf.getSource(), transf.getDest(), transf.getAmount(), transf.getMessageId()))
                    transf.setDone();
                transf.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(transf);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
                this.view.addMessage(msg);
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
            else if ((msg instanceof StateInfo) && (!msg.isResponse())) {
                StateInfo st = (StateInfo) msg;
                if(st.isServerRequest()) {
                    this.view.updateState();
                    st.setNewState();
                    log("New starte: "+this.view.getState());
                }
                st.setState(this.view.getState());
                st.setDone();
                st.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(st);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
            }
            else if (!this.recoverMode && (msg instanceof GetState) && (!msg.isResponse())) {
                GetState getState = (GetState) msg;
                getState.setOperations(this.view.getListSinceId(getState.getLastOpId(), getState.getState()));
                getState.setDone();
                getState.setResponse();
                Message reply = dataSession.createMessage();
                byte[] bytes = ObjectSerializable.ObjectToBytes(getState);
                reply.setPayload(bytes);
                dataSession.multicast(reply, new JGroupsService() , null);
                for (Account account : this.bank.getAccounts()) {
                    log(account.toString());
                }
            }
            else{
                if( msg.isResponse() && msg.getMessageId().equals(this.waitFor)){
                    this.msgReply = msg;
                    this.waitFor=null;
                    notify();
                }
            }

            }
            else{
                if( msg.isResponse() && msg.getMessageId().equals(this.waitFor)){
                    this.msgReply = msg;
                    this.waitFor=null;
                    notify();
                }
            }


        } catch (GroupException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
                if(tokens[1].equals("creat"))  //é o que interessa!!!
                    //creat
                    server = new Server(id, 1, 1);
                else if(tokens[1].equals("load"))
                    //load
                    server = new Server(id, 1, 2);
                break;
            case "join":
                if(tokens[1].equals("creat"))
                    //creat
                    //servidor "novo" que se pretende juntar ao grupo
                    server = new Server(id, 2, 1);
                else if(tokens[1].equals("load"))  //é o que interessa!!!
                    //load
                log("srart on RecoverMode");
                    server = new Server(id, 2, 2);
                break;
            default:
                break;
        }


        System.out.println("Start Server...");

        while (true); //never end...
    }


}
