package bootcamp.academiadecodigo.org;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
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
                Socket socket = serverSocket.accept();
                System.out.println("CONNECTED...");

                ServerWorker sw = new ServerWorker(socket);

                fixedPool.submit(sw);

                //ADD CLIENT TO THE LIST OF USERS
                serverWorkers.add(sw);

            } catch (IOException e) {

                System.out.println(e.getMessage());

            }
        }
    }

    public void sendAll(String message, ServerWorker messenger) {

        for (ServerWorker sw : serverWorkers) {

            if(!sw.equals(messenger)) {

                sw.send(Thread.currentThread().getName() + ": " + message);

            }
        }
    }


    //INNER CLASS FOR THE SERVER WORKER
    public class ServerWorker implements Runnable {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        BufferedReader tBuffer;
        String nick;
        boolean isPrivate;      // TODO private chat
        String friend;          

        //CONSTRUCTOR
        public ServerWorker(Socket socket) {

            this.socket = socket;

            try {

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                tBuffer = new BufferedReader(new InputStreamReader(System.in));

            }catch(IOException e ){
                System.out.println(e.getMessage());
            }
        }


        @Override
        public void run() {

            askNickname();

            while(true) {
                try {

                    String message = in.readLine();

                    if(message.equals("who")){

                        sendWho();

                    }else {

                        sendAll(message, this);

                    }

                } catch (IOException e) {

                    System.out.println(e.getMessage());

                }
            }
        }

        public void askNickname(){

            out.println("Nickname?");

            try {

                String nickname = in.readLine();
                Thread.currentThread().setName(nickname);
                nick = nickname;
                out.println("Welcome " + nickname + "! You can now start chatting" );

            }catch(IOException e){

                System.out.println(e.getMessage());

            }
        }


        public void send(String message) {

            out.println(message);

        }

        public void sendWho(){

            send("PEOPLE IN THIS CHAT");
            send("*******************");

            for(ServerWorker sw : serverWorkers){

                send(sw.nick);

            }

            send("*******************");

        }

        public void privateMessage(){

        }

    }


}
