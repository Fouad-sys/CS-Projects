package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Jass game card
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Card {

    // packed representation of card
    private final int cardPackedRep;

    /**
     * constructs a card with the given packed version (private constructor)
     * 
     * @param cardPack
     *            packed verion of card
     */
    private Card(int cardPack) {
        this.cardPackedRep = cardPack;
    }

    /**
     * constructs a card with the given components (public constructor)
     * 
     * @param c
     *            card's color
     * @param r
     *            card's rank
     * @return new card with the given components
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * constructs a card with the given packed version (public constructor)
     * 
     * @param packed
     *            packed version of card
     * @return new card with the given packed version
     * @throws IllegalArgumentException
     *             if packed version of card isn't valid
     */
    public static Card ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }

    /**
     * gets packed version of card
     * 
     * @return packed version of card
     */
    public int packed() {
        return cardPackedRep;
    }

    /**
     * returns card's color
     * 
     * @return card's color
     */
    public Color color() {
        return PackedCard.color(cardPackedRep);
    }

    /**
     * returns card's rank
     * 
     * @return card's rank
     */
    public Rank rank() {
        return PackedCard.rank(cardPackedRep);
    }

    /**
     * checks if this card is higher than the given card or not
     * 
     * @param trump
     *            turn's trump
     * @param that
     *            card to compare with
     * @return true if this card is higher than the given one and false
     *         otherwise
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.cardPackedRep,
                that.cardPackedRep);
    }

    /**
     * gives points of this card
     * 
     * @param trump
     *            turn's trump
     * @return this card's points (value)
     */
    public int points(Color trump) {
        return PackedCard.points(trump, cardPackedRep);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof Card) {
            return ((Card) thatO).cardPackedRep == this.cardPackedRep;
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
        return packed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedCard.toString(cardPackedRep);
    }

    /**
     * Card's color
     * 
     * @author Szabina Horvath (226459)
     * @author Fouad Mahmoud (303076)
     */
    public enum Color {

        SPADE("\u2660"), HEART("\u2665"), DIAMOND("\u2666"), CLUB("\u2663");

        /**
         * unmodifiable list of all card colors
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        /**
         * number of card colors
         */
        public static final int COUNT = values().length;

        private final String charType;

        /**
         * constructs color with given character type
         * 
         * @param charType
         *            character type of color
         */
        private Color(String charType) {
            this.charType = charType;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return charType;
        }

    }

    /**
     * Card's rank
     * 
     * @author Szabina Horvath (226459)
     * @author Fouad Mahmoud (303076)
     */
    public enum Rank {

        SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK(
                "J"), QUEEN("Q"), KING("K"), ACE("A");

        /**
         * unmodifiable list of all card ranks
         */
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        /**
         * number of card ranks
         */
        public static final int COUNT = values().length;

        private final String charType;
        private final int[] trumpPositions = {0,1,2,7,3,8,4,5,6};

        /**
         * constructs rank with given character type
         * 
         * @param charType
         *            character type of rank
         */
        private Rank(String charType) {
            this.charType = charType;
        }

        /**
         * returns position of each trump card with given rank based on its
         * value
         * 
         * @return position of trump card with given rank
         */
        public int trumpOrdinal() {
            return trumpPositions[this.ordinal()];

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return charType;
        }

    }

}
