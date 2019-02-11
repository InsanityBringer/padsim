package com.insanitybringer.padsim.game.data;

//import org.json.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import com.insanitybringer.padsim.game.Monster;
import com.insanitybringer.padsim.game.Skill;

import java.util.List;

import android.app.Application;
import android.content.res.AssetManager;

public class ElementReader
{
    private Application application;
    public String debug = "it failed";

    public ElementReader(Application application)
    {
        this.application = application;
    }

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
            JsonParser parser = new JsonParser();
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

