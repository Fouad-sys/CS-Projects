package ch.epfl.javass.net;

/**
 * Enumerated type that contains the message types indicating the corresponding
 * method to be used.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public enum JassCommand {

    /**
     * setPlayers method message type
     */
    PLRS,
    /**
     * updateHand method message type
     */
    HAND,
    /**
     * setTrump method message type
     */
    TRMP, 
    /**
     * updateScore method message type 
     */
    SCOR, 
    /**
     * updateTrick method message type
     */
    TRCK, 
    /**
     * cardToPlay method message type
     */
    CARD, 
    /**
     * setWinningTeam method message type
     */
    WINR;

}
