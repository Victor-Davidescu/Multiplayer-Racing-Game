/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MainServer {

    // Server port and socket
    private static int serverPort;
    private static ServerSocket service;

    // Client threads (handlers)
    private static HandlerForClient[] handlerForClients;

    // Game limits
    private static final byte MAX_CLIENTS = 4;
    private static final byte MIN_CLIENTS = 2;
    private static final byte MAX_LAPS = 4;
    private static final byte MIN_LAPS = 1;

    // Game data related
    private static byte totalClients;
    private static byte totalLaps;
    private static boolean allClientsConnected = false;
    private static final short DELAY_TIME = 15;

    // Print tag
    public static final String MAIN_PRINT_TAG = "[MAIN-SERVER-THREAD]";
    public static final boolean DEBUG = false;

    public static boolean gameRunning = true;

    /**
     * Main method
     * @param args Server port, total clients, total laps
     */
    public static void mainServer(String[] args) {

        // Handle the arguments received
        handleArguments(args);

        // Assign size to clients array
        handlerForClients = new HandlerForClient[totalClients];

        // Assign the number of threads for the executors pool
        ExecutorService pool = Executors.newFixedThreadPool(totalClients);

        // Initiate the server socket
        if(initiateSocket()) {

            // Variable for assigning clients ID
            byte clientID = 0;

            // Loop until all clients connected
            while(clientID < totalClients) {

                try {
                    // Start listening for connections
                    Socket clientSocket = service.accept();
                    System.out.println(MAIN_PRINT_TAG + " Client "+ clientID +" joined the server.");

                    // Create new thread for the client
                    HandlerForClient clientThread = new HandlerForClient(clientID, clientSocket);

                    // Add clients to list
                    handlerForClients[clientID] = clientThread;

                    // Execute the client thread
                    pool.execute(clientThread);

                    // Assign ID for the next client
                    clientID += 1;

                } catch (Exception error) {
                    System.err.println(MAIN_PRINT_TAG + " ERROR: " + error);
                }
            }

            // Mark that all clients connected to the server
            allClientsConnected = true;
            System.out.println(MAIN_PRINT_TAG + " All clients connected");

            // Setup the game
            ServerGame mainServerGame = new ServerGame(totalLaps, totalClients);

            // Start the game
            mainServerGame.start();

            try {
                service.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);

        } else {
            System.err.println(MAIN_PRINT_TAG + " Socket failed to initialize.");
            System.exit(-1);
        }
    }

    /**
     * Check arguments received from command terminal
     */
    private static void handleArguments(String[] arguments) {
        // Assign server port, total clients and laps from command line
        if(arguments.length == 3) {

            try {
                serverPort = Integer.parseInt(arguments[0]);
                totalClients = Byte.parseByte(arguments[1]);
                totalLaps = Byte.parseByte(arguments[2]);

                // Check if the value for total clients is outside of set boundaries
                if(totalClients < MIN_CLIENTS || totalClients > MAX_CLIENTS) {
                    // Assign default value
                    totalClients = 2;
                    System.err.println(MAIN_PRINT_TAG + " ERROR: The clients is bigger than max. Resetting to default.");
                }

                // Check if the total laps is bigger than max
                if(totalLaps < MIN_LAPS ||totalLaps > MAX_LAPS) {
                    // Assign default value
                    totalLaps = 1;
                    System.err.println( MAIN_PRINT_TAG + " ERROR: The laps is bigger than max. Resetting to default.");
                }

            } catch (Exception error) {
                System.err.println("Did not receive the expected type for arguments. " + error);
                System.err.println("The arguments are: <server_port> <total_clients> <total_laps>");
            }

        } else {
            System.err.println("Did not receive the exact same number of arguments");
            System.err.println("The arguments are: <server_port> <total_clients> <total_laps>");
        }
    }

    /**
     * Initiate the server socket
     * @return True/False if socket was initialized.
     */
    private static boolean initiateSocket() {
        // Open a socket port
        try {
            service = new ServerSocket(serverPort);
            System.out.println(MAIN_PRINT_TAG + " Socket is initialized.");
            return true;

        } catch (Exception error) {
            System.err.println(MAIN_PRINT_TAG + " ERROR: Failed to initialize.");
            return false;
        }
    }

    /**
     * Check if minimum clients are still connected
     * @return True/False
     */
    public static boolean checkMinClientsAreActive() {
        byte activeClients = 0;
        for (byte i = 0; i < totalClients; i++) {
            // Check if client thread is still running
            if(handlerForClients[i].getThreadActive()) {
                activeClients++;
            }
        }
        // Return true if clients are at least at minimum number
        return activeClients >= MIN_CLIENTS;
    }

    /**
     * Custom function for printing messages for debugging.
     * @param printTag A print tag, e.g. [MAIN-THREAD]
     * @param message Message that will be displayed
     */
    public static void debugPrintLn(String printTag, String message) {
        if(DEBUG) {
            System.out.println(printTag + " " + message);
        }
    }

    /**
     * Get the client handlers array
     * @return ClientHandler[]
     */
    public static HandlerForClient[] getClientHandlers() { return handlerForClients; }

    /**
     * Get the total clients for the game
     * @return Byte
     */
    public static byte getTotalClients() { return totalClients; }

    /**
     * Get the main delay time
     * @return Short
     */
    public static short getDelayTime() { return DELAY_TIME; }

    /**
     * Get all clients connected confirmation
     * @return Boolean
     */
    public static boolean getAllClientsConnected() { return allClientsConnected; }
}
