package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * The class represents a JASS game. The game runs till one of the team reached
 * a given number of points. A turn consists of 9 tricks. The player who owns
 * the Diamond 7 starts the first turn. The next turns are started by the
 * sequential players. The trump is chosen randomly at the beginning of each
 * turn.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class JassGame {

    private static final List<Card> DECK = Collections.unmodifiableList(deck());

    private final Random shuffleRng;
    private final Random trumpRng;
    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> playerCards;
    private PlayerId firstPlayer;
    private TurnState turnState;

    /**
     * Creates a new JASS game using a single seed and player attributes. It
     * picks the trump, distributes the cards, chooses the first player and
     * initializes the first turn.
     * 
     * @param rngSeed
     *            Random seed for choosing the trump and shuffling the deck
     * @param players
     *            Associative table containing the player identifications
     * @param playerNames
     *            Associative table containing the player names
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());
        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));
        this.playerCards = new EnumMap<>(PlayerId.class);
        shuffleDistribute();
        this.firstPlayer = initialPlayer();
        this.turnState = initialState();
        displayGame();
        displayTurn();
    }

    private static List<Card> deck() {
        List<Card> deck = new ArrayList<>();
        for (Color c : Color.ALL) {
            for (Rank r : Rank.ALL)
                deck.add(Card.of(c, r));
        }
        return deck;
    }

    private List<Card> shuffle() {
        List<Card> shuffledDeck = new ArrayList<>(DECK);
        Collections.shuffle(shuffledDeck, shuffleRng);
        return shuffledDeck;
    }

    private void distribute(List<Card> shuffledDeck) {
        for (int i = 0; i < PlayerId.COUNT; i++) {
            playerCards.put(PlayerId.ALL.get(i), CardSet.of(shuffledDeck
                    .subList(Jass.HAND_SIZE * i, Jass.HAND_SIZE * (i + 1))));
        }
    }

    private void shuffleDistribute() {
        List<Card> shuffledDeck = shuffle();
        distribute(shuffledDeck);
    }

    private Color trump() {
        return Color.ALL.get(trumpRng.nextInt(Color.COUNT));
    }

    private PlayerId initialPlayer() {
        PlayerId initialPlayer = null;
        for (PlayerId playerId : PlayerId.ALL) {
            if (playerCards.get(playerId)
                    .contains(Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN)))
                initialPlayer = playerId;
        }
        return initialPlayer;
    }

    private TurnState initialState() {
        return TurnState.initial(trump(), Score.INITIAL, firstPlayer);
    }

    private PlayerId firstPlayer() {
        return PlayerId.ALL.get((firstPlayer.ordinal() + 1) % 4);
    }

    private TurnState firstState() {
        return TurnState.initial(trump(), turnState.score().nextTurn(),
                firstPlayer);
    }

    private void startTurn() {
        shuffleDistribute();
        firstPlayer = firstPlayer();
        turnState = firstState();
        displayTurn();
    }

    private void displayGame() {
        for (Map.Entry<PlayerId, Player> player : players.entrySet())
            player.getValue().setPlayers(player.getKey(), playerNames);
    }

    private void displayTurn() {
        Color trump = turnState.trick().trump();
        for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
            player.getValue().updateHand(playerCards.get(player.getKey()));
            player.getValue().setTrump(trump);
        }
    }

    private void displayScoreTrick() {
        Score score = turnState.score();
        Trick trick = turnState.trick();
        for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
            player.getValue().updateScore(score);
            player.getValue().updateTrick(trick);
        }
    }

    private void displayHand(PlayerId player) {
        players.get(player).updateHand(playerCards.get(player));
    }

    private void displayTrick() {
        Trick trick = turnState.trick();
        for (Map.Entry<PlayerId, Player> player : players.entrySet())
            player.getValue().updateTrick(trick);
    }

    private void displayScoreWinner() {
        Score score = turnState.score();
        if (PackedScore.totalPoints(turnState.packedScore(),
                TeamId.TEAM_1) >= Jass.WINNING_POINTS) {
            for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
                player.getValue().setWinningTeam(TeamId.TEAM_1);
                player.getValue().updateScore(score);
            }
        } else {
            for (Map.Entry<PlayerId, Player> player : players.entrySet()) {
                player.getValue().setWinningTeam(TeamId.TEAM_2);
                player.getValue().updateScore(score);
            }
        }
    }

    private void play(PlayerId player) {
        CardSet hand = playerCards.get(player);
        Card card = players.get(player).cardToPlay(turnState, hand);
        turnState = turnState.withNewCardPlayed(card);
        playerCards.replace(player, hand.remove(card));
    }

    /**
     * Checks for the end of the game.
     * 
     * @return true if the game is over and false otherwise
     */
    public boolean isGameOver() {
        if (PackedScore.totalPoints(turnState.packedScore(),
                TeamId.TEAM_1) >= Jass.WINNING_POINTS
                || PackedScore.totalPoints(turnState.packedScore(),
                        TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
            return true;
        }
        return false;
    }

    /**
     * Makes the game progress. It moves along till the end of a next trick and
     * does nothing if the game is over.
     */
    public void advanceToEndOfNextTrick() {

        if (turnState.isTerminal())
            startTurn();

        displayScoreTrick();

        PlayerId player;
        while (!turnState.trick().isFull()) {
            player = turnState.nextPlayer();
            play(player);
            displayHand(player);
            displayTrick();
        }

        if (turnState.trick().isFull())
            turnState = turnState.withTrickCollected();

        if (isGameOver()) {
            displayScoreWinner();
        }
    }

}
