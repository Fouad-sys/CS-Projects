package ch.epfl.javass.gui;

import java.util.Arrays;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Class representing a bean containing sevwral hand properties and methods to
 * modify them.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class HandBean {

    private final ObservableList<Card> hand = FXCollections
            .observableArrayList(Arrays.asList(new Card[9]));
    private final ObservableSet<Card> playableCard = FXCollections.observableSet();

    /**
     * Gets the hand property.
     * 
     * @return hand property
     */
    public ObservableList<Card> hand() {

        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Sets the hand to the given card set.
     * 
     * @param newHand
     *            new hand to set
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                hand.set(i, newHand.get(i));
            }
        } else {
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                if (hand.get(i) != null && !newHand.contains(hand.get(i))) {
                    hand.set(i, null);
                }
            }
        }
    }

    /**
     * Gets the playable cards property.
     * 
     * @return playable cards property
     */
    public ObservableSet<Card> playableCardsProperty() {

        return FXCollections.unmodifiableObservableSet(playableCard);
    }

    /**
     * Sets the playable cards to the given card set.
     * 
     * @param newPlayableCards
     *            new playable cards to set
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCard.clear();

        for (int i = 0; i < newPlayableCards.size(); i++) {
            playableCard.add(newPlayableCards.get(i));
        }

    }
}
