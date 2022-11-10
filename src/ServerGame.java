/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
public class ServerGame {

    // Declare array of karts and their sets of checkpoints
    private final Kart[] KARTS;
    private final Checkpoint[] CHECKPOINTS;

    // Game stats
    private final byte TOTAL_CLIENTS;
    private final byte TOTAL_CHECKPOINTS;
    private byte winnerID = -1;

    /**
     * Constructor to initialize the game
     */
    public ServerGame(byte totalLaps, byte clientsNo) {

        // Obtain the total number of clients for this game
        this.TOTAL_CLIENTS = clientsNo;

        // Initiate karts, checkpoints the the total number of checkpoints for the race.
        KARTS = new Kart[clientsNo];
        CHECKPOINTS = new Checkpoint[clientsNo];

        // Startup positions for each kart
        Location[] START_LOCATIONS = {
                new Location(1420, 470),
                new Location(1480, 470),
                new Location(1420, 570),
                new Location(1480, 570)
        };

        // Iterate through each client
        for ( byte i = 0; i < TOTAL_CLIENTS; i++) {

            // Create new kart
            KARTS[i] = new Kart(START_LOCATIONS[i].getX(), START_LOCATIONS[i].getY());

            // Create new set of checkpoints for the client
            CHECKPOINTS[i] = new Checkpoint();
        }

        // Initiate the total number of checkpoints
        TOTAL_CHECKPOINTS = (byte) (CHECKPOINTS[0].getNumberOfCheckpoints() * totalLaps);
    }

    /**
     * Start game
     */
    public void start() {
        MainServer.gameRunning = true;
        gameLoop();
    }

    /**
     * Keeps the game looping until ends
     */
    private void gameLoop() {

        // Check if game is still running
        while(MainServer.gameRunning) {

            // Mandatory sleep to keep the thread clear
            try {
                Thread.sleep(MainServer.getDelayTime());
            } catch (Exception error) {
                System.err.println(MainServer.MAIN_PRINT_TAG + " ERROR: " + error);
            }

            // Check if the minimum clients are still connected
            if(MainServer.checkMinClientsAreActive()) {

                // Check if there is still no winner
                if(winnerID == -1) {

                    // Send packages to clients with end code -2, to mark that game is still in progress
                    preparePackagesToClients((byte) -2);

                    // Read packages from clients
                    readPackagesFromClients();

                    // Update data related to karts
                    updateKartsStatus();

                    // Update data related to checkpoints
                    updateCheckpoints();

                } else {

                    // Send packages to clients with end code winner ID to mark the end of the race with a winner
                    preparePackagesToClients(winnerID);

                    // Give time to send packages, then shutdown
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Stop the game
                    MainServer.gameRunning = false;
                }

            } else {

                // Send packages to clients with end code -1, to mark that game ended with no winner
                preparePackagesToClients((byte) -1);

                // Give time to send packages, then shutdown
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                MainServer.gameRunning = false;
            }
        }
    }

    /**
     * Send packages 'PackageFromServer' to the clients with the updates
     */
    private void preparePackagesToClients(byte gameEndCode) {

        // Declare temporary variables for common data used in package
        short[] tmpKartsX = new short[TOTAL_CLIENTS];
        short[] tmpKartsY = new short[TOTAL_CLIENTS];
        byte[] tmpKartsDirection = new byte[TOTAL_CLIENTS];

        // Fill the temporary variables for common data used in package
        for(byte i = 0; i < TOTAL_CLIENTS; i++) {
            tmpKartsX[i] = KARTS[i].getX();
            tmpKartsY[i] = KARTS[i].getY();
            tmpKartsDirection[i] = KARTS[i].getDirection();
        }

        // Create new packages and save for each client thread
        for (byte i = 0; i < TOTAL_CLIENTS; i++) {

            // Set a new package for the client
            MainServer.getClientHandlers()[i].setPackageFromServer( new PackageFromServer(
                    gameEndCode, tmpKartsX, tmpKartsY, tmpKartsDirection,
                    KARTS[i].getHealth(), KARTS[i].getPassedCheckpoints(), CHECKPOINTS[i].getCurrentLocation()));

            MainServer.debugPrintLn(MainServer.MAIN_PRINT_TAG, "Created package for client " + i);
        }
    }

    /**
     * Read packages received from clients with their updates
     */
    private void readPackagesFromClients() {

        // Iterate through each client
        for (byte i = 0; i < TOTAL_CLIENTS; i++) {

            MainServer.debugPrintLn(MainServer.MAIN_PRINT_TAG, "Waiting for package from client " + i);

            // Wait to receive package from client
            while(MainServer.getClientHandlers()[i].getPackageFromClient() == null) {
                try {
                    Thread.sleep(MainServer.getDelayTime());
                } catch (Exception error) {
                    System.err.println(MainServer.MAIN_PRINT_TAG + " ERROR: " + error);
                }
            }

            MainServer.debugPrintLn(MainServer.MAIN_PRINT_TAG, "Package from client " + i + " received.");

            byte tmpSpeed = MainServer.getClientHandlers()[i].getPackageFromClient().getSpeed();
            byte tmpDirection = MainServer.getClientHandlers()[i].getPackageFromClient().getDirection();

            // Check if client's speed is valid to avoid unusual values
            if(tmpSpeed >= -1 && tmpSpeed <= 3) {

                // Assign speed
                KARTS[i].setSpeed(tmpSpeed);
            }

            // Check if client's direction is valid to avoid unusual values
            if(tmpDirection >= 0 && tmpDirection <= 15) {

                // Assign new direction from client
                KARTS[i].setDirection(tmpDirection);
            }
        }
    }

    /**
     * Update all karts locations and check for any collision
     */
    private void updateKartsStatus() {

        // Get the new locations for the karts
        Location[] newKartsLocations = new Location[TOTAL_CLIENTS];
        for (byte i = 0; i < TOTAL_CLIENTS; i++) {
            newKartsLocations[i] = kartNewXYCalculator(
                    KARTS[i].getX(),
                    KARTS[i].getY(),
                    KARTS[i].getDirection(),
                    KARTS[i].getSpeed());
        }

        // Iterate through each kart
        for (byte i = 0; i < TOTAL_CLIENTS; i++) {

            // Check if kart is in move, if not don't update it
            if (KARTS[i].getSpeed() != 0 && KARTS[i].getHealth() > 0) {

                // Check if kart collides with the edges of the racetrack
                if (!(checkExteriorEdgeCollision(newKartsLocations[i], KARTS[i].getWIDTH(), KARTS[i].getHEIGHT()) ||
                        checkInteriorEdgeCollision(newKartsLocations[i], KARTS[i].getWIDTH(), KARTS[i].getHEIGHT()))) {

                    // Check if kart collides with any other kart
                    boolean hit = false;

                    // Iterate through karts
                    for(byte j = 0; j < TOTAL_CLIENTS; j++) {

                        // Avoid checking if kart collides with itself
                        if(i != j) {

                            // Check if kart collides with another kart
                            if(checkIfKartsCollide(
                                    newKartsLocations[i], KARTS[i].getWIDTH(), KARTS[i].getHEIGHT(),
                                    newKartsLocations[j], KARTS[j].getWIDTH(), KARTS[j].getHEIGHT())) {
                                hit = true;
                                break;
                            }
                        }
                    }

                    // If the kart collied stop it, apply hit damage, if not set the new XY location
                    if(hit) {
                        KARTS[i].stop();
                        KARTS[i].hit(newKartsLocations[i]);
                        KARTS[i].hit(newKartsLocations[i]);

                    } else {
                        KARTS[i].setX(newKartsLocations[i].getX());
                        KARTS[i].setY(newKartsLocations[i].getY());
                    }

                } else {
                    KARTS[i].stop();
                    KARTS[i].hit(newKartsLocations[i]);
                }
            }
        }
    }

    /**
     * Checks if kart has reached the edge of the racetrack, this is to make sure the kart stays within the area
     * @param xy Coordinates, kart's x and y position
     * @param width Byte, kart's width (kart's image width)
     * @param height Byte, kart's height (kart's image width)
     * @return True, False
     */
    private boolean checkExteriorEdgeCollision(Location xy, byte width, byte height) {
        // Race track width & height
        short racetrackWidth = 1600;
        short racetrackHeight = 900;
        return xy.getX() <= 0 || xy.getY() <= 0 || xy.getX() + width >= racetrackWidth || xy.getY() + height >= racetrackHeight;
    }

    /**
     * Check if kart is colliding with the interior edges
     * @param xy Kart's X and Y location
     * @param width Kart's width
     * @param height Kart's height
     * @return True/False if there is a collision
     */
    private boolean checkInteriorEdgeCollision(Location xy, byte width, byte height) {
        short interiorEdgeX = 200;
        short interiorEdgeY = 200;
        short interiorEdgeWidth = 1180;
        short interiorEdgeHeight = 480;

        // Check if there is a collision on X axis
        boolean xAxisCollision = xy.getX() < interiorEdgeX + interiorEdgeWidth &&
                xy.getX() + width > interiorEdgeX;

        // Check if there is a collision on Y axis
        boolean yAxisCollision = xy.getY() < interiorEdgeY + interiorEdgeHeight &&
                xy.getY() + height > interiorEdgeY;

        // Return True if there is a collision on both axis, False otherwise
        return xAxisCollision && yAxisCollision;
    }


    /**
     * Checks if the karts collide with each other.
     * It uses an algorithm to calculate the intersection between two rectangles.
     * In the algorithm a size modifier is used to avoid colliding with the invisible edges of the kart.
     * @param kart1 Coordinates, Updated kart 1 location
     * @param kart1Width Byte, kart 1 width
     * @param kart1Height Byte, kart 1 height
     * @param kart2 Coordinates, Updated kart 2 location
     * @param kart2Width Byte, kart 2 width
     * @param kart2Height Byte, kart 2 height
     * @return True/False
     */
    private boolean checkIfKartsCollide(Location kart1, byte kart1Width, byte kart1Height,
                                        Location kart2, byte kart2Width, byte kart2Height) {
        byte sizeModifier = 7;

        boolean xAxisCollision =
                kart1.getX() + sizeModifier < kart2.getX() + kart2Width - sizeModifier &&
                kart1.getX() + kart1Width - sizeModifier > kart2.getX() + sizeModifier;

        boolean yAxisCollision =
                kart1.getY() + sizeModifier < kart2.getY() + kart2Height - sizeModifier &&
                kart1.getY() + kart1Height - sizeModifier > kart2.getY() + sizeModifier;

        return xAxisCollision && yAxisCollision;
    }


    /**
     * Calculates the new X and Y location for the cart using the following parameters.
     * @param oldX Short, Kart's current X location
     * @param oldY Short, Kart's current Y location
     * @param direction Byte, Kart's current direction
     * @param speed Byte, Kart's current speed
     * @return Coordinates, new X and Y position for the kart.
     */
    private Location kartNewXYCalculator(short oldX, short oldY, byte direction, byte speed) {

        Location newXY = new Location(oldX,oldY);

        if(speed != 0) {
            switch (direction) {
                case 0 -> newXY.setY((short) (oldY - 2 * speed));
                case 1 -> {
                    newXY.setX((short) (oldX + speed));
                    newXY.setY((short) (oldY - 2 * speed));
                }
                case 2 -> {
                    newXY.setX((short) (oldX + 2 * speed));
                    newXY.setY((short) (oldY - 2 * speed));
                }
                case 3 -> {
                    newXY.setX((short) (oldX + 2 * speed));
                    newXY.setY((short) (oldY - speed));
                }
                case 4 -> newXY.setX((short) (oldX + 2 * speed));
                case 5 -> {
                    newXY.setX((short) (oldX + 2 * speed));
                    newXY.setY((short) (oldY + speed));
                }
                case 6 -> {
                    newXY.setX((short) (oldX + 2 * speed));
                    newXY.setY((short) (oldY + 2 * speed));
                }
                case 7 -> {
                    newXY.setX((short) (oldX + speed));
                    newXY.setY((short) (oldY + 2 * speed));
                }
                case 8 -> newXY.setY((short) (oldY + 2 * speed));
                case 9 -> {
                    newXY.setX((short) (oldX - speed));
                    newXY.setY((short) (oldY + 2 * speed));
                }
                case 10 -> {
                    newXY.setX((short) (oldX - 2 * speed));
                    newXY.setY((short) (oldY + 2 * speed));
                }
                case 11 -> {
                    newXY.setX((short) (oldX - 2 * speed));
                    newXY.setY((short) (oldY + speed));
                }
                case 12 -> newXY.setX((short) (oldX - (2 * speed)));
                case 13 -> {
                    newXY.setX((short) (oldX - 2 * speed));
                    newXY.setY((short) (oldY - speed));
                }
                case 14 -> {
                    newXY.setX((short) (oldX - 2 * speed));
                    newXY.setY((short) (oldY - 2 * speed));
                }
                case 15 -> {
                    newXY.setX((short) (oldX - speed));
                    newXY.setY((short) (oldY - 2 * speed));
                }
            }
        }
        return newXY;
    }


    /**
     * Updates the next checkpoints for both karts
     */
    public void updateCheckpoints() {

        for (byte i = 0; i < TOTAL_CLIENTS; i++) {

            // Check if kart is moving
            if(KARTS[i].getSpeed() != 0) {

                // Check if kart is inside checkpoint
                if(CHECKPOINTS[i].isKartInCheckpoint(KARTS[i].getX(), KARTS[i].getY(),KARTS[i].getWIDTH(), KARTS[i].getHEIGHT())) {

                    if(KARTS[i].getPassedCheckpoints() < TOTAL_CHECKPOINTS - 1) {
                        KARTS[i].nextCheckpoint();
                        CHECKPOINTS[i].next();

                    } else if (KARTS[i].getPassedCheckpoints() == TOTAL_CHECKPOINTS - 1) {
                        KARTS[i].nextCheckpoint();
                        CHECKPOINTS[i].finish();

                    } else if (KARTS[i].getPassedCheckpoints() > TOTAL_CHECKPOINTS - 1) {
                        winnerID = i;
                    }
                }
            }
        }
    }
}
