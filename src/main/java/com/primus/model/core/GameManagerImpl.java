package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.model.player.Player;
import com.primus.utils.GameState;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
// TODO aggiungere i veri import
// import com.primus.model.player.PlayerFactory;
// import com.primus.model.player.HumanPlayer;
// import com.primus.model.deck.Deck;
// import com.primus.model.deck.DiscardPile;

/**
 * Implementation of {@link GameManager} to manage the game flow. It offers an API
 * to initialize the game, advance turns, execute player actions, and check for game completion to the
 * {@link com.primus.controller.GameController}
 */
public final class GameManagerImpl implements GameManager {
    private static final int CARD_NUMBER = 7;

    private final List<Player> players;
    private final Deck deck;
    private final DiscardPile discardPile;
    private final Sanctioner sanctioner;
    private Scheduler scheduler;
    private Player activePlayer;

    /**
     * Constructor initializes the game manager with necessary components.
     */
    public GameManagerImpl() {
        this.deck = new DeckImpl();
        this.discardPile = new DiscardPileimpl();
        this.sanctioner = new SanctionerStub();
        this.players = new ArrayList<>();
        this.init(); // Ensure the game is initialized upon creation
    }

    @Override
    public void init() {
        this.players.clear();
        this.discardPile.clear();
        this.deck.reset();
        this.activePlayer = null;
        this.sanctioner.reset();

        // TODO: creare in modo corretto i giocatori quando esisterà il metodo per farlo
        // Create human player
        // Player human = new HumanPlayer("Giocatore Umano");
        // this.players.add(human);

        // Create bot players
        // this.players.add(PlayerFactory.createBot(Type, "Bot 1"));
        // this.players.add(PlayerFactory.createBot(Type, "Bot 2"));
        // this.players.add(PlayerFactory.createBot(Type, "Bot 3"));

        // Create the scheduler by passing the players to it
        this.scheduler = new SchedulerImpl(this.players);

        // Distribute cards
        for (final Player p : this.players) {
            for (int i = 0; i < CARD_NUMBER; i++) {
                final Card c = this.deck.draw();
                p.addCards(List.of(c));
            }
        }

        // Draw the start card
        this.discardPile.add(this.deck.draw());
    }

    @Override
    public GameState getGameState() {
        if (this.activePlayer == null) {
            return new GameState(this.discardPile.getTopCard(), scheduler.peekNextPlayer().getHand());
        }
        return new GameState(this.discardPile.getTopCard(), this.activePlayer.getHand());
    }

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "Player is a public API interface - intentional exposure"
    )
    @Override
    public Player nextPlayer() {
        this.activePlayer = scheduler.nextPlayer();
        return this.activePlayer;
    }

    @Override
    public boolean resolvePreTurnMalus() {
        Objects.requireNonNull(this.activePlayer);

        if (sanctioner.hasPendingMalus()) {
            final int amount = sanctioner.getCarteDaAggiungere();

            // Apply malus
            for (int i = 0; i < amount; i++) {
                drawCardForPlayer(this.activePlayer);
            }

            // Reset sanctioner
            sanctioner.reset();

            // Skip turn for the player due to malus
            scheduler.skipTurn();

            return true;
        }
        return false;
    }

    @Override
    public boolean executeTurn(final Player player, final Card card) {
        Objects.requireNonNull(player);

        // User chooses to draw a card
        if (card == null) {
            drawCardForPlayer(player);
            return true;
        }

        // User plays a card, so it must be validated
        if (!validateCard(card, player)) {
            player.notifyMoveResult(card, false);
            return false;
        }

        // Confirm the move and apply effects
        player.notifyMoveResult(card, true);
        this.discardPile.add(card);

        applyCardEffects(card);

        return true;
    }

    @Override
    public Optional<Player> getWinner() {
        for (final Player p : players) {
            if (p.getHand().isEmpty()) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /**
     * Validates if the played card can be legally played.
     *
     * @param cardCandidate the card being played
     * @param player        the player attempting to play the card
     * @return `True` if the card can be played
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean validateCard(final Card cardCandidate, final Player player) {
        // TODO gestire quando si avrà il vero validator
        final Card topCard = this.discardPile.getTopCard();
        // Logica stub
        if (cardCandidate.getColor() == topCard.getColor()
                || cardCandidate.getValue() == topCard.getValue()) {
            return true;
        }
        return cardCandidate.getColor().name().contains("WILD");
    }

    /**
     * Draws a card from the deck and adds it to the player's hand.
     *
     * @param p the player drawing the card
     */
    private void drawCardForPlayer(final Player p) {
        Objects.requireNonNull(p);

        final Card c = this.deck.draw();
        if (c != null) {
            p.addCards(List.of(c));
        }
    }

    /**
     * Applies the effects of the played card to the game state.
     *
     * @param card the card whose effects are to be applied
     */
    private void applyCardEffects(final Card card) {
        //TODO gestire quando si avrà il sanctioner
        Objects.requireNonNull(card);
        final String val = Objects.requireNonNull(card.getValue().name());

        if ("SKIP".equals(val)) {
            scheduler.skipTurn();
        } else if ("REVERSE".equals(val)) {
            scheduler.reverseDirection();
        }

        sanctioner.evaluateCard(card);
    }

    // Necessary development stubs, development only

    public interface Deck {

        Card draw();

        void reset();
    }

    private final class DeckImpl implements Deck {

        @Override
        public Card draw() {
            return null;
        }

        @Override
        public void reset() {
        }
    }

    public interface DiscardPile {

        void add(Card firstCard);

        Card getTopCard();

        void clear();
    }

    private final class DiscardPileimpl implements DiscardPile {

        @Override
        public void add(Card firstCard) {
        }

        @Override
        public Card getTopCard() {
            return null;
        }

        @Override
        public void clear() {
        }
    }

    public interface Sanctioner {
        boolean hasPendingMalus();

        int getCarteDaAggiungere();

        void evaluateCard(Card card);

        void reset();
    }

    private final class SanctionerStub implements Sanctioner {
        private int count = 0;

        public boolean hasPendingMalus() {
            return count > 0;
        }

        public int getCarteDaAggiungere() {
            return count;
        }

        public void reset() {
            count = 0;
        }

        public void evaluateCard(Card c) {
            if (c.getValue().name().equals("DRAW_TWO")) count += 2;
            if (c.getValue().name().equals("WILD_DRAW_FOUR")) count += 4;
        }
    }
}
