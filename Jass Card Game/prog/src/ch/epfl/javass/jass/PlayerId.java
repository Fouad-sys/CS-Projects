package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumerated type that contains the four different players
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public enum PlayerId {

    /**
     * first player
     */
    PLAYER_1,
    /**
     * second player
     */
    PLAYER_2,
    /**
     * third player
     */
    PLAYER_3,
    /**
     * fourth player
     */
    PLAYER_4;

    /**
     * unmodifiable list of all the game's players
     */
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    /**
     * number of players in the game
     */
    public static final int COUNT = values().length;

    /**
     * returns team of player in question
     * 
     * @return player's team
     */
    public TeamId team() {
        switch (this) {
        case PLAYER_1:
            return TeamId.TEAM_1;
        case PLAYER_2:
            return TeamId.TEAM_2;
        case PLAYER_3:
            return TeamId.TEAM_1;
        case PLAYER_4:
            return TeamId.TEAM_2;
        default:
            throw new Error();
        }
    }

}
