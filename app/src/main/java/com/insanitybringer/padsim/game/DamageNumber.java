package com.insanitybringer.padsim.game;

public class DamageNumber
{
    public final static float baseVelocity = 7.5f;

    private GameState gameState;
    public NumberInterpolator interpolator;
    public float x, y;
    public float velx, vely;
    public int index;
    public int initialDamage, finalDamage;
    public int attribute;
    public float alpha;

    private long startChangeTimer;
    private long fadeTimer;

    public DamageNumber(int initial, int finalDamage, int attribute, float x, float y, int index)
    {
        gameState = GameState.GameStateSingleton.getGameState();
        initialDamage = initial;
        this.finalDamage = finalDamage;
        this.x = x; this.y = y;
        this.index = index;
        this.alpha = 1.0f;
        this.attribute = attribute;

        interpolator = new NumberInterpolator(initialDamage, finalDamage, 14);

        startChangeTimer = gameState.gameTicks + 14;
        fadeTimer = gameState.gameTicks + 38;

        float ang = (float)Math.PI / 4.0f;
        velx = baseVelocity * -(float)Math.cos(ang);
        vely = baseVelocity * -(float)Math.sin(ang);
    }

    public boolean isLive()
    {
        return alpha >= 0.0001f;
    }

    public void tick()
    {
        if (gameState.gameTicks >= startChangeTimer)
        {
            if (gameState.gameTicks == startChangeTimer) //Only change velocity once
            {
                if (finalDamage < initialDamage)
                {
                    //Invert the velocity
                    velx *= -1.5f; vely *= -1.5f;
                }
            }
            interpolator.tick();
        }

        if (gameState.gameTicks >= fadeTimer)
        {
            long tickssince = gameState.gameTicks - fadeTimer;
            alpha = lerp(1.0f, 0.0f, tickssince / 6.0f);
        }

        //Move the number
        x += velx; y += vely;
        //Drag. Should be altered so resisted things slow down much faster, it seems
        velx *= .9; vely *= .9;
    }

    public static float lerp(float a, float b, float x)
    {
        return ((b - a) * x) + a;
    }
}
