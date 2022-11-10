/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.Serializable;

public class Location implements Serializable {

    private short x;
    private short y;

    /**
     * Constructor of this class
     * @param x Int
     * @param y Int
     */
    public Location(int x, int y) {
        this.x = (short) x;
        this.y = (short) y;
    }

    /**
     * Set new X location
     * @param x Short
     */
    public void setX(short x) { this.x = x; }

    /**
     * Set new Y location
     * @param y Short
     */
    public void setY(short y) { this.y = y; }

    /**
     * Get X location
     */
    public short getX() { return x; }

    /**
     * Get Y location
     */
    public short getY() { return y; }
}