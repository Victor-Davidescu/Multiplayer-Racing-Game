/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import javax.swing.*;

public class Kart {

    // Total number of images that a kart can have (each one is at +22.5 degrees angle from previous one)
    private final byte TOTAL_IMAGES = 16;

    // Kart images for displaying on panel
    public final ImageIcon[] IMAGES = new ImageIcon[TOTAL_IMAGES];

    // Kart movements
    private byte direction = 0;
    private byte speed = 0;
    private final byte SPEED_MODIFIER = 1;
    private final byte[] SPEED_LIMIT = {-1,3};

    // Kart image data
    private short x;
    private short y;

    // Kart health
    private byte health = 10;
    private boolean dead = false;

    // Kart checkpoint
    private byte passedCheckpoints = 0;

    // KArt last hit location
    private Location lastHitLocation = new Location(0,0);

    /**
     * Kart images
     * @param x Coordinate on X axis for the kart to spawn.
     * @param y Coordinate on Y axis for the kart to spawn.
     */
    public Kart(short x, short y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Load images for the kart
     * @param kartImagesDir Directory path for the kart images
     */
    public void loadImages(String kartImagesDir) {
        for(int count = 0; count < TOTAL_IMAGES; count++) {
            this.IMAGES[count] = new ImageIcon(kartImagesDir + "//" + count + ".png");
        }
    }

    /**
     * Get X location
     * @return Short
     */
    public short getX() { return x; }

    /**
     * Set new X location
     * @param x Short
     */
    public void setX(short x) { this.x = x; }

    /**
     * Get Y location
     * @return Short
     */
    public short getY() { return y; }

    /**
     * Set new Y location
     * @param y Short
     */
    public void setY(short y) { this.y = y; }

    /**
     * Get kart's height
     * @return Byte
     */
    public byte getHEIGHT() {
        return 55; }

    /**
     * Get kart's width
     * @return Byte
     */
    public byte getWIDTH() {
        return 55; }

    /**
     * Get kart's direction
     * @return Byte
     */
    public byte getDirection() { return direction; }

    /**
     * Set kart's direction
     * @param direction Byte
     */
    public void setDirection(byte direction) { this.direction = direction; }

    /**
     * Get kart's speed
     * @return Byte
     */
    public byte getSpeed() { return speed; }

    /**
     * Get kart's speed
     * @param speed Byte
     */
    public void setSpeed(byte speed) {this.speed = speed; }

    /**
     * Get kart's health
     * @return Byte
     */
    public byte getHealth() { return health; }

    /**
     * Set new health for kart
     * @param health Byte
     */
    public void setHealth(byte health) { this.health = health; }

    /**
     * Get number of passed checkpoint by kart
     * @return Byte
     */
    public byte getPassedCheckpoints() { return passedCheckpoints; }

    /**
     * Set a number of passed checkpoint by kart
     * @param passedCheckpoints Byte
     */
    public void setPassedCheckpoints(byte passedCheckpoints) { this.passedCheckpoints = passedCheckpoints; }

    /**
     * Add another checkpoint
     */
    public void nextCheckpoint() {passedCheckpoints += 1;}

    /**
     * Stop the kart
     */
    public void stop() {speed = 0;}

    /**
     * Kart receives a hit and its health drops
     */
    public void hit(Location hitLocation) {

        // Check if the current hit location is the same as last one, to avoid killing the car after 1 hit
        if(hitLocation.getX() != lastHitLocation.getX() && hitLocation.getY() != lastHitLocation.getY()) {
            lastHitLocation = hitLocation;
            stop();
            health -= 1;

            if(health == 0) {
                dead = true;
            }
        }
    }

    /**
     * Reset the last hit location
     */
    private void resetLastHitLocation() {
        lastHitLocation.setX((short) 0);
        lastHitLocation.setX((short) 0);
    }

    /**
     * Increases the kart's speed.
     */
    public void increaseSpeed() {
        if(!dead && speed < SPEED_LIMIT[1]) {
            speed += SPEED_MODIFIER;
            resetLastHitLocation();
        }
    }

    /**
     * Decreases the kart's speed.
     */
    public void decreaseSpeed() {
        if(!dead && speed > SPEED_LIMIT[0]) {
            speed -= SPEED_MODIFIER;
            resetLastHitLocation();
        }
    }

    /**
     * Makes the kart to turn left (anti-clockwise).
     */
    public void turnLeft() {
        if(speed != 0) {
            if(direction == 0)
                direction = 15;
            else
                direction -= 1;
        }
    }

    /**
     * Makes the kart to turn right (clockwise)
     */
    public void turnRight() {
        if(speed != 0) {
            if(direction == 15)
                direction = 0;
            else
                direction += 1;
        }
    }
}
