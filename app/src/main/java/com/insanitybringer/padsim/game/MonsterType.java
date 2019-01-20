package com.insanitybringer.padsim.game;

import com.insanitybringer.padsim.R;

public class MonsterType
{
    public final static String[] killerNames = {"God Killer", "Dragon Killer", "Devil Killer", "Machine Killer",
            "Balanced Killer", "Attacker Killer", "Physical Killer", "Healer Killer"};
    public final static int[] resourceids = {
            R.drawable.type_evomat, R.drawable.type_balanced, R.drawable.type_physical, R.drawable.type_healer,
            R.drawable.type_dragon, R.drawable.type_god, R.drawable.type_attacker, R.drawable.type_devil,
            R.drawable.type_machine, R.drawable.type_unknown, R.drawable.type_unknown, R.drawable.type_unknown,
            R.drawable.type_awoken, R.drawable.type_unknown, R.drawable.type_enhance, R.drawable.type_redeemable};
    public final static int TYPE_UNDEFINED = -1;
    public final static int TYPE_EVOMAT = 0;
    public final static int TYPE_BALANCED = 1;
    public final static int TYPE_PHYSICAL = 2;
    public final static int TYPE_HEALER = 3;
    public final static int TYPE_DRAGON = 4;
    public final static int TYPE_GOD = 5;
    public final static int TYPE_ATTACKER = 6;
    public final static int TYPE_DEVIL = 7;
    public final static int TYPE_MACHINE = 8;
    public final static int TYPE_9 = 9;
    public final static int TYPE_10 = 10;
    public final static int TYPE_11 = 11;
    public final static int TYPE_AWOKEN = 12;
    public final static int TYPE_13 = 13;
    public final static int TYPE_ENHANCE = 14;
    public final static int TYPE_REDEEMABLE = 15;
}