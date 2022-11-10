/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.*;
import java.net.*;

public class HandlerForClient implements Runnable {

    // Client ID
    private final byte CLIENT_ID;

    // Mark if thread is running
    private boolean keepThreadLoop;
    private boolean threadActive;

    // Terminal print tag
    private final String PRINT_TAG;

    // Packages that will be sent to/from client
    private PackageFromClient packageFromClient = null;
    private PackageFromServer packageFromServer = null;

    // Client socket
    private final Socket CLIENT_SOCKET;

    // Input data
    private BufferedReader inputStream;
    private ObjectInputStream inputObject;

    // Output data
    public DataOutputStream outputStream;
    private ObjectOutputStream outputObject;

    /**
     * Client handler to act as a thread for each
     * @param clientID Client ID
     * @param clientSocket Client socket
     */
    public HandlerForClient(byte clientID, Socket clientSocket) {

        // Mark thread active
        threadActive = true;

        // Assign client ID and socket
        this.CLIENT_ID = clientID;
        this.CLIENT_SOCKET = clientSocket;

        // Create a print tag to know where each message comes from
        PRINT_TAG = "[CLIENT " + clientID + "]";

        try {
            // Assign input firsts
            inputStream = new BufferedReader(new InputStreamReader(this.CLIENT_SOCKET.getInputStream())); //1st
            inputObject = new ObjectInputStream(this.CLIENT_SOCKET.getInputStream()); //2nd

            // Assign output second
            outputStream = new DataOutputStream(this.CLIENT_SOCKET.getOutputStream()); //3rd
            outputObject = new ObjectOutputStream(this.CLIENT_SOCKET.getOutputStream()); //4th

            System.out.println(PRINT_TAG + " Thread initiated.");

        } catch (Exception error) {
            System.err.println(PRINT_TAG + " ERROR: " + error);
            closeConnection();
        }
    }


    @Override
    public void run() {

        // Check if id was sent
        if(sendClientID()) {

            // Send the total number of clients
            if(sendClientsNumber()) {

                // Wait for the go ahead to start the game
                waitForGameToStart();

                // Notify the client
                if(sendGameReadyConfirmation()) {

                    // Mark thread loop to true
                    keepThreadLoop = true;

                    // Enter in a thread loop to receive and send packages
                    clientThreadLoop();
                }
            }
        }

        // Close connection and end this thread
        closeConnection();
    }

    /**
     * Send client ID
     */
    public boolean sendClientID() {

        // Convert byte to string
        String message = "" + CLIENT_ID;

        try {
            outputStream.writeBytes(message + "\n");
            MainServer.debugPrintLn(PRINT_TAG," Client ID sent.");
            return true;

        } catch (Exception error) {
            System.err.println(PRINT_TAG + " Failed to send client ID. " + error);
            return false;
        }
    }

    /**
     * Send clients number
     */
    public boolean sendClientsNumber() {
        // Convert byte to string
        String message = "" + MainServer.getTotalClients();
        try {
            outputStream.writeBytes(message + "\n");
            MainServer.debugPrintLn(PRINT_TAG," Client ID sent.");
            return true;

        } catch (Exception error) {
            System.err.println(PRINT_TAG + " Failed to send client ID. " + error);
            return false;
        }
    }

    /**
     * Wait for the other clients to join and once all clients connected, the game will start
     */
    private void waitForGameToStart() {
        MainServer.debugPrintLn(PRINT_TAG," Waiting for game to start...");

        // Check if all clients connected
        while (!MainServer.getAllClientsConnected()) {
            try {
                Thread.sleep(MainServer.getDelayTime());

            } catch (Exception error) {
                System.err.println(PRINT_TAG + " Failed to sleep. " + error);
            }
        }
    }

    /**
     * Send to client confirmation that game is ready to start.
     */
    public boolean sendGameReadyConfirmation() {
        try {
            outputStream.writeBytes("ready" + "\n");
            MainServer.debugPrintLn(PRINT_TAG," Sent game ready confirmation.");
            return true;

        } catch (Exception error) {
            System.err.println(PRINT_TAG + " Failed to send ready confirmation. " + error);
            return false;
        }
    }

    /**
     * Thread enters in a loop that will continue receiving and sending packages
     */
    public void clientThreadLoop() {

        // Simulate a game loop
        System.out.println(PRINT_TAG + " Entered in thread loop.");

        // Check if thread must run and game is running
        while(keepThreadLoop) {

            try {
                // Put thread to sleep to give enough time
                Thread.sleep(MainServer.getDelayTime());

                // Wait for the game to set the first package to server
                while(packageFromServer == null) { Thread.sleep(MainServer.getDelayTime()); }

                // Send package to client
                outputObject.writeObject(packageFromServer);
                MainServer.debugPrintLn(PRINT_TAG," Package sent to client.");

                if(packageFromServer.getGameEndCode() == -2) {
                    // Receive package from client
                    packageFromClient = (PackageFromClient) inputObject.readObject();
                    MainServer.debugPrintLn(PRINT_TAG," Package received from client");

                    // Keep thread looping based on client last received state
                    keepThreadLoop = packageFromClient.getClientState();

                } else {
                    keepThreadLoop = false;
                }

            } catch (Exception error) {
                // Close thread loop if something went wrong
                System.err.println(PRINT_TAG + " ERROR at thread loop: " + error);
                keepThreadLoop = false;
            }
        }
    }

    /**
     * Set thread running to true/false state
     * @param keepThreadLoop Boolean
     */
    public void setKeepThreadLoop(boolean keepThreadLoop) {this.keepThreadLoop = keepThreadLoop; }

    /**
     * Get threadRunning state
     * @return Boolean
     */
    public boolean getThreadActive() {return threadActive;}

    /**
     * Get package from client
     * @return Object PackageFromClient
     */
    public PackageFromClient getPackageFromClient() {return packageFromClient;}

    /**
     * Set a new package that will be send to client
     * @param packageFromServer Object PackageFromServer
     */
    public void setPackageFromServer(PackageFromServer packageFromServer) {this.packageFromServer = packageFromServer;}

    /**
     * Close the input/output streams and socket
     */
    private void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            outputObject.close();
            inputObject.close();
            CLIENT_SOCKET.close();

            System.out.println(PRINT_TAG + " Thread completely closed");
            threadActive = false;

        } catch (Exception error) {
            System.err.println(PRINT_TAG + " ERROR: " + error);
        }
    }
}