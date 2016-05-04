import Client.BankStub;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by carlosmorais on 28/04/16.
 */
public class TestTransf {

    private static final int CONTAS = 6;
    private static final int TRANSF = 100;
    private static final int startAmount = 100;
    private long firstId;

    @org.junit.Test
    public void teste(){
        HashMap<Long, AtomicInteger> contas = new HashMap();
        BankStub bank = new BankStub();
        long idAux;

        long id1 = bank.newAccount(null);
        firstId = id1;
        contas.put(id1, new AtomicInteger(startAmount));
        long  id2 = bank.newAccount(null);
        contas.put(id2, new AtomicInteger(startAmount));
        long  id3 = bank.newAccount(null);
        contas.put(id3, new AtomicInteger(startAmount));
        long  id4 = bank.newAccount(null);
        contas.put(id4, new AtomicInteger(startAmount));
        long  id5 = bank.newAccount(null);
        contas.put(id5, new AtomicInteger(startAmount));
        long  id6 = bank.newAccount(null);
        contas.put(id6, new AtomicInteger(startAmount));

        bank.mov(id1, startAmount, null);
        bank.mov(id2, startAmount, null);
        bank.mov(id3, startAmount, null);
        bank.mov(id4, startAmount, null);
        bank.mov(id5, startAmount, null);
        bank.mov(id6, startAmount, null);

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
            long idS=0, idD=0;
            for(int i = 0;i<TRANSF;i++) {
                int val = generator.nextInt(100)+1;

                do {
                    idS = generator.nextInt(CONTAS)+firstId;
                    idD = generator.nextInt(CONTAS)+firstId;
                }
                while(idS<=0 && idD<=0 && idD!=idS);

                if(bank.transf(idS, idD, val, null)) {
                    this.contas.get(idS).addAndGet(-val);
                    this.contas.get(idD).addAndGet(val);
                }
            }
        }
    }
}
