package com.insanitybringer.padsim;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.insanitybringer.padsim.game.LatentAwoken;
import com.insanitybringer.padsim.game.MonsterType;

public class LatentPickerDialogFragment extends DialogFragment
{
    private static final String ARG_SLOT = "slot";
    private static final String ARG_SHORTLIST = "shortList";
    private static final String ARG_TYPE1 = "type1";
    private static final String ARG_TYPE2 = "type2";
    private static final String ARG_TYPE3 = "type3";
    private boolean shortList;
    private int slot;
    private int type1, type2, type3;
    private int[] finalMapping;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_pick_latent);
        Bundle args = getArguments();
        CharSequence[] list = generateArray(args.getBoolean(ARG_SHORTLIST), args.getInt(ARG_TYPE1), args.getInt(ARG_TYPE2), args.getInt(ARG_TYPE3));
        //builder.setItems(R.array.latent_drawable_array, new DialogInterface.OnClickListener()
        builder.setItems(list, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Bundle args = getArguments();
                System.out.printf("Picked latent %d\n", finalMapping[which]);
                ((CardEditorActivity)getActivity()).updateLatent(args.getInt(ARG_SLOT), finalMapping[which]);
            }
        });

        return builder.create();
    }

    private CharSequence[] generateArray(boolean shortList, int type1, int type2, int type3)
    {
        //Either returned directly for the shortlist or copied into the built long list
        String[] shortlist = {"Remove", "Enhanced HP", "Enhanced ATK", "Enhanced RCV", "Time Extend",
                "Autoheal", "Fire Resist", "Water Resist", "Wood Resist", "Light Resist", "Dark Resist",
                "Skill Delay Resist"};
        //Copied into the end of the list in reverse order
        String[] reverseList = {"Super Resist Dark", "Super Resist Light", "Super Resist Wood",
                                "Super Resist Water", "Super Resist Fire", "Super Time Extend",
                                "Super Enhanced RCV", "Super Enhanced ATK", "Super Enhanced HP"};
        if (shortList)
        {
            finalMapping = new int[shortlist.length];
            for (int i = 0; i < shortlist.length; i++)
            {
                finalMapping[i] = i;
            }
            return shortlist;
        }
        //bit 0: god, 1: dragon, 2: devil, 3: machine, 4: balance 5: attacker 6: physical 7: healer
        //    1       2          4         8           16         32          64          128
        int bitfield = 0;
        if (type1 == MonsterType.TYPE_BALANCED || type2 == MonsterType.TYPE_BALANCED || type3 == MonsterType.TYPE_BALANCED)
            bitfield |= 255; //All killers allowed
        else
        {
            if (type1 == MonsterType.TYPE_GOD || type2 == MonsterType.TYPE_GOD || type3 == MonsterType.TYPE_GOD)
                bitfield |= 4; //Devil killers only
            if (type1 == MonsterType.TYPE_DRAGON || type2 == MonsterType.TYPE_DRAGON || type3 == MonsterType.TYPE_DRAGON ||
                    type1 == MonsterType.TYPE_PHYSICAL || type2 == MonsterType.TYPE_PHYSICAL || type3 == MonsterType.TYPE_PHYSICAL)
                bitfield |= 8 | 128; //Machine and healer. these two weirdos have the same killers i dunno
            if (type1 == MonsterType.TYPE_DEVIL || type2 == MonsterType.TYPE_DEVIL || type3 == MonsterType.TYPE_DEVIL)
                bitfield |= 1; //God killers only
            if (type1 == MonsterType.TYPE_MACHINE || type2 == MonsterType.TYPE_MACHINE || type3 == MonsterType.TYPE_MACHINE)
                bitfield |= 1 | 16; //God and Balance
            if (type1 == MonsterType.TYPE_ATTACKER || type2 == MonsterType.TYPE_ATTACKER || type3 == MonsterType.TYPE_ATTACKER)
                bitfield |= 4 | 64; //Devil and Physical
            if (type1 == MonsterType.TYPE_HEALER || type2 == MonsterType.TYPE_HEALER || type3 == MonsterType.TYPE_HEALER)
                bitfield |= 2 | 32; //dragon and attacker
        }

        int basecount = 0;
        int mask = 1;
        //Determine how many bits were actually set from the above computation
        for (int i = 0; i < 8; i++)
        {
            if ((bitfield & mask) != 0)
            {
                basecount++;
            }
            mask <<= 1;
        }
        //Get the final count of latents to show
        int count = 26 + basecount; //Number of non main killer latents
        String[] list = new String[count];
        finalMapping = new int[count];
        //Copy in the beginning and the end of the list
        System.arraycopy(shortlist, 0, list, 0, shortlist.length);
        for (int i = 0; i < shortlist.length; i++)
        {
            finalMapping[i] = i;
        }
        for (int i = 0; i < reverseList.length; i++)
        {
            list[list.length - i - 1] = reverseList[i];
            finalMapping[list.length - i - 1] = LatentAwoken.NUMLATENTS - i;
        }
        list[shortlist.length] = "Imp. All Stats"; finalMapping[shortlist.length] = 12;
        list[shortlist.length + 1] = "Evo Material Killer"; finalMapping[shortlist.length + 1] = 16;
        list[shortlist.length + 2] = "Awoken Material Killer"; finalMapping[shortlist.length + 2] = 17;
        list[shortlist.length + 3] = "Enhance Material Killer"; finalMapping[shortlist.length + 3] = 18;
        list[shortlist.length + 4] = "Redeemable Killer"; finalMapping[shortlist.length + 4] = 19;
        //Dynamically generate the main killer list
        int pos = shortlist.length + 5;
        mask = 1;
        for (int i = 0; i < 8; i++)
        {
            if ((bitfield & mask) != 0)
            {
                list[pos] = MonsterType.killerNames[i];
                finalMapping[pos] = LatentAwoken.LATENT_GODKILLER + i; //in order on purpose woo
                pos++;
            }
            mask <<= 1;
        }

        for (int i = 0; i < count; i++)
        {
            String heh = list[i];
            if (heh != null)
                System.out.printf("String %d: %s\n", i, heh);
            else
                System.out.printf("String %d is null\n", i);
        }

        return list;
    }

    public static LatentPickerDialogFragment newInstance(int slot, boolean shortList, int type1, int type2, int type3)
    {
        LatentPickerDialogFragment frag = new LatentPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SLOT, slot);
        args.putBoolean(ARG_SHORTLIST, shortList);
        args.putInt(ARG_TYPE1, type1);
        args.putInt(ARG_TYPE2, type2);
        args.putInt(ARG_TYPE3, type3);
        frag.setArguments(args);
        return frag;
    }
}
