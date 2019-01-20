package com.insanitybringer.padsim.game;

public class Action
{
    protected int ticks = 0;
    protected GameState gameState;

    public Action(GameState gameState)
    {
        this.gameState = gameState;
    }

    public boolean isLive()
    {
        return false;
    }

    public void tick()
    {
        ticks++;
    }

    public void onKill()
    {

    }
}
