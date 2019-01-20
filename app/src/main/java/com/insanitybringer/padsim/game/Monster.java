package com.insanitybringer.padsim.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Monster
{
    public final static int EnemyMaxLevel = 10;
    public static String[] types = {"Undefined", "Evo Material", "Balanced", "Physical", "Healer", "Dragon", "God", "Attacker", "Devil", "Machine", "9", "10", "11", "Awoken Material", "13", "Enhance Material", "Redeemable Material"};
    public static String[] awokenNames = {"Undefined", "Enhanced HP", "Enhanced Attack", "Enhanced Recovery", "Reduce Fire Damage", "Reduce Water Damage", "Reduce Wood Damage",
            "Reduce Light Damage", "Reduce Dark Damage", "Auto-Recover", "Resistance-Bind", "Resistance-Blind", "Resistance-Jammer", "Resistance-Poison",
            "Enhanced Fire Orbs", "Enhanced Water Orbs", "Enhanced Wood Orbs", "Enhanced Light Orbs", "Enhanced Dark Orbs", "Extend Time", "Recover Bind",
            "Skill Boost", "Enhanced Fire Att.", "Enhanced Water Att.", "Enhanced Wood Att.", "Enhanced Light Att.", "Enhanced Dark Att.", "Two-Pronged Attack",
            "Enhanced Heal Orbs", "Multi Boost", "Dragon Killer", "God Killer", "Devil Killer", "Machine Killer", "Attacker Killer", "Physical Killer",
            "Healer Killer", "Balanced Killer", "Awoken Killer", "Enhance Killer", "Redeemable Killer", "Evo Killer", "Enhanced Combos", "Guard Break",
            "Bonus Attack", "Enhanced Team HP", "Enhanced Team RCV", "Damave Void Piercer", "Awoken Assist", "Super Bonus Attack", "Skill Charge",
            "Resistance-Bind+", "Extended Move Time+", "Resistance-Clouds", "Resistance-Immobilize", "Skill Boost+", "80% or more HP Enhanced",
            "50% or less HP Enhanced", "[L] Damage Reduction", "[L] Increased Attack", "Super Enhanced Combos", "Combo Orb", "Character Voice", "Dungeon Boost"};

    private short minHealth;
    private short minAttack;
    private short minRecovery;
    private short maxHealth;
    private short maxAttack;
    private short maxRecovery;

    private byte limitBreakBoost;
    private byte maxLevel;
    private boolean canInherit;
    private byte type1;
    private byte type2;
    private byte type3;

    private String name;
    private String idname;

    private int minEHealth;
    private int minEAttack;
    private int minEDefense;
    private int maxEHealth;
    private int maxEAttack;
    private int maxEDefense;
    private float ehpCurve;
    private float eatkCurve;
    private float edefCurve;
    private byte enemyTurns;
    private short collabID;
    private int id;

    private float hpCurve;
    private float atkCurve;
    private float rcvCurve;

    private Monster altForm;

    private byte attribute;
    private byte subAttribute;

    private short activeID;
    private short leaderID;

    private int[] awokenSkills = new int[9];
    private int numAwokens = 0;
    private int numSuperAwokens = 0;

    private int[] superAwokenSkills = new int[9];

    public int countAwokens(int id, int to)
    {
        int count = 0;
        for (int i = 0; i < to; i++)
        {
            if (awokenSkills[i] == id)
                count++;
        }
        return count;
    }

    public void fromObject(JsonObject object)
    {
        id = object.get("card_id").getAsInt();
        name = object.get("name").getAsString();
        minHealth = object.get("min_hp").getAsShort();
        minAttack = object.get("min_atk").getAsShort();
        minRecovery = object.get("min_rcv").getAsShort();
        maxHealth = object.get("max_hp").getAsShort();
        maxAttack = object.get("max_atk").getAsShort();
        maxRecovery = object.get("max_rcv").getAsShort();
        maxLevel = object.get("max_level").getAsByte();
        limitBreakBoost = object.get("limit_mult").getAsByte();
        hpCurve = object.get("hp_scale").getAsFloat();
        atkCurve = object.get("atk_scale").getAsFloat();
        rcvCurve = object.get("rcv_scale").getAsFloat();
        attribute = object.get("attr_id").getAsByte();
        subAttribute = object.get("sub_attr_id").getAsByte();
        type1 = object.get("type_1_id").getAsByte();
        type2 = object.get("type_2_id").getAsByte();
        type3 = object.get("type_3_id").getAsByte();
        minEHealth = object.get("enemy_hp_1").getAsInt();
        minEDefense = object.get("enemy_def_1").getAsInt();
        minEAttack = object.get("enemy_atk1").getAsInt();
        maxEHealth = object.get("enemy_hp_10").getAsInt();
        maxEAttack = object.get("enemy_def_10").getAsInt();
        maxEDefense = object.get("enemy_at_k10").getAsInt();
        ehpCurve = object.get("enemy_hp_gr").getAsFloat();
        eatkCurve = object.get("enemy_atk_gr").getAsFloat();
        edefCurve = object.get("enemy_def_gr").getAsFloat();
        collabID = object.get("unknown_066").getAsShort();
        activeID = object.get("active_skill_id").getAsShort();
        leaderID = object.get("leader_skill_id").getAsShort();
        canInherit = object.get("inheritable").getAsBoolean();

        if (id == 0)
        {
            name = "None";
            minHealth = maxHealth = 0;
            minAttack = maxAttack = 0;
            minRecovery = maxRecovery = 0;
        }

        JsonArray awokenArray = object.get("awakenings").getAsJsonArray();
        numAwokens = awokenArray.size();
        for (int i = 0; i < awokenArray.size(); i++)
            awokenSkills[i] = awokenArray.get(i).getAsInt();

        awokenArray = object.get("super_awakenings").getAsJsonArray();
        numSuperAwokens = awokenArray.size();
        for (int i = 0; i < awokenArray.size(); i++)
            superAwokenSkills[i] = awokenArray.get(i).getAsInt();

        idname = String.format("%d: %s", id, name);


        if (id == 1058)
        {
            for (int i = 0; i < 9; i++)
            {
                awokenSkills[i] = AwokenSkill.AWOKEN_SUPERENHANCEDCOMBOS;
            }
        }
    }

    public int getHPAt(int level)
    {
        if (maxLevel == 1) return minHealth;
        int diff = maxHealth - minHealth;
        float factor = (float) Math.pow((level - 1f) / (maxLevel - 1.0f), hpCurve);
        return Math.round(minHealth + diff * factor);
    }

    public int getATKAt(int level)
    {
        if (maxLevel == 1) return minAttack;
        int diff = maxAttack - minAttack;
        float factor = (float) Math.pow((level - 1f) / (maxLevel - 1.0f), atkCurve);
        return Math.round(minAttack + diff * factor);
    }

    public int getRCVAt(int level)
    {
        if (maxLevel == 1) return minRecovery;
        int diff = maxRecovery - minRecovery;
        float factor = (float) Math.pow((level - 1f) / (maxLevel - 1.0f), rcvCurve);
        return Math.round(minRecovery + diff * factor);
    }

    public int getEHPAt(int level)
    {
        int diff = maxEHealth - minEHealth;
        float factor = (float) Math.pow((level - 1f) / (EnemyMaxLevel - 1.0f), ehpCurve);
        return Math.round(minEHealth + diff * factor);
    }

    public int getEATKAt(int level)
    {
        int diff = maxEAttack - minEAttack;
        float factor = (float) Math.pow((level - 1f) / (EnemyMaxLevel - 1.0f), eatkCurve);
        return Math.round(minEAttack + diff * factor);
    }

    public int getEDEFAt(int level)
    {
        int diff = maxEDefense - minEDefense;
        float factor = (float) Math.pow((level - 1f) / (EnemyMaxLevel - 1.0f), edefCurve);
        return Math.round(minEDefense + diff * factor);
    }

    public short getMinHealth()
    {
        return minHealth;
    }

    public short getMinAttack()
    {
        return minAttack;
    }

    public short getMinRecovery()
    {
        return minRecovery;
    }


    public short getMaxHealth()
    {
        return maxHealth;
    }


    public short getMaxAttack()
    {
        return maxAttack;
    }


    public short getMaxRecovery()
    {
        return maxRecovery;
    }

    public boolean getCanInherit()
    {
        return canInherit;
    }

    public byte getMaxLevel()
    {
        return maxLevel;
    }

    public byte getLimitBreakBoost()
    {
        return limitBreakBoost;
    }

    public byte getLBMaxLevel()
    {
        if (maxLevel == 99 && limitBreakBoost > 0)
            return 110;
        return maxLevel;
    }

    public void setMaxLevel(byte maxLevel)
    {
        this.maxLevel = maxLevel;
    }

    public byte getType(int id)
    {
        switch (id)
        {
            case 1:
                return type2;
            case 2:
                return type3;
        }
        return type1;
    }

    public String getName()
    {
        return name;
    }

    public String getIDName()
    {
        return idname;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getMinEHealth()
    {
        return minEHealth;
    }

    public void setMinEHealth(int minEHealth)
    {
        this.minEHealth = minEHealth;
    }

    public int getMinEAttack()
    {
        return minEAttack;
    }

    public void setMinEAttack(int minEAttack)
    {
        this.minEAttack = minEAttack;
    }

    public int getMinEDefense()
    {
        return minEDefense;
    }

    public void setMinEDefense(int minEDefense)
    {
        this.minEDefense = minEDefense;
    }

    public int getMaxEHealth()
    {
        return maxEHealth;
    }

    public void setMaxEHealth(int maxEHealth)
    {
        this.maxEHealth = maxEHealth;
    }

    public int getMaxEAttack()
    {
        return maxEAttack;
    }

    public void setMaxEAttack(int maxEAttack)
    {
        this.maxEAttack = maxEAttack;
    }

    public int getMaxEDefense()
    {
        return maxEDefense;
    }

    public void setMaxEDefense(int maxEDefense)
    {
        this.maxEDefense = maxEDefense;
    }

    public byte getEnemyTurns()
    {
        return enemyTurns;
    }

    public void setEnemyTurns(byte enemyTurns)
    {
        this.enemyTurns = enemyTurns;
    }

    public short getCollabID()
    {
        return collabID;
    }

    public void setCollabID(short collabID)
    {
        this.collabID = collabID;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public float getHpCurve()
    {
        return hpCurve;
    }

    public float getAtkCurve()
    {
        return atkCurve;
    }

    public float getRcvCurve()
    {
        return rcvCurve;
    }

    public Monster getAltForm()
    {
        return altForm;
    }

    public void setAltForm(Monster altForm)
    {
        this.altForm = altForm;
    }

    public byte getAttribute()
    {
        return attribute;
    }

    public void setAttribute(byte attribute)
    {
        this.attribute = attribute;
    }

    public byte getSubAttribute()
    {
        return subAttribute;
    }

    public short getActiveID()
    {
        return activeID;
    }

    public short getLeaderID()
    {
        return leaderID;
    }

    public int[] getAwokenSkills()
    {
        return awokenSkills;
    }

    public int getNumAwokens()
    {
        return numAwokens;
    }

    public int[] getSuperAwokenSkills()
    {
        return superAwokenSkills;
    }

    public int getNumSuperAwokens()
    {
        return numSuperAwokens;
    }

}
