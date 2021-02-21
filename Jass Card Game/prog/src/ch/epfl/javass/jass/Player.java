package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * The interface represents a player. It will be implemented by the different
 * types of players, i.e. simulated, paced etc.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public interface Player {

    /**
     * Returns the card that the player wants to play. Note: needs to be
     * overwritten.
     * 
     * @param state
     *            the current state of the turn
     * @param hand
     *            the cards in hand
     * @return the card that the player wants to play
     */
    Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Informs the actual player about the label of the players. Called one time
     * at the beginning of the game.
     * 
     * @param ownId
     *            the actual player to be informed
     * @param playerNames
     *            contains the label of the players
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    }

    /**
     * Updates the cards in hand of the player. Called several times when the
     * cards in hand change.
     * 
     * @param newHand
     *            the updated cards in hand
     */
    default void updateHand(CardSet newHand) {
    }

    /**
     * Updates the trump for the actual turn. Called at the beginning of each
     * turn to inform the player.
     * 
     * @param trump
     *            the actual trump chosen for the turn
     */
    default void setTrump(Color trump) {
    }

    /**
     * Updates the trick. Called each time when the trick changes.
     * 
     * @param newTrick
     *            the state of the updated trick
     */
    default void updateTrick(Trick newTrick) {
    }

    /**
     * Updates the score. Called each time when the score changes, at the end of
     * the trick.
     * 
     * @param score
     *            the state of the updated score
     */
    default void updateScore(Score score) {
    }

    /**
     * Updates the winning team. Called once when a team reaches the
     * winning points or more.
     * 
     * @param winningTeam
     *            the id of the winning team
     */
    default void setWinningTeam(TeamId winningTeam) {
    }

}
