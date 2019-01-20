package com.insanitybringer.padsim.game.skills.leader;

import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.DynamicMatchCriteria;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.Match;
import com.insanitybringer.padsim.game.PadMath;
import com.insanitybringer.padsim.game.Skill;

public class MultiLeaderSkill extends Skill
{
    //GameState is used enough to warrant caching it
    private GameState gameState = GameState.GameStateSingleton.getGameState();
    public void updateDynamicMatch(Match match, DynamicMatchCriteria criteria, Card card)
    {
        int skill;
        for (int i = 0; i < 3; i++)
        {
            skill = skillParams[i];
            if (skill != 0)
                gameState.getSkill(skill).updateDynamicMatch(match, card.criteria[i], card);
        }
    }

    public int getDynamicMultiplier(DynamicMatchCriteria criteria, Card card)
    {
        int dynMult = 100;
        int skill;

        for (int i = 0; i < 3; i++)
        {
            skill = skillParams[i];
            if (skill != 0)
                dynMult = PadMath.fracMult(dynMult, gameState.getSkill(skill).getDynamicMultiplier(card.criteria[i], card));
        }

        return dynMult;
    }

}
