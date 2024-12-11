package microtruco.games.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class Trick implements Serializable {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TrickState.PlayerTurn.class, name = "PlayerTurn"),
            @JsonSubTypes.Type(value = TrickState.ElevenHand.class, name = "ElevenHand"),
            @JsonSubTypes.Type(value = TrickState.Challenge.class, name = "Challenge"),
            @JsonSubTypes.Type(value = TrickState.Over.class, name = "Over"),
    })
    public static sealed interface TrickState extends Serializable {
        public record PlayerTurn(int player) implements TrickState {
        }

        public record ElevenHand() implements TrickState {
        }

        public record Challenge(int challenger, int challenged, int worth) implements TrickState {
        }

        public record Over(int winningTeam, int winningPlayer, int points) implements TrickState {
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TrickAction.PlayCard.class, name = "PlayCard"),
            @JsonSubTypes.Type(value = TrickAction.PlayHiddenCard.class, name = "PlayHiddenCard"),
            @JsonSubTypes.Type(value = TrickAction.Bet.class, name = "Bet"),
            @JsonSubTypes.Type(value = TrickAction.Call.class, name = "Call"),
            @JsonSubTypes.Type(value = TrickAction.Fold.class, name = "Fold")
    })
    public static sealed interface TrickAction extends Serializable {
        public record PlayCard(Card card) implements TrickAction {
        }

        public record PlayHiddenCard(Card card) implements TrickAction {
        }

        public record Bet(int value) implements TrickAction {
        }

        public record Call() implements TrickAction {
        }

        public record Fold() implements TrickAction {
        }
    }

    public static record PlayerActions(int player, List<TrickAction> actions) implements Serializable {
    }

    private final List<TrickState> state;
    private final int startingPlayer;
    private final List<Hand> hands;
    private final List<Card> table;
    private final List<Long> players;
    private final Card flip;
    private final boolean isFirstTrick;
    private final int trickValue;

    private Trick(List<TrickState> state, List<Hand> hands, List<Card> table, List<Long> players, Card flip,
            boolean isFirstTrick, int trickValue, int startedAt) {
        this.state = state;
        this.startingPlayer = startedAt;
        this.hands = hands;
        this.table = table;
        this.players = players;
        this.flip = flip;
        this.isFirstTrick = isFirstTrick;
        this.trickValue = trickValue;
    }

    public Trick(List<Long> players, List<Hand> hands, Card flip, int startingPlayer, boolean isFirstTrick,
            int trickValue) {
        this.state = List.of(new TrickState.PlayerTurn(startingPlayer));
        this.startingPlayer = startingPlayer;
        this.hands = hands;
        this.table = List.of();
        this.players = players;
        this.flip = flip;
        this.isFirstTrick = isFirstTrick;
        this.trickValue = trickValue;
    }

    public Trick pushState(TrickState newState) {
        List<TrickState> newStateList = new ArrayList<>(state);
        newStateList.add(newState);
        return new Trick(List.copyOf(newStateList), hands, table, players, flip, isFirstTrick, trickValue,
                startingPlayer);
    }

    public Trick popState() {
        List<TrickState> newStateList = new ArrayList<>(state);
        newStateList.remove(newStateList.size() - 1);
        return new Trick(List.copyOf(newStateList), hands, table, players, flip, isFirstTrick, trickValue,
                startingPlayer);
    }

    public Trick replaceState(TrickState newState) {
        List<TrickState> newStateList = new ArrayList<>(state);
        newStateList.set(newStateList.size() - 1, newState);
        return new Trick(List.copyOf(newStateList), hands, table, players, flip, isFirstTrick, trickValue,
                startingPlayer);
    }

    public Trick withTrickValue(int trickValue) {
        return new Trick(state, hands, table, players, flip, isFirstTrick, trickValue, startingPlayer);
    }

    public Trick withCardPlayed(int player, Card card) {
        var newHands = new ArrayList<>(hands);
        newHands.set(player, hands.get(player).remove(card));
        var newTable = new ArrayList<>(table);
        newTable.add(card);
        return new Trick(state, List.copyOf(newHands), List.copyOf(newTable), players, flip, isFirstTrick, trickValue,
                startingPlayer);
    }

    public Trick withHiddenCardPlayed(int player, Card card) {
        var newHands = new ArrayList<>(hands);
        newHands.set(player, hands.get(player).remove(card));
        var newTable = new ArrayList<>(table);
        newTable.add(Card.HIDDEN_CARD);
        return new Trick(state, List.copyOf(newHands), List.copyOf(newTable), players, flip, isFirstTrick, trickValue,
                startingPlayer);
    }

    @JsonIgnore
    public PlayerActions getActions() {
        int playerToDecide;
        List<TrickAction> actions;

        switch (state.getLast()) {
            case TrickState.Over(int _, int _, int _):
                actions = List.of();
                playerToDecide = 0;
                break;
            case TrickState.ElevenHand():
                actions = List.of(
                        new TrickAction.Call(),
                        new TrickAction.Fold());
                // Assertion: there is a previous element on the stack, and it is PlayerTurn
                playerToDecide = ((TrickState.PlayerTurn) state.get(state.size() - 2)).player();
                break;
            case TrickState.Challenge(int _, int challenged, int worth): {
                List<TrickAction> plrActions = new ArrayList<>();
                switch (worth) {
                    case 3:
                        plrActions.add(new TrickAction.Bet(6));
                    case 6:
                        plrActions.add(new TrickAction.Bet(9));
                    case 9:
                        plrActions.add(new TrickAction.Bet(12));
                        break;
                    default:
                        break;
                }
                plrActions.add(new TrickAction.Call());
                plrActions.add(new TrickAction.Fold());
                actions = List.copyOf(plrActions);
                playerToDecide = challenged;
                break;
            }
            case TrickState.PlayerTurn(int currentPlayer): {
                List<TrickAction> plrActions = new ArrayList<>();

                // can play all cards from the deck
                for (var card : hands.get(currentPlayer).getCards()) {
                    plrActions.add(new TrickAction.PlayCard(card));
                }

                // if we aren't in the first turn, we can play any card from the deck,
                // hidden
                if (!isFirstTrick) {
                    for (var card : hands.get(currentPlayer).getCards()) {
                        plrActions.add(new TrickAction.PlayHiddenCard(card));
                    }
                }

                // can bet at any point if we're not up to 12 points
                switch (trickValue) {
                    case 1:
                        plrActions.add(new TrickAction.Bet(3));
                        break;
                    case 3:
                        plrActions.add(new TrickAction.Bet(6));
                    case 6:
                        plrActions.add(new TrickAction.Bet(9));
                    case 9:
                        plrActions.add(new TrickAction.Bet(12));
                        break;
                }

                // can fold at any point
                plrActions.add(new TrickAction.Fold());

                actions = plrActions;
                playerToDecide = currentPlayer;
                break;
            }
        }

        return new PlayerActions(playerToDecide, actions);

    }

    public Trick applyAction(int player, TrickAction action) {
        switch (state.getLast()) {
            case TrickState.Over(int _, int _, int _):
                return this;
            case TrickState.ElevenHand():
                switch (action) {
                    case TrickAction.Call():
                        return popState().withTrickValue(3);
                    case TrickAction.Fold():
                        return pushState(new TrickState.Over(player % 2 + 1, player, trickValue));
                    default:
                        throw new IllegalArgumentException("Invalid action for state");
                }
            case TrickState.Challenge(int challenger, int _, int worth):
                switch (action) {
                    case TrickAction.Bet(int value): {
                        // value must be a multiple of 3, is greater than the current worth
                        // and is less than or equal to 12
                        if (value % 3 != 0 || value <= worth || value > 12) {
                            throw new IllegalArgumentException("Invalid bet value");
                        }
                        return replaceState(new TrickState.Challenge(player, challenger, value))
                                .withTrickValue(worth);
                    }
                    case TrickAction.Call():
                        return popState().withTrickValue(worth);
                    case TrickAction.Fold():
                        return pushState(new TrickState.Over(challenger % 2 + 1, challenger, trickValue));
                    default:
                        throw new IllegalArgumentException("Invalid action for state");
                }
            case TrickState.PlayerTurn(int currentPlayer):
                switch (action) {
                    case TrickAction.PlayCard(Card card): {
                        // card must be in the player's hand
                        if (!hands.get(currentPlayer).getCards().contains(card)) {
                            throw new IllegalArgumentException("Invalid card");
                        }

                        var newTable = new ArrayList<>(table);
                        newTable.add(card);

                        if (newTable.size() == players.size()) {
                            // trick is over
                            // find out argmax of best card
                            int bestCardIndex = 0;
                            boolean tied = false;
                            for (int i = 1; i < newTable.size(); i++) {
                                var thisCard = newTable.get(i);
                                var bestCard = newTable.get(bestCardIndex);
                                var comparison = thisCard.compareToWithTrump(bestCard, flip.rank());
                                if (comparison > 0) {
                                    bestCardIndex = i;
                                    tied = false;
                                } else if (comparison == 0) {
                                    tied = true;
                                }
                            }

                            if (tied) {
                                // trick is a draw
                                return pushState(new TrickState.Over(0, 0, trickValue));
                            } else {
                                // trick is won by the best card
                                return pushState(
                                        new TrickState.Over((startingPlayer + bestCardIndex) % 2 + 1,
                                                (startingPlayer + bestCardIndex) % players.size(), trickValue));
                            }
                        } else {
                            // more cards to play
                            return replaceState(new TrickState.PlayerTurn((currentPlayer + 1) % players.size()))
                                    .withCardPlayed(currentPlayer, card);
                        }
                    }
                    case TrickAction.PlayHiddenCard(Card card): {
                        // card must be in the player's hand
                        if (!hands.get(currentPlayer).getCards().contains(card)) {
                            throw new IllegalArgumentException("Invalid card");
                        }

                        var newTable = new ArrayList<>(table);
                        newTable.add(Card.HIDDEN_CARD);

                        if (newTable.size() == players.size()) {
                            // trick is over
                            // find out argmax of best card
                            int bestCardIndex = 0;
                            boolean tied = false;
                            for (int i = 1; i < newTable.size(); i++) {
                                var thisCard = newTable.get(i);
                                var bestCard = newTable.get(bestCardIndex);
                                var comparison = thisCard.compareToWithTrump(bestCard, flip.rank());
                                if (comparison > 0) {
                                    bestCardIndex = i;
                                    tied = false;
                                } else if (comparison == 0) {
                                    tied = true;
                                }
                            }

                            if (tied) {
                                // trick is a draw
                                return pushState(new TrickState.Over(0, 0, trickValue));
                            } else {
                                // trick is won by the best card
                                return pushState(
                                        new TrickState.Over((startingPlayer + bestCardIndex) % 2 + 1,
                                                (startingPlayer + bestCardIndex) % players.size(), trickValue));
                            }
                        } else {
                            // more cards to play
                            return replaceState(new TrickState.PlayerTurn((currentPlayer + 1) % players.size()))
                                    .withCardPlayed(currentPlayer, card);
                        }
                    }
                    case TrickAction.Bet(int value): {
                        // value must be 3 for the first bet
                        if (trickValue == 1 && value != 3) {
                            throw new IllegalArgumentException("Invalid bet value");
                        }

                        // value must be a multiple of 3, is greater than the current worth
                        // and is less than or equal to 12
                        if (value % 3 != 0 || value <= trickValue || value > 12) {
                            throw new IllegalArgumentException("Invalid bet value");
                        }

                        return pushState(
                                new TrickState.Challenge(currentPlayer, (currentPlayer + 1) % players.size(), value));
                    }
                    case TrickAction.Fold():
                        return pushState(new TrickState.Over((currentPlayer + 1) % 2 + 1, currentPlayer, trickValue));
                    default:
                        throw new IllegalArgumentException("Invalid action for state");
                }
        }
    }

    public enum TrickResult {
        DRAW, PLAYER0, PLAYER1, PLAYER2, PLAYER3, IN_PROGRESS;

        public GameResult toGameResult() {
            switch (this) {
                case DRAW:
                    return GameResult.DRAW;
                case PLAYER0:
                case PLAYER2:
                    return GameResult.TEAM1;
                case PLAYER1:
                case PLAYER3:
                    return GameResult.TEAM2;
                case IN_PROGRESS:
                    return GameResult.IN_PROGRESS;
                default:
                    throw new IllegalArgumentException("Invalid trick result");
            }
        }

    }

    @JsonIgnore
    public TrickResult getResult() {
        if (state.getLast() instanceof TrickState.Over over) {
            if (over.winningPlayer() == 0) {
                return TrickResult.PLAYER0;
            } else if (over.winningPlayer() == 1) {
                return TrickResult.PLAYER1;
            } else if (over.winningPlayer() == 2) {
                return TrickResult.PLAYER2;
            } else if (over.winningPlayer() == 3) {
                return TrickResult.PLAYER3;
            } else {
                return TrickResult.DRAW;
            }
        }
        return TrickResult.IN_PROGRESS;
    }

    public int getTrickValue() {
        return trickValue;
    }

    public List<Hand> getHands() {
        return hands;
    }

    public List<TrickState> getState() {
        return state;
    }

    public List<Card> getTable() {
        return table;
    }

    public Card getFlip() {
        return flip;
    }

    public Trick hideHand(int playerIndex) {
        List<Hand> newHands = IntStream.range(0, hands.size())
                .mapToObj(i -> i == playerIndex ? hands.get(i).hide() : hands.get(i))
                .collect(Collectors.toUnmodifiableList());
        return new Trick(state, newHands, table, players, flip, isFirstTrick, trickValue, startingPlayer);
    }
}