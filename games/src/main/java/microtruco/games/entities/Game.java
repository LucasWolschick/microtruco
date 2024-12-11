package microtruco.games.entities;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Game implements Serializable {
    private List<Long> players;

    // game state
    private int teamAScore;
    private int teamBScore;
    private Round currentRound;
    private int startingPlayer;

    public Game(List<Long> players) {
        this.players = players;
        this.teamAScore = 0;
        this.teamBScore = 0;
        this.currentRound = new Round(players, startingPlayer, teamAScore, teamBScore);
        this.startingPlayer = 0;
    }

    @JsonIgnore
    public Trick.PlayerActions getActions() {
        return this.currentRound.getActions();
    }

    public GameResult getGameResult() {
        if (this.teamAScore >= 12) {
            return GameResult.TEAM1;
        } else if (this.teamBScore >= 12) {
            return GameResult.TEAM2;
        } else {
            return GameResult.IN_PROGRESS;
        }
    }

    private GameResult getRoundResult() {
        return this.currentRound.getRoundResult();
    }

    public void applyAction(int player, Trick.TrickAction action) {
        if (getGameResult() != GameResult.IN_PROGRESS) {
            throw new IllegalStateException("Game is already over");
        }

        currentRound.applyAction(player, action);
        if (getRoundResult() != GameResult.IN_PROGRESS) {
            // we've finished the round, add score
            switch (this.getRoundResult()) {
                case TEAM1:
                    this.teamAScore += currentRound.getRoundValue();
                    break;
                case TEAM2:
                    this.teamBScore += currentRound.getRoundValue();
                    break;
                default:
                    // tie, no points
                    break;
            }
            // if the game is over, don't start a new round
            if (getGameResult() == GameResult.IN_PROGRESS) {
                this.startingPlayer = (this.startingPlayer + 1) % players.size();
                this.currentRound = new Round(players, startingPlayer, teamAScore, teamBScore);
            }
        }
    }

    public void hideHand(int playerIndex) {
        this.currentRound.hideHand(playerIndex);
    }

    public void hideHandsForPlayer(int playerIndex) {
        for (var i = 0; i < players.size(); i++) {
            if (i != playerIndex) {
                hideHand(i);
            }
        }
    }

    public int getTeamAScore() {
        return teamAScore;
    }

    public int getTeamBScore() {
        return teamBScore;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public int getStartingPlayer() {
        return startingPlayer;
    }
}
