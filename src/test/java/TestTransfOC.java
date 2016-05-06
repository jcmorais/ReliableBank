import Client.BankStub;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by carlosmorais on 06/05/16.
 */
public class TestTransfOC extends TestCase {
    private static final int CONTAS = 6;
    private static final int TRANSF = 100;
    private static final int startAmount = 100;
    private int firstId;


    @org.junit.Test
    public void teste(){
        HashMap<Integer, Integer> contas = new HashMap<>();
        BankStub bank = new BankStub();
        Random generator = new Random();

        int id1 = bank.newAccount(null);
        firstId = id1;
        contas.put(id1, startAmount);
        int id2 = bank.newAccount(null);
        contas.put(id2, startAmount);
        int id3 = bank.newAccount(null);
        contas.put(id3, startAmount);
        int id4 = bank.newAccount(null);
        contas.put(id4, startAmount);
        int id5 = bank.newAccount(null);
        contas.put(id5, startAmount);
        int id6 = bank.newAccount(null);
        contas.put(id6, startAmount);

        bank.mov(id1, startAmount, null);
        bank.mov(id2, startAmount, null);
        bank.mov(id3, startAmount, null);
        bank.mov(id4, startAmount, null);
        bank.mov(id5, startAmount, null);
        bank.mov(id6, startAmount, null);

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
                int aux = contas.get(idS) - val;
                contas.put(idS, aux);
                aux = contas.get(idD) + val;
                contas.put(idD, aux);
            }
        }

        assertEquals(contas.get(id1).intValue(), bank.getBalance(id1));
        assertEquals(contas.get(id2).intValue(), bank.getBalance(id2));
        assertEquals(contas.get(id3).intValue(), bank.getBalance(id3));
        assertEquals(contas.get(id4).intValue(), bank.getBalance(id4));
        assertEquals(contas.get(id5).intValue(), bank.getBalance(id5));
        assertEquals(contas.get(id6).intValue(), bank.getBalance(id6));

    }




}
