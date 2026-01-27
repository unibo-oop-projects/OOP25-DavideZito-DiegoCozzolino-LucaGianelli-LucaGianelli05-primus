package model.player.bot;

import model.deck.Card;
import model.player.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Represents a bot player in the game. The bot implements the {@link Player} interface
 * and provides its own behavior for playing cards, passing turns, and managing its hand.
 */

public final class Bot implements Player {

    private final List<Card> hand = new ArrayList<>();
    private final Set<Card> rejectedCards = new LinkedHashSet<>();
    private final Random r = new Random();
    //tiene traccia di quante carte ha provato a giocare se è uguale a al numero che ha in mano implica che
    // bisogna passare il turno
    private Card currentTriedCard;
    private final int id;

    /**
     * Constructs a new Bot instance with a unique identifier.
     *
     * @param id the unique identifier for this bot
     */
    public Bot(final int id) {
        this.id = id;
    }

    @Override
    public Card playCard() {
        currentTriedCard = hand.get(r.nextInt(0, hand.size()));
        return currentTriedCard;
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

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void notifyMoveResult(final boolean valid) {
        if (valid) {
            rejectedCards.add(currentTriedCard);
        } else {
            resetTurn();
        }
    }

    private void resetTurn() {
        hand.remove(currentTriedCard);
        currentTriedCard = null;
        rejectedCards.clear();
    }
}
