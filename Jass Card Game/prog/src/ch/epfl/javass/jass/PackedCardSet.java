package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Rank;

/**
 * a packed card set used to determine a player's cards in hand
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class PackedCardSet {

    /**
     * default private PackedCardSet constructor
     */
    private PackedCardSet() {
    }

    // index of first bit and size in bits of components
    private static final int SPADE_CARDS_START = 0, HEART_CARD_START = 16,
            DIAMOND_CARDS_START = 32, CLUB_CARDS_START = 48;
    private static final int COLOR_CARDS_SIZE = Rank.COUNT;

    /**
     * represents an empty packed card set
     */
    public static final long EMPTY = 0L;
    /*
     * packed card set containing all cards
     */
    public static final long ALL_CARDS = Bits64.mask(SPADE_CARDS_START,
            COLOR_CARDS_SIZE) | Bits64.mask(HEART_CARD_START, COLOR_CARDS_SIZE)
            | Bits64.mask(DIAMOND_CARDS_START, COLOR_CARDS_SIZE)
            | Bits64.mask(CLUB_CARDS_START, COLOR_CARDS_SIZE);

    private static final long[][] TRUMP_TABLE = tableFill();

    private static final long[] tableColour = {
            Bits64.mask(SPADE_CARDS_START, COLOR_CARDS_SIZE),
            Bits64.mask(HEART_CARD_START, COLOR_CARDS_SIZE),
            Bits64.mask(DIAMOND_CARDS_START, COLOR_CARDS_SIZE),
            Bits64.mask(CLUB_CARDS_START, COLOR_CARDS_SIZE) };

    // new method that fills the trump table where each game card represents an
    // index in the table having the value of its higher trump cards in set
    private static long[][] tableFill() {
        long[][] table = new long[Card.Color.COUNT][Card.Rank.COUNT];
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                int constructedCard = PackedCard.pack(c, r);
                table[c.ordinal()][r.ordinal()] = getHigher(constructedCard);
            }
        }
        return table;
    }

    // returns higher cards than given one in set
    private static long getHigher(int pkCard) {
        long higher = EMPTY;
        Card.Color color = PackedCard.color(pkCard);
        for (Card.Rank rank : Card.Rank.ALL) {
            int cardToCompare = PackedCard.pack(color, rank);
            if (PackedCard.isBetter(color, cardToCompare, pkCard)) {
                higher = add(higher, cardToCompare);
            }
        }
        return higher;
    }

    /**
     * Checks if the given packed card set is a valid one (unused bits are 0).
     * 
     * @param pkCardSet
     *            : packed card set to check
     * @return true if packed card set respects validity conditions, false
     *         otherwise
     */
    public static boolean isValid(long pkCardSet) {
        return ((~ALL_CARDS & pkCardSet) == EMPTY);
    }

    /**
     * returns a set of cards that are strictly superior than the given card
     * 
     * @param pkCard
     *            : packed card to determine higher ones (must be valid)
     * @return packed set of cards that are strictly superior than the given one
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        int color = PackedCard.color(pkCard).ordinal();
        int rank = PackedCard.rank(pkCard).ordinal();
        return TRUMP_TABLE[color][rank];

    }

    /**
     * returns set of cards containing only the given packed one
     * 
     * @param pkCard
     *            : packed card to contain in set (must be valid)
     * @return packed card set containing only the given one
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return Bits64.mask(pkCard, 1);
    }

    /**
     * checks if there are no cards in the given packed card set
     * 
     * @param pkCardSet
     *            : packed card set to check emptiness (must be valid)
     * @return true if packed card set is empty and false otherwise
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * returns the size (number of cards) in the packed card set
     * 
     * @param pkCardSet
     *            : packed card set to get size from (must be valid)
     * @return the number of bits at 1 in the set (number of cards in it)
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * returns the packed version of the card at the given index in the set
     * 
     * @param pkCardSet
     *            : packed card set to extract card from (must be valid)
     * @param index
     *            : position in packed set to get card from (must be between 0
     *            and the number of cards in the set)
     * @return
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet);
        assert index >= 0 && index <= size(pkCardSet);
        for (int i = 0; i < index; i++) {
            pkCardSet ^= Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * adds given card in given set
     * 
     * @param pkCardSet
     *            : packed card set to add card in (must be valid)
     * @param pkCard
     *            : packedd card to add in set (must be valid)
     * @return the new packed card set with the added card
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return union(pkCardSet, singleton(pkCard));
    }

    /**
     * removes given card from given set
     * 
     * @param pkCardSet
     *            : packed card set to remove card from (must be valid)
     * @param pkCard
     *            : packed card to remove from given set (must be valid)
     * @return new packed card set with removed card
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return intersection(pkCardSet, complement(singleton(pkCard)));
    }

    /**
     * checks if the given card is contained in the given set
     * 
     * @param pkCardSet
     *            : packed card set to check card in (must be valid)
     * @param pkCard
     *            : packed card to check (must be valid)
     * @return true if the given card is contained in the given set and false
     *         otherwise
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);
        return Bits64.extract(pkCardSet, pkCard, 1) == 1;
    }

    /**
     * returns the complement of the given packed card set (cards that are not
     * contained in it)
     * 
     * @param pkCardSet
     *            : packed card set to get complement from (must be valid)
     * @return complement of the given packed card set
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet ^ ALL_CARDS;
    }

    /**
     * returns a packed card set resulting of the union of the two given packed
     * card sets
     * 
     * @param pkCardSet1
     *            : first packed card set (must be valid)
     * @param pkCardSet2
     *            : second packed card set (must be valid)
     * @return union of two given packed card sets (packed set of cards
     *         contained in either one)
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * returns a packed card set resulting of the intersection of the two given
     * packed card sets
     * 
     * @param pkCardSet1
     *            : first packed card set (must be valid)
     * @param pkCardSet2
     *            : second packed card set (must be valid)
     * @return intersection of packed card sets (packed set of cards contained
     *         in both sets)
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * returns packed card set of cards contained in the first given packed set
     * but not the second
     * 
     * @param pkCardSet1
     *            : first packed card set (must be valid)
     * @param pkCardSet2
     *            : second packed card set (must be valid)
     * @return difference of the two packed card sets
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);
        return pkCardSet1 ^ (pkCardSet1 & pkCardSet2);
    }

    /**
     * returns a set containing all the elements in the given set of the given
     * color
     * 
     * @param pkCardSet
     *            : packed card set to get subset of color from (must be valid)
     * @param color
     *            : color of cards to get
     * @return packed card set containing all the elements in the given set of
     *         the given color
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return pkCardSet & tableColour[color.ordinal()];
    }

    /**
     * returns textual representation of given packed card set
     * 
     * @param pkCardSet
     *            : packed card set to get string version of (must be valid)
     * @return textual representation of given packed card set
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkCardSet); i++) {
            j.add(PackedCard.toString(get(pkCardSet, i)));
        }
        return j.toString();
    }

}
