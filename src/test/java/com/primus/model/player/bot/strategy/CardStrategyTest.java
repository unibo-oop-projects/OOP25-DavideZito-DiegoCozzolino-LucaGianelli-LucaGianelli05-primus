package com.primus.model.player.bot.strategy;

import com.primus.model.deck.Card;
import com.primus.model.deck.Color;
import com.primus.model.deck.PrimusCard;
import com.primus.model.deck.Values;
import com.primus.model.player.bot.strategy.card.AggressiveStrategy;
import com.primus.model.player.bot.strategy.card.CardStrategy;
import com.primus.model.player.bot.strategy.card.RandomStrategy;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardStrategyTest {

    private Card card(final Color c, final Values v) {
        return new PrimusCard(c, v);
    }

    @Test
    void testAggressiveStrategyPicksWildDrawFour() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card weakCard = card(Color.RED, Values.ONE);
        final Card strongCard = card(Color.BLACK, Values.WILD_DRAW_FOUR);
        final Card mediumCard = card(Color.BLACK, Values.WILD);
        final Optional<Card> result = strategy.chooseCard(List.of(weakCard, strongCard, mediumCard));
        assertTrue(result.isPresent());
        assertEquals(strongCard, result.get(), "Should choose Wild Draw Four (highest priority)");
    }

    @Test
    void testAggressiveStrategyPicksDrawTwoOverWild() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card wildCard = card(Color.BLACK, Values.WILD);
        final Card drawTwo = card(Color.BLUE, Values.DRAW_TWO);
        final Optional<Card> result = strategy.chooseCard(List.of(wildCard, drawTwo));
        assertTrue(result.isPresent());
        assertEquals(drawTwo, result.get(), "Should choose Draw Two over Wild");
    }

    @Test
    void testAggressiveStrategyStandard() {
        final CardStrategy strategy = new AggressiveStrategy();
        final Card c1 = card(Color.RED, Values.ONE);
        final Card c2 = card(Color.BLUE, Values.FIVE);
        final Optional<Card> result = strategy.chooseCard(List.of(c1, c2));
        assertTrue(result.isPresent());
        assertTrue(List.of(c1, c2).contains(result.get()));
    }

    @Test
    void testRandomStrategyReturnsEmptyOnEmptyList() {
        final CardStrategy strategy = new RandomStrategy();
        final Optional<Card> result = strategy.chooseCard(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testRandomStrategyPicksCard() {
        final CardStrategy strategy = new RandomStrategy();
        final Card c1 = card(Color.RED, Values.ONE);
        final Optional<Card> result = strategy.chooseCard(List.of(c1));
        assertTrue(result.isPresent());
        assertEquals(c1, result.get());
    }
}
