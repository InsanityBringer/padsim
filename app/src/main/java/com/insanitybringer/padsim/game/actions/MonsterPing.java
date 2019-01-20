package com.insanitybringer.padsim.game.actions;

import com.insanitybringer.padsim.game.Action;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.Enemy;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.renderer.particle.PlayerAttack;

public class MonsterPing extends Action
{
    private long pingTime;
    private long deathTime;
    private float srcx, srcy;
    private float dstx, dsty;
    private int attribute;
    private Card card;
    private Enemy enemy;
    private boolean combo;
    public MonsterPing(GameState gameState, int delay, float srcx, float srcy, float dstx, float dsty, int attribute, Card card, Enemy enemy, boolean combo)
    {
        super(gameState);
        pingTime = gameState.gameTicks + delay;
        deathTime = gameState.gameTicks + delay + 7;
        this.srcx = srcx; this.srcy = srcy;
        this.dstx = dstx; this.dsty = dsty;
        this.attribute = attribute;
        this.card = card;
        this.enemy = enemy;
        this.combo = combo;
    }

    @Override
    public boolean isLive()
    {
        return gameState.gameTicks < deathTime;
    }

    @Override
    public void tick()
    {
        //Phase 1 is to dispatch the visual effect of the ping
        if (gameState.gameTicks == pingTime)
        {
            PlayerAttack particle = new PlayerAttack(srcx, srcy, dstx, dsty, attribute);
            gameState.addEffect(particle);
        }
    }

    @Override
    public void onKill()
    {
        //Phase 2 is to do the damage
        int damage = enemy.computeDamage(card, card.getDamage(), card.attackAttribute, combo ? Enemy.DamageFlags.Combo : 0);
        gameState.addNumber(enemy.getDamageNumber(card, card.getDamage(), card.attackAttribute, combo ? Enemy.DamageFlags.Combo : 0));
        enemy.takeVisualDamage(card, damage);
    }
}
