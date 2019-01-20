package com.insanitybringer.padsim.game;

public class Orb
{
    public final int NUM_ORBTYPES = 10; //fire water wood light dark heart jammer poison super poison bomb
    public static float[] orbSizes = { 104f, 104f, 104f, 104f, 104f, 88f, 86f, 94f, 104f, 104f };

    public int getAttribute()
    {
        return attribute;
    }

    private int attribute;

    public float getPosX()
    {
        return posX;
    }

    public float getPosY()
    {
        return posY;
    }

    //private Vector3 orbPosition; //Position of the orb isn't implicit, for convenience
    private float posX, posY;

    //Animation properties
    //private Vector3 orbSource;
    //private Vector3 orbDestination;
    private float srcX, srcY;
    private float dstX, dstY;

    public OrbAnimationState getAnimationState()
    {
        return animationState;
    }

    private OrbAnimationState animationState = OrbAnimationState.Idle;
    private int animationTimer = 0;

    public boolean isMarked()
    {
        return marked;
    }

    public void setMarked(boolean marked)
    {
        this.marked = marked;
    }

    public boolean isAdded()
    {
        return added;
    }

    public void setAdded(boolean added)
    {
        this.added = added;
    }

    private boolean marked = false;
    private boolean added = false;

    private int heldLevel = 0;
    private boolean fadingIn = false;

    public float getAlpha()
    {
        return alpha;
    }

    private float alpha = 1.0f;

    public Orb(int x, int y, int attribute, int boardSize)
    {
        this.attribute = attribute;
        posX = x * 105f + 52.5f;
        //posY = (4-y) * 105f + 52.5f;
        posY = y * 105f + 52.5f;
    }

    public void swapTo(int x, int y, int size, OrbAnimationState newAnimation)
    {
        srcX = posX; srcY = posY;
        dstX = x * 105f + 52.5f; dstY = (y) * 105f + 52.5f;
        animationTimer = 4;
        animationState = newAnimation;
    }

    public void fallTo(int x, int y, int startx, int starty, int size)
    {
        srcX = startx * 105f + 52.5f; srcY = (starty) * 105f + 52.5f;
        dstX = x * 105f + 52.5f; dstY = (y) * 105f + 52.5f;
        animationTimer = 12;
        animationState = OrbAnimationState.Drop;
    }

    public void clearFade()
    {
        heldLevel = 0;
        alpha = 1.0f;
    }

    public void offsetTo(int x, int y, int size)
    {
        posX = x * 105f + 52.5f; posY = (y) * 105f + 52.5f;
    }

    public void match(int x, int y)
    {
        posX = x * 105f + 52.5f; posY = (y) * 105f + 52.5f;
        animationTimer = 12;
        animationState = OrbAnimationState.Match;
    }

    public void fade()
    {
        heldLevel = 0;
        animationTimer = 15;
        animationState = OrbAnimationState.Fade;
    }

    public void unfade()
    {
        heldLevel = 0;
        animationTimer = 11;
        animationState = OrbAnimationState.Unfade;
    }

    public void quickFade()
    {
        heldLevel = 1;
        fadingIn = false;
    }

    public void quickUnfade()
    {
        fadingIn = true;
    }

    public float lerp(float x, float y, float l)
    {
        return x + ((y - x) * l);
    }

    public float blend(float x)
    {
        return 3 * (x * x) - 2 * (x * x * x);
    }

    public void update()
    {
        if (animationState != OrbAnimationState.Idle)
        {
            //Swap animations
            if (animationTimer > 0)
            {
                animationTimer--;
                if (animationState == OrbAnimationState.SwapHorizontal || animationState == OrbAnimationState.SwapVertical)
                {
                    posX = lerp(srcX, dstX, blend(1.0f - (animationTimer / 4.0f)));
                    posY = lerp(srcY, dstY, blend(1.0f - (animationTimer / 4.0f)));

                    float offset = (float)Math.sin((1 - (animationTimer / 4.0f)) * (Math.PI));

                    if (animationState == OrbAnimationState.SwapHorizontal)
                    {
                        posY -= (offset) * 63f;
                    }
                    if (animationState == OrbAnimationState.SwapVertical)
                    {
                        posX += (offset) * 63f;
                    }
                }
                else if (animationState == OrbAnimationState.Match)
                {
                    alpha = (animationTimer / 12.0f);
                }
                else if (animationState == OrbAnimationState.Drop)
                {
                    posX = dstX;
                    posY = lerp(srcY, dstY, blend(1.0f - (animationTimer / 12.0f)));
                }
                else if (animationState == OrbAnimationState.Fade)
                {
                    alpha = (animationTimer / 15.0f) * .75f + .25f;
                }
                else if (animationState == OrbAnimationState.Unfade)
                {
                    alpha = ((11-animationTimer) / 11.0f) * .75f + .25f;
                }
            }
        }

        if (animationTimer == 0)
        {
            animationState = OrbAnimationState.Idle;
        }

        if (heldLevel > 0)
        {
            alpha = 1.0f - .75f * (heldLevel / 7.0f);
            if (!fadingIn)
            {
                if (heldLevel < 7)
                {
                    heldLevel++;
                }
            }
            else
            {
                heldLevel--;
            }
        }
    }
}
