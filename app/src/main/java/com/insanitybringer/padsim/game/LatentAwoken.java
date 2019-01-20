package com.insanitybringer.padsim.game;

import com.insanitybringer.padsim.R;

public class LatentAwoken
{
    public final static int[] resourceids = {R.drawable.latent_none, R.drawable.latent_hp, R.drawable.latent_atk,
                                                R.drawable.latent_rcv, R.drawable.awoken_timeextend, R.drawable.latent_autoheal,
                                                R.drawable.latent_resistfire, R.drawable.latent_resistwater, R.drawable.latent_resistwood,
                                                R.drawable.latent_resistlight, R.drawable.latent_resistdark, R.drawable.latent_sdr,
                                                R.drawable.latent_double_stats, R.drawable.latent_none, R.drawable.latent_none,
                                                R.drawable.latent_none, R.drawable.latent_double_evokiller, R.drawable.latent_double_awokenkiller,
                                                R.drawable.latent_double_enkiller, R.drawable.latent_double_rkiller, R.drawable.latent_double_gkiller,
                                                R.drawable.latent_double_dkiller, R.drawable.latent_double_devkiller, R.drawable.latent_double_mkiller,
                                                R.drawable.latent_double_bkiller, R.drawable.latent_double_akiller, R.drawable.latent_double_pkiller,
                                                R.drawable.latent_double_hkiller, R.drawable.latent_double_hp, R.drawable.latent_double_atk,
                                                R.drawable.latent_double_rcv, R.drawable.latent_double_timeextend, R.drawable.latent_double_resistfire,
                                                R.drawable.latent_double_resistwater, R.drawable.latent_double_resistwood, R.drawable.latent_double_resistlight,
                                                R.drawable.latent_double_resistdark};
    public final static int LATENT_NONE = 0;
    public final static int LATENT_HPBOOST = 1;
    public final static int LATENT_ATKBOOST = 2;
    public final static int LATENT_RCVBOOST = 3;
    public final static int LATENT_TIMEEXTEND = 4;
    public final static int LATENT_AUTOHEAL = 5;
    public final static int LATENT_FIRERESIST = 6;
    public final static int LATENT_WATERRESIST = 7;
    public final static int LATENT_WOODRESIST = 8;
    public final static int LATENT_LIGHTRESIST = 9;
    public final static int LATENT_DARKRESIST = 10;
    public final static int LATENT_SKILLDELAYRESIST = 11;
    public final static int LATENT_STATBOOST = 12;
    public final static int LATENT_13 = 13;
    public final static int LATENT_14 = 14;
    public final static int LATENT_15 = 15;
    public final static int LATENT_EVOKILLER = 16;
    public final static int LATENT_AWOKENKILLER = 17;
    public final static int LATENT_ENHANCEDKILLER = 18;
    public final static int LATENT_REDEEMABLEKILLER = 19;
    public final static int LATENT_GODKILLER = 20;
    public final static int LATENT_DRAGONKILLER = 21; //i like how the IDs are different relative compared to normal awokens
    public final static int LATENT_DEVILKILLER = 22;
    public final static int LATENT_MACHINEKILLER = 23;
    public final static int LATENT_BALANCEDKILLER = 24;
    public final static int LATENT_ATTACKERKILLER = 25;
    public final static int LATENT_PHYSICALKILLER = 26;
    public final static int LATENT_HEALERKILLER = 27;
    public final static int LATENT_SUPERHPBOOST = 28;
    public final static int LATENT_SUPERATKBOOST = 29;
    public final static int LATENT_SUPERRCVBOOST = 30;
    public final static int LATENT_SUPERTIMEEXTEND = 31;
    public final static int LATENT_SUPERFIRERESIST = 32;
    public final static int LATENT_SUPERWATERRESIST = 33;
    public final static int LATENT_SUPERWOODRESIST = 34;
    public final static int LATENT_SUPERLIGHTRESIST = 35;
    public final static int LATENT_SUPERDARKRESIST = 36;
    public final static int NUMLATENTS = LATENT_SUPERDARKRESIST;

    public final static int LATENT_PLACEHOLDER = -1; //Causes a given latent cell to not be drawn, for double latents
}