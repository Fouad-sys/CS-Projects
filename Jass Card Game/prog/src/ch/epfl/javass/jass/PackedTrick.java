package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class represents the packed version of a trick. The player play their
 * cards sequentially. The first player can play any of its cards. The other
 * players have restrictions on which card to play based on the cards already
 * played in the trick.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class PackedTrick {

    /**
     * Invalid format of the packed version of the card
     */
    public static final int INVALID = Bits32.mask(0, Integer.SIZE);

    private static final int CARDS_PER_TRICK = 4;
    private static final int CARD_LENGTH = 6;
    private static final int FIRST_CARD = 0;
    private static final int LAST_CARD = 18;
    private static final int INDEX_START = 24;
    private static final int INDEX_LENGTH = 4;
    private static final int PLAYER_START = 28;
    private static final int PLAYER_LENGTH = 2;
    private static final int TRUMP_START = 30;
    private static final int TRUMP_LENGTH = 2;

    /**
     * Default Bits32 Constructor denies the instantiation of the class
     */
    private PackedTrick() {
    }

    /**
     * Checks if the packed trick is valid.
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return true if the packed trick is a valid trick
     */
    public static boolean isValid(int pkTrick) {

        int trickIndex = Bits32.extract(pkTrick, INDEX_START, INDEX_LENGTH);

        if (trickIndex < Jass.TRICKS_PER_TURN && trickIndex >= 0) {
            boolean valid = false;
            int actualCard;
            for (int i = 0; i < CARDS_PER_TRICK; i++) {
                actualCard = Bits32.extract(pkTrick,
                        ((CARDS_PER_TRICK - 1 - i) * CARD_LENGTH), CARD_LENGTH);

                if (actualCard != PackedCard.INVALID)
                    valid = true;
                else {
                    if (valid)
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    // method to avoid code duplication
    private static int emptyTrickWith(int index, int firstPlayer,
            int packedTrump) {
        return Bits32.pack(PackedCard.INVALID, CARD_LENGTH, PackedCard.INVALID,
                CARD_LENGTH, PackedCard.INVALID, CARD_LENGTH,
                PackedCard.INVALID, CARD_LENGTH, index, INDEX_LENGTH,
                firstPlayer, PLAYER_LENGTH, packedTrump, TRUMP_LENGTH);
    }

    /**
     * Produces an empty packed trick with initial conditions (trump, player,
     * index 0)
     * 
     * @param trump
     *            color of trump
     * @param firstPlayer
     *            first player who starts the trick
     * @return empty packed trick with given trump, player and index 0
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {

        return emptyTrickWith(0, firstPlayer.ordinal(), trump.ordinal());
    }

    /**
     * Provides the next empty trick after the one given in the attribute
     * 
     * @param pkTrick
     *            the passed packed trick
     * @return next empty packed trick
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        assert isFull(pkTrick);

        int index = Bits32.extract(pkTrick, INDEX_START, INDEX_LENGTH);
        int packedTrump = Bits32.extract(pkTrick, TRUMP_START, TRUMP_LENGTH);
        int firstPlayer = winningPlayer(pkTrick).ordinal();

        if (index != (Jass.TRICKS_PER_TURN - 1)) {

            return emptyTrickWith((index + 1), firstPlayer, packedTrump);
        }

        return INVALID;
    }

    /**
     * Checks whether the trick is the last
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return true if the given trick is the last one
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return (Bits32.extract(pkTrick, INDEX_START,
                INDEX_LENGTH) == (Jass.TRICKS_PER_TURN - 1));
    }

    /**
     * Checks whether a new trick starts, contains no cards
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return true if no cards have been placed
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);

        if (Bits32.extract(pkTrick, FIRST_CARD,
                CARD_LENGTH) == PackedCard.INVALID)
            return true;
        return false;
    }

    /**
     * Checks whether the trick is full, contains 4 cards
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return true if 4 cards have been placed
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);

        if (Bits32.extract(pkTrick, LAST_CARD,
                CARD_LENGTH) != PackedCard.INVALID)
            return true;
        return false;
    }

    /**
     * Gives the number of cards already placed
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return number of cards placed in the trick
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);

        int[] cards = new int[CARDS_PER_TRICK];
        int cardPlaced = FIRST_CARD;
        for (int i = 0; i < cards.length; i++) {
            cards[i] = Bits32.extract(pkTrick, (i * CARD_LENGTH), CARD_LENGTH);
            if (cards[i] != PackedCard.INVALID)
                cardPlaced++;
        }

        return cardPlaced;
    }

    /**
     * Gives the trump
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return color of the trump
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);

        return Color.values()[Bits32.extract(pkTrick, TRUMP_START,
                TRUMP_LENGTH)];
    }

    /**
     * Provides the player in the trick depending on the index
     * 
     * @param pkTrick
     *            the packed trick to obtain the first player
     * @param index
     *            the required player id
     * @return the PlayerId in the trick
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);

        int playerIndex = (Bits32.extract(pkTrick, PLAYER_START, PLAYER_LENGTH)
                + index) % PlayerId.COUNT;
        return PlayerId.values()[playerIndex];
    }

    /**
     * Provides the index of the trick
     * 
     * @param pkTrick
     *            the packed trick to check
     * @return index of the trick
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);

        return Bits32.extract(pkTrick, INDEX_START, INDEX_LENGTH);
    }

    /**
     * Gives the packed version of the required card
     * 
     * @param pkTrick
     *            the packed trick to obtain the card from
     * @param index
     *            the index of the required card in the trick
     * @return packed version of the required card
     */

    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick);
        assert (index >= 0 && index < size(pkTrick));

        return Bits32.extract(pkTrick, index * CARD_LENGTH, CARD_LENGTH);
    }

    /**
     * Adds a card to the trick
     * 
     * @param pkTrick
     *            packed version of trick
     * @param pkCard
     *            card to be added
     * @return packed trick with added card
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert PackedCard.isValid(pkCard);
        assert !isFull(pkTrick);

        int position = size(pkTrick);

        int leftMask = Bits32.mask(position * CARD_LENGTH + CARD_LENGTH,
                Integer.SIZE - (position * CARD_LENGTH + CARD_LENGTH));
        int rightMask = Bits32.mask(0, position * CARD_LENGTH);

        int mask = pkTrick & (leftMask | rightMask);
        return mask | (pkCard << (position * CARD_LENGTH));
    }

    /**
     * Gives the base color of the trick
     * 
     * @param pkTrick
     *            Packed trick set to extract the base color from
     * @return The color of the first card to be played (the base color)
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        assert (size(pkTrick) > 0);

        return PackedCard.color(card(pkTrick, 0));
    }

    private static boolean trumpPlayed(int pkTrick) {
        for (int i = 0; i < size(pkTrick); i++) {
            if (PackedCard.color(card(pkTrick, i)) == trump(pkTrick)) {
                return true;
            }
        }
        return false;
    }

    private static int highestTrump(int pkTrick) {
        int highestTrumpCard = 0;
        for (int i = 0; i < size(pkTrick); i++) {
            int card = card(pkTrick, i);
            if (PackedCard.color(card) == trump(pkTrick) && PackedCard
                    .isBetter(trump(pkTrick), card, highestTrumpCard)) {
                highestTrumpCard = card;
            }
        }
        return highestTrumpCard;
    }

    /**
     * The method chooses the cards from hand which can be played according to
     * the game rules.
     * 
     * @param pkTrick
     *            packed trick set
     * @param pkHand
     *            hand deck of player
     * @return all the cards in the player's hand that he's allowed to play
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert PackedCardSet.isValid(pkHand);
        assert !isFull(pkTrick);

        if (isEmpty(pkTrick)) {
            return pkHand;
        }

        long trumpJack = PackedCardSet
                .singleton(Card.of(trump(pkTrick), Card.Rank.JACK).packed());
        long baseInHand = PackedCardSet.subsetOfColor(pkHand,
                baseColor(pkTrick));
        long trumpInHand = PackedCardSet.subsetOfColor(pkHand, trump(pkTrick));
        long playableTrumpInHand = PackedCardSet.intersection(
                PackedCardSet.trumpAbove(highestTrump(pkTrick)), pkHand);
        long nonTrumpInHand = PackedCardSet.difference(pkHand,
                PackedCardSet.subsetOfColor(pkHand, trump(pkTrick)));

        if (!trumpPlayed(pkTrick) || (baseColor(pkTrick) == trump(pkTrick))) {
            if (!PackedCardSet.isEmpty(baseInHand)) {
                if (baseInHand == trumpJack)
                    return pkHand;
                return PackedCardSet.union(baseInHand, trumpInHand);
            } else
                return pkHand;
        }

        if (PackedCardSet.isEmpty(nonTrumpInHand)) {
            if (PackedCardSet.isEmpty(playableTrumpInHand))
                return pkHand;
            return playableTrumpInHand;
        }

        if (PackedCardSet.isEmpty(baseInHand))
            return PackedCardSet.union(nonTrumpInHand, playableTrumpInHand);
        return PackedCardSet.union(baseInHand, playableTrumpInHand);
    }

    /**
     * Calculates the point of a full trick
     * 
     * @param pkTrick
     *            packed trick set
     * @return total points of the trick
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        assert isFull(pkTrick);

        int pointSum = 0;
        for (int i = 0; i < size(pkTrick); i++) {
            pointSum += PackedCard.points(trump(pkTrick), card(pkTrick, i));
        }

        if (isLast(pkTrick)) {
            pointSum += Jass.LAST_TRICK_ADDITIONAL_POINTS;
        }

        return pointSum;
    }

    /**
     * Provides the winning player
     * 
     * @param pkTrick
     *            packed trick set
     * @return current winning player of the trick
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        assert !isEmpty(pkTrick);

        int winningIndex = 0;
        Color trump = trump(pkTrick);

        for (int i = 0; i < size(pkTrick); i++) {
            if (PackedCard.isBetter(trump, card(pkTrick, i),
                    card(pkTrick, winningIndex))) {
                winningIndex = i;
            }
        }

        return player(pkTrick, winningIndex);
    }

    /**
     * toString function for prints
     * 
     * @param pkTrick
     *            packed trick set
     * @return textual representation of the played cards in the trick
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);

        StringJoiner j = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); i++) {
            j.add(Card.ofPacked(card(pkTrick, i)).toString());
        }
        return j.toString();
    }

}
