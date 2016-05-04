import Client.BankStub;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by carlosmorais on 27/04/16.
 */

/**
 *      Teste que onde são criadas N CONTAS e utilizados 2 clientes
 *  que fazem 100 MOVimentos cada.
 *      No final é verificado que  soma do saldo esperado para cada
 *  conta, é o que está no lado do servidor.
 */

public class TestMOV extends TestCase {
    private static final int CONTAS = 6;
    private static final int MOV = 100;
    private long firstId;

    @org.junit.Test
    public void teste(){
        HashMap<Long, AtomicInteger> contas = new HashMap();
        BankStub bank = new BankStub();
        long idAux;

        long id1 = bank.newAccount(null);
        firstId = id1;
        contas.put(id1, new AtomicInteger(0));
        long  id2 = bank.newAccount(null);
        contas.put(id2, new AtomicInteger(0));
        long  id3 = bank.newAccount(null);
        contas.put(id3, new AtomicInteger(0));
        long  id4 = bank.newAccount(null);
        contas.put(id4, new AtomicInteger(0));
        long  id5 = bank.newAccount(null);
        contas.put(id5, new AtomicInteger(0));
        long  id6 = bank.newAccount(null);
        contas.put(id6, new AtomicInteger(0));

        ClientTest c1 = new ClientTest(contas);
        ClientTest c2 = new ClientTest(contas);

        c1.start();
        c2.start();
        try {
            c1.join();
            c2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(contas.get(id1).get(), bank.getBalance(id1));
        assertEquals(contas.get(id2).get(), bank.getBalance(id2));
        assertEquals(contas.get(id3).get(), bank.getBalance(id3));
        assertEquals(contas.get(id4).get(), bank.getBalance(id4));
        assertEquals(contas.get(id5).get(), bank.getBalance(id5));
        assertEquals(contas.get(id6).get(), bank.getBalance(id6));
    }


    public class ClientTest extends Thread {
        HashMap<Long, AtomicInteger> contas;
        BankStub bank;

        public ClientTest(HashMap<Long, AtomicInteger> contas){
            this.contas=contas;
            this.bank = new BankStub();
        }

        @Override
        public void run() {
            Random generator = new Random();
            long id=0;
            for(int i = 0;i<MOV;i++) {
                int val = generator.nextInt(100)-50;

                do {
                    id = generator.nextInt(CONTAS)+firstId;
                }
                while(id<=0);

                System.out.println(id +" "+ val);
                if(bank.mov(id,val,null)) {
                    this.contas.get(id).addAndGet(val);
                }
            }
        }
    }
}
