package com.insanitybringer.padsim.game;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.insanitybringer.padsim.game.skills.leader.NumMatchedBonus;
import com.insanitybringer.padsim.game.skills.SkillEffect;
import com.insanitybringer.padsim.game.skills.leader.*; //is it against the laws of java to do this

public class Skill
{
    protected short skillID;
    //would be byte but remember that java doesn't believe in unsigned numbers because reasons
    protected short skillType;
    protected int[] skillParams = new int[9];
    private byte minTurns;
    private byte maxTurns;
    protected String name;
    protected String description;

    public static Skill createSkill(JsonObject object)
    {
        short type = object.get("skill_type").getAsShort();
        //THE BIG HACK HOLY FUCK
        //maybe I should just use a hash table and some reflection...
        //it would be cleaner but would it actually be any faster...
        Skill skill;
        switch (type)
        {
            case SkillEffect.Leader_AttackBoost_Attribute: //11
            case SkillEffect.Leader_Shield: //16
            case SkillEffect.Leader_Shield_VsAttribute: //17
            case SkillEffect.Leader_AttackBoost_Type: //22
            case SkillEffect.Leader_HealthBoost_Type: //23
            case SkillEffect.Leader_RecoveryBoost_Type: //24
            case SkillEffect.Leader_AttackBoost: //26
            case SkillEffect.Leader_AttackRecoveryBoost: //28
            case SkillEffect.Leader_AllStatBoost: //29
            case SkillEffect.Leader_HealthBoost_DoubleTypes: //30
            case SkillEffect.Leader_AttackBoost_DoubleTypes: //31
            case SkillEffect.Leader_Shield_VsAttribueDouble: //36
            case SkillEffect.Leader_AttackBoost_DoubleAttribute: //40
            case SkillEffect.Leader_HealthAttackBoost_Attribute: //45
            case SkillEffect.Leader_HealthBoost_DoubleAttribute: //46
            case SkillEffect.Leader_HealthBoost_Attribute: //48
            case SkillEffect.Leader_RecoveryBoost_Attribute: //49
            case SkillEffect.Leader_HealthAttackBoost_Type: //62
            case SkillEffect.Leader_HealthRecoveryBoost_Type: //63
            case SkillEffect.Leader_AttackRecoveryBoost_Type: //64
            case SkillEffect.Leader_StatBoost_Type: //65
            case SkillEffect.Leader_HealthRecoveryBoost_Attribute: //67
            case SkillEffect.Leader_AttackBoost_Attribute_Type: //69
            case SkillEffect.Leader_HealthAttackBoost_Attribute_Type: //73
            case SkillEffect.Leader_AttackRecoveryBoost_Attribute_Type: //75
            case SkillEffect.Leader_StatBoost_Attribute_Type: //76
            case SkillEffect.Leader_HealthAttackBoost_DoubleTypes: //77
            case SkillEffect.Leaders_AttackRecoveryBoost_DoubleTypes: //79
            case SkillEffect.Leader_HealthAttackBoost_DoubleAttributes: //111
            case SkillEffect.Leader_StatBoost_DoubleAttributes: //114
            case SkillEffect.Leader_StatBoost:
            case SkillEffect.Leader_StatBoost_WithShield: //aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            case SkillEffect.Leader_AttackBoost_Attribute_HealthPenalty:
            case SkillEffect.Leader_AttackBoost_RecoveryPenalty:
            case SkillEffect.Leader_StatBoost_Attribute_DoubleBoosts:
            case SkillEffect.Leader_StatBoost_Type_DoubleBoosts:
            case SkillEffect.Leader_StatBoost_MinOrbMatchRequirement:
            case SkillEffect.Leader_StatBoost_4SecMoveTime:
            case SkillEffect.Leader_StatBoost_7x6Board:
            case SkillEffect.Leader_7x6Board:
            case SkillEffect.Leader_StatBoost_TimeExtend: //is it over yet
                skill = new StatBoost();
                break;
            case SkillEffect.Leader_AttackBoost_Combos:
            case SkillEffect.Leader_AttackBoost_Combos_Scaling:
            case SkillEffect.Leader_AttackRecoveryBoost_Combos:
            case SkillEffect.Leader_AttackBoost_Attribute_Combos:
            case SkillEffect.Leader_AttackRecoveryBoost_Combos_Scaling:
            case SkillEffect.Leader_AttackBoost_Combos_Shield:
                skill = new ComboLeader();
                break;
            case SkillEffect.Leader_MultiSkills:
                skill = new MultiLeaderSkill();
                break;
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute:
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Scaling:
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Scaling_Dupe:
            case SkillEffect.Leader_AttackRecoveryBoost_NumberOfMatchedAttribute:
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Shield:
                skill = new NumMatchedBonus();
                break;
            case SkillEffect.Leader_AttackBoost_MatchAttributes:
            case SkillEffect.Leader_AttackRecoveryBoost_MatchAttributes:
            case SkillEffect.Leader_AttackBoost_MatchAttributes_Shield:
                skill = new RainbowBonus();
                break;
            default:
                skill = new Skill();
                break;

        }
        skill.fromObject(object); //Load generic fields
        skill.initSkill(); //Do skill specific init

        return skill;
    }

    public void fromObject(JsonObject object)
    {
        skillID = object.get("skill_id").getAsShort();
        skillType = object.get("skill_type").getAsShort();
        name = object.get("name").getAsString();
        description = object.get("description").getAsString();
        if (object.has("turn_min"))
        {
            minTurns = object.get("turn_min").getAsByte();
            maxTurns = object.get("turn_max").getAsByte();
        }
        JsonArray paramArray = object.get("other_fields").getAsJsonArray();
        for (int i = 0; i < paramArray.size(); i++)
            skillParams[i] = paramArray.get(i).getAsInt();
    }

    public void initSkill()
    {
        //stub
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setID(short id)
    {
        this.skillID = id;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }

    public int getLevels()
    {
        return maxTurns - minTurns + 1;
    }

    public int getTurnsAt(int level)
    {
        return maxTurns - (level - 1);
    }

    public static String attributesHelper(int bits, boolean noAll)
    {
        if (bits == 31 && !noAll)
            return "all";

        StringBuilder stringBuilder = new StringBuilder();
        if ((bits & 1) == 0)
            stringBuilder.append("Fire, ");
        if ((bits & 2) != 0)
            stringBuilder.append("Water, ");
        if ((bits & 4) != 0)
            stringBuilder.append("Wood, ");
        if ((bits & 8) != 0)
            stringBuilder.append("Light, ");
        if ((bits & 16) != 0)
            stringBuilder.append("Dark, ");
        if ((bits & 32) != 0)
            stringBuilder.append("Heal, ");
        if ((bits & 64) != 0)
            stringBuilder.append("Jammer, ");
        if ((bits & 128) != 0)
            stringBuilder.append("Poison, ");
        if ((bits & 256) != 0)
            stringBuilder.append("Mortal Poison, ");
        if ((bits & 512) != 0)
            stringBuilder.append("Bomb, ");

        if (stringBuilder.length() > 0)
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());

        return stringBuilder.toString();
    }

    public static int attributesCount(int bits)
    {
        int count = 0;
        if ((bits & 1) != 0)
            count++;
        if ((bits & 2) != 0)
            count++;
        if ((bits & 4) != 0)
            count++;
        if ((bits & 8) != 0)
            count++;
        if ((bits & 16) != 0)
            count++;
        if ((bits & 32) != 0)
            count++;
        if ((bits & 64) != 0)
            count++;
        if ((bits & 128) != 0)
            count++;
        if ((bits & 256) != 0)
            count++;
        if ((bits & 512) != 0)
            count++;

        return count;
    }

    public static boolean hasAttribute(int bits, int attribute)
    {
        int bit = 1 << attribute;
        return ((bits & bit) != 0);
    }

    public void updateDynamicMatch(Match match, DynamicMatchCriteria criteria, Card card)
    {
    }

    public int getDynamicMultiplier(DynamicMatchCriteria criteria, Card card)
    {
        return 100;
    }

    public void applyStaticEffects(Card card)
    {
    }
}
