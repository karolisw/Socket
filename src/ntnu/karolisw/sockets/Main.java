package ntnu.karolisw.sockets;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        // Using args to run server and client in individual threads!
        if(args.length == 0){
            // The server should be created first
            Server server = new Server(); //TODO where to set ip-address?

            // We ask the server to listen for connections from client
            server.connect();

            // The socket must also communicate
            server.communicate();

            // We break out of communicate method when server closing
            server.close();

        }
        if(args.length == 1){
            Client client = new Client();
            // The client must request to connect
            // Upon client connection, the server connect() method will send an intro to the client
            client.connect();

            // The client must now respond
            client.communicate();

            // We break out of communicate method when server closing
            client.close();
        }
    }
}
