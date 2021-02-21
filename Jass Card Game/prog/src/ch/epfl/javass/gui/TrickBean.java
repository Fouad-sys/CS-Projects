package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Class representing a bean containing several trick properties and methods to
 * modify them.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class TrickBean {

    private final ObjectProperty<Color> trump = new SimpleObjectProperty<>();
    private final ObservableMap<PlayerId, Card> trick = FXCollections
            .observableHashMap();
    private final ObjectProperty<PlayerId> winningPlayer = new SimpleObjectProperty<>();

    /**
     * Gets the trump property.
     * 
     * @return trump property
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {

        return trump;
    }

    /**
     * Gets the winning player property.
     * 
     * @return winning player property
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }

    /**
     * Gets the trick property.
     * 
     * @return trick property
     */
    public ObservableMap<PlayerId, Card> trick() {

        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * Sets the trick to the new given trick while changing the winning player.
     * 
     * @param newTrick
     *            new trick to set
     */
    public void setTrick(Trick newTrick) {
        if (newTrick.isEmpty()) {
            winningPlayer.set(null);
        } else {
            winningPlayer.set(newTrick.winningPlayer());
        }

        for (int i = 0; i < newTrick.size(); i++) {
            trick.put(newTrick.player(i), newTrick.card(i));
        }

        for (int i = newTrick.size(); i < PlayerId.COUNT; i++) {
            trick.put(newTrick.player(i), null);
        }
    }

    /**
     * Sets the trump to the given color.
     * 
     * @param c
     *            color of trump to set
     */
    public void setTrump(Card.Color c) {
        trump.set(c);
    }

}
