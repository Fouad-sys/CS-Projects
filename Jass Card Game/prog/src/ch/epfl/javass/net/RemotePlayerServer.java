package ch.epfl.javass.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class representing the server of a player that waits for a connection on the
 * 5108 port and pilots a local player based on the received messages.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class RemotePlayerServer {

    private final Player localPlayer;
    private int port = 5108;

    /**
     * Constructs a server piloting the given local player.
     * 
     * @param localPlayer
     *            the local player to pilot
     */
    public RemotePlayerServer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    /**
     * Method sending and receiving messages and calls the local player's
     * corresponding methods based on the messages received.
     */
    public void run() {

        try (ServerSocket s0 = new ServerSocket(port);
                Socket s = s0.accept();

                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), US_ASCII))) {

            while (true) {
                String[] messageTable = StringSerializer.split(r.readLine(),
                        " ");
                String messageType = messageTable[0];

                switch (messageType) {

                case "PLRS":

                    int playerPosition = StringSerializer
                            .deserializeInt(messageTable[1]);
                    String[] serialNames = StringSerializer
                            .split(messageTable[2], ",");
                    Map<PlayerId, String> playerNames = new HashMap<>();

                    for (int i = 0; i < PlayerId.COUNT; i++) {
                        String deserializedName = StringSerializer
                                .deserializeString(serialNames[i]);
                        playerNames.put(PlayerId.ALL.get(i), deserializedName);
                    }

                    localPlayer.setPlayers(PlayerId.ALL.get(playerPosition),
                            playerNames);

                    break;

                case "HAND":

                    long pkCardSet = StringSerializer
                            .deserializeLong(messageTable[1]);
                    localPlayer.updateHand(CardSet.ofPacked(pkCardSet));

                    break;

                case "TRMP":

                    int trumpPosition = StringSerializer
                            .deserializeInt(messageTable[1]);
                    localPlayer.setTrump(Card.Color.ALL.get(trumpPosition));

                    break;

                case "TRCK":

                    int packedTrick = StringSerializer
                            .deserializeInt(messageTable[1]);
                    localPlayer.updateTrick(Trick.ofPacked(packedTrick));

                    break;

                case "CARD":
                    String[] turnStateValues = StringSerializer
                            .split(messageTable[1], ",");
                    long currentScore = StringSerializer
                            .deserializeLong(turnStateValues[0]);
                    long unplayedCards = StringSerializer
                            .deserializeLong(turnStateValues[1]);
                    int currentTrick = StringSerializer
                            .deserializeInt(turnStateValues[2]);
                    long playerHand = StringSerializer
                            .deserializeLong(messageTable[2]);
                    TurnState currentState = TurnState.ofPackedComponents(
                            currentScore, unplayedCards, currentTrick);

                    Card cardPlayed = localPlayer.cardToPlay(currentState,
                            CardSet.ofPacked(playerHand));

                    w.write(StringSerializer.serializeInt(cardPlayed.packed()));
                    w.write('\n');
                    w.flush();

                    break;

                case "SCOR":

                    long newScore = StringSerializer
                            .deserializeLong(messageTable[1]);
                    localPlayer.updateScore(Score.ofPacked(newScore));

                    break;

                case "WINR":

                    int winningTeamPosition = StringSerializer
                            .deserializeInt(messageTable[1]);
                    localPlayer.setWinningTeam(
                            TeamId.ALL.get(winningTeamPosition));

                    break;

                default:
                    throw new Error();
                }

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
