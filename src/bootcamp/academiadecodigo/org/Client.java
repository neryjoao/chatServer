package bootcamp.academiadecodigo.org;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by codecadet on 22/06/2018.
 */
public class Client {

    public static void main(String[] args) {

        Socket clientSocket;
        PrintWriter serverOut;
        BufferedReader serverIn;
        //PrintWriter terminalOut;
        //BufferedReader terminalIn;
        String host = "127.0.0.1";
        int port = 4242;

        //CONSTRUCTOR: Creates sockets and In/Out streams
        //public Client(InetAddress ip, int port) {

        try {

            //SOCKET
            clientSocket = new Socket(InetAddress.getByName(host), port);

            //CLIENT TO/FROM SERVER STREAMS
            serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
            serverIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //TERMINAL TO/FROM CLIENT STREAMS
            Scanner sc = new Scanner(System.in);

            //terminalOut = new PrintWriter(new OutputStreamWriter(System.out));
            //terminalIn = new BufferedReader(new InputStreamReader(System.in));

            //}

            //public void start() {

            // ANONIMOUS CLASS TO AVOID CREATING A SEPARATE RUNNABLE CLASS
            Thread thread = new Thread(new Runnable() {

                //TODO PUT A WHILE LOOP IN THE THREAD?

                @Override
                public void run() {
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

            thread.start();


            while (true) {

                    String message = sc.nextLine();
                    serverOut.println(message);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }
}
