/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.util.*;

public class Main {

    /**
     * Main method for selecting if app should run as client or server
     */
    public static void main(String[] args) {

        // Check if main received arguments
        String runModeOption = chooseRunMode(args);

        // Check if arguments are for running as server mode
        if(runModeOption.equals("server")) {
            // Remove the first element of array (option for choosing client/server mode
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            MainServer.mainServer(newArgs);
        }

        // Check if arguments are for running as client mode
        else if(runModeOption.equals("client")) {
            // Remove the first element of array (option for choosing client/server mode
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            MainClient.mainClient(newArgs);
        }

        // Else, initiate manually by asking user for arguments
        else {
            byte userOption = getUserOption();

            if(userOption == 1) {
                serverUserInput();
            } else if(userOption == 2) {
                clientUserInput();
            }
        }
    }

    /**
     * Check arguments received from command terminal
     */
    private static String chooseRunMode(String [] args) {
        if(args.length > 1) {
            if(args[0].equals("server") && args.length == 3 + 1) {
                return "server";
            }
            else if(args[0].equals("client") && args.length == 2 + 1) {
                return "client";
            }
        }
        return "";
    }

    /**
     * Get user's option
     * @return Byte
     */
    private static byte getUserOption(){
        boolean loopMenu = true;
        byte userOption = -1;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("---------------------------------");
            System.out.println("Choose one of the following options:");
            System.out.println("1 - Open application as server");
            System.out.println("2 - Open application as client");
            System.out.println("---------------------------------");
            System.out.print("Your option: ");

            try {
                userOption = scanner.nextByte();
                if(userOption == 1 || userOption == 2) {
                    loopMenu = false;
                }
            } catch (Exception error) {
                System.err.println("ERROR: " + error + " Please try again.");
            }

        } while(loopMenu);

        return userOption;
    }

    /**
     * Launch application as client
     */
    private static void clientUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server's IP address: ");
        String serverIPAddress = scanner.next();
        System.out.print("Enter server's port: ");
        String serverPort = scanner.next();

        String[] args = {serverIPAddress, serverPort};
        MainClient.mainClient(args);
    }

    /**
     * Launch application as server
     */
    private static void serverUserInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the server's port: ");
        String serverPort = scanner.next();
        System.out.print("Enter total number of clients, minimum 2, maximum 4: ");
        String clientsNo = scanner.next();
        System.out.print("Enter total number of laps, minimum 1, maximum 4: ");
        String lapsNo = scanner.next();

        String[] args = {String.valueOf(serverPort), clientsNo, lapsNo};
        MainServer.mainServer(args);
    }
}
