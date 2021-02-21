package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

/**
 * test class that generates a random jass game
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 *
 */
public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId : PlayerId.ALL) {
            System.out.println(pId);
            Player player = new RandomPlayer(2019);
            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(player);
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(2019, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
    }
}
