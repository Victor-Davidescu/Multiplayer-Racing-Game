/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientPanel extends JPanel implements ActionListener, KeyListener {

    // Variables related to timer
    private final Timer TIMER;

    // Variables related to race track
    private final ImageIcon RACETRACK_IMAGE;
    private final short RACETRACK_WIDTH;
    private final short RACETRACK_HEIGHT;

    // Variables related to race cars
    private final Kart[] KARTS;
    private final Checkpoint CLIENT_CHECKPOINT;

    private PackageFromServer packageFromServer;

    // Variables related to checkpoints
    private byte totalCheckpoints;

    /**
     * Initialize the main panel
     */
    public ClientPanel() {
        super();

        // Make sure the panel is focused to make the KeyListeners work!
        setFocusable(true);
        addKeyListener(this);

        // Initialize the karts array
        KARTS = new Kart[MainClient.totalClients];

        // Paths to all images
        String PATH_IMAGES_DIR = "src//images";
        String PATH_KART_DIR = PATH_IMAGES_DIR + "//kart_";
        String PATH_CHECKPOINT_IMG = PATH_IMAGES_DIR + "//checkpoint_";
        String PATH_FINISH_IMG = PATH_IMAGES_DIR + "//finish_";
        String PATH_RACETRACK_IMG = PATH_IMAGES_DIR + "//race_track.png";

        // Initialize each kart
        // Variables related to images
        for (byte i = 0; i < MainClient.totalClients; i++) {
            KARTS[i] = new Kart((short) 0, (short)0);
            KARTS[i].loadImages(PATH_KART_DIR + i);
        }

        // Initialize the client's checkpoints
        CLIENT_CHECKPOINT = new Checkpoint();
        CLIENT_CHECKPOINT.loadImages(PATH_CHECKPOINT_IMG + MainClient.clientID + ".png",
                PATH_FINISH_IMG + MainClient.clientID + ".png");

        // Create the racetrack
        RACETRACK_IMAGE = new ImageIcon(PATH_RACETRACK_IMG);
        RACETRACK_WIDTH = (short) RACETRACK_IMAGE.getIconWidth();
        RACETRACK_HEIGHT = (short) RACETRACK_IMAGE.getIconHeight();

        // Start timer
        TIMER = new Timer(MainClient.timerDelay,this);
        TIMER.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // Wait to receive a new package from the server
        waitForServerPackage();

        // Update karts location and direction based on the new package received
        updateKartsLocationAndDirection();

        // Update client kart
        updateClientKart();

        // Repaint the panel
        repaint();

        // Mandatory sleep to keep thread clear
        try {
            Thread.sleep(25);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        // Check if there is still no winner
        if(MainClient.gameEndCode == -2) {

            // Check if client's kart is still able to race
            if(KARTS[MainClient.clientID].getHealth() > 0) {

                // Send a package back to server with the new updates for client's kart
                sendPackageToServer();
            }

        } else {
            displayEndRacePopupMessage();
            TIMER.stop();
        }
    }

    /**
     * Wait to receive package from server, save it to a private variable.
     */
    private void waitForServerPackage() {
        // Wait to receive package from server
        while(MainClient.serverThread.getPackageFromServer() == null) {
            try {
                Thread.sleep(25);
            } catch (Exception error) {
                System.err.println(MainClient.mainPrintTag + " Error at trying to sleep. " + error);
            }
        }

        // Assign the package to a variable within this class
        this.packageFromServer = MainClient.serverThread.getPackageFromServer();
    }

    /**
     * Update all karts current location and their direction to be displayed properly on screen
     */
    private void updateKartsLocationAndDirection() {
        // Update all karts xy location and their direction
        for(byte i = 0; i < KARTS.length; i++) {
            KARTS[i].setX(packageFromServer.getKartsX()[i]);
            KARTS[i].setY(packageFromServer.getKartsY()[i]);

            // Avoid updating client's own direction
            if(i != MainClient.clientID) {
                KARTS[i].setDirection(packageFromServer.getKartsDirections()[i]);
            }
        }
    }

    /**
     * Update client's kart
     */
    private void updateClientKart() {
        // Get clients kart passed checkpoints
        KARTS[MainClient.clientID].setPassedCheckpoints(packageFromServer.getClientCheckpointNo());
        KARTS[MainClient.clientID].setHealth(packageFromServer.getClientHealth());
        CLIENT_CHECKPOINT.setCurrentLocation(packageFromServer.getClientCheckpointLocation());

        totalCheckpoints = CLIENT_CHECKPOINT.getNumberOfCheckpoints();
    }

    /**
     * Send package back to server with updates to client's kart
     */
    private void sendPackageToServer() {
        // Send new package to server
        MainClient.serverThread.setPackageFromClient(new PackageFromClient(
                KARTS[MainClient.clientID].getSpeed(),
                KARTS[MainClient.clientID].getDirection(), true));
    }

    private void displayEndRacePopupMessage() {
        String message;

        if(MainClient.gameEndCode == -1) {
            message = "Race ended because the other clients left the game. Restart client and server to play again.";

        } else if (MainClient.gameEndCode == MainClient.clientID) {
            message = "You won the race! Restart client and server to play again.";

        } else {

            if(MainClient.gameEndCode == 0) {
                message = "Red kart won race! Restart client and server to play again.";
            } else if(MainClient.gameEndCode == 1) {
                message = "Blue kart won race! Restart client and server to play again.";
            } else if(MainClient.gameEndCode == 2) {
                message = "Green kart won race! Restart client and server to play again.";
            } else if(MainClient.gameEndCode == 3) {
                message = "Purple kart won race! Restart client and server to play again.";
            } else {
                message = "You lost the race! Restart client and server to play again.";
            }
        }
        JOptionPane.showMessageDialog(this, message, "Race ended.", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyHandler(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Do nothing
    }

    // Paint the components in the JPanel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintRaceTrack(g);
        paintInnerEdge(g);
        paintExteriorEdge(g);
        paintInfoPanel(g);
        paintCheckpoints(g);
        paintKarts(g);
    }

    /**
     * Paint the info panel
     * @param g Graphic
     */
    private void paintInfoPanel(Graphics g) {
        short x = 800;
        short y = 370;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);

        // Paint the title
        g2d.setFont(new Font("Serif",Font.PLAIN,30));
        g2d.drawString("INFO PANEL",x,y);

        // Paint client's kart details
        g2d.drawString("YOUR KART",x,y+50);
        g2d.setFont(new Font("Serif",Font.PLAIN,20));
        g2d.drawString("HEALTH: "+ KARTS[MainClient.clientID].getHealth(),x,y+100);
        g2d.drawString("CHECKPOINT: " + KARTS[MainClient.clientID].getPassedCheckpoints(),x,y+150);
    }

    /**
     * Paint the race track
     * @param g Graphic
     */
    private void paintRaceTrack(Graphics g) {
        RACETRACK_IMAGE.paintIcon(this, g, 0, 0);
    }

    /**
     * Paint the exterior edges of the racetrack
     * @param g Graphic
     */
    private void paintExteriorEdge(Graphics g) {
        Color c = Color.black;
        g.setColor(c);
        g.drawRect(0,0, RACETRACK_WIDTH, RACETRACK_HEIGHT);
    }

    /**
     * Paint the interior edges of the racetrack
     * @param g Graphic
     */
    private void paintInnerEdge(Graphics g) {
        Color c = Color.black;
        g.setColor(c);
        short innerEdgeX = 200;
        short innerEdgeY = 200;
        short innerEdgeWidth = 1180;
        short innerEdgeHeight = 480;
        g.drawRect(innerEdgeX, innerEdgeY, innerEdgeWidth, innerEdgeHeight);
    }

    /**
     * Paint the red and blu kart
     * @param g Graphic
     */
    private void paintKarts(Graphics g) {
        for (Kart kart : KARTS) {
            kart.IMAGES[kart.getDirection()].paintIcon(this, g, kart.getX(), kart.getY());
        }
    }

    /**
     * Paint checkpoints
     * @param g Graphic
     */
    private void paintCheckpoints(Graphics g) {
        CLIENT_CHECKPOINT.getImage().paintIcon(this,g, CLIENT_CHECKPOINT.getX(), CLIENT_CHECKPOINT.getY());
    }

    /**
     * Moves karts based on the key pressed
     * @param e KeyEvent
     */
    public void keyHandler(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> KARTS[MainClient.clientID].increaseSpeed();      // Up Arrow Key
            case KeyEvent.VK_DOWN -> KARTS[MainClient.clientID].decreaseSpeed();    // Down Arrow Key
            case KeyEvent.VK_LEFT -> KARTS[MainClient.clientID].turnLeft();         // Left Arrow Key
            case KeyEvent.VK_RIGHT -> KARTS[MainClient.clientID].turnRight();       // Right Arrow Key
        }
    }
}
