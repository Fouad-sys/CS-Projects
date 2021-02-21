package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class containing the main program to run and play a distant Jass game.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public class RemoteMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage arg0) throws Exception {
       
        
        Thread gameThread = new Thread(() -> {
            Player p = new GraphicalPlayerAdapter();
            
            RemotePlayerServer server = new RemotePlayerServer(p);
            server.run();
            System.out.println("La partie commencera à la connexion du client… ");
        });
        
        gameThread.setDaemon(true);
        gameThread.start();
    }

}
