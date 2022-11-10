/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.util.concurrent.*;

public class MainClient {

    // Global print tag for main thread
    public static final String mainPrintTag = "[MAIN-CLIENT-THREAD]";
    public static byte clientID = 0;
    public static byte gameEndCode = -2;

    // Global timer delay for threads
    public static final short timerDelay = 15;

    // Server location
    private static String serverIP;
    private static int serverPort = 0;

    // Server thread
    public static HandlerForServer serverThread;
    public static byte totalClients = 4;

    // Executors pool
    private static final ExecutorService pool = Executors.newFixedThreadPool(1);

    public static final boolean DEBUG = false;


    /**
     * Main method
     * @param args No arguments required
     */
    public static void mainClient(String[] args) {

        handleArguments(args);

        // Create a thread to manage connection with the server
        serverThread = new HandlerForServer(serverIP, serverPort);
        System.out.println(mainPrintTag + " Created server thread.");

        // Execute the thread
        pool.execute(serverThread);
        System.out.println(mainPrintTag + " Added server thread to executors pool.");

        // Create new frame
        new ClientFrame();

        System.out.println(mainPrintTag + " Created frame for game.");
    }

    /**
     * Check arguments received from command terminal
     */
    private static void handleArguments(String [] args) {
        // Assign server port, total clients and laps from command line
        if(args.length == 2) {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
            System.out.println(serverIP);
            System.out.println(serverPort);
        }
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
}
