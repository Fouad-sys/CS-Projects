package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class represents the object oriented version of a trick. The player play
 * their cards sequentially. The first player can play any of its cards. The
 * other players have restrictions on which card to play based on the cards
 * already played in the trick.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Trick {

    /**
     * Invalid version of the trick
     */
    public static final Trick INVALID = new Trick(PackedTrick.INVALID);

    private final int packedTrick;

    private Trick(int packed) {
        this.packedTrick = packed;
    }

    /**
     * Creates a trick based on its packed version
     * 
     * @param packed
     *            packed version of the trick
     * @return Trick of the packed version
     * @throws IllegalArgumentException
     *             : if the given packed trick isn't valid
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Produces an empty trick with initial conditions (trump, player, index 0)
     * 
     * @param trump
     *            trump color of round
     * @param firstPlayer
     *            first player to play
     * @return first empty trick (no cards played)
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return ofPacked(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Provides the packed version of the trick
     * 
     * @return packed version of trick
     */
    public int packed() {
        return packedTrick;
    }

    /**
     * Provides the next empty trick after the one given in the attribute
     * 
     * @return next empty trick
     * @throws IllegalStateException
     *             : if the trick isn't full (not all cards have been played)
     */
    public Trick nextEmpty() {
        Preconditions.checkState(!PackedTrick.isFull(packedTrick));
        return new Trick(PackedTrick.nextEmpty(packedTrick));
    }

    /**
     * @return returns true if the trick is empty (no cards have been played)
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packedTrick);
    }

    /**
     * @return returns true if the trick is full (all cards have been played)
     */
    public boolean isFull() {
        return PackedTrick.isFull(packedTrick);
    }

    /**
     * Checks whether the trick is the last
     * 
     * @return returns true if the trick is the last one
     */
    public boolean isLast() {
        return PackedTrick.isLast(packedTrick);
    }

    /**
     * Gives the number of cards already placed
     * 
     * @return the number of cards played in the trick
     */
    public int size() {
        return PackedTrick.size(packedTrick);
    }

    /**
     * Gives the trump
     * 
     * @return trump color of the trick
     */
    public Color trump() {
        return PackedTrick.trump(packedTrick);
    }

    /**
     * Provides the index of the trick
     * 
     * @return the trick's number
     */
    public int index() {
        return PackedTrick.index(packedTrick);
    }

    /**
     * Provides the player in the trick depending on the index
     * 
     * @param index
     *            the player's number you want
     * @return the wanted player
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(packedTrick, index);
    }

    /**
     * Gives the packed version of the required card
     * 
     * @param index
     *            position of the card you want
     * @return wanted Card
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, PackedTrick.size(packedTrick));
        return Card.ofPacked(PackedTrick.card(packedTrick, index));
    }

    /**
     * Adds a card to the trick
     * 
     * @param c
     *            card to be added
     * @return trick with given card added
     * @throws IllegalStateException
     *             : if the trick is full (all cards have been played so none
     *             can be added)
     */
    public Trick withAddedCard(Card c) {
        Preconditions.checkState(PackedTrick.isFull(packedTrick));
        return ofPacked(PackedTrick.withAddedCard(packedTrick, c.packed()));
    }

    /**
     * Gives the base color of the trick
     * 
     * @return color of the base card (first card to be played)
     * @throws IllegalStateException
     *             : if the trick is empty (no cards have been played)
     */
    public Color baseColor() {
        Preconditions.checkState(PackedTrick.isEmpty(packedTrick));
        return PackedTrick.baseColor(packedTrick);

    }

    /**
     * The method chooses the cards from hand which can be played according to
     * the game rules.
     * 
     * @param hand
     *            hand to choose from
     * @return all the cards in the player's hand that he's allowed to play
     */
    public CardSet playableCards(CardSet hand) {
        Preconditions.checkState(PackedTrick.isFull(packedTrick));
        return CardSet.ofPacked(
                PackedTrick.playableCards(packedTrick, hand.packed()));
    }

    /**
     * Calculates the point of a full trick
     * 
     * @param pkTrick
     *            packed version of trick
     * @return number of points in the trick
     */
    public int points() {
        return PackedTrick.points(packedTrick);
    }

    /**
     * Provides the winning player
     * 
     * @param pkTrick
     *            packed version of trick
     * @return player that leads the trick
     * @throws IllegalStateException
     *             : if the trick is empty
     */
    public PlayerId winningPlayer() {
        Preconditions.checkState(PackedTrick.isEmpty(packedTrick));
        return PackedTrick.winningPlayer(packedTrick);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof Trick)
            return ((Trick) thatO).packedTrick == this.packedTrick;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedTrick);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedTrick.toString(packedTrick);
    }

}