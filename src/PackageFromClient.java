/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.Serializable;

public class PackageFromClient implements Serializable {

    private final byte SPEED;
    private final byte DIRECTION;
    private final boolean CLIENT_ACTIVE;

    /**
     * Create package that will be send to server
     * @param speed Kart's speed
     * @param direction Kart's direction
     * @param clientActive Client's state, if still active or closing
     */
    public PackageFromClient(byte speed, byte direction, boolean clientActive) {
        this.SPEED = speed;
        this.DIRECTION = direction;
        this.CLIENT_ACTIVE = clientActive;
    }

    /**
     * Get kart's speed
     * @return Byte
     */
    public byte getSpeed() { return SPEED; }

    /**
     * Get kart's direction
     * @return Byte
     */
    public byte getDirection() { return DIRECTION; }

    /**
     * Get client state
     * @return Boolean
     */
    public boolean getClientState() { return CLIENT_ACTIVE;}
}
