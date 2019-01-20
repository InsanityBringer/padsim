package com.insanitybringer.padsim.game.actions;

import com.insanitybringer.padsim.game.Action;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.Match;

public class AddMatchDamage extends Action
{
    private Card card;
    private Match match;
    private boolean sub;
    public AddMatchDamage(GameState gameState, Card card, Match match, boolean sub)
    {
        super(gameState);
        this.card = card;
        this.match = match;
        this.sub = sub;
    }

    @Override
    public boolean isLive()
    {
        return ticks < 7;
    }

    @Override
    public void tick()
    {
        if (ticks == 6)
            card.addMatch(match, sub);
        super.tick();
    }
}
