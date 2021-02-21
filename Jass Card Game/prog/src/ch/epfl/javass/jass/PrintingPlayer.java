package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * test class for Jass Game that represents a player that prints out all the
 * game's events
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 *
 */
public final class PrintingPlayer implements Player {

    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.print("C'est à moi de jouer... Je joue : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c);
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("Les joueurs sont : ");
        underlyingPlayer.setPlayers(ownId, playerNames);
        for (Map.Entry<PlayerId, String> entry : playerNames.entrySet())
            if (entry.getKey() == ownId) {
                System.out.println(entry.getValue() + " (moi)");
            } else {
                System.out.println(entry.getValue());
            }
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main est ");
        underlyingPlayer.updateHand(newHand);
        System.out.println(newHand.toString());
    }

    @Override
    public void setTrump(Color trump) {
        System.out.println("L'atout est : ");
        underlyingPlayer.setTrump(trump);
        System.out.println(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
        System.out.println("Pli " + newTrick.index() + ", comence par joueur "
                + newTrick.player(0) + " : " + newTrick.toString());
    }

    @Override
    public void updateScore(Score score) {
        System.out.println("Scores : ");
        underlyingPlayer.updateScore(score);
        System.out.println(score.toString());
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("L'equipe gagnante est ");
        underlyingPlayer.setWinningTeam(winningTeam);
        System.out.println(winningTeam);
    }

}
