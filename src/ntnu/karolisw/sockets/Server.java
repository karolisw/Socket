package ntnu.karolisw.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    InetAddress ipAddress;

    int PORTNR = 8080;
    // ServerSocet used instead of Socket, because ServerSocket instance
    // better encapsulates the passive (waiting) side
    ServerSocket server;
    // clientSocket is created to handle incoming connections on the server
    Socket serverSocket;
    PrintWriter writer;
    BufferedReader reader;
    // communicationIsOpen == true when connection with a client has been established
    Boolean communicationIsOpen = false;


    /**
     * Initializing server socket in server constructor
     * This socket will create new sockets as more clients try to connect
     * @throws IOException
     */
    public Server() {
        try {
            server = new ServerSocket(PORTNR); //todo ip address ?
            ipAddress = InetAddress.getLocalHost();
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
            serverSocket = server.accept();
            //opening communication with client if serverSocket has been created
            if(serverSocket != null){
                openCommunication();
                communicationIsOpen = true;
                intro(); //todo change position if this does not logically fit here (could be in write)
                // the server now must wait for the client to reply
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openCommunication() throws IOException {
        writer = new PrintWriter(serverSocket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    }

    private void intro(){
        if(communicationIsOpen){
            writer.println("Hello! You have reached the server. Do you wish to add or subtract?\n" +
                    "For add: write 'add'\n" +
                    "For subtract: write 'sub'\n");
        }
        else{
            writer.println("It appears the connection has not been established correctly");
        }
    }

    /**
     * The server reads something from the client
     * This could be 'add' (addition), 'sub' (subtraction) or numbers to add/subtract
     */
    public void communicate(){
        try {
            boolean first = true;
            writer.println("Write 'sub' if you wish to subtract when adding.\n" +
                    "Write 'add' if you wish to add when subtracting.\n" +
                    "Write 'exit' if you wish to stop the calculator and see the final result");
            //this is the first line, which should be a 1 or 2
            double result = 0;
            String line = reader.readLine();
            while (line != null){ //<-- false when the reader closes their connection
                if(line.equalsIgnoreCase("add")){ //<-- addition
                    try{
                        while(!line.equalsIgnoreCase("sub") && !line.equalsIgnoreCase("exit")){
                            line = reader.readLine();
                            result += Double.parseDouble(line);
                            writer.println("Current result: " + result);
                            line = reader.readLine();
                            if(line.equalsIgnoreCase("sub") || line.equalsIgnoreCase("exit")){
                                break; //<--hopefully code will exit and enter subtraction brackets //todo remove?
                            }
                        }
                    }catch(Exception e){
                        writer.println("Remember that the calculator can only handle numbers");
                        e.printStackTrace();
                    }
                }
                if(line.equalsIgnoreCase("sub")){
                    try{
                        while(!line.equalsIgnoreCase("add") && !line.equalsIgnoreCase("exit")){
                            line = reader.readLine();

                            //if true, this is the first number, meaning it should not be subtracted from 0
                            if(first){
                                result = Double.parseDouble(line);
                                writer.println("Current result: " + result);
                                first = false;
                            }
                            //if we have a starting number, we subtract this number
                            result -= Double.parseDouble(line);
                            writer.println("Current result: " + result);
                            //after subtracting, we read the next line
                            line = reader.readLine();

                            //we break if this line equals "add" or "exit"
                        }
                        writer.println("The result was :" + result + " :-)");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                if(line.equalsIgnoreCase("exit")){
                    writer.println("Final result:" + result + ":-)");
                }
            }
            close(); //closing down the connection --> todo move this somewhere else if untimely

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Closes up connection
     */
    public void close(){
        try {
            reader.close();
            writer.close();
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
        Server server = new Server(); //TODO where to set ip-address?

        // We ask the server to listen for connections from client
        server.connect();

        // The socket must also communicate
        server.communicate();

        // We break out of communicate method when server closing
        server.close();
    }
}
