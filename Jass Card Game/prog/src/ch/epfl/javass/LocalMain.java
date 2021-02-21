package ch.epfl.javass;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class containing the main program to run and play a local Jass game.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public class LocalMain extends Application {

    private final int DEFAULT_ITERATIONS = 10000;
    private final String DEFAULT_HOST = "localhost";
    private final int DEFAULT_WAIT_TIME = 2000;

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage arg0) throws Exception {
        List<String> arguments = getParameters().getRaw();
        Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        Random rng = (arguments.size() <= 4) ? new Random()
                : new Random(Long.parseLong(arguments.get(4)));
        long gameSeed = rng.nextLong();

        playerNames.put(PlayerId.PLAYER_1, "Aline");
        playerNames.put(PlayerId.PLAYER_2, "Bastien");
        playerNames.put(PlayerId.PLAYER_3, "Colette");
        playerNames.put(PlayerId.PLAYER_4, "David");

        if (!(arguments.size() == 4 || arguments.size() == 5)) {
            printInstructionsError();
        }

        for (PlayerId p : PlayerId.ALL) {
            String[] components = StringSerializer
                    .split(arguments.get(p.ordinal()), ":");
            char playerType = components[0].charAt(0);
            Player player = null;

            if (components.length != 1 && !components[1].equals(""))
                playerNames.put(p, components[1]);

            checkFirstComponent(playerType);

            if (playerType == 'h') {
                checkNumberComponents(components, 2);
                player = new GraphicalPlayerAdapter();
            }

            if (playerType == 's') {
                checkNumberComponents(components, 3);
                if (components.length == 3) {
                    checkNumberIterations(components[2]);
                }
                player = new PacedPlayer(
                        new MctsPlayer(p, rng.nextLong(),
                                components.length <= 2 ? DEFAULT_ITERATIONS
                                        : Integer.parseInt(components[2])),
                        DEFAULT_WAIT_TIME);
            }

            if (playerType == 'r') {
                checkNumberComponents(components, 3);
                try {
                    player = new RemotePlayerClient(
                            (components.length <= 2) ? DEFAULT_HOST
                                    : components[2]);
                } catch (IOException e) {
                    System.err.println(
                            ErrorMessages.serverConnection + components[2]);
                    System.exit(1);
                }
            }

            players.put(p, player);

        }
        if (arguments.size() == 5) {
            checkSeed(arguments.get(4));
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(gameSeed, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        });

        gameThread.setDaemon(true);
        gameThread.start();

    }

    private void checkFirstComponent(char playerType) {
        if (!(playerType == 'h' || playerType == 's' || playerType == 'r')) {
            System.err.println(ErrorMessages.wrongFirstArg + playerType);
            System.exit(1);
        }
    }

    private void checkNumberComponents(String[] components, int maxComponents) {
        if (components.length > maxComponents) {
            System.err.println(ErrorMessages.tooManyArgs + components.length);
            System.exit(1);
        }
    }

    private void checkNumberIterations(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.println(ErrorMessages.seedUnvalidLong + arg);
            System.exit(1);
        }

        if (Integer.parseInt(arg) < 10) {
            System.err.println(ErrorMessages.unvalidIterations + arg);
            System.exit(1);
        }
    }

    private void checkSeed(String seed) {
        try {
            Long.parseLong(seed);
        } catch (NumberFormatException e) {
            System.err.println(ErrorMessages.seedUnvalidLong + seed);
            System.exit(1);
        }
    }

    private void printInstructionsError() {
        System.err.printf("Utilisation: java %s <j1>…<j4> [<graine>]",
                LocalMain.class);
        System.err.println(
                " où : "
                + "\n <jn> spécifie le joueur n, ainsi:"
                + "\n     h[:<nom>]  un joueur humain nommé <nom>"
                + "\n     s[:<nom>[:<n>]]  un joueur simulé nommé (optionnel) <nom> dont le nombre d'iterations de son algorithme MCTS est <n>"
                + "\n     r[:<nom>[:<address>]]  un joueur distant nommé <nom> (optionnel) ayant l'adresse IP <address>"
                + "\n <graine> (optionnelle) spécifie la graine à utiliser pour générer les graines des différents générateurs aléatoires du programme");

        System.exit(1);
    }

    /**
     * Private Interface containing the possible error messages.
     * 
     * @author Fouad Mahmoud (303076)
     * @author Max Germano (302702)
     *
     */
    private interface ErrorMessages {
        String wrongFirstArg = "Erreur : la première composante d'une spécification de joueur n'est pas h, s ou r : ";
        String tooManyArgs = "Erreur : la spécification d'un joueur comporte trop de composantes : ";
        String serverConnection = "Erreur : une erreur se produit lors de la connexion au serveur d'un joueur distant : ";
        String seedUnvalidLong = "Erreur : la graine aléatoire n'est pas un entier long valide : ";
        String unvalidIterations = "Erreur : le nombre d'itérations d'un joueur simulé n'est pas un entier int valide, ou est inférieur à 10 : ";
    }

}
