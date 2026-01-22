package model.player.bot;

import model.deck.Card;
import model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a bot player in the game. The bot implements the {@link Player} interface
 * and provides its own behavior for playing cards, passing turns, and managing its hand.
 */
public final class Bot implements Player {

    private final List<Card> hand = new ArrayList<>();
    private final Random r = new Random();

    @Override
    public Card playCard() {
        return hand.get(r.nextInt(0, hand.size()));
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public List<Card> getHand() {
        return List.copyOf(hand);
    }

    @Override
    public void addCards(final List<Card> cards) {
        hand.addAll(cards);
    }

    @Override
    public boolean passTurn() {
        return false;
    }
}
