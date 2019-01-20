package com.insanitybringer.padsim.game;

import java.util.ArrayList;

public class Match
{
    public int attribute;
    public ArrayList<OrbPosition> orbs = new ArrayList<>();
    public float centerX, centerY;
    public float minX = 1000.0f, minY = 1000.0f;
    public float maxX = 0.0f, maxY = 0.0f;
    public boolean attack = false;

    public Match(int attribute)
    {
        this.attribute = attribute;
    }
}
