package org.campus02.ecom;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BasketServerST {

    public static void main(String[] args) {

        try(ServerSocket server = new ServerSocket(1111)){
            System.out.println("Server started at port 1111...");

            while(true){
                System.out.println("Waiting for client...");
                Socket client = server.accept();
                System.out.println("Connected...");

                EcommerceLogic ec = new EcommerceLogic(client);

                //ec.run(); Single Threaded

                Thread thread = new Thread(ec);
                thread.start(); // Multi Threaded
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
