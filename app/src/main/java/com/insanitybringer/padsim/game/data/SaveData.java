package com.insanitybringer.padsim.game.data;

import android.app.Application;
import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.insanitybringer.padsim.game.Card;
import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.Monster;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class SaveData
{
    public static void saveInternalTeam(Application application, int teamNum, Card[] team)
    {
        try
        {
            FileOutputStream stream = application.openFileOutput(String.format(Locale.getDefault(), "team_%d.json", teamNum), Context.MODE_PRIVATE);
            OutputStreamWriter bw = new OutputStreamWriter(stream);
            JsonArray teamArray = new JsonArray();
            for (int i = 0; i < 6; i++)
            {
                teamArray.add(team[i].generateObject());
            }
            Gson gson = new Gson();
            String output = gson.toJson(teamArray);
            bw.write(output);
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e)
        {
            System.err.println("did you know that a function that is supposed to open a file for writing, creating if it it doesn't exist, can throw a FileNotFoundException? Well, I got some bad news. This actually happened. I don't know if there's some strange semantics that can cause this to happen, like trying to write into a directory that doesn't exist, but this seems moot since you can't even specify path separators into the filename parameter, so I dunno. I'm lost and confused, but it somehow happened. I'll just have to learn to live with it. Anyways, here's the stack trace");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            //TODO: I should actually do something here
            System.err.println("This is more plausible since you might have run out of space or something");
            e.printStackTrace();
        }

        Gson gson = new Gson();
        //return gson.toJson(teamArray);
    }

    public static Card[] loadInternalTeam(Application application, int teamNum)
    {
        Card[] team = new Card[6];
        try
        {
            FileInputStream stream = application.openFileInput(String.format(Locale.getDefault(), "team_%d.json", teamNum));
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(new InputStreamReader(stream));
            JsonArray array = root.getAsJsonArray();
            for (int i = 0; i < 6; i++)
            {
                team[i] = Card.generateCardFromObject(i, array.get(i).getAsJsonObject());
            }
        }
        catch (FileNotFoundException e)
        {
            //If the file is not found, there's no save for this slot, so load in a prebuilt team
            List<Monster> monsters = GameState.GameStateSingleton.getGameState().getMonsters();
            if (teamNum == 0)
            {
                team[0] = new Card(monsters.get(1736), 0);
                team[0].setLevel(99);
                team[0].setAwokenLevel(9);
                team[1] = new Card(monsters.get(0), 1);
                team[1].setLevel(1);
                team[1].setAwokenLevel(9);
                team[2] = new Card(monsters.get(895), 2);
                team[2].setLevel(99);
                team[2].setAwokenLevel(9);
                team[3] = new Card(monsters.get(912), 3);
                team[3].setLevel(99);
                team[3].setAwokenLevel(9);
                team[4] = new Card(monsters.get(893), 4);
                team[4].setLevel(99);
                team[4].setAwokenLevel(9);
                team[5] = new Card(monsters.get(3506), 5); //1074
                team[5].setLevel(99);
                team[5].setAwokenLevel(9);
            }
            else
            {
                team[0] = new Card(monsters.get(4740), 0);
                team[0].setLevel(99);
                team[0].setAwokenLevel(9);
                team[1] = new Card(monsters.get(4196), 1);
                team[1].setLevel(99);
                team[1].setAwokenLevel(9);
                team[2] = new Card(monsters.get(3755), 2);
                team[2].setLevel(99);
                team[2].setAwokenLevel(9);
                team[3] = new Card(monsters.get(4635), 3);
                team[3].setLevel(99);
                team[3].setAwokenLevel(9);
                team[4] = new Card(monsters.get(3504), 4);
                team[4].setLevel(99);
                team[4].setAwokenLevel(9);
                team[5] = new Card(monsters.get(4740), 5); //1074
                team[5].setLevel(99);
                team[5].setAwokenLevel(9);
            }
        }
        return team;
    }
}
