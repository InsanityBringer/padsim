package com.insanitybringer.padsim.game.skills.leader;

import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.DynamicMatchCriteria;
import com.insanitybringer.padsim.game.Match;
import com.insanitybringer.padsim.game.Skill;
import com.insanitybringer.padsim.game.skills.SkillEffect;

public class NumMatchedBonus extends Skill
{
    private int attributes;
    private int minMatched;
    private int maxMatched;

    private int atkMult;
    private int atkMultExtra;
    private int rcvMult;
    private int rcvMultExtra;
    private int shieldAmount;

    @Override
    public void initSkill()
    {
        switch (skillType)
        {
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                break;
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Scaling:
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Scaling_Dupe:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                atkMultExtra = skillParams[3];
                maxMatched = skillParams[4];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_NumberOfMatchedAttribute:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[3];
                atkMultExtra = skillParams[4];
                rcvMultExtra = skillParams[5];
                maxMatched = skillParams[6];
                break;
            case SkillEffect.Leader_AttackBoost_NumberOfMatchedAttribute_Shield:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                shieldAmount = skillParams[3];
                break;
        }
    }

    @Override
    public void updateDynamicMatch(Match match, DynamicMatchCriteria criteria, Card card)
    {
        System.out.println("Hi world");
        if (hasAttribute(attributes, match.attribute) && match.orbs.size() > criteria.numCriteria)
        {
            criteria.numCriteria = match.orbs.size();
        }
    }

    @Override
    public int getDynamicMultiplier(DynamicMatchCriteria criteria, Card card)
    {
        int numExtra = Math.min(criteria.numCriteria, maxMatched) - minMatched;
        int modifier = atkMult + (atkMultExtra * numExtra);
        if (criteria.numCriteria >= minMatched)
        {
            System.out.printf("dynMult: %d\n", modifier);
            return modifier;
        }
        return 100;
    }
}
