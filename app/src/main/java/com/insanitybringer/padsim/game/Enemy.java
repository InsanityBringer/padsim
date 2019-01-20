package com.insanitybringer.padsim.game;

public class Enemy
{
    //Packing of monster icons, as the X coordinate of their centers.
    private final static float[] oneMonsterPacking = {320.0f, 0.0f, 0.0f, 0.0f, 0.0f};
    private final static float[] twoMonsterPacking = {250.0f, 390.0f, 0.0f, 0.0f, 0.0f};
    private final static float[] threeMonsterPacking = {195.0f, 320.0f, 445.0f, 0.0f, 0.0f};
    private final static float[] fourMonsterPacking = {110.0f, 250.0f, 390.0f, 530.0f, 0.0f};
    private final static float[] fiveMonsterPacking = {75.0f, 195.0f, 320.0f, 445.0f, 565.0f};

    public final static float[][] packing = {oneMonsterPacking, twoMonsterPacking, threeMonsterPacking, fourMonsterPacking, fiveMonsterPacking};

    private Monster monsterType;
    private GameState gameState;

    public int health;
    public int spawnHealth;
    public int defense;
    public int attack;

    private int variable = 0;
    private int skillConditionVariable = 0;
    private int turnCounter = 0;
    private int attackCounter;
    private int countdown;

    public int attribute, subAttribute;
    public float x, y;
    public float alpha = 0.0f;
    public int flags = 0;
    public NumberInterpolator displayHealth;

    private long fadeInStart = 0;

    private Card killer = null;

    public Enemy(GameState gameState, Monster monster, int level)
    {
        this.monsterType = monster;
        this.gameState = gameState;

        health = spawnHealth = monster.getEHPAt(level);
        defense = monster.getEDEFAt(level);
        attack = monster.getEATKAt(level);
        defense = 0;

        flags |= EnemyFlags.EF_Spawned;
        attribute = monster.getAttribute();
        subAttribute = monster.getSubAttribute();

        displayHealth = new NumberInterpolator(health, health, 0);
    }

    public void packFormation(int numMonsters, int pos)
    {
        y = 0.0f;
        x = packing[numMonsters-1][pos] - 50.0f;
        //TODO: Scale fade in time based on how many enemies there are
        fadeInStart = gameState.gameTicks + (22 * pos);
    }

    public void tick()
    {
        if ((flags & EnemyFlags.EF_Spawned) != 0)
        {
            if (gameState.gameTicks > fadeInStart)
            {
                //Fade in 8 ticks
                alpha = Math.min(1.0f, alpha + .125f);
                if (alpha >= 0.9999) //Faded in entirely
                {
                    flags -= EnemyFlags.EF_Spawned;
                }
            }
        }
        else if ((flags & EnemyFlags.Dying) != 0)
        {
            //Fade out in 30 ticks
            if (displayHealth.getCurrent() == 0)
            {
                alpha = Math.max(0.0f, alpha - .033f);
                if (alpha < 0.0001) //Faded in entirely
                {
                    flags -= EnemyFlags.Dying;
                    //gameState.freeLock();
                }
            }
        }
        displayHealth.tick();
    }

    public int getID()
    {
        return monsterType.getId();
    }

    public void takeDamage(Card card, int damage, int attackAttribute, int attackFlags)
    {
        int finalDamage = computeDamage(card, damage, attackAttribute, attackFlags);
        health = Math.max(health - finalDamage, 0); //Cannot overkill monsters, lowest possible HP is 0
        if (health == 0) //I died
        {
            if (killer == null) //Set the killer to know when it's time to fade out
            {
                killer = card;
            }
        }

        System.out.printf("Enemy %s took %d damage from %s, has %d hp left\n", monsterType.getName(), damage, card.getMonster().getName(), health);
    }

    public void takeVisualDamage(Card card, int damage)
    {
        displayHealth.addTo(-damage, 15);
        if (card == killer)
        {
            killMonster();
        }
    }

    public int computeDamage(Card card, int damage, int attackAttribute, int attackFlags)
    {
        int finalDamage = damage;
        if (attackAttribute != -1) //Apply attribute effectiveness
        {
            int effectiveness = GameState.matchupTable[card.getAttribute()][attribute];
            if (effectiveness == 1) //Double damage done if attribute is effective
                finalDamage = PadMath.wholeMult(finalDamage, 200);
            else if (effectiveness == -1) //Half damage if ineffective
                finalDamage = PadMath.wholeMult(finalDamage, 50);
        }
        finalDamage = Math.max(finalDamage - defense, 1);
        return finalDamage;
    }

    public DamageNumber getDamageNumber(Card card, int damage, int attackAttribute, int attackFlags)
    {
        int baseDamage = Math.max(damage - defense, 1);
        int finalDamage = computeDamage(card, damage, attackAttribute, attackFlags);

        DamageNumber number = new DamageNumber(baseDamage, finalDamage, attackAttribute, x + 50.0f, y + 50.f + 150.0f, 0);
        return number;
    }

    public void killMonster()
    {
        flags |= EnemyFlags.Dying;
        flags |= EnemyFlags.Dead;
        //gameState.addLock();
    }

    public boolean isDead()
    {
        return (flags & EnemyFlags.Dead) != 0;
    }

    public class EnemyFlags
    {
        public final static int EF_Spawned = 1; //Spawning in, fade in
        public final static int Dying = 2; //Dying, fade out
        public final static int Dead = 4; //Dead, skip over this encounter for calculations
        public final static int Gone = 8; //Completely gone. Never think about me again, for fuck's sake
    }

    public class DamageFlags
    {
        public final static int Combo = 1; //Damage is done by combo, triggers combo shields based on global combo count
        public final static int IgnoreDefense = 2; //Damage ignores defense, for guard break awoken and lasers
    }
}
