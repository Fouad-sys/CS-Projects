package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * The class represents a paced player who plays for a given amount of time.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class PacedPlayer implements Player{

    private final Player underlyingPlayer;
    private final double minTime;
    
    /**
     * Creates the same player as given in attribute. It assures that the player
     * is playing during a given period of time.
     * 
     * @param underlyingPlayer
     *            player to be waited
     * @param minTime
     *            the minimum time to wait
     */
    public PacedPlayer(Player underlyingPlayer, double minTime){
        this.underlyingPlayer= underlyingPlayer;
        this.minTime= minTime;
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }
    
    /* 
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
    
    /* Chooses the card to play
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime= System.currentTimeMillis();
        Card cardToPlay= underlyingPlayer.cardToPlay(state, hand);
        long endTime= System.currentTimeMillis();
        long deltaTime= endTime-startTime;
        if(deltaTime<(long)minTime) {
            try {
                Thread.sleep(((long)minTime)-deltaTime);
              } catch (InterruptedException e) {}
        }
        return cardToPlay;
    }

}