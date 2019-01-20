package com.insanitybringer.padsim.game;

import java.util.Random;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.insanitybringer.padsim.renderer.UpdateRenderer;

public class Card
{
    private Monster monster;
    private int level;
    private int awokenLevel;
    private int skillLevels = 1;

    private int health;
    private int attack;
    private int recovery;

    private int hpPowerups;
    private int atkPowerups;
    private int rcvPowerups;

    private int superAwoken = -1; //0 based for convenience, use -1 for no super awoken

    private Card inheritCard;

    //In battle information
    private int skillCharge;
    private int staticMultHP = 100;
    private int staticMultATK = 100;
    private int staticMultRCV = 100;

    //Gameplay attributes
    private int attribute;
    private int subAttribute;
    private int damage; //The actual accumulated damage
    //3 dynamic match criteria for 3 effect leader skills. It's a worrying solution,
    //since skills can have a fairly indefinite amount of leader skills, but this makes it easier
    public int attackAttribute; //Since the attack might not match the card's attribute
    public DynamicMatchCriteria[] criteria = new DynamicMatchCriteria[3];

    //Positioning and rendering
    private int position = 0;
    public int posX = 0;
    public int posY = 0;
    NumberInterpolator damageNumber = new NumberInterpolator(0, 0, 0);
    public boolean showNumber = false;
    public int flags = 0;

    //hacks
    private int toNumTimer = 0;
    private Match nextMatch;

    private Random rand = new Random();

    private int[] latents = new int[6];

    private Card(int position)
    {
        this.position = position;
        if (position != -1)
        {
            posX = 5 + (position * 106);
            posY = 0;
            for (int i = 0; i < 3; i++)
                criteria[i] = new DynamicMatchCriteria();
        }
    }

    public Card(Monster monster, int position)
    {
        this.position = position;
        this.monster = monster;
        if (position != -1)
            this.inheritCard = new Card(GameState.GameStateSingleton.getGameState().getMonsters().get(0), -1);
        attribute = monster.getAttribute();
        subAttribute = monster.getSubAttribute();
        if (position != -1)
        {
            if (monster.getId() != 0)
            {
                for (int i = 0; i < 6; i++)
                {
                    if (i < 5)
                    {
                        latents[i] = rand.nextInt(36) + 1;
                        while (latents[i] == 13 || latents[i] == 14 || latents[i] == 15)
                            latents[i] = rand.nextInt(36) + 1;

                        if (latents[i] >= 12)
                        {
                            i++;
                            latents[i] = LatentAwoken.LATENT_PLACEHOLDER;
                        }
                    } else
                    {
                        latents[i] = rand.nextInt(11) + 1;
                    }
                }

                setLevel(monster.getMaxLevel());
                setHpPowerups(rand.nextInt(100));
                setAtkPowerups(rand.nextInt(100));
                setRcvPowerups(rand.nextInt(100));
                if (monster.getNumAwokens() != 0)
                    awokenLevel = rand.nextInt(monster.getNumAwokens());
            }
            posX = 18 + (position * 103);
            posY = 0;
            for (int i = 0; i < 3; i++)
                criteria[i] = new DynamicMatchCriteria();
        }
        else
        {
            setLevel(monster.getMaxLevel());
        }
    }

    public void setLevel(int level)
    {
        //System.out.printf("Level set to %d\n", level);
        this.level = level;
        computeStats();
    }

    public void computeStats()
    {
        int statlevel = Math.min(99, level);
        int basehealth = monster.getHPAt(statlevel);
        int baseattack = monster.getATKAt(statlevel);
        int baserecovery = monster.getRCVAt(statlevel);
        if (level > statlevel) //Apply limit break
        {
            float boostAmount = (level - statlevel) / 11.0f;
            float maxBoost = PadMath.getFloatFromFixed(monster.getLimitBreakBoost()) * boostAmount;
            basehealth += Math.round(basehealth * maxBoost);
            baseattack += Math.round(baseattack * maxBoost);
            baserecovery += Math.round(baserecovery * maxBoost);
        }
        //System.out.printf("Template health is %d %d %d\n", basehealth, baseattack, baserecovery);
        int healthlatents = Math.round(countLatents(LatentAwoken.LATENT_HPBOOST) * .015f * basehealth);
        int attacklatents = Math.round(countLatents(LatentAwoken.LATENT_ATKBOOST) * .01f * baseattack);
        int recoverylatents = Math.round(countLatents(LatentAwoken.LATENT_RCVBOOST) * .05f * baserecovery);
        this.health = basehealth + healthlatents + (hpPowerups * 10);
        this.attack = baseattack + attacklatents + (atkPowerups * 5);
        this.recovery = baserecovery + recoverylatents + (rcvPowerups * 3);
        if (this.inheritCard != null && this.inheritCard.getID() != 0 && this.inheritCard.attribute == attribute)
        {
            int hpbonus = inheritCard.getMonster().getHPAt(inheritCard.getLevel());
            int atkbonus = inheritCard.getMonster().getATKAt(inheritCard.getLevel());
            int rcvbonus = inheritCard.getMonster().getRCVAt(inheritCard.getLevel());

            if (inheritCard.getPowerups() == 297)
            {
                hpbonus += 990;
                atkbonus += 495;
                rcvbonus += 297;
            }

            hpbonus = Math.round(hpbonus / 10.0f);
            atkbonus = Math.round(atkbonus / 20.0f);
            rcvbonus = Math.round(rcvbonus * (3.0f / 20.f));

            //System.out.printf("Inherit bonus is %d %d %d\n", hpbonus, atkbonus, rcvbonus);

            this.health += hpbonus;
            this.attack += atkbonus;
            this.recovery += rcvbonus;
        }
        this.health += monster.countAwokens(AwokenSkill.AWOKEN_HPBOOST, awokenLevel) * 500;
        this.attack += monster.countAwokens(AwokenSkill.AWOKEN_ATKBOOST, awokenLevel) * 100;
        this.recovery += monster.countAwokens(AwokenSkill.AWOKEN_RCVBOOST, awokenLevel) * 200;
        if (inheritCard != null)
        {
            this.health += inheritCard.monster.countAwokens(AwokenSkill.AWOKEN_HPBOOST, 9) * 500;
            this.attack += inheritCard.monster.countAwokens(AwokenSkill.AWOKEN_ATKBOOST, 9) * 100;
            this.recovery += inheritCard.monster.countAwokens(AwokenSkill.AWOKEN_RCVBOOST, 9) * 200;
        }
    }

    public void setLatent(int num, int id)
    {
        latents[num] = id;
        if (num < 5 && id > 12)
        {
            //latents[num+1] = LatentAwoken.LATENT_PLACEHOLDER;
            setLatent(num+1, LatentAwoken.LATENT_PLACEHOLDER);
        }
        else if (num < 5) //Update the next latent, if it's a placeholder
        {
            if (latents[num+1] == LatentAwoken.LATENT_PLACEHOLDER)
                setLatent(num+1, LatentAwoken.LATENT_NONE);
                //latents[num+1] = LatentAwoken.LATENT_NONE;
        }
        computeStats();
    }

    public Monster getMonster()
    {
        return monster;
    }

    public void setMonster(Monster monster)
    {
        this.monster = monster;
        this.attribute = monster.getAttribute();
        this.subAttribute = monster.getSubAttribute();
        if (monster.getId() == 0)
        {
            hpPowerups = atkPowerups = rcvPowerups = 0;
            //Clear the inherit. Don't try to clear the inherit's inherit.
            if (inheritCard != null)
            {
                inheritCard.setMonster(monster);
            }
            for (int i = 0; i < 6; i++)
            {
                latents[i] = 0;
            }
        }
        else if (monster.getActiveID() == 0)
        {
            if (inheritCard != null)
            {
                //Clear inherit if I have no skill
                inheritCard.setMonster(GameState.GameStateSingleton.getGameState().getMonsters().get(0));
            }
        }
        Skill newSkill = GameState.GameStateSingleton.getGameState().getSkill(monster.getActiveID());
        if (skillLevels > newSkill.getLevels())
            setSkillLevels(newSkill.getLevels());
        computeStats();
    }

    public int countLatents(int id)
    {
        int count = 0;
        for (int i = 0; i < 6; i++)
        {
            //Cheap way of counting statboost awokens. Pretend they are always HP, ATK, or RCV single latents
            if (latents[i] == LatentAwoken.LATENT_STATBOOST && (id == LatentAwoken.LATENT_HPBOOST || id == LatentAwoken.LATENT_ATKBOOST || id == LatentAwoken.LATENT_RCVBOOST))
            {
                count++;
            }
            //All tier two latents are 3x the value of single latents, so here's another hack
            else if (latents[i] == LatentAwoken.LATENT_SUPERHPBOOST && id == LatentAwoken.LATENT_HPBOOST)
            {
                count += 3;
            }
            else if (latents[i] == LatentAwoken.LATENT_SUPERATKBOOST && id == LatentAwoken.LATENT_ATKBOOST)
            {
                count += 3;
            }
            else if (latents[i] == LatentAwoken.LATENT_SUPERRCVBOOST && id == LatentAwoken.LATENT_RCVBOOST)
            {
                count += 3;
            }
            else if (latents[i] == id)
            {
                count++;
            }
        }
        return count;
    }

    //Though GSON's automatic serialization would be very workable here, problems arise in the
    //instance of Monster driving the card, which gets serialized too. rip.
    public JsonObject generateObject()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("monsterID", monster.getId());
        obj.addProperty("level", level);
        obj.addProperty("awokenLevel", awokenLevel);
        obj.addProperty("skillLevel", skillLevels);
        obj.addProperty("health", health);
        obj.addProperty("attack", attack);
        obj.addProperty("recovery", recovery);
        obj.addProperty("hpPowerups", hpPowerups);
        obj.addProperty("atkPowerups", atkPowerups);
        obj.addProperty("rcvPowerups", rcvPowerups);
        obj.addProperty("superAwoken", superAwoken);
        JsonArray latentArray = new JsonArray();
        for (int i = 0; i < 6; i++)
            latentArray.add(latents[i]);
        obj.add("latents", latentArray);
        if (inheritCard != null)
            obj.add("inheritCard", inheritCard.generateObject());

        return obj;
    }

    public static Card generateCardFromObject(int position, JsonObject obj)
    {
        Card card = new Card(position);
        card.monster = GameState.GameStateSingleton.getGameState().getMonsters().get(obj.get("monsterID").getAsInt());
        card.attribute = card.monster.getAttribute(); card.subAttribute = card.monster.getSubAttribute();
        card.level = obj.get("level").getAsInt();
        card.awokenLevel = obj.get("awokenLevel").getAsInt();
        card.skillLevels = obj.get("skillLevel").getAsInt();
        card.health = obj.get("health").getAsInt();
        card.attack = obj.get("attack").getAsInt();
        card.recovery = obj.get("recovery").getAsInt();
        card.hpPowerups = obj.get("hpPowerups").getAsInt();
        card.atkPowerups = obj.get("atkPowerups").getAsInt();
        card.rcvPowerups = obj.get("rcvPowerups").getAsInt();
        card.superAwoken = obj.get("superAwoken").getAsInt();
        JsonArray latentArray = obj.get("latents").getAsJsonArray();
        for (int i = 0; i < 6; i++)
            card.latents[i] = latentArray.get(i).getAsInt();

        if (obj.has("inheritCard"))
            card.inheritCard = generateCardFromObject(-1, obj.get("inheritCard").getAsJsonObject());

        card.computeStats();

        return card;
    }

    public boolean finalDamageCalculation(int combos, int dynamicMultATK)
    {
        boolean boosted = false;
        int comboMult = 100 + 25 * (combos - 1);
        damage = PadMath.wholeMult(damage, comboMult);

        if (combos >= 7)
        {
            int num7c = monster.countAwokens(AwokenSkill.AWOKEN_ENHANCEDCOMBOS, awokenLevel);
            for (int c = 0; c < num7c; c++)
            {
                damage *= 2;
                boosted = true;
            }
        }

        if (combos >= 10)
        {
            int num10c = monster.countAwokens(AwokenSkill.AWOKEN_SUPERENHANCEDCOMBOS, awokenLevel);
            for (int c = 0; c < num10c; c++)
            {
                damage *= 5;
                boosted = true;
            }
        }

        if (staticMultATK != 100)
        {
            damage = PadMath.wholeMult(damage, staticMultATK, 0);
            boosted = true;
        }
        if (dynamicMultATK != 100)
        {
            damage = PadMath.wholeMult(damage, dynamicMultATK, 0);
            boosted = true;
        }

        if (boosted)
        {
            damageNumber.updateTo(damage, 15);
        }

        Skill leaderSkill = GameState.GameStateSingleton.getGameState().getSkill(monster.getLeaderID());
        if ((position == 0 || position == 5 ) && leaderSkill.getDynamicMultiplier(criteria[0], this) != 100)
        {
            flags |= CardFlags.CF_Boost;
        }

        return boosted;
    }

    public void addDamage(int damage, int attackAttribute)
    {
        //Update both the displayed damage and the current damage.
        damageNumber.addTo(damage, 15);
        this.damage += damage;
        this.attackAttribute = attackAttribute;
        showNumber = true;
    }

    public int findTarget(Enemy[] enemies, boolean allowDead)
    {
        Enemy enemy;
        int target = -1;
        int bestDefense = Integer.MAX_VALUE;
        //Find an enemy that is weak to my attribute
        for (int i = 0; i < 7; i++)
        {
            enemy = enemies[i];
            if (enemy != null && (enemy.flags & Enemy.EnemyFlags.Gone) == 0)
            {
                if (enemy.health > 0 || allowDead)
                {
                    if (GameState.matchupTable[attribute][enemy.attribute] == 1)
                    {
                        if (enemy.defense <= bestDefense)
                        {
                            target = i;
                            bestDefense = enemy.defense;
                        }
                    }
                }
            }
        }
        if (target == -1) //Didn't find a weak target, try a neutral one
        {
            //System.out.println("Didn't find weak target");
            bestDefense = Integer.MAX_VALUE;
            for (int i = 0; i < 7; i++)
            {
                enemy = enemies[i];
                if (enemy != null && (enemy.flags & Enemy.EnemyFlags.Gone) == 0)
                {
                    if (enemy.health > 0 || allowDead)
                    {
                        if (GameState.matchupTable[attribute][enemy.attribute] == 0)
                        {
                            if (enemy.defense <= bestDefense)
                            {
                                target = i;
                                bestDefense = enemy.defense;
                            }
                        }
                    }
                }
            }
        }
        if (target == -1) //Didn't find a neutral target, so as a failsafe attack the lowest defense thing
        {
            //System.out.println("Didn't find neutral target");
            bestDefense = Integer.MAX_VALUE;
            for (int i = 0; i < 7; i++)
            {
                enemy = enemies[i];
                if (enemy != null && (enemy.flags & Enemy.EnemyFlags.Gone) == 0)
                {
                    if (enemy.health > 0 || allowDead)
                    {
                        if (enemy.defense <= bestDefense)
                        {
                            target = i;
                            bestDefense = enemy.defense;
                        }
                    }
                }
            }
        }

        if (target == -1 && !allowDead)
        {
            target = findTarget(enemies, true);
        }
        return target;
    }

    public void displayCombo(int combo)
    {
        if (damage != 0)
        {
            //System.out.printf("Got combo %d. Damage is %d\n", combo, damage);
            int newDamage = PadMath.wholeMult(damage, 100 + (25 * (combo - 1)));
            damageNumber.updateTo(newDamage, 15);
        }
    }

    public void addMatch(Match match, boolean sub)
    {
        if (!sub)
        {
            if (match.attribute == attribute)
            {
                int damage = PadMath.wholeMult(attack, 25 + (match.orbs.size() * 25));
                addDamage(damage, this.attribute);
            }
        }
    }

    public void resetDamage()
    {
        damageNumber.replace(0, 0, 1);
        showNumber = false;
        damage = 0;
        for (int i = 0; i < 3; i++)
        {
            criteria[i].clear();
        }
    }

    public void tick()
    {
        damageNumber.tick();
    }

    public void initGameData()
    {
        //Init all the in-dungeon data for this next game
        damageNumber.replace(0, 0, 0);
        damage = 0;
        showNumber = false;
    }

    public int getModifiedHealth()
    {
        return PadMath.wholeMult(health, staticMultHP);
    }

    public float getBoost()
    {
        return 1.0f - (damageNumber.getTicks() / 15.0f);
    }

    public int getDisplayedDamage()
    {
        return damageNumber.getCurrent();
    }

    public int getLevel()
    {
        return level;
    }

    public int getAwokenLevel()
    {
        return awokenLevel;
    }

    public void setAwokenLevel(int awokenLevel)
    {
        this.awokenLevel = awokenLevel;
        computeStats(); //for stat boost awokens
    }

    public int getSuperAwoken()
    {
        return superAwoken;
    }

    public void setSuperAwoken(int superAwoken)
    {
        this.superAwoken = superAwoken;
        computeStats();
    }

    public int getSkillLevels()
    {
        return skillLevels;
    }

    public void setSkillLevels(int skillLevels)
    {
        this.skillLevels = skillLevels;
    }

    public int getHealth()
    {
        return health;
    }

    public int getAttack()
    {
        return attack;
    }

    public int getRecovery()
    {
        return recovery;
    }

    public int getHpPowerups()
    {
        return hpPowerups;
    }

    public void setHpPowerups(int hpPowerups)
    {
        this.hpPowerups = hpPowerups;
        computeStats();
    }

    public int getAtkPowerups()
    {
        return atkPowerups;
    }

    public void setAtkPowerups(int atkPowerups)
    {
        this.atkPowerups = atkPowerups;
        computeStats();
    }

    public int getRcvPowerups()
    {
        return rcvPowerups;
    }

    public void setRcvPowerups(int rcvPowerups)
    {
        this.rcvPowerups = rcvPowerups;
        computeStats();
    }

    public int getPowerups()
    {
        return hpPowerups + atkPowerups + rcvPowerups;
    }

    public Card getInheritCard()
    {
        return inheritCard;
    }

    public void setInheritCard(Card inheritCard)
    {
        this.inheritCard = inheritCard;
    }

    public int getSkillCharge()
    {
        return skillCharge;
    }

    public void setSkillCharge(int skillCharge)
    {
        this.skillCharge = skillCharge;
    }

    public int getStaticMultHP()
    {
        return staticMultHP;
    }

    public void setStaticMultHP(int staticMultHP)
    {
        this.staticMultHP = staticMultHP;
    }

    public int getStaticMultATK()
    {
        return staticMultATK;
    }

    public void setStaticMultATK(int staticMultATK)
    {
        this.staticMultATK = staticMultATK;
    }

    public int getStaticMultRCV()
    {
        return staticMultRCV;
    }

    public void setStaticMultRCV(int staticMultRCV)
    {
        this.staticMultRCV = staticMultRCV;
    }

    public int getAttribute()
    {
        return attribute;
    }

    public void setAttribute(int attribute)
    {
        this.attribute = attribute;
    }

    public int getSubAttribute()
    {
        return subAttribute;
    }

    public void setSubAttribute(int subAttribute)
    {
        this.subAttribute = subAttribute;
    }

    public int getDamage()
    {
        return damage;
    }

    public void setDamage(int damage)
    {
        this.damage = damage;
    }

    public int getPosX()
    {
        return posX;
    }

    public void setPosX(int posX)
    {
        this.posX = posX;
    }

    public int getPosY()
    {
        return posY;
    }

    public void setPosY(int posY)
    {
        this.posY = posY;
    }

    public boolean isShowNumber()
    {
        return showNumber;
    }

    public void setShowNumber(boolean showNumber)
    {
        this.showNumber = showNumber;
    }

    public int getToNumTimer()
    {
        return toNumTimer;
    }

    public void setToNumTimer(int toNumTimer)
    {
        this.toNumTimer = toNumTimer;
    }

    public Match getNextMatch()
    {
        return nextMatch;
    }

    public void setNextMatch(Match nextMatch)
    {
        this.nextMatch = nextMatch;
    }

    public int getLatent(int id)
    {
        return latents[id];
    }

    public int getID()
    {
        return monster.getId();
    }
    //Returns the height of the card, not relative to the bottom
    public float getRenderHeight()
    {
        return GameState.GameStateSingleton.getGameState().screenHeight - UpdateRenderer.TeamBaseline - posY;
    }

    public class CardFlags
    {
        public final static int CF_Boost = 1; //When boosting the damage number, cause this monster to boost up also
    }
}
