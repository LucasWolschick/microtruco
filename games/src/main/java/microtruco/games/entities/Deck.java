package microtruco.games.entities;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import microtruco.games.entities.Card.Rank;
import microtruco.games.entities.Card.Suit;

/**
 * Represents a deck of cards.
 */
public class Deck implements Serializable {

    /**
     * A deque to hold the cards in the deck.
     */
    private Deque<Card> cards;

    /**
     * Constructs an empty deck.
     */
    public Deck() {
        cards = new ArrayDeque<>();
    }

    /**
     * Fills the deck with a standard set of cards.
     * Clears any existing cards in the deck before filling.
     */
    public void fill() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (suit != Suit.HIDDEN && rank != Rank.HIDDEN)
                    cards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * Shuffles the deck.
     * Randomly rearranges the order of the cards in the deck.
     */
    public void shuffle() {
        List<Card> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards = new ArrayDeque<>(list);
    }

    /**
     * Adds a card to the deck.
     *
     * @param card the card to be added to the deck
     */
    public void add(Card card) {
        cards.add(card);
    }

    /**
     * Draws a card from the deck.
     * Removes and returns the first card from the deck.
     *
     * @return the drawn card
     * @throws NoSuchElementException if the deck is empty
     */
    public Card draw() {
        return cards.removeFirst();
    }

    /**
     * Returns the number of cards in the deck.
     *
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Deals the cards in the deck.
     */
    public List<Hand> deal(int numPlayers, int numCards) {
        List<Hand> hands = new ArrayList<>();
        for (int j = 0; j < numPlayers; j++) {
            var cards = new ArrayList<Card>();
            for (int i = 0; i < numCards; i++) {
                cards.add(draw());
            }
            hands.add(new Hand(cards));
        }
        return hands;
    }
}
