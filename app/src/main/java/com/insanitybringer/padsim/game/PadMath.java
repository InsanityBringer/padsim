package com.insanitybringer.padsim.game;

public class PadMath
{
    public static int fracMult(int left, int right)
    {
        return (int)((left / 100.0f) * (right / 100.0f) * 100.0f);
    }

    public static int wholeMult(int left, int right)
    {
        return wholeMult(left, right, 1);
    }
    public static int wholeMult(int left, int right, int roundmode)
    {
        float t = left * (right / 100.0f);
        int result;
        if (roundmode == 0)
        {
            result = Math.round(t);
        }
        else if (roundmode == 1)
        {
            t = (float)Math.ceil(t);
            result = (int)t;
        }
        else
        {
            t = (float)Math.floor(t);
            result = (int)t;
        }

        if (t > Integer.MAX_VALUE) result = Integer.MAX_VALUE;
        return result;
    }

    public static float getFloatFromFixed(int fix)
    {
        return (fix / 100.0f);
    }
}
