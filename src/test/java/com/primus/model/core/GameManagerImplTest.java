package com.primus.model.core;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import com.primus.model.player.Player;
import com.primus.utils.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameManagerImplTest {

    private GameManagerImpl gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManagerImpl();
        // The constructor already calls init, but we call it again to ensure a fresh state
        gameManager.init();
    }

    @Test
    @DisplayName("Test game initialization")
    void testInit() {
        final GameState state = gameManager.getGameState();
        assertNotNull(state, "GameState should not be null after initialization");
        assertNotNull(state.topCard(), "Top card should not be null after initialization");

        final Optional<Player> winner = gameManager.getWinner();
        assertTrue(winner.isEmpty(), "No winner should be present at game start");
    }

    @Test
    @DisplayName("Text turn rotation between players")
    void testNextPlayer() {
        final Player firstPlayer = gameManager.nextPlayer();
        assertNotNull(firstPlayer, "First player should not be null");

        assertEquals(7, firstPlayer.getHand().size(), "First player should have 7 cards initially");

        final Player secondPlayer = gameManager.nextPlayer();
        assertNotNull(secondPlayer, "Second player should not be null");

        assertNotEquals(firstPlayer, secondPlayer, "Second player should be different from first player");
    }

    @Test
    @DisplayName("Test draw card action")
    void testDrawCard() {
        final Player current = gameManager.nextPlayer();
        int initialHandSize = current.getHand().size();

        final Card preTurnTopCard = gameManager.getGameState().topCard();
        final boolean turnResult = gameManager.executeTurn(null); // This means drawing a card

        assertTrue(turnResult, "Turn should be successful when drawing a card");
        assertEquals(initialHandSize + 1, current.getHand().size(), "Player should have one more card after drawing");
        assertEquals(gameManager.getGameState().topCard(), preTurnTopCard, "Top card should remain unchanged after drawing");
    }

    @Test
    @DisplayName("Rule: Same Color -> Success")
    void testPlaySameColor() {
        final Player player = gameManager.nextPlayer();
        final Card top = gameManager.getGameState().topCard();

        // Create a card with the same color as the top card
        final Card validCard = new PrimusCard(top.getColor(), Values.ZERO);
        player.addCards(List.of(validCard));

        assertTrue(gameManager.executeTurn(validCard), "Should accept card with same color");
        assertEquals(validCard, gameManager.getGameState().topCard(), "Top card should update");
    }

    @Test
    @DisplayName("Rule: Same Value -> Success")
    void testPlaySameValue() {
        final Player player = gameManager.nextPlayer();
        final Card top = gameManager.getGameState().topCard();

        // Create a card with the same value but different color
        Color diffColor = (top.getColor() == Color.RED) ? Color.BLUE : Color.RED;

        final Card validCard = new PrimusCard(diffColor, top.getValue());
        player.addCards(List.of(validCard));

        assertTrue(gameManager.executeTurn(validCard), "Should accept card with same value");
        assertEquals(validCard, gameManager.getGameState().topCard(), "Top card should update");
    }

    @Test
    @DisplayName("Rule: Invalid Card -> Rejected")
    void testPlayInvalidCard() {
        gameManager.nextPlayer();
        final Card top = gameManager.getGameState().topCard();

        // Create a card with different color and different value
        final Color diffColor = (top.getColor() == Color.RED) ? Color.BLUE : Color.RED;
        final Values diffValue = (top.getValue() == Values.ZERO) ? Values.ONE : Values.ZERO;

        final Card invalidCard = new PrimusCard(diffColor, diffValue);

        assertFalse(gameManager.executeTurn(invalidCard), "Should reject invalid card");

        assertEquals(top, gameManager.getGameState().topCard(), "Top card should remain unchanged after invalid play");
    }

    @Test
    @DisplayName("Rule: Play Wild Card -> Success")
    void testPlayWildCard() {
        final Player player = gameManager.nextPlayer();

        final Card wildCard = new PrimusCard(Color.BLUE, Values.WILD);
        player.addCards(List.of(wildCard));

        assertTrue(gameManager.executeTurn(wildCard), "Should accept Wild card");
        assertEquals(wildCard, gameManager.getGameState().topCard(), "Top card should update to Wild card");
        assertEquals(Color.BLUE, gameManager.getGameState().topCard().getColor(), "Wild card color should be set correctly");
    }

    @Test
    @DisplayName("Test GameState Consistency")
    void testGameStateConsistency() {
        GameState stateBeforeStart = gameManager.getGameState();
        assertNotNull(stateBeforeStart.playerDeck(), "Player deck should not be null");
        assertFalse(stateBeforeStart.playerDeck().isEmpty(), "Player deck should not be empty at game start");

        final Player p1 = gameManager.nextPlayer();
        GameState stateAfterNext = gameManager.getGameState();

        assertEquals(p1.getHand().size(), stateAfterNext.playerDeck().size(), "Player deck size should match current player's hand size");
    }

    @Test
    @DisplayName("Test Winner Detection")
    void testWinnerDetection() {
        final Optional<Player> winner = gameManager.getWinner();
        assertTrue(winner.isEmpty(), "No winner should be present at game start");
    }
}
