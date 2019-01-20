package com.insanitybringer.padsim.game.skills.leader;

import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.DynamicMatchCriteria;
import com.insanitybringer.padsim.game.Match;
import com.insanitybringer.padsim.game.Skill;
import com.insanitybringer.padsim.game.skills.SkillEffect;

public class RainbowBonus extends Skill
{
    private int atkMult;
    private int atkMultExtra;
    private int rcvMult;
    private int rcvMultExtra;
    private int shieldAmount = 0;
    private int totalAttributes = 0;

    private int minMatched = 0;

    private int attributes;

    @Override
    public void initSkill()
    {
        switch (skillType)
        {
            case SkillEffect.Leader_AttackBoost_MatchAttributes:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                atkMultExtra = skillParams[3];
                break;
            case SkillEffect.Leader_AttackRecoveryBoost_MatchAttributes:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                rcvMult = skillParams[3];
                atkMultExtra = skillParams[4];
                rcvMultExtra = skillParams[5];
                break;
            case SkillEffect.Leader_AttackBoost_MatchAttributes_Shield:
                attributes = skillParams[0];
                minMatched = skillParams[1];
                atkMult = skillParams[2];
                shieldAmount = skillParams[3];
                break;
        }
        totalAttributes = attributesCount(attributes);
    }

    @Override
    public void updateDynamicMatch(Match match, DynamicMatchCriteria criteria, Card card)
    {
        if (match.attribute < 5 && !match.attack) return;
        int bit = 1 << match.attribute;
        for (int i = 0; i < totalAttributes; i++)
        {
            if (hasAttribute(attributes, match.attribute))
            {
                //System.out.printf("Accepted attribute %d\n", match.attribute);
                if (criteria.criteria[i] == match.attribute)
                {
                    return;
                }
                else if (criteria.criteria[i] == -1)
                {
                    criteria.criteria[i] = match.attribute;
                    criteria.numCriteria++;
                    break;
                }
            }
            /*else
            {
                System.out.printf("Rejected attribute %d\n", match.attribute);
            }*/
        }
    }

    @Override
    public int getDynamicMultiplier(DynamicMatchCriteria criteria, Card card)
    {
        int numExtra = Math.min(criteria.numCriteria, totalAttributes) - minMatched;
        int modifier = atkMult + (atkMultExtra * numExtra);
        if (criteria.numCriteria >= minMatched)
        {
            return modifier;
        }
        return 100;
    }
}
