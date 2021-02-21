package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Packed version of score
 * 
 * @author Szabina Horvath (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Score {

    /**
     * game's initial score (all six components ae 0)
     */
    public static final Score INITIAL = new Score(0L);

    // packed representation of score
    private final long scorePackedRep;

    /**
     * private Score constructor
     * 
     * @param scorePackedRep
     *            : packed representation of score
     */
    private Score(long scorePackedRep) {
        this.scorePackedRep = scorePackedRep;
    }

    /**
     * constructs a new score with the given packed version
     * 
     * @param packed
     *            : packed version of score
     * @return new score with the given component
     * @throws IllegalArgumentException
     *             if the packed score isn't a valid one
     */
    public static Score ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
     * gets packed version of score
     * 
     * @return packed version of score
     */
    public long packed() {
        return scorePackedRep;
    }

    /**
     * returns number of tricks won by given team
     * 
     * @param t
     *            : given team
     * @return number of tricks won by given team
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(scorePackedRep, t);
    }

    /**
     * returns number of turn points won by given team
     * 
     * @param t
     *            : given team
     * @return number of turn points won by given team
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(scorePackedRep, t);
    }

    /**
     * returns number of game points won by given team
     * 
     * @param t
     *            : given team
     * @return number of game points won by given team
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(scorePackedRep, t);
    }

    /**
     * returns total points won by given team
     * 
     * @param t
     *            : given team
     * @return total points won by given team
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(scorePackedRep, t);
    }

    /**
     * updates score after a trick has been played
     * 
     * @param winningTeam
     *            : team that won the trick
     * @param trickPoints
     *            : number of points the team scored
     * @return updated score after the trick has been played
     * @throws IllegalArgumentException
     *             if the trick points are negative
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints)
            throws IllegalArgumentException {
        Preconditions.checkArgument(trickPoints >= 0);
        return ofPacked(PackedScore.withAdditionalTrick(scorePackedRep,
                winningTeam, trickPoints));
    }

    /**
     * updates score for next turn (with turn points added to game points)
     * 
     * @return updated score
     */
    public Score nextTurn() {
        return ofPacked(PackedScore.nextTurn(scorePackedRep));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO instanceof Score) {
            return ((Score) thatO).scorePackedRep == this.scorePackedRep;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(scorePackedRep);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedScore.toString(scorePackedRep);
    }

}
