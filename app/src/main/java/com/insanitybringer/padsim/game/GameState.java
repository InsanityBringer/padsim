package com.insanitybringer.padsim.game;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Random;
import android.app.Application;

import com.insanitybringer.padsim.game.data.ElementReader;
import com.insanitybringer.padsim.game.data.SaveData;
import com.insanitybringer.padsim.renderer.Effect;
import com.insanitybringer.padsim.renderer.particle.*;
import com.insanitybringer.padsim.game.actions.*;

public class GameState
{

    public Application getApplication()
    {
        return application;
    }

    private Application application;

    private List<Monster> monsters = new ArrayList<>();
    private List<Monster> cleanMonsters = new ArrayList<>();
    private List<Skill> skills = new ArrayList<>();

    //Teams for the first, second, and third players in MP modes
    private Card[] team = new Card[6];
    private Card[] team2 = new Card[6];
    private Card[] team3 = new Card[6];
    public Enemy[] enemies = new Enemy[7];

    //State of the player's team
    public int currentHealth;
    public int maxHealth;
    private int dynamicAttackMultiplier = 100;
    private int numcombos = 0;
    public short massAttackAttribs = 0;

    //Game control
    private List<Action> actions;
    private List<Effect> effects;
    private List<DamageNumber> numbers;
    private List<Match> matches;
    private Random random = new Random();
    private int gameControlLock = 0; //Gameblock is a counter. When gameBlock is 0, the game can progress to the next phase
    //When gameBlock is not zero, the game cannot move forward. Things can ask to increment or decrement it
    public int turnFlags = 0; //Set to determine what is done when gameControlLock is 0

    public float screenHeight = 1.0f;
    //private LinkedList<Match> matchList = new LinkedList<Match>();

    //private GameOptions options;

    public Board getBoard()
    {
        return board;
    }

    private Board board;

    public long gameTicks = 0;

    //TODO: Debug code
    public String debug = "it fine";

    private GameState(Application application)
    {
        this.application = application;

        actions = new ArrayList<>();
        effects = new ArrayList<>();
        matches = new ArrayList<>();
        numbers = new ArrayList<>();
        board = new Board(0, this);
    }

    public void loadGameData()
    {
        ElementReader reader = new ElementReader(application);
        reader.FastLoadMonsters(monsters, cleanMonsters);
        System.gc();
        reader.FastLoadSkills(skills);
        team = SaveData.loadInternalTeam(application, 0);
        team2 = SaveData.loadInternalTeam(application, 1);
        team3 = SaveData.loadInternalTeam(application, 2);
    }

    public void startGame()
    {
        gameTicks = 0;
        enemies[0] = new Enemy(this, monsters.get(1463), 10);
        enemies[1] = new Enemy(this, monsters.get(1956), 10);
        enemies[2] = new Enemy(this, monsters.get(41), 10);

        enemies[0].packFormation(3, 0);
        enemies[1].packFormation(3, 1);
        enemies[2].packFormation(3, 2);

        computeHealth();
    }

    public void computeHealth()
    {
        maxHealth = 0;
        for (Card card : getTeam())
        {
            maxHealth += card.getModifiedHealth();
        }
        if (currentHealth == 0 || currentHealth > maxHealth)
            currentHealth = maxHealth;
    }

    public void update()
    {
        if (touchDown)
            touchProcess(touchX, touchY);
        else
            touchUpProcess();
        gameTicks++;
        board.update();
        Effect effect;
        Action action;
        DamageNumber number;
        for (int i = 0; i < actions.size(); i++)
        {
            action = actions.get(i);
            if (action.isLive())
            {
                action.tick();
            }
            else
            {
                //TODO: massive hack investigate better solution
                action.onKill();
                actions.remove(i);
                i--;
            }
        }
        for (int i = 0; i < effects.size(); i++)
        {
            effect = effects.get(i);
            if (effect.isLive())
            {
                effect.tick();
            }
            else
            {
                //TODO: massive hack investigate better solution
                effects.remove(i);
                i--;
            }
        }
        //I kinda hate doing this
        for (int i = 0; i < numbers.size(); i++)
        {
            number = numbers.get(i);
            if (number.isLive())
            {
                number.tick();
            }
            else
            {
                //TODO: massive hack investigate better solution
                numbers.remove(i);
                i--;
            }
        }
        for (Card card : getTeam())
        {
            card.tick();
        }
        for (Enemy enemy : enemies)
        {
            if (enemy != null)
                enemy.tick();
        }
        if (gameControlLock == 0)
        {
            if ((turnFlags & GameFlags.GF_Swapped) != 0)
            {
                turnFlags -= GameFlags.GF_Swapped;
                endPlayerTurn();
            }
            if ((turnFlags & GameFlags.GF_Matched) != 0)
            {
                turnFlags -= GameFlags.GF_Matched;
                //endAttack();
                endRound();
            }
        }
    }

    private boolean boardTouched = false;
    private float touchX, touchY;
    private boolean touchDown = false;
    public void touch(float x, float y)
    {
        touchX = x; touchY = y; touchDown = true;
    }

    public void touchUp()
    {
        touchDown = false;
    }

    private void touchProcess(float x, float y)
    {
        float boardx = 3f;
        float boardy = screenHeight - (105f * 5);
        boardTouched = board.touch(x - boardx, y - boardy);
    }

    private void touchUpProcess()
    {
        if (boardTouched)
        {
            board.touchUp();
            boardTouched = false;
        }
    }

    public void setScreenHeight(float screenHeight)
    {
        this.screenHeight = screenHeight;
    }

    public Card[] getTeam()
    {
        return team;
    }
    public Card[] getTeam(int i)
    {
        if (i == 0) return team;
        else if (i == 1) return team2;
        else return team3;
    }

    public void saveTeamPrivate(int teamid)
    {
        Card[] currentTeam = getTeam(teamid);
        SaveData.saveInternalTeam(application, teamid, currentTeam);
    }

    public void startMatches()
    {
        addLock();
    }

    public void dispatchComboEffect(Match match)
    {
        int attribBit = 1 << match.attribute;
        if (match.orbs.size() >= 5) //Handle mass attacks as bitflags checked on attack
            massAttackAttribs |= attribBit;
        numcombos++;
        //Track all the matches for later down the line
        matches.add(match);
        int count = 0;
        int[] tempPositions = new int[12];
        Card[] team = getTeam();
        Card card;
        card = team[0];
        for (int i = 0; i < 6; i++)
        {
            card = team[i];
            if (card.getID() != 0)
            {
                if (card.getAttribute() == match.attribute)
                {
                    match.attack = true;
                    tempPositions[count] = i;
                    Action matchAction = new AddMatchDamage(this, card, match, false);
                    actions.add(matchAction);
                    count++;
                }
                if (card.getSubAttribute() == match.attribute)
                {
                    match.attack = true;
                    tempPositions[count] = i;
                    count++;
                }
            }
        }
        Skill leaderSkill;
        card = team[0];
        if (card.getID() != 0 && card.getMonster().getLeaderID() != 0)
        {
            leaderSkill = skills.get(card.getMonster().getLeaderID());
            leaderSkill.updateDynamicMatch(match, card.criteria[0], card);
        }
        card = team[5];
        if (card.getID() != 0 && card.getMonster().getLeaderID() != 0)
        {
            leaderSkill = skills.get(card.getMonster().getLeaderID());
            leaderSkill.updateDynamicMatch(match, card.criteria[0], card);
        }
        int[] positions = new int[count];
        System.arraycopy(tempPositions, 0, positions, 0, count);
        MatchEffect effect = new MatchEffect(match, positions);
        effects.add(effect);
    }

    public void endCombo()
    {
        Card[] team = getTeam();
        Card card;
        card = team[0];
        Skill leaderSkill;
        dynamicAttackMultiplier = 100;
        if (card.getID() != 0 && card.getMonster().getLeaderID() != 0)
        {
            leaderSkill = skills.get(card.getMonster().getLeaderID());
            dynamicAttackMultiplier = PadMath.fracMult(100, leaderSkill.getDynamicMultiplier(card.criteria[0], card));
        }
        card = team[5];
        if (card.getID() != 0 && card.getMonster().getLeaderID() != 0)
        {
            leaderSkill = skills.get(card.getMonster().getLeaderID());
            dynamicAttackMultiplier = PadMath.fracMult(dynamicAttackMultiplier, leaderSkill.getDynamicMultiplier(card.criteria[0], card));
        }
        System.out.printf("Dynamic mult is %d\n", dynamicAttackMultiplier);
        board.fadeOrbs();
        actions.add(new TeamDamageBoost(this, numcombos));
        freeLock();
    }

    public void endAttack()
    {
        for (Card card : getTeam())
        {
            card.resetDamage();
        }
        numcombos = 0;
        massAttackAttribs = 0;
    }

    public void endPlayerTurn()
    {
        for (Enemy enemy : enemies)
        {
            if (enemy != null)
                if ((enemy.flags & Enemy.EnemyFlags.Dead) != 0)
                    enemy.flags |= Enemy.EnemyFlags.Gone;
        }
    }

    public void endRound()
    {
        board.unfadeOrbs();
        board.isActive = true;
    }

    public boolean finalDamageBoost()
    {
        int boosts = 0;

        for (Card card : getTeam())
        {
            if (card.getID() != 0)
                 if (card.finalDamageCalculation(numcombos, dynamicAttackMultiplier))
                    boosts++;
        }

        return boosts > 0;
    }

    public void addLock()
    {
        gameControlLock++;
    }

    public void freeLock()
    {
        gameControlLock--;
    }

    public void addAction(Action action)
    {
        actions.add(action);
    }

    public void addEffect(Effect effect)
    {
        effects.add(effect);
    }

    public void addNumber(DamageNumber number) { numbers.add(number); }

    public List<Monster> getMonsters()
    {
        return monsters;
    }

    public List<Monster> getCleanMonsters()
    {
        return cleanMonsters;
    }

    public Skill getSkill(int skill)
    {
        return skills.get(skill);
    }

    public List<Effect> getEffects() { return effects; }

    public List<DamageNumber> getNumbers() { return numbers; }

    public int getDynamicAttackMultiplier()
    {
        return dynamicAttackMultiplier;
    }

    public class GameFlags
    {
        public static final int GF_Attacked = 1; //An attack has been made, so when all actions end, handle post-attack actions (like changing ATT)
        public static final int GF_Swapped = 2; //A swap has been made on the board, so when all actions end, start the enemy turn
        public static final int GF_Matched = 3; //Matches have been made, so when all actions end, start the player post turn (increment skills, perform FUA, etc)

        //Some notes:
        //Laser and nuke actives cause GF_Attacked. This will handle monsters dying and changing attributes as needed. Multi lasers change att after all are done
        //A successful Orb Refresh will trigger Attacked and Matched, but not swapped. This means after attack processing will occur, and the player post turn will also, but enemies will not get turns.
        //Moving without causing any matches will cause only Swapped to occur, causing the monsters to attack, but no skill boosts or other post player turn to occur.
        //If no flag is set and the action list is empty, player control is granted.
    }

    public static int[][] matchupTable = {{0, -1, 1, 0, 0}, {1, 0, -1, 0, 0}, {-1, 1, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 1, 0}};

    public static class GameStateSingleton
    {
        private static GameState gameState;
        public static GameState getGameState(Application application)
        {
            if (gameState == null)
            {
                gameState = new GameState(application);
                gameState.loadGameData();
            }
            return gameState;
        }
        //Not safe to call if gameState hasn't been initialized, so call if it has been
        public static GameState getGameState()
        {
            return gameState;
        }
    }
}
