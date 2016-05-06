import Client.BankStub;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by carlosmorais on 06/05/16.
 */
public class TestMovOC extends TestCase {
    private static final int CONTAS = 6;
    private static final int MOV = 100;
    private int firstId;


    @org.junit.Test
    public void teste(){
        HashMap<Integer, Integer> contas = new HashMap<>();
        BankStub bank = new BankStub();
        Random generator = new Random();

        int id1 = bank.newAccount(null);
        firstId = id1;
        contas.put(id1, 0);
        int id2 = bank.newAccount(null);
        contas.put(id2, 0);
        int id3 = bank.newAccount(null);
        contas.put(id3, 0);
        int id4 = bank.newAccount(null);
        contas.put(id4, 0);
        int id5 = bank.newAccount(null);
        contas.put(id5, 0);
        int id6 = bank.newAccount(null);
        contas.put(id6, 0);


        int id=0;
        for(int i = 0;i<MOV;i++) {
            // TODO: 05/05/16 valores negativos...
            int val = generator.nextInt(100)-50;

            do {
                id = generator.nextInt(CONTAS)+firstId;
            }
            while(id<=0);

            System.out.println(id +" "+ val);
            if(bank.mov(id,val,null)) {
                int aux = contas.get(id) + val;
                contas.put(id, aux);
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
