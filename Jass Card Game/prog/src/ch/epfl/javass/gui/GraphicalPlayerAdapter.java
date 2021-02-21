package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;
import ch.epfl.javass.jass.Card.Color;

/**
 * Class adapting the graphical interface to a player.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class GraphicalPlayerAdapter implements Player {

    private final ScoreBean skBean;
    private final TrickBean trkBean;
    private final HandBean handBean;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> queue;

    /**
     * Constructs a graphical player adapter.
     */
    public GraphicalPlayerAdapter() {

        skBean = new ScoreBean();
        trkBean = new TrickBean();
        handBean = new HandBean();
        queue = new ArrayBlockingQueue<>(1);

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        try {
            Platform.runLater(() -> {
                handBean.setPlayableCards(state.trick().playableCards(hand));
            });
            Card playedCard = queue.take();
            Platform.runLater(() -> {
                handBean.setPlayableCards(CardSet.EMPTY);
            });
            return playedCard;
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId,
     * java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, skBean,
                trkBean, handBean, queue);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            trkBean.setTrump(trump);
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            trkBean.setTrick(newTrick);
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.javass.jass.Player#updateScore(ch.epfl.javass.jass.Score)
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            skBean.setTurnPoints(TeamId.TEAM_1,
                    score.turnPoints(TeamId.TEAM_1));
            skBean.setTurnPoints(TeamId.TEAM_2,
                    score.turnPoints(TeamId.TEAM_2));
            skBean.setGamePoints(TeamId.TEAM_1,
                    score.gamePoints(TeamId.TEAM_1));
            skBean.setGamePoints(TeamId.TEAM_2,
                    score.gamePoints(TeamId.TEAM_2));
            skBean.setTotalPoints(TeamId.TEAM_1,
                    score.totalPoints(TeamId.TEAM_1));
            skBean.setTotalPoints(TeamId.TEAM_2,
                    score.totalPoints(TeamId.TEAM_2));
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            skBean.setWinningTeam(winningTeam);
            ;
        });
    }
}
