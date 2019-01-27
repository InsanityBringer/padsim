package com.insanitybringer.padsim.game;

import java.util.ArrayList;

public class DungeonSpawner
{
    private class SpawnInfo
    {
        //ID of the monster to spawn
        public short id;
        //Level of the monster to spawn
        public short level;
        //The weight of this encounter to spawn vs. others
        public int weight;

        //i really need to stop passing around gamestate everywhere aaa
        public Enemy generateEnemy(GameState gameState)
        {
            return new Enemy(gameState, gameState.getMonsters().get(id), level);
        }
    }

    private class SpawnPool
    {
        //This pool's weight, for nested pools
        public int weight;
        //How many rolls to make for this particular spawn
        //If numRolls is <0, instead add all subpools and spawns to the enemy list
        public int numRolls;

        public ArrayList<SpawnPool> subPools = new ArrayList<>();
        public ArrayList<SpawnInfo> spawns = new ArrayList<>();
        //TODO: Investagate better ways of weighted spawn
        private int totalWeight;
        private int[] weightList; //Contains IDs into the subPools list and the spawns list,
                                // + is spawn, - is sub pool

        //TODO: parsing

        public void buildWeightList()
        {
            weightList = new int[totalWeight];
            int weightListIndex = 0;
            for (int i = 0; i < subPools.size(); i++)
            {
                for (int w = 0; w < subPools.get(i).weight; w++)
                    weightList[weightListIndex++] = -i;
            }

            for (int i = 0; i < spawns.size(); i++)
            {
                for (int w = 0; w < spawns.get(i).weight; w++)
                    weightList[weightListIndex++] = i;
            }
        }

        public void addSpawns(GameState gameState, ArrayList<Enemy> enemies)
        {
            if (numRolls < 0) //Handle fixed formation encounters
            {
                for (int i = 0; i < subPools.size(); i++)
                {
                    subPools.get(i).addSpawns(gameState, enemies);
                }

                for (int i = 0; i < spawns.size(); i++)
                {
                    spawns.get(i).generateEnemy(gameState);
                }
            }
            else
            {
                for (int roll = 0; roll < numRolls; roll++)
                {
                    int id = gameState.random.nextInt(weightList.length);
                    int spawnid = weightList[id];
                    if (spawnid < 0)
                    {
                        subPools.get(-spawnid).addSpawns(gameState, enemies);
                    } else
                    {
                        enemies.add(spawns.get(id).generateEnemy(gameState));
                    }
                }
            }
        }
    }
}
