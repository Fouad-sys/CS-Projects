package ch.epfl.javass.jass;

import java.util.Random;

/**
 * Class representing a player that plays randomly
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 *
 */
public final class RandomPlayer implements Player {
    private final Random rng;

    /**
     * constructors a random player given the random seed
     * 
     * @param rngSeed
     *            random seed
     */
    public RandomPlayer(long rngSeed) {
        this.rng = new Random(rngSeed);
    }

    
    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playable = state.trick().playableCards(hand);
        return playable.get(rng.nextInt(playable.size()));
    }

}
