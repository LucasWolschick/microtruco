package microtruco.games.entities;

import java.io.Serializable;

public record Card(Suit suit, Rank rank) implements Comparable<Card>, Serializable {
    public static enum Rank {
        HIDDEN, FOUR, FIVE, SIX, SEVEN, QUEEN, JACK, KING, ACE, TWO, THREE;

        public Rank next() {
            var values = values();
            var nextIndex = (ordinal() + 1) % values.length;
            if (nextIndex == 0) {
                nextIndex++;
            }
            return values[nextIndex];
        }
    }

    public static enum Suit {
        HIDDEN, DIAMONDS, SPADES, HEARTS, CLUBS;
    }

    public static final Card HIDDEN_CARD = new Card(Suit.HIDDEN, Rank.HIDDEN);

    @Override
    public int compareTo(Card other) {
        var suitComparison = suit.compareTo(other.suit);
        if (suitComparison != 0) {
            return suitComparison;
        }
        return rank.compareTo(other.rank);
    }

    public int compareToWithTrump(Card other, Rank trump) {
        if (rank == trump && other.rank == trump) {
            return suit.compareTo(other.suit);
        } else if (rank == trump) {
            return 1;
        } else if (other.rank == trump) {
            return -1;
        } else {
            return rank.compareTo(other.rank);
        }
    }
}
