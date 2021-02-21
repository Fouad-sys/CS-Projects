package ch.epfl.javass.gui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class representing the graphical interface of the Jass game.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class GraphicalPlayer {

    private final PlayerId player;
    private final Map<PlayerId, String> playerNames;
    private StackPane gamePanes;
    private ArrayBlockingQueue<Card> queue;
    private static final int TRICK_CARD_WIDTH = 120, TRICK_CARD_HEIGHT = 180,
            HAND_CARD_WIDTH = 80, HAND_CARD_HEIGHT = 120, TRUMP_WIDTH = 101,
            TRUMP_HEIGHT = 101;
    private static final double blurOp = 0.2, visOp = 1.0;
    private final static int gaussianRadius = 4;

    /**
     * Constructs a new graphical player interface.
     * 
     * @param player
     *            the graphical interface's correspondant player
     * @param playerNames
     *            map joining the identity of the players to their names
     * @param skBean
     *            score bean
     * @param trkBean
     *            trick bean
     * @param handBean
     *            hand bean
     * @param queue
     *            the queue to exchange the card to play in
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> playerNames,
            ScoreBean skBean, TrickBean trkBean, HandBean handBean,
            ArrayBlockingQueue<Card> queue) {
        this.player = player;
        this.playerNames = playerNames;
        this.queue = queue;

        BorderPane gamePane = new BorderPane(createTrickPane(trkBean));
        gamePane.setTop(createScorePane(skBean));
        gamePane.setBottom(createHandPane(handBean));

        BorderPane victoryPane1 = createVictoryPanes(skBean).get(0);
        victoryPane1.visibleProperty()
                .bind(skBean.winningTeamProperty().isEqualTo(TeamId.TEAM_1));
        BorderPane victoryPane2 = createVictoryPanes(skBean).get(1);
        victoryPane2.visibleProperty()
                .bind(skBean.winningTeamProperty().isEqualTo(TeamId.TEAM_2));

        gamePanes = new StackPane(gamePane, victoryPane1, victoryPane2);

    }

    private List<PlayerId> getPlayers(TeamId team) {
        List<PlayerId> players = new LinkedList<>();
        if (team == TeamId.TEAM_1) {
            players.add(PlayerId.PLAYER_1);
            players.add(PlayerId.PLAYER_3);
        } else {
            players.add(PlayerId.PLAYER_2);
            players.add(PlayerId.PLAYER_4);
        }
        return players;
    }

    /**
     * Creates the score pane of the graphical interface.
     * 
     * @param skBean
     *            score bean
     * @return the grid pane of the scores
     */
    private GridPane createScorePane(ScoreBean skBean) {
        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima;\n" + "-fx-background-color: lightgray;\n"
                        + "-fx-padding: 5px;\n" + "-fx-alignment: center;");

        Label[] teamNames = new Label[TeamId.COUNT];
        StringExpression[] turnPtsProperty = new StringExpression[TeamId.COUNT];
        Text[] turnPoints = new Text[TeamId.COUNT];
        StringProperty trickPtsProperty1 = new SimpleStringProperty(
                " (+" + skBean.turnPointsProperty(TeamId.TEAM_1).get() + ")");
        StringProperty trickPtsProperty2 = new SimpleStringProperty(
                " (+" + skBean.turnPointsProperty(TeamId.TEAM_2).get() + ")");
        Text[] trickPoints = new Text[TeamId.COUNT];
        StringExpression[] gamePtsProperty = new StringExpression[TeamId.COUNT];
        Text[] gamePoints = new Text[TeamId.COUNT];

        for (int i = 0; i < TeamId.COUNT; i++) {
            TeamId team = i == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2;
            List<PlayerId> players = getPlayers(team);
            teamNames[i] = new Label(playerNames.get(players.get(1)) + " et "
                    + playerNames.get(players.get(0)) + " : ");

            turnPtsProperty[i] = Bindings
                    .convert(skBean.turnPointsProperty(team));
            turnPoints[i] = new Text((turnPtsProperty[i]).toString());
            turnPoints[i].textProperty().bind(turnPtsProperty[i]);

            StringProperty trickPtsProperty = i == 0 ? trickPtsProperty1
                    : trickPtsProperty2;
            skBean.turnPointsProperty(team)
                    .addListener(
                            (o, oV, nV) -> trickPtsProperty
                                    .setValue(" (+"
                                            + Integer.toString(
                                                    nV.intValue() == 0 ? 0
                                                            : nV.intValue() - oV
                                                                    .intValue())
                                            + ")"));
            trickPoints[i] = new Text(trickPtsProperty.get());
            trickPoints[i].textProperty().bind(trickPtsProperty);

            Label total = new Label(" / Total : ");

            gamePtsProperty[i] = Bindings
                    .convert(skBean.gamePointsProperty(team));
            gamePoints[i] = new Text(gamePtsProperty[i].toString());
            gamePoints[i].textProperty().bind(gamePtsProperty[i]);

            scorePane.addRow(i, teamNames[i], turnPoints[i], trickPoints[i],
                    total, gamePoints[i]);

            GridPane.setHalignment(teamNames[i], HPos.RIGHT);
            GridPane.setHalignment(turnPoints[i], HPos.RIGHT);
            GridPane.setHalignment(trickPoints[i], HPos.LEFT);
            GridPane.setHalignment(total, HPos.LEFT);
            GridPane.setHalignment(gamePoints[i], HPos.RIGHT);

        }

        return scorePane;

    }

    private void fillImageMap(ObservableMap<Card, Image> m) {
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                String cardFile = "/card_" + c.ordinal() + "_" + r.ordinal()
                        + "_240.png";
                Image card = new Image(cardFile);
                m.put(Card.of(c, r), card);
            }
        }
    }

    private void fillHandImageMap(ObservableMap<Card, Image> m) {
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                String handFile = "/card_" + c.ordinal() + "_" + r.ordinal()
                        + "_160.png";
                Image card = new Image(handFile);
                m.put(Card.of(c, r), card);
            }
        }
    }

    private void fillTrumpMap(ObservableMap<Card.Color, Image> m) {
        for (Card.Color c : Card.Color.ALL) {
            String trumpFile = "/trump_" + c.ordinal() + ".png";
            Image trump = new Image(trumpFile);
            m.put(c, trump);
        }
    }

    /**
     * Creates the trick pane of the graphical interface.
     * 
     * @param trickBean
     *            trick bean
     * @return the grid pane of the trick
     */
    private GridPane createTrickPane(TrickBean trickBean) {
        GridPane trickPane = new GridPane();
        trickPane.setStyle("-fx-background-color: whitesmoke;\n"
                + "-fx-padding: 5px;\n" + "-fx-border-width: 3px 0px;\n"
                + "-fx-border-style: solid;\n" + "-fx-border-color: gray;\n"
                + "-fx-alignment: center;");

        ObservableMap<Card.Color, Image> trumps = javafx.collections.FXCollections
                .observableHashMap();
        ObservableMap<Card, Image> cards = javafx.collections.FXCollections
                .observableHashMap();
        fillImageMap(cards);
        fillTrumpMap(trumps);

        ObjectBinding<Image> t = Bindings.valueAt(trumps,
                trickBean.trumpProperty());
        ImageView trumpView = new ImageView(t.get());
        trumpView.setFitWidth(TRUMP_WIDTH);
        trumpView.setFitHeight(TRUMP_HEIGHT);
        trumpView.imageProperty().bind(t);
        trickPane.add(trumpView, 1, 1);
        GridPane.setHalignment(trumpView, HPos.CENTER);

        List<PlayerId> arrangedPlayers = new LinkedList<>();
        arrangedPlayers.addAll(PlayerId.ALL);
        Collections.rotate(arrangedPlayers, PlayerId.COUNT - player.ordinal());

        List<VBox> playerCards = new LinkedList<>();

        for (PlayerId p : arrangedPlayers) {
            ObjectBinding<Card> v = Bindings.valueAt(trickBean.trick(), p);
            ObjectBinding<Image> o = Bindings.valueAt(cards, v);
            ImageView cardView = new ImageView(o.get());
            cardView.setFitWidth(TRICK_CARD_WIDTH);
            cardView.setFitHeight(TRICK_CARD_HEIGHT);
            cardView.imageProperty().bind(o);

            Rectangle halo = new Rectangle();
            halo.setWidth(TRICK_CARD_WIDTH);
            halo.setHeight(TRICK_CARD_HEIGHT);
            halo.setStyle("-fx-arc-width: 20;\n" + "-fx-arc-height: 20;\n"
                    + "-fx-fill: transparent;\n" + "-fx-stroke: lightpink;\n"
                    + "-fx-stroke-width: 5;\n" + "-fx-opacity: 0.5;");
            halo.setEffect(new GaussianBlur(gaussianRadius));
            halo.visibleProperty()
                    .bind(trickBean.winningPlayerProperty().isEqualTo(p));
            StackPane playedCard = new StackPane(halo, cardView);

            Text playerName = new Text(playerNames.get(p));
            playerName.setStyle("-fx-font: 14 Optima;");
            VBox playerCard;
            playerCard = p == player ? new VBox(playedCard, playerName)
                    : new VBox(playerName, playedCard);
            playerCard
                    .setStyle("-fx-padding: 5px;\n" + "-fx-alignment: center;");

            playerCards.add(playerCard);

        }

        trickPane.add(playerCards.get(0), 1, 2);
        trickPane.add(playerCards.get(1), 2, 0, 1, 3);
        trickPane.add(playerCards.get(2), 1, 0);
        trickPane.add(playerCards.get(3), 0, 0, 1, 3);

        return trickPane;
    }

    /**
     * Creates the victory panes of the teams where one is only visible once the
     * game is over.
     * 
     * @param scoreBean
     *            score bean
     * @return a list of the victory border panes
     */
    private List<BorderPane> createVictoryPanes(ScoreBean scoreBean) {
        List<BorderPane> victoryPanes = new LinkedList<>();
        String[] winningPlayers = new String[TeamId.COUNT];
        StringExpression[] winString = new StringExpression[TeamId.COUNT];
        Text[] winningText = new Text[TeamId.COUNT];
        BorderPane[] victoryPane = new BorderPane[TeamId.COUNT];

        for (int i = 0; i < TeamId.COUNT; i++) {
            TeamId team = i == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2;
            winningText[i] = new Text();
            List<PlayerId> players = getPlayers(team);
            winningPlayers[i] = playerNames.get(players.get(0)) + " et "
                    + playerNames.get(players.get(1));
            ReadOnlyIntegerProperty winningPoints = scoreBean
                    .totalPointsProperty(team);
            ReadOnlyIntegerProperty losingPoints = scoreBean
                    .totalPointsProperty(team.other());
            winString[i] = Bindings.format(
                    winningPlayers[i] + " ont gagné avec %d points contre %d.",
                    winningPoints, losingPoints);
            winningText[i].textProperty().bind(winString[i]);

            victoryPane[i] = new BorderPane(winningText[i]);
            victoryPane[i].setStyle(
                    "-fx-font: 16 Optima;\n" + "-fx-background-color: white;");
            victoryPanes.add(victoryPane[i]);
        }

        return victoryPanes;
    }

    /**
     * Creates the hand pane of the graphical interface.
     * 
     * @param handBean
     *            hand bean
     * @return an HBox of the player's current hand where only the playable
     *         cards are in full opacity
     */
    private HBox createHandPane(HandBean handBean) {
        HBox handPane = new HBox();
        handPane.setStyle("-fx-background-color: lightgray;\n"
                + "-fx-spacing: 5px;\n" + "-fx-padding: 5px;");

        ObservableMap<Card, Image> handCards = javafx.collections.FXCollections
                .observableHashMap();
        fillHandImageMap(handCards);

        for (int i = 0; i < Jass.HAND_SIZE; i++) {
            int cardPos = i;
            ObjectBinding<Card> card = Bindings.valueAt(handBean.hand(),
                    cardPos);
            ObjectBinding<Image> image = Bindings.valueAt(handCards, card);
            BooleanBinding isPlayable = Bindings.createBooleanBinding(
                    () -> handBean.playableCardsProperty()
                            .contains(handBean.hand().get(cardPos)),
                    handBean.hand(), handBean.playableCardsProperty());
            ImageView cardView = new ImageView(image.get());
            cardView.setFitWidth(HAND_CARD_WIDTH);
            cardView.setFitHeight(HAND_CARD_HEIGHT);
            cardView.imageProperty().bind(image);
            cardView.setOnMouseClicked((x) -> {
                try {
                    queue.put(card.get());
                } catch (InterruptedException e) {
                    throw new Error();
                }
            });
            cardView.opacityProperty().bind(
                    Bindings.when(isPlayable).then(visOp).otherwise(blurOp));
            cardView.disableProperty().bind(isPlayable.not());
            handPane.getChildren().add(cardView);
        }

        return handPane;

    }

    /**
     * Creates the stage of the graphical interface (executed on the JavaFX
     * application thread).
     * 
     * @return the stage of the graphical interface
     */
    public Stage createStage() {
        Scene s = new Scene(gamePanes);
        Stage stage = new Stage();
        stage.setScene(s);
        stage.setTitle("Javass - " + playerNames.get(player));
        return stage;

    }
}
