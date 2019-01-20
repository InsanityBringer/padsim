package com.insanitybringer.padsim.game.data;

//import org.json.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import com.insanitybringer.padsim.game.Monster;
import com.insanitybringer.padsim.game.Skill;

import java.nio.ByteBuffer;
import java.util.List;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;

import org.json.JSONArray;

public class ElementReader
{
    private Application application;
    public String debug = "it failed";

    public ElementReader(Application application)
    {
        this.application = application;
    }
    /*
    //This is not a general purpose reader and has some serious flaws...
    public void LoadMonsters(List<Monster> monsters)
    {
        AssetManager assetManager = application.getResources().getAssets();
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("pad_na_cards.json"))))
        {
            String line = reader.readLine();
            String json;
            JSONTokener tokener;
            JSONObject mons;
            int baseID;
            while (line != null)
            {
                if (line.charAt(0) != '[')
                {
                    if (line.contains("}")) //end of object
                    {
                        stringBuilder.append("}\n"); //append a different ending line
                        json = stringBuilder.toString();
                        tokener = new JSONTokener(json);

                        Object obj = tokener.nextValue();
                        if (obj instanceof JSONObject)
                        {
                            mons = (JSONObject) obj;
                            Monster monster = new Monster();
                            monster.fromObject(mons);
                            if (monster.getId() > 200000)
                            {
                                baseID = monster.getId() - 200000;
                                monsters.get(baseID).setAltForm(monster);
                            } else if (monster.getId() > 100000)
                            {
                                baseID = monster.getId() - 100000;
                                monsters.get(baseID).setAltForm(monster);
                            } else
                            {
                                monsters.add(monster);
                            }
                        }
                        //stringBuilder = new StringBuilder();
                        //avoid further allocations and just reuse what we have
                        stringBuilder.delete(0, stringBuilder.length());
                    } else
                    {
                        stringBuilder.append(line);
                        stringBuilder.append('\n'); //Probably not required, is JSON whitespace sensitive?
                    }
                }

                line = reader.readLine();
            }
        }
        catch (IOException exc)
        {
            //TODO debug
            System.out.println("this literally shouldn't happen but it did so i'm stumped.");
            Monster heh = new Monster();
            heh.setName("Something pretty bad happened");
            heh.setId(1);
            monsters.add(heh);
            debug = "it blew up";
            return;
        }
        catch (JSONException e)
        {
            System.err.println("Malformed JSON file. This shouldn't happen, data is corrupted?");
            e.printStackTrace();
            Monster heh = new Monster();
            heh.setName("Something pretty bad happened");
            heh.setId(1);
            monsters.add(heh);
            return;
        }
    }*/

    public void FastLoadMonsters(List<Monster> monsters, List<Monster> cleanMonsters)
    {
        AssetManager assetManager = application.getResources().getAssets();
        try (InputStream stream = assetManager.open("pad_na_cards.json"))
        {
            /*int len = stream.available();
            byte[] buffer = new byte[len];
            stream.read(buffer);
            stream.close();

            System.out.println("Done reading file");
            String ohcrap = new String(buffer, "UTF-8");*/
            //JSONTokener tokener = new JSONTokener(ohcrap);
            JsonParser parser = new JsonParser();
            //JsonArray array = parser.parse(ohcrap).getAsJsonArray();
            JsonElement test = parser.parse(new InputStreamReader(stream));
            System.out.println("Done parsing file");
            JsonArray array = test.getAsJsonArray();
            System.out.println("Done converting to array");
            int baseID = 0;
            Monster monster;
            for (int i = 0; i < array.size(); i++)
            {
                if (i % 500 == 0)
                {
                    System.out.printf("Loading monster %d\n", i);
                }
                monster = new Monster();
                monster.fromObject(array.get(i).getAsJsonObject());
                if (monster.getId() > 200000)
                {
                    baseID = monster.getId() - 200000;
                    monsters.get(baseID).setAltForm(monster);
                } else if (monster.getId() > 100000)
                {
                    baseID = monster.getId() - 100000;
                    monsters.get(baseID).setAltForm(monster);
                } else
                {
                    monsters.add(monster);
                    if (monster.getName().charAt(0) != '*')
                        cleanMonsters.add(monster);
                }
            }
        }
        catch (IOException exc)
        {
            //TODO debug
            System.out.println("this literally shouldn't happen but it did so i'm stumped.");
            Monster heh = new Monster();
            heh.setName("Something pretty bad happened");
            heh.setId(1);
            monsters.add(heh);
            debug = "it blew up";
            return;
        }
    }

    public void FastLoadSkills(List<Skill> skills)
    {
        AssetManager assetManager = application.getResources().getAssets();
        try (InputStream stream = assetManager.open("pad_na_skills.json"))
        {
            /*int len = stream.available();
            byte[] buffer = new byte[len];
            stream.read(buffer);
            stream.close();

            System.out.println("Done reading file");
            String ohcrap = new String(buffer, "UTF-8");*/
            //JSONTokener tokener = new JSONTokener(ohcrap);
            JsonParser parser = new JsonParser();
            //JsonArray array = parser.parse(ohcrap).getAsJsonArray();
            JsonElement test = parser.parse(new InputStreamReader(stream));
            System.out.println("Done parsing file");
            JsonArray array = test.getAsJsonArray();
            System.out.println("Done converting to array");
            int baseID = 0;
            Skill skill;
            for (int i = 0; i < array.size(); i++)
            {
                if (i % 250 == 0)
                {
                    System.gc();
                }
                if (i % 500 == 0)
                {
                    System.out.printf("Loading skill %d\n", i);
                }
                skill = Skill.createSkill(array.get(i).getAsJsonObject());
                skills.add(skill);
            }
        }
        catch (IOException exc)
        {
            //TODO debug
            System.out.println("this literally shouldn't happen but it did so i'm stumped.");
            Skill heh = new Skill();
            heh.setName("Something pretty bad happened");
            heh.setID((short)1);
            skills.add(heh);
            debug = "it blew up";
            return;
        }
    }
}

