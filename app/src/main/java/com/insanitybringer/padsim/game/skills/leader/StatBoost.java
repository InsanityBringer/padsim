package com.insanitybringer.padsim.game.skills.leader;

import com.insanitybringer.padsim.game.Skill;
import com.insanitybringer.padsim.game.skills.SkillEffect;

public class StatBoost extends Skill
{
    private int hpMult;
    private int atkMult;
    private int rcvMult;
    private int shieldAmount;
    private int shieldMask;

    private int attributeMask;
    private int typeMask;

    private int attributeMask2;
    private int typeMask2;
    private int hpMult2;
    private int atkMult2;
    private int rcvMult2;

    private boolean hpPenalty = false;

    //apparently min matched is also a passive stat now, so
    private int minMatchCount = 0;
    //... as is board size
    private boolean board7x6 = false;
    //..... and fucking move time fixing
    private int moveTimeFix = 0;
    //........ and extending move time
    private int timeExtend = 0;

    @Override
    public void initSkill()
    {
        switch (skillType)
        {
            case SkillEffect.Leader_AttackBoost_Attribute: //11
                attributeMask = 1 << (skillParams[0]);
                atkMult = skillParams[1];
                break;
            case SkillEffect.Leader_Shield: //16
                shieldMask = 31;
                shieldAmount = skillParams[0];
                break;
            case SkillEffect.Leader_Shield_VsAttribute: //17
                shieldMask = 1 << skillParams[0];
                shieldAmount = skillParams[1];
                break;
            case SkillEffect.Leader_AttackBoost_Type: //22
                typeMask = 1 << skillParams[0];
                atkMult = skillParams[1];
                break;
            case SkillEffect.Leader_HealthBoost_Type: //23
                typeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                break;
            case SkillEffect.Leader_RecoveryBoost_Type: //24
                typeMask = 1 << skillParams[0];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_AttackBoost: //26
                attributeMask = 31;
                atkMult = skillParams[0];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost: //28
                attributeMask = 1 << skillParams[0];
                atkMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_AllStatBoost: //29
                attributeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_HealthBoost_DoubleTypes: //30
                typeMask = 1 << skillParams[0];
                typeMask |= 1 << skillParams[1];
                hpMult = skillParams[2];
                break;
            case SkillEffect.Leader_AttackBoost_DoubleTypes: //31
                typeMask = 1 << skillParams[0];
                typeMask |= 1 << skillParams[1];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_Shield_VsAttribueDouble: //36
                shieldMask = 1 << skillParams[0];
                shieldMask |= 1 << skillParams[1];
                shieldAmount = skillParams[2];
                break;
            case SkillEffect.Leader_AttackBoost_DoubleAttribute: //40
                attributeMask = 1 << skillParams[0];
                attributeMask |= 1 << skillParams[1];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthAttackBoost_Attribute: //45
                attributeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthBoost_DoubleAttribute: //46
                attributeMask = 1 << skillParams[0];
                attributeMask |= 1 << skillParams[1];
                hpMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthBoost_Attribute: //48
                attributeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                break;
            case SkillEffect.Leader_RecoveryBoost_Attribute: //49
                attributeMask = 1 << skillParams[0];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_HealthAttackBoost_Type: //62
                typeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[1];
                break;
            case SkillEffect.Leader_HealthRecoveryBoost_Type: //63
                typeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_Type: //64
                typeMask = 1 << skillParams[0];
                atkMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_StatBoost_Type: //65
                typeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_HealthRecoveryBoost_Attribute: //67
                attributeMask = 1 << skillParams[0];
                hpMult = skillParams[1];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_AttackBoost_Attribute_Type: //69
                attributeMask = 1 << skillParams[0];
                typeMask = 1 << skillParams[1];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthAttackBoost_Attribute_Type: //73
                attributeMask = 1 << skillParams[0];
                typeMask = 1 << skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_Attribute_Type: //75
                attributeMask = 1 << skillParams[0];
                typeMask = 1 << skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[2];
                break;
            case SkillEffect.Leader_StatBoost_Attribute_Type: //76
                attributeMask = 1 << skillParams[0];
                typeMask = 1 << skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[2];
                rcvMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthAttackBoost_DoubleTypes: //77
                typeMask = 1 << skillParams[0];
                typeMask |= 1 << skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leaders_AttackRecoveryBoost_DoubleTypes: //79
                typeMask = 1 << skillParams[0];
                typeMask |= 1 << skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[2];
                break;
            case SkillEffect.Leader_HealthAttackBoost_DoubleAttributes: //111
                attributeMask = 1 << skillParams[0];
                attributeMask |= 1 << skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_StatBoost_DoubleAttributes: //114
                attributeMask = 1 << skillParams[0];
                attributeMask |= 1 << skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[2];
                rcvMult = skillParams[2];
                break;
            case SkillEffect.Leader_StatBoost:
                attributeMask = skillParams[0];
                typeMask = skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[3];
                rcvMult = skillParams[4];
                break;
            case SkillEffect.Leader_StatBoost_WithShield:
                attributeMask = skillParams[0];
                typeMask = skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[3];
                rcvMult = skillParams[4];
                shieldMask = skillParams[5];
                shieldAmount = skillParams[6];
                break;
            case SkillEffect.Leader_AttackBoost_Attribute_HealthPenalty:
                hpPenalty = true;
                typeMask = 1 << skillParams[1];
                hpMult = skillParams[0];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_AttackBoost_RecoveryPenalty:
                attributeMask = 31;
                atkMult = skillParams[0];
                rcvMult = skillParams[1];
                break;
            case SkillEffect.Leader_StatBoost_Attribute_DoubleBoosts:
                attributeMask = skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[3];
                attributeMask2 = skillParams[4];
                hpMult2 = skillParams[5];
                atkMult2 = skillParams[6];
                rcvMult2 = skillParams[7];
                break;
            case SkillEffect.Leader_StatBoost_Type_DoubleBoosts:
                typeMask = skillParams[0];
                hpMult = skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[3];
                typeMask2 = skillParams[4];
                hpMult2 = skillParams[5];
                atkMult2 = skillParams[6];
                rcvMult2 = skillParams[7];
                break;
            case SkillEffect.Leader_StatBoost_MinOrbMatchRequirement: //i fucking hate you gungho
                minMatchCount = skillParams[0];
                attributeMask = skillParams[1];
                typeMask = skillParams[2];
                hpMult = skillParams[3];
                atkMult = skillParams[4];
                rcvMult = skillParams[5];
                break;
            case SkillEffect.Leader_StatBoost_4SecMoveTime:
                moveTimeFix = skillParams[0];
                attributeMask = skillParams[1];
                typeMask = skillParams[2];
                hpMult = skillParams[3];
                atkMult = skillParams[4];
                rcvMult = skillParams[5];
                break;
            case SkillEffect.Leader_StatBoost_7x6Board:
                board7x6 = true;
                attributeMask = skillParams[0];
                typeMask = skillParams[1];
                hpMult = skillParams[2];
                atkMult = skillParams[3];
                rcvMult = skillParams[4];
                break;
            case SkillEffect.Leader_7x6Board:
                board7x6 = true;
                break;
            case SkillEffect.Leader_StatBoost_TimeExtend:
                timeExtend = skillParams[0];
                attributeMask = skillParams[1];
                typeMask = skillParams[2];
                hpMult = skillParams[3];
                atkMult = skillParams[4];
                rcvMult = skillParams[5];
                break;

        }
        //System.out.printf("ID: %d Name: %s Opcode: %d\n", skillID, name, skillType);
        //System.out.printf("Description: %s\n", description);
    }
}
