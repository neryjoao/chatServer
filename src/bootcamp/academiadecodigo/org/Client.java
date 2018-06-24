package bootcamp.academiadecodigo.org;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Client {

    public static void main(String[] args) {

        Socket clientSocket;
        PrintWriter serverOut;
        BufferedReader serverIn;
        BufferedReader terminalIn;

        String host = "127.0.0.1";
        int port = 4242;

        try {

            //SOCKET
            clientSocket = new Socket(InetAddress.getByName(host), port);

            //CLIENT TO/FROM SERVER STREAMS
            serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
            serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //TERMINAL STREAM
            terminalIn = new BufferedReader(new InputStreamReader(System.in));


            ExecutorService fixedPool = Executors.newFixedThreadPool(1000);

            // ANONIMOUS CLASS TO AVOID CREATING A SEPARATE RUNNABLE CLASS
            fixedPool.submit(new Runnable() {

                @Override
                public void run() {

                    //RECEIVE A MESSAGE
                    while (true) {
                        try {

                            String message = serverIn.readLine();
                            System.out.println(message);

                        } catch (IOException e) {

                            System.out.println(e.getMessage());

                        }
                    }
                }
            });

            //SEND A MESSAGE
            while (true) {

                    String message = terminalIn.readLine();
                    serverOut.println(message);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}
