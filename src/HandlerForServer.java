/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.*;
import java.net.Socket;

public class HandlerForServer implements Runnable {

    // Print tag for this thread
    private final String serverPrintTag = "[SERVER-THREAD]";

    // Server's address and port
    private final String serverIP;
    private final int serverPort;

    // Packages that will be sent to/from client
    private PackageFromClient packageFromClient = null;
    private PackageFromServer packageFromServer = null;

    // Server connection and input/output stream/object
    private Socket clientSocket = null;
    private BufferedReader streamInput = null;
    private DataOutputStream streamOutput = null;
    private ObjectInputStream objectInput = null;
    private ObjectOutputStream objectOutput = null;

    // Variable to keep thread looping
    private boolean threadLoop = true;

    /**
     * Constructor
     * @param serverIPAddress Server's IP address
     * @param serverPort Server's port
     */
    public HandlerForServer(String serverIPAddress, int serverPort) {

        // Obtain server's ip address and port
        this.serverIP = serverIPAddress;
        this.serverPort = serverPort;

        // Initialize client socket
        if(initiateSocket()) {

            // Initialize input/output streams and objects for the client socket
            if(initializeInputOutput()) {

                // Receive the client's ID
                byte clientID = receiveByteFromServer();
                MainClient.clientID = clientID;

                // Check if client ID was retrieved successful
                if(clientID != -1) {

                    // Receive the total number of clients
                    MainClient.totalClients = receiveByteFromServer();

                    // Receive game ready confirmation from the server
                    boolean gameReady = gameReadyConfirmation();

                    // Check if confirmation is okay
                    if(gameReady) {
                        System.out.println(serverPrintTag + " Ready for game on.");

                    } else {
                        closeInputOutput();
                        closeSocket();
                        System.exit(-1);
                    }
                } else {
                    closeInputOutput();
                    closeSocket();
                    System.exit(-1);
                }
            } else {
                closeSocket();
                System.exit(-1);
            }
        } else {
            System.exit(-1);
        }
    }

    /**
     * Initialize socket
     * @return True/False if socket was initialized successfully
     */
    private boolean initiateSocket() {
        try {
            clientSocket = new Socket(serverIP, serverPort);
            System.out.println(serverPrintTag + "INFO: Socket initialized.");
            return true;

        } catch (Exception error) {
            System.err.println(serverPrintTag + " ERROR: " + error);
            return false;
        }
    }

    /**
     * Initialize input/outputs streams and objects for the client socket
     * @return True/False if initialization was successful
     */
    private boolean initializeInputOutput() {
        try {
            // Assign output stream/object in exactly this order
            streamOutput = new DataOutputStream(clientSocket.getOutputStream()); // 1st
            objectOutput = new ObjectOutputStream(clientSocket.getOutputStream()); // 2nd
            MainClient.debugPrintLn(serverPrintTag, "INFO: Stream & object output initialized.");

            // Assign input stream/object in exactly this order
            streamInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // 3rd
            objectInput = new ObjectInputStream(clientSocket.getInputStream()); // 4th
            MainClient.debugPrintLn(serverPrintTag,"INFO: Stream & object input initialized.");
            return true;

        } catch (Exception error) {
            System.err.println(serverPrintTag + " ERROR: " + error);
            return false;
        }
    }

    /**
     * Retrieve client's ID to assign a kart (red or blu)
     * @return Client ID
     */
    private byte receiveByteFromServer() {
        byte clientID;
        try {
            String message = streamInput.readLine();
            clientID = Byte.parseByte(message);
            MainClient.debugPrintLn(serverPrintTag," Obtained client ID " + clientID);

        } catch (Exception error) {
            System.err.println(serverPrintTag + "ERROR: " + error);
            return -1;
        }
        return clientID;
    }

    private boolean gameReadyConfirmation() {
        String message;
        try {
            message = streamInput.readLine();
            if(message.equals("ready")) {
                MainClient.debugPrintLn(serverPrintTag," INFO: Obtained game ready confirmation.");
                return true;
            }
            else {
                return false;
            }

        } catch (Exception error) {
            System.err.println(serverPrintTag + "ERROR: " + error);
            return false;
        }
    }

    @Override
    public void run() {

        System.out.println(serverPrintTag + " Entered in thread loop.");

        // Enter in a game loop
        while(threadLoop) {

            try {
                Thread.sleep(MainClient.timerDelay);

                // Obtain package from server
                packageFromServer = (PackageFromServer) objectInput.readObject();
                MainClient.debugPrintLn(serverPrintTag," Package from server received.");

                // Wait until package from client is ready
                MainClient.debugPrintLn(serverPrintTag," Waiting to send package to server.");
                while(packageFromClient == null) {
                    Thread.sleep(25);
                }

                // Check if a clients won the race
                if(packageFromServer.getGameEndCode() == -2) {

                    // Send package to server
                    objectOutput.writeObject(packageFromClient);
                    MainClient.debugPrintLn(serverPrintTag," Package sent to server.");

                    threadLoop = packageFromClient.getClientState();

                } else {
                    threadLoop = false;
                    MainClient.gameEndCode = packageFromServer.getGameEndCode();
                }

            } catch (Exception error) {
                System.err.println(serverPrintTag + "ERROR: " + error);
                threadLoop = false;
            }
        }
        closeInputOutput();
        closeSocket();
    }

    /**
     * Get server package
     * @return PackageFromServer
     */
    public PackageFromServer getPackageFromServer() {
        return packageFromServer;
    }

    /**
     * Set package from client
     * @param packageFromClient PackageFromClient
     */
    public void setPackageFromClient(PackageFromClient packageFromClient) {
        this.packageFromClient = packageFromClient;
    }

    /**
     * Close all input/output streams and objects
     */
    private void closeInputOutput() {
        try {
            streamOutput.close();
            objectOutput.close();
            streamInput.close();
            objectInput.close();
            MainClient.debugPrintLn(serverPrintTag," Input/Output streams and objects closed.");

        } catch (Exception error) {
            System.err.println(serverPrintTag + "ERROR: " + error);
        }
    }

    /**
     * Close socket
     */
    private void closeSocket() {
        try {
            clientSocket.close();
            System.out.println(serverPrintTag + " Socket closed.");

        } catch (Exception error) {
            System.err.println(serverPrintTag + "ERROR: " + error);
        }
    }
}
