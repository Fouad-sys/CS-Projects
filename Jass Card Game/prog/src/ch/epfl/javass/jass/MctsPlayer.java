package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * The class represents a simulated player. The class uses the Monte Carlo Tree
 * Search for the realization of the player. The possible evolution of the game
 * is simulated. The states are added to the tree of the game evolution and are
 * evaluated. Based on this evaluation the player chooses his best card to play.
 *
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class MctsPlayer implements Player {

    private static final int C_TERM = 40;

    private final PlayerId ownId;
    private final SplittableRandom cardRng;
    private final int iterations;

    /**
     * Creates an MctsPlayer given a identification of the player, a single seed
     * and the number of iterations.
     * 
     * @param ownId
     *            the identification of the player, its PlayerId
     * @param rngSeed
     *            single seed for random simulations
     * @param iterations
     *            the number of iterations of the simulation
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        this.ownId = ownId;
        this.cardRng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    private static final class Node {
        
        private TurnState nodeState;
        private Node[] expandedCards;
        private CardSet notExpandedCards;
        
        private int totalPoints;
        private int numberOfTurns;
        
        private Node(TurnState state, CardSet cards) {
            
            this.nodeState = state;
            this.expandedCards = new Node[cards.size()];
            this.notExpandedCards = cards;
            
            this.totalPoints = 0;
            this.numberOfTurns = 0;
        }

        private int bestChild(int c) {

            int size = expandedCards.length - notExpandedCards.size();
            if (size < expandedCards.length) {
                return size;
            }

            double comTerm = 2.0 * Math.log((double) numberOfTurns);
            double avgTerm, expTerm;
            int turns, points;
            
            int bestIndex = 0;
            double bestValue = 0;
            double nextValue;

            for (int i = 0; i < size; i++) {
                turns = expandedCards[i].numberOfTurns;
                points = expandedCards[i].totalPoints;
                
                avgTerm = points / turns;
                expTerm = c * Math.sqrt(comTerm / turns);
                
                nextValue = avgTerm + expTerm;

                if (nextValue > bestValue) {
                    bestValue = nextValue;
                    bestIndex = i;
                }
            }

            return bestIndex;
        }

        private boolean isTerminal() {
            return nodeState.isTerminal();
        }

    }

    private long randomScore(Node node, CardSet hand) {

        TurnState randomState = node.nodeState;
        PlayerId player;
        CardSet cards;
        Card card;

        while (!randomState.isTerminal()) {
            player = randomState.nextPlayer();
            cards = playableCards(randomState, player, hand);
            card = cards.get(cardRng.nextInt(cards.size()));
            randomState = randomState.withNewCardPlayedAndTrickCollected(card);
        }

        return randomState.packedScore();
    }
    
    private List<Node> addNewNode(Node root, CardSet hand) {

        int bestIndex;
        int expandIndex = root.notExpandedCards.size();
        
        List<Node> nodePath = new ArrayList<>();
        nodePath.add(root);
        Node parentNode = root;
        
        while (expandIndex == 0) {
            if (parentNode.isTerminal())
                return nodePath;
            
            bestIndex = parentNode.bestChild(C_TERM);
            parentNode = parentNode.expandedCards[bestIndex];
            expandIndex = parentNode.notExpandedCards.size();
            nodePath.add(parentNode);
        }

        Card cardToExpand = parentNode.notExpandedCards.get(0);
        TurnState childState = parentNode.nodeState
                .withNewCardPlayedAndTrickCollected(cardToExpand);
        CardSet notExpCards;

        if (childState.isTerminal()) {
            notExpCards = CardSet.EMPTY;
        } else {
            notExpCards = playableCards(childState,
                    childState.nextPlayer(), hand);
        }
        Node childNode = new Node(childState, notExpCards);
        
        nodePath.add(childNode);

        int size = parentNode.expandedCards.length - parentNode.notExpandedCards.size();
        parentNode.expandedCards[size] = childNode;
        parentNode.notExpandedCards = parentNode.notExpandedCards
                .remove(cardToExpand);

        return nodePath;
    }
    
    private CardSet playableCards(TurnState state, PlayerId playerId,
            CardSet hand) {
        
        CardSet cards = state.unplayedCards();
        CardSet cardsInHand = cards.intersection(hand);

        if (this.ownId == playerId)
            return state.trick().playableCards(cardsInHand);
        
        return state.trick().playableCards(cards.difference(hand));
    }
    
    private void propagateScore(List<Node> nodePath, CardSet hand) {

        long score;
        TeamId team;
        Node node;
        Node parentNode;
        Node rootNode = nodePath.get(0);
        Node lastNode = nodePath.get(nodePath.size() - 1);
        
        if (lastNode.isTerminal())
            score = lastNode.nodeState.packedScore();
        else
            score = randomScore(lastNode, hand);
        
        for (int i = 1; i < nodePath.size(); i++) {
            node = nodePath.get(i);
            parentNode = nodePath.get(i - 1);
            team = parentNode.nodeState.nextPlayer().team();
            
            node.totalPoints += PackedScore.turnPoints(score, team);
            node.numberOfTurns += 1;
        }
        
        rootNode.numberOfTurns += 1;
    }

    /* Chooses the card to play
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        List<Node> nodePath = new ArrayList<>();
        CardSet cards = state.trick().playableCards(hand);
        Node root = new Node(state, cards);
        CardSet cardsToPlay = root.notExpandedCards;

        for (int i = 0; i < iterations; i++) {
            nodePath = addNewNode(root, hand);
            propagateScore(nodePath, hand);
        }

        int bestIndex = root.bestChild(0);        
        return cardsToPlay.get(bestIndex);
    }

}
