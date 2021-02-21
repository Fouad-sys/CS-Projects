package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
/**
 * Class representing a client acting as a player and communicating with the server.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;
    private int port = 5108;

    /**
     * Constructs a new client with the given host name.
     * 
     * @param hostName host name of the server of the distant player
     * @throws IOException if the host can't be reached
     */
    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, port);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), US_ASCII));

    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        String messageType = JassCommand.CARD.name();
        String packedScore = StringSerializer
                .serializeLong(state.packedScore());
        String unplayedCards = StringSerializer
                .serializeLong(state.packedUnplayedCards());
        String packedTrick = StringSerializer.serializeInt(state.packedTrick());
        String turnState = StringSerializer.combine(",", packedScore,
                unplayedCards, packedTrick);
        String packedHand = StringSerializer.serializeLong(hand.packed());
        String message = StringSerializer.combine(" ", messageType, turnState, packedHand);
        write(message);

        int packedCard = StringSerializer.deserializeInt(readLine());
        Card cardPlayed = Card.ofPacked(packedCard);
        return cardPlayed;
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setPlayers(ch.epfl.javass.jass.PlayerId, java.util.Map)
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        // We chose to use here a StringJoiner to be able to append
        // Strings to it while iterating on the PlayerIds instead of using the
        // combine method of StringSerializer which would cause us to duplicate
        // code (as it won't be used in the iteration).
        StringJoiner serialNames = new StringJoiner(",");
        String messageType = JassCommand.PLRS.name();
        String playerPosition = StringSerializer.serializeInt(ownId.ordinal());
        for (Map.Entry<PlayerId, String> p : playerNames.entrySet()) {
            String serializedPlayer = StringSerializer
                    .serializeString(p.getValue());
            serialNames.add(serializedPlayer);
        }
        String message = StringSerializer.combine(" ", messageType, playerPosition, serialNames.toString());
        write(message);

    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateHand(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public void updateHand(CardSet newHand) {
        String messageType = JassCommand.HAND.name();
        String packedHand = StringSerializer.serializeLong(newHand.packed());
        String message = messageType + " " + packedHand;
        write(message); 
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setTrump(ch.epfl.javass.jass.Card.Color)
     */
    @Override
    public void setTrump(Color trump) {
        String messageType = JassCommand.TRMP.name();
        String newTrump = StringSerializer.serializeInt(trump.ordinal());
        String message = messageType + " " + newTrump;
        write (message);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#updateTrick(ch.epfl.javass.jass.Trick)
     */
    @Override
    public void updateTrick(Trick newTrick) {
        String messageType = JassCommand.TRCK.name();
        String updatedTrick = StringSerializer.serializeInt(newTrick.packed());
        String message = messageType + " " + updatedTrick;
        write (message); 
    }

    @Override
    public void updateScore(Score score) {
        String messageType = JassCommand.SCOR.name();
        String newScore = StringSerializer.serializeLong(score.packed());
        String message = messageType + " " + newScore;
        write (message); 
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#setWinningTeam(ch.epfl.javass.jass.TeamId)
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        String messageType = JassCommand.WINR.name();
        String winTeamPosition = StringSerializer.serializeInt(winningTeam.ordinal());
        String message = messageType + " " + winTeamPosition;
        write (message);
    }

    /* (non-Javadoc)
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        r.close();
        w.close();
        s.close();

    }

    private String readLine() {
        try {
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(String s) {
        try {
            w.write(s);
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    

}