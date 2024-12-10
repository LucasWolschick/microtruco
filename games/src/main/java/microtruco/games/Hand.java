/**
 * Represents a hand of cards in the game.
 */
package microtruco.games;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a hand of cards.
 */
public class Hand {
    private final List<Card> cards;

    /**
     * Creates an empty hand.
     */
    public Hand() {
        cards = List.of();
    }

    /**
     * Creates a hand with the specified list of cards.
     *
     * @param cards the list of cards to initialize the hand with
     */
    public Hand(List<Card> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * Returns the list of cards in the hand.
     *
     * @return the list of cards in the hand
     */
    public List<Card> getCards() {
        return cards;
    }

    /**
     * Adds a card to the hand, maintaining the order.
     *
     * @param card the card to add
     * @return a new Hand instance with the card added
     */
    public Hand add(Card card) {
        List<Card> cards = new ArrayList<>();
        int index = 0;
        while (index < this.cards.size() && card.compareTo(this.cards.get(index)) >= 0) {
            cards.add(this.cards.get(index));
            index++;
        }
        cards.add(card);
        cards.addAll(this.cards.subList(index, this.cards.size()));
        return new Hand(cards);
    }

    /**
     * Removes a card from the hand.
     *
     * @param card the card to remove
     * @return a new Hand instance with the card removed
     */
    public Hand remove(Card card) {
        List<Card> cards = new ArrayList<>();
        for (Card c : this.cards) {
            if (!c.equals(card)) {
                cards.add(c);
            }
        }
        return new Hand(cards);
    }
}