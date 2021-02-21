package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * a card set used to determine a player's cards in hand
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class CardSet {

    /**
     * an empty card set
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    /**
     * card set containing all cards
     */
    public static final CardSet ALL_CARDS = new CardSet(
            PackedCardSet.ALL_CARDS);

    private final long packed;

    /**
     * default private card set constructor
     * 
     * @param packed
     *            : packed version of card set
     */
    private CardSet(long packed) {
        this.packed = packed;
    }

    /**
     * constructs a card set containing the given cards (public constructor)
     * 
     * @param cards
     *            : list of cards contained in the card set to be created
     * @return new card set containing the given cards
     */
    public static CardSet of(List<Card> cards) {
        CardSet set = EMPTY;
        for (Card c : cards) {
            assert PackedCard.isValid(c.packed());
            set = set.add(c);
        }
        return set;
    }

    /**
     * constructs a card set from the given packed version
     * 
     * @param packed
     *            : packed version of card set (must be valid)
     * @return new card set with the given packed version
     * @throws IllegalArgumentException
     *             if the packed card set isn't valid
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * gets the packed version of the card set
     * 
     * @return packed version of the card set
     */
    public long packed() {
        return packed;
    }

    /**
     * checks if the card set is empty
     * 
     * @return true if the card set is empty, false otherwise
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packed);
    }

    /**
     * returns the number of cards in the card set
     * 
     * @return number of cards in the card set
     */
    public int size() {
        return PackedCardSet.size(packed);
    }

    /**
     * gets card from card set at the given index
     * 
     * @param index
     *            : index of card to get (must be between 0 and the size of the
     *            set)
     * @return card in the set at the given index
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(packed, index));
    }

    /**
     * adds given card in set
     * 
     * @param card
     *            : card to add (must have a valid packed version)
     * @return card set with given card added
     */
    public CardSet add(Card card) {
        return ofPacked(PackedCardSet.add(packed, card.packed()));
    }

    /**
     * removes given card from card set
     * 
     * @param card
     *            : card to remove from card set (must have valid packed
     *            version)
     * @return card set with card removed
     */
    public CardSet remove(Card card) {
        return ofPacked(PackedCardSet.remove(packed, card.packed()));
    }

    /**
     * checks if the given card is contained in the card set
     * 
     * @param card
     *            : card to check (must have a valid packed version)
     * @return true if card is contained in set and false otherwise
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(packed, card.packed());
    }

    /**
     * returns the complement of the card set
     * 
     * @return set of cards not contained in card set
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(packed));
    }

    /**
     * returns a card set resulting of the union of this card set and the given
     * one
     * 
     * @param that
     *            : card set to unify with (must have valid packed version)
     * @return card set of cards contained in either of the card sets
     */
    public CardSet union(CardSet that) {
        return ofPacked(PackedCardSet.union(packed, that.packed));
    }

    /**
     * returns a card set resulting of the intersection of this card set and the
     * given one
     * 
     * @param that
     *            : card set to intersect with (must have a valid packed
     *            version)
     * @return card set of cards contained in both card sets
     */
    public CardSet intersection(CardSet that) {
        return ofPacked(PackedCardSet.intersection(packed, that.packed));
    }

    /**
     * returns a card set that contains cards in this card set that aren't in
     * the given card set
     * 
     * @param that
     *            : card set to get difference of (must have a valid packed
     *            version)
     * @return difference of this card set and the given one
     */
    public CardSet difference(CardSet that) {
        return ofPacked(PackedCardSet.difference(packed, that.packed));
    }

    /**
     * returns a card set containing only cards of this card set of the given
     * color
     * 
     * @param color
     *            : color of cards to get
     * @return card set containing only cards of this card set of the given
     *         color
     */
    public CardSet subsetOfColor(Card.Color color) {
        return ofPacked(PackedCardSet.subsetOfColor(packed, color));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof CardSet) {
            return ((CardSet) thatO).packed == this.packed;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(packed);
    }

}
