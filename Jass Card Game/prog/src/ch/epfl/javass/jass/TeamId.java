package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * enumerated type that contains the two different teams
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public enum TeamId {

    /**
     * first team
     */
    TEAM_1,
    /**
     * second team
     */
    TEAM_2;

    /**
     * unmodifiable list containing both teams
     */
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    /**
     * number of teams in the game
     */
    public static final int COUNT = values().length;

    /**
     * returns the opposite team of the one in question
     * 
     * @return opposite team
     */
    public TeamId other() {
        return this == TEAM_1 ? TEAM_2 : TEAM_1;
    }

}
