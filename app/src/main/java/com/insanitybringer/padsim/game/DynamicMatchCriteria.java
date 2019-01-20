package com.insanitybringer.padsim.game;

public class DynamicMatchCriteria
{
    public int[] criteria = new int[10]; //one for each orb type
    public int numCriteria;

    public DynamicMatchCriteria()
    {
        clear();
    }

    public void clear()
    {
        numCriteria = 0;
        for (int i = 0; i < 10; i++)
        {
            criteria[i] = -1;
        }
    }
}
