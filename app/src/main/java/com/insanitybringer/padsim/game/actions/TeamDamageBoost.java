package com.insanitybringer.padsim.game.actions;

import com.insanitybringer.padsim.game.Action;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.GameState;

public class TeamDamageBoost extends Action
{
    private int combos;
    private int currentCombo = 1;
    private int phase = 0;

    private long timer = 0;
    public TeamDamageBoost(GameState gameState, int combos)
    {
        super(gameState);
        this.combos = combos;
        gameState.addLock();
        timer = gameState.gameTicks + 5;
    }

    @Override
    public boolean isLive()
    {
        return phase < 4;
    }

    @Override
    public void tick()
    {
        if (phase == 0) //Very slight delay between the last match fading and the combo loop starting
        {
            if (gameState.gameTicks >= timer)
            {
                phase++;
            }
        }

        if (phase == 1) //Combos. Instant if combo skip is enabled
        {
            if (gameState.gameTicks >= timer)
            {
                System.out.printf("combos %d currentCombo %d\n", combos, currentCombo);
                if (currentCombo >= combos)
                {
                    phase++;
                }
                /*
                else
                {
                    timer = gameState.gameTicks + 15;
                    currentCombo++;
                    for (Card card : gameState.getTeam())
                    {
                        card.displayCombo(currentCombo);
                    }
                }*/
                else //COMBO SKIP ENABLED
                {
                    timer = gameState.gameTicks + 15;
                    currentCombo = combos;
                    for (Card card : gameState.getTeam())
                    {
                        card.displayCombo(currentCombo);
                    }
                }
            }
        }

        if (phase == 2)
        {
            //I don't really like this but finalDamageBoost does the actual boost, so we need to do it only once
            boolean boosted = gameState.finalDamageBoost();
            if (boosted)
            {
                phase = 3;
                timer = gameState.gameTicks + 15;
            }
            else phase = 4;
        }
        if (phase == 3)
        {
            if (gameState.gameTicks >= timer)
            {
                phase++;
            }
        }

        super.tick();
    }

    @Override
    public void onKill()
    {
        gameState.addAction(new TeamAttack(gameState, true));
        gameState.freeLock();
    }
}
