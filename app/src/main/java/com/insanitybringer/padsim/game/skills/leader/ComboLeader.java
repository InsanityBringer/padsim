package com.insanitybringer.padsim.game.skills.leader;

import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.DynamicMatchCriteria;
import com.insanitybringer.padsim.game.Match;
import com.insanitybringer.padsim.game.Skill;
import com.insanitybringer.padsim.game.skills.SkillEffect;

public class ComboLeader extends Skill
{
    private int atkMult;
    private int atkMultExtra;
    private int rcvMult;
    private int rcvMultExtra;
    private int shieldAmount = 0;

    private int minCombos = 0;
    private int maxCombos = 0;

    private int attributes;

    @Override
    public void initSkill()
    {
        switch (skillType)
        {
            case SkillEffect.Leader_AttackBoost_Combos:
                attributes = 31;
                atkMult = skillParams[1];
                minCombos = maxCombos = skillParams[0];
                break;
            case SkillEffect.Leader_AttackBoost_Combos_Scaling:
                attributes = 31;
                atkMult = skillParams[1];
                minCombos = skillParams[0];
                atkMultExtra = skillParams[2];
                maxCombos = skillParams[3];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_Combos:
                attributes = 31;
                if (skillParams[1] != 0)
                    atkMult = skillParams[3];
                if (skillParams[2] != 0)
                    rcvMult = skillParams[3];
                minCombos = maxCombos = skillParams[0];
                break;
            case SkillEffect.Leader_AttackBoost_Attribute_Combos:
                attributes = skillParams[1];
                if (skillParams[2] != 0)
                    atkMult = skillParams[4];
                if (skillParams[3] != 0)
                    rcvMult = skillParams[4];
                minCombos = maxCombos = skillParams[0];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_Combos_Scaling:
                attributes = 31;
                atkMult = skillParams[1];
                rcvMult = skillParams[2];
                minCombos = skillParams[0];
                atkMultExtra = skillParams[3];
                rcvMultExtra = skillParams[4];
                maxCombos = skillParams[5];
                break;
            case SkillEffect.Leader_AttackBoost_Combos_Shield:
                attributes = 31;
                minCombos = maxCombos = skillParams[0];
                atkMult = skillParams[1];
                shieldAmount = skillParams[2];
                break;
        }
    }

    @Override
    public void updateDynamicMatch(Match match, DynamicMatchCriteria criteria, Card card)
    {
        criteria.numCriteria++; //combos don't care about anything else
    }

    @Override
    public int getDynamicMultiplier(DynamicMatchCriteria criteria, Card card)
    {
        int numExtra = Math.min(criteria.numCriteria, maxCombos) - minCombos;
        int modifier = atkMult + (atkMultExtra * numExtra);
        if (criteria.numCriteria >= minCombos)
        {
            //Console.WriteLine("dynMult: {0}", modifier);
            return modifier;
        }
        return 100;
    }
}
