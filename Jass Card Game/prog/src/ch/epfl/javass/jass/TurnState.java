package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class represents the state of a turn (current trick, current scores,
 * unplayed cards)
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class TurnState {

    private final long currentScore;
    private final long unplayedCards;
    private final int currentTrick;

    /**
     * private turnState constructor
     * 
     * @param currentScore
     *            current score of turn
     * @param unplayedCards
     *            cards that haven't been played
     * @param currentTrick
     *            current trick of turn
     */
    private TurnState(long currentScore, long unplayedCards, int currentTrick) {
        this.currentScore = currentScore;
        this.unplayedCards = unplayedCards;
        this.currentTrick = currentTrick;
    }

    /**
     * Creates the state of a new turn, given the trump, first player and
     * scores.
     * 
     * @param trump
     *            trump color of turn
     * @param score
     *            current score
     * @param firstPlayer
     *            first player of turn
     * @return new turn ready to start the first trick
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {

        long pkScore = score.packed();
        int trick = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(pkScore, PackedCardSet.ALL_CARDS, trick);
    }

    /**
     * creates a new turn state with the given components (public constructor)
     * 
     * @param pkScore
     *            packed current score of turn (must be valid)
     * @param pkUnplayedCards
     *            packed set of unplayed cards (must be valid)
     * @param pkTrick
     *            packed current trick (must be valid)
     * @return new turn state with given components
     * @throws IllegalArgumentException
     *             if any of the components isn't valid
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {

        boolean cond1 = PackedScore.isValid(pkScore);
        boolean cond2 = PackedCardSet.isValid(pkUnplayedCards);
        boolean cond3 = PackedTrick.isValid(pkTrick);

        Preconditions.checkArgument(cond1 && cond2 && cond3);
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * gets packed version of score
     * 
     * @return packed version of score
     */
    public long packedScore() {
        return currentScore;
    }

    /**
     * gets packed card set of unplayed cards
     * 
     * @return packed card set of unplayed cards
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * gets packed version of trick
     * 
     * @return packed version of trick
     */
    public int packedTrick() {
        return currentTrick;
    }

    /**
     * gets current score (Object version)
     * 
     * @return current score
     */
    public Score score() {
        return Score.ofPacked(currentScore);
    }

    /**
     * gets card set of unplayed cards (Object version)
     * 
     * @return card set of unplayed cards
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * gets current trick (Object version)
     * 
     * @return current trick
     */
    public Trick trick() {
        return Trick.ofPacked(currentTrick);
    }

    /**
     * checks if the last trick of the turn has been played (and the turn is
     * finished)
     * 
     * @return true if the turn is terminal, false otherwise
     */
    public boolean isTerminal() {
        return currentTrick == PackedTrick.INVALID;
    }

    /**
     * returns the identity of the player that should play the next card
     * 
     * @return the identity of the player that should play the next card
     * @throws IllegalStateException
     *             if the trick is full (everyone has played)
     */
    public PlayerId nextPlayer() {

        Preconditions.checkState(PackedTrick.isFull(currentTrick));
        int size = PackedTrick.size(currentTrick);
        return PackedTrick.player(currentTrick, size);
    }

    /**
     * returns a new turn state where the given card has been played
     * 
     * @param card
     *            card that has been played
     * @return new turn state where the given card has been played
     * @throws IllegalStateException
     *             if the trick is full (no cards can be played in it)
     */
    public TurnState withNewCardPlayed(Card card) {

        Preconditions.checkState(PackedTrick.isFull(currentTrick));
        assert PackedCardSet.contains(unplayedCards, card.packed());
        long cardSet = PackedCardSet.remove(unplayedCards, card.packed());
        int trick = PackedTrick.withAddedCard(currentTrick, card.packed());
        return ofPackedComponents(currentScore, cardSet, trick);
    }

    /**
     * returns a new turn state where the current trick has been collected
     * (scores updated, trick updated)
     * 
     * @return new turn state where the current trick has been collected
     * @throws IllegalStateException
     *             if current trick isn't full
     */
    public TurnState withTrickCollected() {

        Preconditions.checkState(!PackedTrick.isFull(currentTrick));
        TeamId winner = PackedTrick.winningPlayer(currentTrick).team();
        int points = PackedTrick.points(currentTrick);
        long score = PackedScore.withAdditionalTrick(currentScore, winner,
                points);
        int trick = PackedTrick.nextEmpty(currentTrick);
        return new TurnState(score, unplayedCards, trick);
    }

    /**
     * returns a new turn state where a new card has been played and the trick
     * has been collected
     * 
     * @param card
     *            card to be played
     * @return new turn state where a new card has been played and the trick has
     *         been collected
     * @throws IllegalStateException
     *             if the current trick is full
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {

        Preconditions.checkState(PackedTrick.isFull(currentTrick));
        TurnState nextState = withNewCardPlayed(card);

        boolean isFull = PackedTrick.isFull(nextState.currentTrick);
        if (isFull)
            return nextState.withTrickCollected();
        return nextState;
    }
}
