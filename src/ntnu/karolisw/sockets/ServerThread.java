package ntnu.karolisw.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

class ServerThread extends Thread{


    BufferedReader reader;
    PrintWriter writer;
    Socket socket;
    Boolean communicationIsOpen = false;


    public ServerThread(Socket socket){
        this.socket = socket;
    }

    public void run() {
        try {
            openCommunication();
            intro();
            double result = 0;
            String line = reader.readLine();
            while (!line.equalsIgnoreCase("exit") && line != null) { //<-- false when the reader closes their connection
                if (line.equalsIgnoreCase("add")) { //<-- addition
                    try {
                        writer.println("Write a number ");
                        while (!line.equalsIgnoreCase("sub") && !line.equalsIgnoreCase("exit")) {
                            line = reader.readLine();

                            // We break if the user writes 'add' or 'exit'
                            if (legalExpression(line)) {
                                break;
                            }
                            if (isNumber(line)) {
                                // Only numbers will get in here
                                result += Double.parseDouble(line);
                                writer.println("Current result: " + result);
                            } else if (!legalExpression(line)) {
                                writer.println("You entered an illegal character. Please write a number,'sub' or 'exit ");
                            }
                            // we will only enter this branch if the user entered an illegal expression
                            // todo check for sub?
                        }
                    } catch (Exception e) {
                        writer.println("Remember that the calculator can only handle numbers");
                        e.printStackTrace();
                    }
                } else if (line.equalsIgnoreCase("sub")) {
                    try {
                        writer.println("Write a number ");
                        while (!line.equalsIgnoreCase("add") && !line.equalsIgnoreCase("exit")) {
                            line = reader.readLine();

                            // We break if the user writes 'add' or 'exit'
                            if (legalExpression(line)) {
                                break;
                            }
                            // Only numbers will get in here
                            if (isNumber(line)) {
                                // All subtractions except the first will land here
                                result -= Double.parseDouble(line);
                                writer.println("Current result: " + result);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (line.equalsIgnoreCase("exit")) {
                    writer.println("Final result:" + result + ":-)");
                }
            }
            close(); //closing down the connection --> todo move this somewhere else if untimely

        } catch (IOException e) {
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
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method reader used to check that input reader subject to rules of what the server can handle
     * OK formatting: 'add', 'sub', 'exit'
     * Not OK formatting: everything else
     *
     * @param line reader the user (client) input to check for faults
     * @return true if line fits OK formatting constrains
     */
    public boolean legalExpression(String line) {
        return line.equalsIgnoreCase("add") ||
                line.equalsIgnoreCase("sub") ||
                line.equalsIgnoreCase("exit");
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

    private void openCommunication() throws IOException {
        writer = new PrintWriter(socket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        communicationIsOpen = true;
    }

    private void intro(){
        if(communicationIsOpen){
            writer.println("Hello! You have reached the server. Do you wish to add or subtract?\n" +
                    "For add: write 'add'\n" +
                    "For subtract: write 'sub'\n" +
                    "For exiting: write 'exit'");
        }
        else{
            writer.println("It appears the connection has not been established correctly");
        }
    }
}
