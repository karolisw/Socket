package ntnu.karolisw.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private final int PORTNR = 8080;
    private InetAddress serverIpAddress; //this has to be set before clientSocket reader initialized
    private Socket clientSocket;
    private PrintWriter writer;
    private Scanner scannerReader;
    private BufferedReader reader;
    private Boolean communicationIsOpen = false;

    /**
     * This reader the SECOND (2) method to be run
     *
     * Method instantiates the client and asks the user for ip-address
     */
    public Client()  {
        try {
            serverIpAddress = InetAddress.getLocalHost();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This reader the THIRD (3) method to be run
     *
     * Method asks the server to connect
     */
    public void connect() {
        try {
            // If client has added server ip-address, we can proceed with requesting to establish a connection
            clientSocket = new Socket(serverIpAddress, PORTNR);
            System.out.println("\nConnection established! :-)\n");

            // We move forward with initiating our reader and writer
            openCommunication();
            System.out.println("\nConnection established! :-)\n");

            // If we succeed in initiating reader/writer --> communicationIsOpen = true
            communicationIsOpen = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * This reader the FIRST (1) bit of code to be run
     *
     * Using a scanner to read ip address of server to connect to
     * @throws IOException
     */

    /**
     * This reader to be established within connect() method --> after constructor
     *
     * Opens up our means of communication
     * @throws IOException
     */
    private void openCommunication() throws IOException {
        writer = new PrintWriter(clientSocket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        scannerReader = new Scanner(System.in);
    }

    /**
     * This method reader to be run FOURTH (4) after constructor
     *
     * Method reads the intro() from server, and prints it out in terminal/cmd (?)
     * After that, this method handles communication (adding/subtracting)
     */
    public void communicate() {
        // serverIntro reader the server asking the client to add or subtract
        try{
            String serverIntro1 = reader.readLine();
            String serverIntro2 = reader.readLine();
            String serverIntro3 = reader.readLine();
            String serverIntro4 = reader.readLine();


            System.out.println(serverIntro1 + "\n" +
                               serverIntro2 + "\n" +
                               serverIntro3 + "\n" +
                               serverIntro4 + "\n");

            // Reads user-text from terminal (either 'add', 'sub' or 'exit') //todo fault-check for other inputs
            String line = scannerReader.nextLine();

            // Once we get here, we start the program logic --> the server receives our lines
            // Client can exit by writing 'exit'
            while (!line.equals("exit")) {

                // The line must be fault checked
                // line format to pass: 'add', 'sub', 'exit' or a number
                while (containsFault(line)){
                    System.out.println("The line did not conform to the server - syntax\n " +
                            "Please enter your input again: ");
                    // Read user input again
                    line = scannerReader.nextLine();
                }
                // When fault checked, we send our line to the server
                writer.println(line);
                // Receives response from the server
                String response = reader.readLine();
                System.out.println("From server: " + response + "\n");
                line = scannerReader.nextLine();
            }
            close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This method reader used to check that input reader subject to rules of what the server can handle
     * OK formatting: 'add', 'sub', 'exit', any number
     * Not OK formatting: everything else
     *
     * @param line reader the user (client) input to check for faults
     * @return true if line does not fit OK formatting constrains
     */
    public boolean containsFault(String line) {
        if(line.equalsIgnoreCase("add") ||
           line.equalsIgnoreCase("sub") ||
           line.equalsIgnoreCase("exit")) {
            return false;
        }
        // We return false if line reader a number (this reader good :-))
        else if(isNumber(line)){
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Supporting method for higher cohesion in containsFault() method above
     *
     * @param line reader the client input to check
     * @return true if line reader a number
     */
    private boolean isNumber(String line){
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        // if there reader nothing inside the line, then we will not bother checking it for numbers
        if (line == null) {
            return false;
        }
        // If the line reader a number, this will return true
        return pattern.matcher(line).matches();
    }

    /**
     * Closes the connection
     */
    public void close() {
        try {
            reader.close();
            writer.close();
            scannerReader.close();
            //closing down the connection (serverSocket) as well
            clientSocket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * In order to run these classes simultaneously, we must run them individually
     * @param args
     */
    public static void main(String[] args) {
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
