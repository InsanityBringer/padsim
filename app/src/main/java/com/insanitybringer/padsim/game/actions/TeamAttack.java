package com.insanitybringer.padsim.game.actions;

import com.insanitybringer.padsim.game.Action;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.Enemy;
import com.insanitybringer.padsim.game.GameState;

public class TeamAttack extends Action
{
    private boolean combo = false;
    private long timer;
    public TeamAttack(GameState gameState, boolean combo)
    {
        super(gameState);
        gameState.addLock();
        this.combo = combo;
        int pingcount = 0;
        Card[] team = gameState.getTeam();
        Enemy[] enemies = gameState.enemies;
        int attribBit;
        Card card;
        Enemy enemy;
        int target = 0;
        for (int i = 0; i < 6; i++)
        {
            card = team[i];
            if (card.getDamage() > 0)
            {
                attribBit = 1 << card.attackAttribute;

                //Handle a mass attack, if one was defined for this attribute
                if ((gameState.massAttackAttribs & attribBit) != 0)
                {
                    //System.out.println("Multi target?");
                    for (int e = 0; e < 7; e++)
                    {
                        enemy = enemies[e];
                        if (enemy != null && (enemy.flags & Enemy.EnemyFlags.Gone) == 0)
                        {
                            //if (!enemy.isDead())
                            {
                                enemy.takeDamage(card, card.getDamage(), card.attackAttribute, combo ? Enemy.DamageFlags.Combo : 0);
                                gameState.addAction(new MonsterPing(gameState, pingcount * 4,
                                        card.posX + 50, card.getRenderHeight() + 50,
                                        enemy.x + 50, 150 + enemy.y + 50, card.attackAttribute, card, enemy, combo));
                            }
                        }
                    }
                } else
                {
                    target = card.findTarget(enemies, false);
                    //System.out.printf("Single target? found target %d\n", target);
                    enemies[target].takeDamage(card, card.getDamage(), card.attackAttribute, combo ? Enemy.DamageFlags.Combo : 0);
                    gameState.addAction(new MonsterPing(gameState, pingcount * 4, card.posX + 50,
                            card.getRenderHeight() + 50, enemies[target].x + 50,
                            150 + enemies[target].y + 50, card.attackAttribute, card, enemies[target], combo));
                }
                pingcount++;
            }
        }
        timer = gameState.gameTicks + (4 * pingcount) + 7;
    }

    @Override
    public boolean isLive()
    {
        return gameState.gameTicks < timer;
    }

    @Override
    public void onKill()
    {
        gameState.freeLock();
        gameState.endAttack();
    }
}
