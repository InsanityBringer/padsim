package com.insanitybringer.padsim.game;

import java.util.ArrayList;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Dungeon
{
    private class SpawnInfo
    {
        //ID of the monster to spawn
        public short id;
        //Level of the monster to spawn
        public short level;
        //The weight of this encounter to spawn vs. others
        public int weight;

        public SpawnInfo(JsonObject spawnInfo, int weight)
        {
            id = spawnInfo.get("id").getAsShort();
            level = spawnInfo.get("level").getAsShort();
            this.weight = weight; //Already extracted earlier in the pipeline
        }

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
        private int totalWeight;
        private int[] weightList; //Contains IDs into the subPools list and the spawns list,
                                // + is spawn, - is sub pool

        //TODO: Investigate whether or not GSON's reflection based automatic deserialization
        //can work here. It would probably require some modifications to the way data is stored
        //in my dungeon JSON files, but overall should be possible and might be faster?
        public SpawnPool(JsonObject pool, int weight)
        {
            //weight = pool.get("weight").getAsInt();
            this.weight = weight;
            numRolls = pool.get("numRolls").getAsInt();
            JsonArray poolContents = pool.get("contents").getAsJsonArray();
            JsonObject content; String type; int spawnWeight;
            for (JsonElement elem : poolContents)
            {
                content = elem.getAsJsonObject();
                type = content.get("type").getAsString();
                spawnWeight = content.get("weight").getAsInt();
                //ew
                if (type.compareTo("spawn") == 0)
                {
                    spawns.add(new SpawnInfo(content, spawnWeight));
                }
                else if (type.compareTo("pool") == 0)
                {
                    subPools.add(new SpawnPool(pool, spawnWeight));
                }
                else
                {
                    System.err.printf("Unknown type %s found when parsing spawn pool contents", type);
                }
                totalWeight += spawnWeight;
            }

            buildWeightList();
        }

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
                        enemies.add(spawns.get(spawnid).generateEnemy(gameState));
                    }
                }
            }
        }
    }
}
