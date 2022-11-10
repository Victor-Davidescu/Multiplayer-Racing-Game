/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import javax.swing.*;

public class Checkpoint {

    // All X,Y locations for each checkpoint location
    private final Location[] locations = {
            new Location(1420,70),  // 1
            new Location(70,70),   // 2
            new Location(70,700),   // 3
            new Location(1420,700),  // 4
            new Location(1420,390)   // Finish line
    };

    // Set the total number of checkpoints
    private final byte NUMBER_OF_CHECKPOINTS = (byte) (locations.length - 1);

    // Set the initial location
    private byte currentLocation = 0;

    // Image icons for checkpoint and the checkpoint for finish line
    private ImageIcon image;
    private ImageIcon imageFinish;

    /**
     * Create a set of checkpoints for the specific kart/client
     */
    public Checkpoint() {
        // nothing
    }

    /**
     * Load images for checkpoint
     * @param path Path to the checkpoint image
     * @param pathFinish Path to the finish checkpoint image
     */
    public void loadImages(String path, String pathFinish) {
        this.image = new ImageIcon(path);
        this.imageFinish = new ImageIcon(pathFinish);
    }

    /**
     * Checks if kart is inside the checkpoint
     * @param carX Short
     * @param carY Short
     * @param carWidth Byte
     * @param carHeight Byte
     * @return True or False
     */
    public boolean isKartInCheckpoint(short carX, short carY, byte carWidth, byte carHeight) {

        // Checkpoint image width and height
        byte WIDTH = 99;
        byte HEIGHT = 99;

        // Check if car is in X axis of the checkpoint
        if(getX() <= carX && carX+carWidth <= getX()+ WIDTH) {

            // Check if car is in Y axis of the checkpoint
            return (getY() <= carY) && (carY + carHeight <= getY() + HEIGHT);
        }
        else {
            return false;
        }
    }

    /**
     * Load the next checkpoint for kart.
     */
    public void next() {
        if(currentLocation >= locations.length - 2) {
            currentLocation = 0;
        } else {
            currentLocation += 1;
        }
    }

    /**
     * Set the finish checkpoint
     */
    public void finish() {
        currentLocation = (byte) (locations.length-1);
    }

    /**
     * Set a new location for the checkpoints
     * @param location Byte
     */
    public void setCurrentLocation(byte location) {
        this.currentLocation = location;

        // Check if the checkpoint is the finish line
        if(currentLocation == locations.length-1) {
            image = imageFinish;
        }
    }

    /**
     * Get image
     */
    public ImageIcon getImage() {return image;}

    /**
     * Get current location of the checkpoint
     * @return Byte
     */
    public byte getCurrentLocation() { return currentLocation; }

    /**
     * Get the number of checkpoints
     * @return Byte
     */
    public byte getNumberOfCheckpoints() { return NUMBER_OF_CHECKPOINTS; }

    /**
     * Get the x location of checkpoint
     * @return Short
     */
    public short getX() {return locations[currentLocation].getX();}

    /**
     * Get the y location of the checkpoint
     * @return Short
     */
    public short getY() {return locations[currentLocation].getY();}
}
