import Client.BankStub;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by carlosmorais on 28/04/16.
 */


/**
 *  São utilizadas duas Threads que simulam a execução simultanea de dois Clients, para qualquer número de Servers
 *  São criadas 'CONTAS' contas;
 *  É feito um deposito no valor de 'startAmount' para cada conta criada;
 *  São feitas 'TRANSF' transferências aleatórias entre as contas criadas;
 *  No fim é verificado que o saldo no Server, para cada conta criada corresponde ao valor esperado
 *
 *  Nota: Problemas com 2 Servidores, solução: http://jopereira.github.io/jgcs/jgcs-spread/index.html
 */

public class TestTransfTC {

    private static final int CONTAS = 6;
    private static final int TRANSF = 50;
    private static final int startAmount = 100;
    private int firstId;

    @org.junit.Test
    public void teste(){
        HashMap<Integer, AtomicInteger> contas = new HashMap();
        BankStub bank = new BankStub();
        long idAux;

        int id1 = bank.newAccount(null);
        firstId = id1;
        contas.put(id1, new AtomicInteger(startAmount));
        int id2 = bank.newAccount(null);
        contas.put(id2, new AtomicInteger(startAmount));
        int id3 = bank.newAccount(null);
        contas.put(id3, new AtomicInteger(startAmount));
        int id4 = bank.newAccount(null);
        contas.put(id4, new AtomicInteger(startAmount));
        int id5 = bank.newAccount(null);
        contas.put(id5, new AtomicInteger(startAmount));
        int id6 = bank.newAccount(null);
        contas.put(id6, new AtomicInteger(startAmount));

        bank.mov(id1, startAmount, null);
        bank.mov(id2, startAmount, null);
        bank.mov(id3, startAmount, null);
        bank.mov(id4, startAmount, null);
        bank.mov(id5, startAmount, null);
        bank.mov(id6, startAmount, null);

        ClientTest c1 = new ClientTest(contas);
        ClientTest c2 = new ClientTest(contas);

        long startTime = System.currentTimeMillis();
        c1.start();
        c2.start();
        try {
            c1.join();
            c2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println((TRANSF/(estimatedTime/1000))+" requests per second");

        assertEquals(contas.get(id1).get(), bank.getBalance(id1));
        assertEquals(contas.get(id2).get(), bank.getBalance(id2));
        assertEquals(contas.get(id3).get(), bank.getBalance(id3));
        assertEquals(contas.get(id4).get(), bank.getBalance(id4));
        assertEquals(contas.get(id5).get(), bank.getBalance(id5));
        assertEquals(contas.get(id6).get(), bank.getBalance(id6));
    }


    public class ClientTest extends Thread {
        HashMap<Integer, AtomicInteger> contas;
        BankStub bank;

        public ClientTest(HashMap<Integer, AtomicInteger> contas){
            this.contas=contas;
            this.bank = new BankStub();
        }

        @Override
        public void run() {
            Random generator = new Random();
            int idS=0;
            int idD=0;
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
