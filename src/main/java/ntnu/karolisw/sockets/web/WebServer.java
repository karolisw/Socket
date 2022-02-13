package ntnu.karolisw.sockets.web;


import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class WebServer {
    private final int PORT = 80;
    private final Logger logger = Logger.getLogger(String.valueOf(WebServer.class));
    private String httpHeader;
    private Boolean connectSuccess = false;
    InetAddress ipAddress;

    // ServerSocet used instead of Socket, because ServerSocket instance
    // better encapsulates the passive (waiting) side
    //ServerSocket server;
    HttpServer server;
    // clientSocket reader created to handle incoming connections on the server
    Socket serverSocket;
    PrintWriter writer;
    BufferedReader reader;
    Boolean close = false;
    Boolean communicationIsOpen = false;


    /**
     * Initializing a web server using built in java class "HttpServer"
     * This Server will only be able to handle one client at a time
     *
     * Backlog = 0 --> meaning that we do not allow other clients to queue up
     * @throws IOException
     */
    public WebServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost",PORT),0);
            System.out.println("WebServer created on port 80\n");
            // HttpContext = is a mapping from a path (arg[0]), to the handler (arg[1])
            // The handler is invokes when a client requests to receive the path
            server.createContext("/test", new MyHttpHandler());
            // The executor is the threadPool to execute the handlers for each request by each client
            // Executor == null means that there is no threadPool, and that the main thread will execute
            server.setExecutor(null);
            server.start();
            logger.info("Server started on port 80");
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
            System.out.println("Listening for connection requests... \n");
            //serverSocket = server.accept();

            if(serverSocket != null){
                System.out.println("Server connected!\n");
                connectSuccess = true;
                openCommunication();
                System.out.println("Communication is open :-) \n");
                // the server now must wait for the client to reply
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error upon connection");
        }
    }

    /**
     * The header line will look different depending on whether
     * the connection between Server and Client was established or not
     */
    public void setHeader(){
        if(connectSuccess){
            httpHeader = "HTTP/1.0 200 OK\n";
        }
        else httpHeader = "HTTP/10 404 "; //todo write proper header
    }

    private void openCommunication() throws IOException {
        writer = new PrintWriter(serverSocket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        communicationIsOpen = true;
    }

    public void communicate(){

    }


    public static void main(String[] args) {
        // 1. When the browser connects to the server, the server sends a welcome-message on the format <h1>
        // 2. The browser will request to connect using an HTTP-request --> GET /index.html HTTP/1.1
        // 3. The browser will send a header upon connection.
                // This header consists of several lines, and ends with an empty line
        // 4. The server will also return an HTTP-header, followed by the rest of the information
        // 5. The server will close right after sending information to the browser
        WebServer webServer = new WebServer();
    }

}
