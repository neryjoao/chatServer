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
    LinkedList<ServerWorker> serverWorkers = new LinkedList<>();    // TODO use a synchronized map. Faster access with a map.


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
                addServerWorker(sw);

            } catch (IOException e) {

                System.out.println(e.getMessage());

            }
        }
    }

    public synchronized void addServerWorker(ServerWorker sw) {

        serverWorkers.add(sw);

    }


    public void sendAll(String message, ServerWorker messenger) {

        synchronized (serverWorkers) {

            for (ServerWorker sw : serverWorkers) {

                if (!sw.equals(messenger)) {

                    sw.send(Thread.currentThread().getName() + ": " + message);

                }
            }
        }
    }


    //INNER CLASS FOR THE SERVER WORKER
    public class ServerWorker implements Runnable {
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        String nick;
        boolean isPrivate = false;      // TODO private chat
        ServerWorker privSw;

        //CONSTRUCTOR
        public ServerWorker(Socket socket) throws IOException {     //Throwing exception to the server where server worker is initiated.

            this.socket = socket;

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }


        @Override
        public void run() {     //TODO move this to the server

            askNickname();

            while (true) {
                try {

                    String message = in.readLine();

                    if (message.equals("/who")) {

                        sendWho();

                    } else if (isPrivate) {

                        String privMessage = getMessage(message);
                        sendPrivateMessage(privSw, privMessage);

                    } else if (message.startsWith("/privChat_")) {

                        privateMessage(message);

                    } else {

                        sendAll(message, this);

                    }

                } catch (IOException e) {

                    System.out.println(e.getMessage());

                }
            }
        }

        private void askNickname() {

            out.println("Nickname?");

            try {

                String nickname = in.readLine();
                Thread.currentThread().setName(nickname);

                nick = nickname;
                out.println("Welcome " + nickname + "! You can now start chatting");

            } catch (IOException e) {

                System.out.println(e.getMessage());

            }
        }


        private void send(String message) {

            out.println(message);

        }

        private void sendWho() {

            send("PEOPLE IN THIS CHAT");
            send("*******************");

            for (ServerWorker sw : serverWorkers) {

                send(sw.nick);

            }

            send("*******************");

        }

        private void privateMessage(String message) {

            String privMessage = getMessage(message);

            ServerWorker privSw = checkName(message);

            if (privSw == null) {

                out.println("Wrong nickname... Try again");


            } else {

                sendPrivateMessage(privSw, privMessage);
                this.privSw = privSw;
                this.isPrivate = true;
            }

        }

        private ServerWorker checkName(String message) {

            String privateNick = message.substring(message.indexOf("_") + 1, message.indexOf(" "));

            System.out.println(privateNick);
            for (ServerWorker sw : serverWorkers) {

                if (privateNick.equals(sw.nick)) {
                    return sw;
                }
            }

            return null;
        }

        private String getMessage(String message) {

            return message.substring(message.indexOf(" ") + 1, message.length());

        }

        private void sendPrivateMessage(ServerWorker sw, String privMessage) {

            sw.send(privMessage);

        }

    }


}
