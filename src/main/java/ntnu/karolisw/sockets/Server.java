package ntnu.karolisw.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    InetAddress ipAddress;
    ServerThread serverThread;

    int PORTNR = 8080;
    // ServerSocet used instead of Socket, because ServerSocket instance
    // better encapsulates the passive (waiting) side
    ServerSocket server;
    // clientSocket reader created to handle incoming connections on the server
    Socket serverSocket;
    PrintWriter writer;
    BufferedReader reader;
    Boolean close = false;


    /**
     * Initializing server socket in server constructor
     * This socket will create new sockets as more clients try to connect
     * @throws IOException
     */
    public Server() {
        try {
            server = new ServerSocket(PORTNR); //todo ip address ?
            ipAddress = InetAddress.getLocalHost();
            System.out.println("Server created\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect() listens for incoming connect()-requests.
     * Upon connection, the serverSocket will create a new socket
     * (serverSocket) in order to handle the incoming connection.
     * serverSocket will handle communication with clientSocket
     */
    public void connect(){
        try{
            //serverSocket = server.accept();

            //opening communication with client if serverSocket has been created
            // Using while-loop to create threads on the go when clients request connecting
            System.out.println("outside while loop");

            while(!close){  //todo edit condition --> getting exception upon closing class
                System.out.println("inside while loop");
                serverSocket = server.accept();
                System.out.println("server is accepting");

                serverThread = new ServerThread(serverSocket);
                System.out.println("started a server thread");

                serverThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error upon connection when creating new thread");
        }
    }


    /**
     * Closes up connection
     */
    public void close(){
        try {
            reader.close();
            writer.close();
            close = true;
            //closing down the connection (serverSocket) as well
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * In order to run these classes simultaneously, we must run them individually
     * @param args
     */
    public static void main(String[] args) {
        // The server should be created first
        Server server = new Server();

        // We ask the server to listen for connections from client
        server.connect();

        //todo server.close?
    }





}
