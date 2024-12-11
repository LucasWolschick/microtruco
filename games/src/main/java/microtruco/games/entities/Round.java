package microtruco.games.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Round implements Serializable {
    public static final int MAX_ROUNDS = 3;

    private Trick currentTrick;
    private Deck deck;
    private int currentStarter;
    private List<GameResult> results;
    private List<Long> players;

    public Round(List<Long> players, int currentStarter) {
        this.results = new ArrayList<>();
        this.deck = new Deck();
        this.currentStarter = currentStarter;
        this.players = List.copyOf(players);

        this.deck.fill();
        this.deck.shuffle();
        var hands = this.deck.deal(players.size(), 3);
        var flip = this.deck.draw();

        this.currentTrick = new Trick(this.players, hands, flip, currentStarter, true, 1);
    }

    @JsonIgnore
    public Trick.PlayerActions getActions() {
        return this.currentTrick.getActions();
    }

    private GameResult getTrickResult() {
        return this.currentTrick.getResult().toGameResult();
    }

    public void applyAction(int player, Trick.TrickAction action) {
        if (getRoundResult() != GameResult.IN_PROGRESS) {
            throw new IllegalStateException("Round is already over");
        }

        this.currentTrick = this.currentTrick.applyAction(player, action);
        if (getTrickResult() != GameResult.IN_PROGRESS) {
            // we've finished the trick, add to table
            this.results.add(getTrickResult());
            if (getRoundResult() == GameResult.IN_PROGRESS) {
                // we're not done yet, start a new trick
                switch (this.currentTrick.getResult()) {
                    case PLAYER0:
                        this.currentStarter = 0;
                        break;
                    case PLAYER1:
                        this.currentStarter = 1;
                        break;
                    case PLAYER2:
                        this.currentStarter = 2;
                        break;
                    case PLAYER3:
                        this.currentStarter = 3;
                        break;
                    default:
                        // don't change the starter if it tied
                        break;
                }
                this.currentTrick = new Trick(this.players, this.currentTrick.getHands(), this.deck.draw(),
                        this.currentStarter, false, this.currentTrick.getTrickValue());
            }
        }
    }

    @JsonIgnore
    public GameResult getRoundResult() {
        // check to see if there's a winner
        if (this.results.size() == 2) {
            // we have a winner only if the first result was a win
            // and the second result was a win or a tie
            var first = this.results.get(0);
            var second = this.results.get(1);
            if (first != GameResult.DRAW && (second == GameResult.DRAW || second == first)) {
                // first is the winner
                return first;
            }
        } else if (this.results.size() == 3) {
            var first = this.results.get(0);
            var second = this.results.get(1);
            var third = this.results.get(2);

            if (first == GameResult.DRAW) {
                if (second == GameResult.DRAW) {
                    // T T x
                    // third is the winner
                    return third;
                } else {
                    // T x _
                    // second is the winner
                    return second;
                }
            } else {
                if (second == GameResult.DRAW || second == first) {
                    // x T _
                    // x x _
                    // first is the winner
                    return first;
                } else if (third == GameResult.DRAW) {
                    // x y T
                    // first is the winner
                    return first;
                } else {
                    // x y z
                    // third is the winner
                    return third;
                }
            }
        }

        return GameResult.IN_PROGRESS;
    }

    public int getRoundValue() {
        return currentTrick.getTrickValue();
    }

    public static int getMaxRounds() {
        return MAX_ROUNDS;
    }

    public Trick getCurrentTrick() {
        return currentTrick;
    }

    public Deck getDeck() {
        return deck;
    }

    public int getCurrentStarter() {
        return currentStarter;
    }

    public List<GameResult> getResults() {
        return results;
    }

    public void hideHand(int playerIndex) {
        currentTrick = currentTrick.hideHand(playerIndex);
    }
}
