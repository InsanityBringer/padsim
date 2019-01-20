package com.insanitybringer.padsim.game;

public class NumberInterpolator
{
    private int min;
    private int max;
    private int current = 0;

    private int ticks = 0;
    private int maxTicks;

    public NumberInterpolator(int min, int max, int ticks)
    {
        this.min = min;
        this.max = max;
        this.current = min;
        this.maxTicks = ticks;
    }

    public void tick()
    {
        if (min != max)
        {
            if (ticks < maxTicks)
            {
                double progress = (double)ticks/(maxTicks-1);
                current = (int) (lerp(min, max, progress));

                ticks++;
            }
        }
    }

    public void updateTo(int value, int ticks)
    {
        this.ticks = 0;
        maxTicks = ticks;
        min = max;
        max = value;
    }

    public void addTo(int value, int ticks)
    {
        this.ticks = 0;
        maxTicks = ticks;
        min = current;
        max += value;
        max = Math.max(0, max);
    }

    public void addTo(int value, int ticks, int cap)
    {
        this.ticks = 0;
        maxTicks = ticks;
        min = current;
        max += value;
        max = Math.max(0, max);
        max = Math.min(cap, max);
    }

    public void replace(int min, int max, int ticks)
    {
        this.ticks = 0;
        maxTicks = ticks;
        this.min = min;
        this.max = max;
        this.current = min;
    }

    public int getCurrent() { return current; }
    public int getTicks() { return Math.min(ticks, maxTicks); }

    public static double lerp(double a, double b, double x)
    {
        return ((b - a) * x) + a;
    }
}
