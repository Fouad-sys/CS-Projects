package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * Packed version of score
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class PackedScore {

    /**
     * default packedScore private constructor
     */
    private PackedScore() {
    }

    /**
     * turn's initial score
     */
    public static final long INITIAL = 0L;

    private static final int MAXIMUM_TURN_POINTS = 257,
            MAXIMUM_GAME_POINTS = 2000;
    // indexes of first bit of components
    private static final int TEAM_1_TRICKS_WON_START = 0,
            TEAM_2_TRICKS_WON_START = 32;
    private static final int TEAM_1_TURN_POINTS_START = 4,
            TEAM_2_TURN_POINTS_START = 36;
    private static final int TEAM_1_GAME_POINTS_START = 13,
            TEAM_2_GAME_POINTS_START = 45;
    // sizes in bits of components
    private static final int TRICKS_WON_SIZE = 4, TURN_POINTS_SIZE = 9,
            GAME_POINTS_SIZE = 11;
    // unused bits starting position and size in bits
    private static final int EMPTY_BITS_START = 24, EMPTY_BITS_SIZE = 8;

    /**
     * checks if the given score is a valid one (respects validity conditions of
     * method isValid32)
     * 
     * @param pkScore
     *            packed version of score
     * @return true if score is valid, false otherwise
     */
    public static boolean isValid(long pkScore) {

        return isValid32(Bits64.extract(pkScore, Integer.SIZE, Integer.SIZE))
                && isValid32(Bits64.extract(pkScore, 0, Integer.SIZE));
    }

    /**
     * packs all given components into a packed score
     * 
     * @param turnTricks1
     *            tricks won by team 1 (must be valid)
     * @param turnPoints1
     *            team 1's points in current turn (must be valid)
     * @param gamePoints1
     *            team 1's game points (must be valid)
     * @param turnTricks2
     *            tricks won by team 2 (must be valid)
     * @param turnPoints2
     *            team 2's points in current turn (must be valid)
     * @param gamePoints2
     *            team 2's game points (must be valid)
     * @return packed score with given components
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {

        long pkScore = (pack32(turnTricks2, turnPoints2,
                gamePoints2) << Integer.SIZE)
                | pack32(turnTricks1, turnPoints1, gamePoints1);
        return pkScore;
    }

    /**
     * returns number of tricks won by the given team in current turn
     * 
     * @param pkScore
     *            packed score (must be valid)
     * @param t
     *            team to get score of
     * @return number of tricks won by the given team in current turn
     */
    public static int turnTricks(long pkScore, TeamId t) {

        assert isValid(pkScore);

        int startPosition = t == TeamId.TEAM_1 ? TEAM_1_TRICKS_WON_START
                : TEAM_2_TRICKS_WON_START;
        return (int) Bits64.extract(pkScore, startPosition, TRICKS_WON_SIZE);
    }

    /**
     * returns current turn points of given team
     * 
     * @param pkScore
     *            packed score (must be valid)
     * @param t
     *            team to get turn points of
     * @return current turn points of given team
     */
    public static int turnPoints(long pkScore, TeamId t) {

        assert isValid(pkScore);

        int startPosition = t == TeamId.TEAM_1 ? TEAM_1_TURN_POINTS_START
                : TEAM_2_TURN_POINTS_START;
        return (int) Bits64.extract(pkScore, startPosition, TURN_POINTS_SIZE);
    }

    /**
     * returns game points of given team
     * 
     * @param pkScore
     *            packed score (must be valid)
     * @param t
     *            team to get game points of
     * @return game points of given team
     */
    public static int gamePoints(long pkScore, TeamId t) {

        assert isValid(pkScore);

        int startPosition = t == TeamId.TEAM_1 ? TEAM_1_GAME_POINTS_START
                : TEAM_2_GAME_POINTS_START;
        return (int) Bits64.extract(pkScore, startPosition, GAME_POINTS_SIZE);
    }

    /**
     * returns total points won by a given team
     * 
     * @param pkScore
     *            packed score (must be valid)
     * @param t
     *            team to get total points of
     * @return total points won by a given team
     */
    public static int totalPoints(long pkScore, TeamId t) {

        assert isValid(pkScore);

        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * updates score after winning team has won the previous trick and scored
     * trickPoints
     * 
     * @param pkScore
     *            packed score to be updated (must be valid)
     * @param winningTeam
     *            team that won the trick
     * @param trickPoints
     *            number of points of the won trick
     * @return updated score
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {

        assert isValid(pkScore);
        int newTurnTricks = turnTricks(pkScore, winningTeam) + 1;
        int newTurnPoints = turnPoints(pkScore, winningTeam) + trickPoints;
        if (newTurnTricks == Jass.TRICKS_PER_TURN) {
            newTurnPoints += Jass.MATCH_ADDITIONAL_POINTS;
        }

        if (winningTeam == TeamId.TEAM_1) {

            return Bits64.pack(
                    pack32(newTurnTricks, newTurnPoints,
                            gamePoints(pkScore, winningTeam)),
                    Integer.SIZE,
                    Bits64.extract(pkScore, Integer.SIZE, Integer.SIZE),
                    Integer.SIZE);

        } else {

            return Bits64.pack(Bits64.extract(pkScore, 0, Integer.SIZE),
                    Integer.SIZE, pack32(newTurnTricks, newTurnPoints,
                            gamePoints(pkScore, winningTeam)),
                    Integer.SIZE);
        }

    }

    /**
     * updates scores for the turn to come (with turn points added to game
     * points)
     * 
     * @param pkScore
     *            score to be updated (must be valid)
     * @return updated score for the turn to come
     */
    public static long nextTurn(long pkScore) {

        assert isValid(pkScore);
        return pack(0, 0, totalPoints(pkScore, TeamId.TEAM_1), 0, 0,
                totalPoints(pkScore, TeamId.TEAM_2));
    }

    /**
     * ¨returns textual representation of scores
     * 
     * @param pkScore
     *            packed version of scores to be represented
     * @return textual representation of scores
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);
        return "(" + totalPoints(pkScore, TeamId.TEAM_1) + ","
                + turnPoints(pkScore, TeamId.TEAM_1) + ","
                + turnTricks(pkScore, TeamId.TEAM_1) + ") / ("
                + totalPoints(pkScore, TeamId.TEAM_2) + ","
                + turnPoints(pkScore, TeamId.TEAM_2) + ","
                + turnTricks(pkScore, TeamId.TEAM_2) + ")";
    }

    /**
     * checks if a team's score is valid (32 bits)
     * 
     * @param pkScore
     *            score to evaluate
     * @return true if 32 bit score is in the given boundaries and false
     *         otherwise
     */
    private static boolean isValid32(long pkScore) {
        return isValidTurnTricks((int) Bits64.extract(pkScore,
                TEAM_1_TRICKS_WON_START, TRICKS_WON_SIZE))
                && isValidTurnPoints((int) Bits64.extract(pkScore,
                        TEAM_1_TURN_POINTS_START, TURN_POINTS_SIZE))
                && isValidGamePoints((int) Bits64.extract(pkScore,
                        TEAM_1_GAME_POINTS_START, GAME_POINTS_SIZE))
                && Bits64.extract(pkScore, EMPTY_BITS_START,
                        EMPTY_BITS_SIZE) == 0;
    }

    /**
     * checks if number of tricks won by a team is between 0 and the maaximum
     * trick number per turn included
     * 
     * @param turnTricks
     *            number of tricks won
     * @return true if given turn tricks is valid, false otherwise
     */
    private static boolean isValidTurnTricks(int turnTricks) {
        return (0 <= turnTricks && turnTricks <= Jass.TRICKS_PER_TURN);
    }

    /**
     * checks if the turn points of a team are valid (between 0 and the maximum
     * number of turn points included)
     * 
     * @param turnPoints
     *            turn points to be checked
     * @return true if given turn points is valid, false otherwise
     */
    private static boolean isValidTurnPoints(int turnPoints) {
        return (0 <= turnPoints && turnPoints <= MAXIMUM_TURN_POINTS);
    }

    /**
     * checks if the game points won by a team is valid (between 0 and the
     * boudary of the maximum number of game points included)
     * 
     * @param gamePoints
     *            game points to be checked
     * @return true if game points is valid, false otherwise
     */
    private static boolean isValidGamePoints(int gamePoints) {
        return (0 <= gamePoints && gamePoints <= MAXIMUM_GAME_POINTS);
    }

    /**
     * packs turn tricks, turn points and game points into a 32 bit score
     * 
     * @param turnTricks1
     *            number of tricks won in the turn (must be valid)
     * @param turnPoints1
     *            number of points won in the turn (must be valid)
     * @param gamePoints1
     *            number of game points won in the turn (must be valid)
     * @return packed 32 bit score with given components
     */
    private static long pack32(int turnTricks1, int turnPoints1,
            int gamePoints1) {
        assert isValidTurnTricks(turnTricks1);
        assert isValidTurnPoints(turnPoints1);
        assert isValidGamePoints(gamePoints1);
        return (long) Bits32.pack(turnTricks1, TRICKS_WON_SIZE, turnPoints1,
                TURN_POINTS_SIZE, gamePoints1, GAME_POINTS_SIZE);
    }

}
