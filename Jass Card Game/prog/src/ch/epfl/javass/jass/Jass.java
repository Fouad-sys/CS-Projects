package ch.epfl.javass.jass;

/**
 * interface containing main constant values for the game of Jass
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public interface Jass {

    /**
     * number of cards the player has in his hand at the start of the game
     */
    int HAND_SIZE = 9;
    /**
     * number of tricks in a turn
     */
    int TRICKS_PER_TURN = 9;
    /**
     * minimal number of points a team should attain to win the game
     */
    int WINNING_POINTS = 1000;
    /**
     * additional points given to a team that won all the tricks in the turn
     */
    int MATCH_ADDITIONAL_POINTS = 100;
    /**
     * points given to a team that won the last trick of a turn
     */
    int LAST_TRICK_ADDITIONAL_POINTS = 5;

}
