package Client;

import Entitys.Movement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by carlosmorais on 15/02/16.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        BankStub bank = new BankStub();

        BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in));
        String line;
        int id;
        int amount;

        String help = "------- Options -------\n"+
                "new account -> 1 \n"+
                "get balance -> 2 : id \n"+
                "movement    -> 3 : id : amount\n"+
                "transfer    -> 4 : idSource : idDest : amount\n"+
                "movements   -> 5 : id : n\n";

        System.out.println(help);
        while((line = in.readLine()) != null){
            String[] tokens = line.split(":");

            switch (tokens[0]){
                case "1":
                    System.out.println("ID: "+bank.newAccount(null));
                    break;
                case "2":
                    id = Integer.parseInt(tokens[1]);
                    amount = bank.getBalance(id);
                    if(amount>=0)
                        System.out.println("balance: "+amount);
                    else
                        System.out.println("bad :(");
                    break;
                case "3":
                    id = Integer.parseInt(tokens[1]);
                    amount = Integer.parseInt(tokens[2]);
                    if(bank.mov(id, amount, null))
                        System.out.println("good :)");
                    else
                        System.out.println("bad :(");
                    break;
                case "4":
                    int s = Integer.parseInt(tokens[1]);
                    int d = Integer.parseInt(tokens[2]);
                    amount = Integer.parseInt(tokens[3]);
                    if(bank.transf(s, d, amount, null))
                        System.out.println("good :)");
                    else
                        System.out.println("bad :(");
                    break;
                case "5":
                    id = Integer.parseInt(tokens[1]);
                    int n = Integer.parseInt(tokens[2]);
                    List<Movement> movs = bank.movList(id, n);
                    if(movs != null) {
                        if(movs.size() == 0)
                            System.out.println("No movements");
                        for(Movement m:movs)
                            System.out.println(m.toString());
                    }
                    else
                        System.out.println("bad :(");
                    break;
                default:
                    System.out.println("What?");
                    break;
            }
            System.out.println(help);
        }

    }
}
