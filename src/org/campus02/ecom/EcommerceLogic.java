package org.campus02.ecom;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EcommerceLogic implements Runnable{

    private Socket socket;

    public EcommerceLogic(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ){
            System.out.println("EcommerceLogic: Socket Connection Opened.");

            BasketAnalyzer ba = null;

            String command;
            while((command = br.readLine()) != null){

                System.out.println("received from client >> " + command);

                String parts[] = command.split(" ");

                if(parts.length == 2){
                    //OpenFile, GetEveryNth
                    if(parts[0].toLowerCase(Locale.ROOT).equals("openfile")){
                        String path = parts[1];
                        try {
                            ba = new BasketAnalyzer(BasketDataLoader.load(path));
                            bw.write("<<< Basket Data loaded with " + "ba.????" + " entries. >>>");
                            bw.newLine();
                            bw.flush();
                        } catch (DataFileException e) {
                            e.printStackTrace();

                            bw.write("Fehler: " + e.getMessage());
                            bw.newLine();
                            bw.flush();
                        }
                    }
                    else if(parts[0].toLowerCase(Locale.ROOT).equals("geteverynth") && ba != null){
                            int n = Integer.parseInt(parts[1]);

                            List<BasketData> list = ba.getEveryNthBasket(n);

                            bw.write("EveryNthBasket " + list.size());
                            bw.newLine();
                            bw.flush();
                    }
                    else{
                        bw.write(parts[0] + " unknown command");
                        bw.newLine();
                        bw.flush();
                    }
                }
                else if(parts.length == 1){
                    // EXIT, GetStats
                    if(parts[0].toLowerCase(Locale.ROOT).equals("exit")){
                        bw.write("Good Bye!");
                        bw.newLine();
                        bw.flush();

                        bw.close();
                        br.close();
                        socket.close();
                        return;
                    }
                    else if(parts[0].toLowerCase(Locale.ROOT).equals("getstats") && ba != null){
                        HashMap<String, ArrayList<Double>> map = ba.groupByProductCategory();

                        for (String key : map.keySet()) {
                            ArrayList<Double> list = map.get(key);
                            double sum = 0;
                            for(double d : list){
                                sum += d;
                            }

                            sum = sum / list.size();

                            bw.write(key + " - " + sum);
                            bw.newLine();
                        }
                        bw.flush();
                    }
                    else{
                        bw.write(parts[0] + " unknown command");
                        bw.newLine();
                        bw.flush();
                    }
                }
                else{
                    bw.write(parts[0] + " unknown command");
                    bw.newLine();
                    bw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
