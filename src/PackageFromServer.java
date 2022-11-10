/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import java.io.Serializable;

public class PackageFromServer implements Serializable {

    // Message from server to the client
    private final byte GAME_END_CODE;

    // Common data for all karts
    private final short[] KARTS_X;
    private final short[] KARTS_Y;
    private final byte[] KARTS_DIRECTION;

    // Specific data for the client only
    private final byte CLIENT_HEALTH;
    private final byte CLIENT_CHECKPOINT_NO;
    private final byte CLIENT_CHECKPOINT_LOCATION;

    /**
     * Create package that will be send to clients
     * @param gameEndCode Byte to mark if game still continues, or ended with or without a winner
     * @param kartsX Short[], array of X location for each kart
     * @param kartsY Short[], array of Y location for each kart
     * @param kartsDirection Byte[], array of currentDirection for each kart
     * @param clientHealth Byte, client's kart health
     * @param clientCheckpointNo Byte, client's kart checkpoint number
     * @param clientCheckpointLocation Byte, client's kart checkpoint number
     */
    public PackageFromServer(byte gameEndCode, short[] kartsX, short[] kartsY, byte[] kartsDirection,
                             byte clientHealth, byte clientCheckpointNo, byte clientCheckpointLocation) {
        this.GAME_END_CODE = gameEndCode;
        this.KARTS_X = kartsX;
        this.KARTS_Y = kartsY;
        this.KARTS_DIRECTION = kartsDirection;
        this.CLIENT_HEALTH = clientHealth;
        this.CLIENT_CHECKPOINT_NO = clientCheckpointNo;
        this.CLIENT_CHECKPOINT_LOCATION = clientCheckpointLocation;
    }

    /**
     * Get game code end: still going(-2), ended unexpected(-1), winner id(0,1,2,3)
     * @return String
     */
    public byte getGameEndCode() { return GAME_END_CODE; }

    /**
     * Get array containing all kart's x position
     * @return Short[]
     */
    public short[] getKartsX() { return KARTS_X; }

    /**
     * Get array containing all karts y position
     * @return Short[]
     */
    public short[] getKartsY() { return KARTS_Y; }

    /**
     * Get array containing all karts direction
     * @return Byte[]
     */
    public byte[] getKartsDirections() { return KARTS_DIRECTION; }

    /**
     * Get client's kart health
     * @return Byte
     */
    public byte getClientHealth() { return CLIENT_HEALTH; }

    /**
     * Get client's kart checkpoint number
     * @return Byte
     */
    public byte getClientCheckpointNo() { return CLIENT_CHECKPOINT_NO; }

    /**
     * Get client's kart checkpoint location
     * @return Byte
     */
    public byte getClientCheckpointLocation() { return CLIENT_CHECKPOINT_LOCATION; }
}
