package bootcamp.academiadecodigo.org;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Server {

    ServerSocket serverSocket;
    LinkedList<ServerWorker> serverWorkers = new LinkedList<>();

    //CONSTRUCTOR
    public Server(int port) {
        try {

            this.serverSocket = new ServerSocket(port);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void start() {

        ExecutorService fixedPool = Executors.newFixedThreadPool(1000);

        while (true) {

            try {
                // BLOCKING
                Socket clientSocket = serverSocket.accept();
                System.out.println("CONNECTED...");

                ServerWorker sw = new ServerWorker(clientSocket);

                fixedPool.submit(sw);
                serverWorkers.add(sw);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public void sendAll(String message) {
        for (ServerWorker sv : serverWorkers) {
            sv.send(message);
        }

    }


    //INNER CLASS
    public class ServerWorker implements Runnable {
        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;


        //CONSTRUCTOR
        public ServerWorker(Socket clientSocket) {

            this.clientSocket = clientSocket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }catch(IOException e ){
                System.out.println(e.getMessage());
            }
        }


        @Override
        public void run() {
            while(true) {
                try {

                    String message = in.readLine();

                    sendAll(message);

                } catch (IOException e) {

                    System.out.println(e.getMessage());

                }
            }
        }

        public void send(String message) {

            out.println(message);

        }

    }


}
