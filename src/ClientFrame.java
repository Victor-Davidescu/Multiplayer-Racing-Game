/////////////////////////////////////////////////////////////////////////////////////////////////
//  AUTHOR  : VICTOR-FLORIAN DAVIDESCU
//  SID: 1705734
////////////////////////////////////////////////////////////////////////////////////////////////
import javax.swing.*;

public class ClientFrame extends JFrame{

    /**
     * Constructor of this class
     */
    public ClientFrame() {

        // Makes sure the constructor of the JFrame() gets called first
        super();

        // Set title for the main frame
        setTitle("Racing game");

        //ensure the application closes when this window gets closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set frame on fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Add the main panel to the main frame
        ClientPanel mainPanel = new ClientPanel();
        add(mainPanel);

        // make window visible
        setVisible(true);

        // In case user clicked to close the window, it will warn server that it will exit
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                MainClient.serverThread.setPackageFromClient(new PackageFromClient((byte) 0,(byte) 0,false));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
