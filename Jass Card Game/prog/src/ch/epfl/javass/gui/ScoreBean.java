package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.*;

/**
 * Class representing a bean containing several score properties and methods to
 * modify them.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class ScoreBean {

    private final IntegerProperty turnPointsTeam1 = new SimpleIntegerProperty();
    private final IntegerProperty turnPointsTeam2 = new SimpleIntegerProperty();
    private final IntegerProperty gamePointsTeam1 = new SimpleIntegerProperty();
    private final IntegerProperty gamePointsTeam2 = new SimpleIntegerProperty();
    private final IntegerProperty totalPointsTeam1 = new SimpleIntegerProperty();
    private final IntegerProperty totalPointsTeam2 = new SimpleIntegerProperty();
    private final ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();

    /**
     * Returns the turn points property of the given team.
     * 
     * @param team
     *            team to get its turn points property
     * @return team's turn points property
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {

        return team == TeamId.TEAM_1 ? turnPointsTeam1 : turnPointsTeam2;
    }

    /**
     * Sets the turn points of the given team to the given turn points.
     * 
     * @param team
     *            the team to set its turn points
     * @param newTurnPoints
     *            new turn points to set
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1) {
            turnPointsTeam1.set(newTurnPoints);
        } else {
            turnPointsTeam2.set(newTurnPoints);
        }
    }

    /**
     * Returns the game points property of the given team.
     * 
     * @param team
     *            team to get its game points property
     * @return game points property of the given team
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {

        return team == TeamId.TEAM_1 ? gamePointsTeam1 : gamePointsTeam2;
    }

    /**
     * Sets the given team's game points to the new given value.
     * 
     * @param team
     *            team to set its game points
     * @param newGamePoints
     *            new game points to set
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1) {
            gamePointsTeam1.set(newGamePoints);
        } else {
            gamePointsTeam2.set(newGamePoints);
        }
    }

    /**
     * Gets the given team's total points property.
     * 
     * @param team
     *            team to get its total points property
     * @return given team's total points property
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {

        return team == TeamId.TEAM_1 ? totalPointsTeam1 : totalPointsTeam2;
    }

    /**
     * Sets the given team's total points to the new given value.
     * 
     * @param team
     *            team to set its total points
     * @param newTotalPoints
     *            new total points to set
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team == TeamId.TEAM_1) {
            totalPointsTeam1.set(newTotalPoints);
        } else {
            totalPointsTeam2.set(newTotalPoints);
        }
    }

    /**
     * Gets the winning team property.
     * 
     * @return winning team property
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {

        return winningTeam;
    }

    /**
     * Sets the winning team to the given team.
     * 
     * @param winningTeam
     *            new winning team to set
     */
    public void setWinningTeam(TeamId winningTeam) {

        this.winningTeam.set(winningTeam);
    }

}
