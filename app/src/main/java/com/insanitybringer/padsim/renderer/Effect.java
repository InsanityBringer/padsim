package com.insanitybringer.padsim.renderer;

public class Effect
{
    int texture;
    int renderMode;
    public float textureX, textureY;
    public float textureSize;
    public float size;

    protected int ticks = 0;

    //The amount of particles in a given effect is known, so we'll just use an array
    public Particle[] particles;

    //Whether or not coordinates are relative from the top or bottom
    public boolean fromBottom = false;

    public class Particle
    {
        //there's probably no point to encapsulate these if I'm frank
        public float x, y;
        //Needed for doing mathematical operations to drive particles
        public float srcx, dstx;
        public float srcy, dsty;
        public float scale = 1.0f;
        public float rotation = 0.0f;
        public float r = 1.0f, g = 1.0f, b = 1.0f;
    }

    public boolean isLive()
    {
        return false;
    }

    public void tick()
    {
        ticks++;
    }

    public static float lerp(float a, float b, float x)
    {
        return ((b - a) * x) + a;
    }
}
