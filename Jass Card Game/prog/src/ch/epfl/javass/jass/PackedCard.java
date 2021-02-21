package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;

/**
 * Packed version of card
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class PackedCard {

    /**
     * default packedCard private constructor
     */
    private PackedCard() {}

    /**
     * an invalid packed card
     */
    public final static int INVALID = 0b111111;

    // starting position (index of first bit) and size in bits of components
    private static final int CARD_RANK_START = 0, CARD_RANK_SIZE = 4;
    private static final int CARD_COLOR_START = 4, CARD_COLOR_SIZE = 2;
    private static final int EMPTY_BITS_START = 6, EMPTY_BITS_SIZE = 26;
    private static final int[] value = { 0, 0, 0, 0, 10, 2, 3, 4, 11 };
    private static final int[] valueTrump = { 0, 0, 0, 14, 10, 20, 3, 4, 11 };

    /**
     * checks if the given card is valid (fulfills validity conditions)
     * 
     * @param pkCard
     *            (int) : packed representation of card to be checked
     * @return (boolean) : true if the card is valid, false otherwise
     */
    public static boolean isValid(int pkCard) {

        boolean unusedBitsAre0 = Bits32.extract(pkCard, EMPTY_BITS_START,
                EMPTY_BITS_SIZE) == 0;
        boolean rankMax8 = Bits32.extract(pkCard, CARD_RANK_START,
                CARD_RANK_SIZE) <= 8;

        return unusedBitsAre0 && rankMax8;
    }

    /**
     * gives packed version of the card with the given color and rank
     * 
     * @param c
     *            (Card.Color) : color of card
     * @param r
     *            (Card.Rank) : rank of card
     * @return (int) : packed version of corresponding card
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), CARD_RANK_SIZE, c.ordinal(),
                CARD_COLOR_SIZE);
    }

    /**
     * gives color of given packed card
     * 
     * @param pkCard
     *            (int) : packed version of card (should be valid)
     * @return (Card.Color) : card's color
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        return Card.Color.values()[(pkCard >> CARD_COLOR_START)];
    }

    /**
     * gives rank of given packed card
     * 
     * @param pkCard
     *            (int) : packed version of card (should be valid)
     * @return (Card.Color) : card's rank
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        return Card.Rank.values()[(pkCard
                & Bits32.mask(CARD_RANK_START, CARD_RANK_SIZE))];

    }

    /**
     * compares both cards' values and returns true if the first is superior
     * than the second and false otherwise
     * 
     * @param trump
     *            (Card.Color) : turn's trump
     * @param pkCardL
     *            (int) : left packed card to be compared to (must be valid)
     * @param pkCardR
     *            (int) : right packed card to be compared with first (must be
     *            valid)
     * @return (boolean) : true if the left card is superior than the right one
     *         and false otherwise (including non comparable cards)
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL);
        assert isValid(pkCardR);

        boolean isTrumpAndRightIsnt = color(pkCardL) == trump
                && color(pkCardR) != trump;
        boolean sameButHigherColor = color(pkCardL) == color(pkCardR)
                && color(pkCardL) != trump
                && rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
        boolean sameButHigherTrump = color(pkCardL) == color(pkCardR)
                && color(pkCardL) == trump
                && rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal();

        return isTrumpAndRightIsnt || sameButHigherColor || sameButHigherTrump;

    }

    /**
     * returns value of given packed card (number of points it has)
     * 
     * @param trump
     *            (Card.Color) : turn's trump
     * @param pkCard
     *            (int) : packed version of card (should be valid)
     * @return (int) : number of points of given card
     */
    public static int points(Card.Color trump, int pkCard) {
        assert isValid(pkCard);
        if (trump == color(pkCard)) {
            return valueTrump[rank(pkCard).ordinal()];
        } else {
            return value[rank(pkCard).ordinal()];
        }
    }

    /**
     * returns textual representation of given card (color and rank)
     * 
     * @param pkCard
     *            (int) : packed version of card (should be valid)
     * @return (String) : string representation of card
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
