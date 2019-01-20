package com.insanitybringer.padsim;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.data.ElementReader;
import com.insanitybringer.padsim.game.Monster;

import java.io.InputStream;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
{
    private ArrayList<Monster> monsters;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        TextView textView = findViewById(R.id.hehtextview);

        //TODO hyperdebug code
        /*ElementReader debug = new ElementReader(this.getApplication());
        monsters = new ArrayList<Monster>();
        debug.LoadMonsters(monsters);


        textView.setText(debug.debug);*/
        /*try
        {
            System.out.println("!!!!!!!!!!!!!!!!!!!hiii");
            InputStream heh = getResources().getAssets().open("BLOCK2.PNG");
            System.out.println("made it here");
            BitmapDrawable test = new BitmapDrawable(this.getResources(), heh);
            textView.setText(Integer.toString(test.getBitmap().getWidth()));
        }
        catch (Exception exc)
        {
            textView.setText(String.format("it blew up: %s, %s ",exc.getClass().getCanonicalName(), exc.getMessage()));
        }*/
        if (GameState.GameStateSingleton.getGameState(this.getApplication()) != null)
        {
            textView.setText(GameState.GameStateSingleton.getGameState(this.getApplication()).debug);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TextView textView = findViewById(R.id.hehtextview);
        textView.setText(GameState.GameStateSingleton.getGameState(this.getApplication()).debug);
    }

    public void debugGLInstance(View sender)
    {
        Intent intent = new Intent(this, DungeonActivity.class);
        startActivity(intent);
    }

    public void debugListInstance(View sender)
    {
        Intent intent = new Intent(this, MonsterList.class);
        startActivity(intent);
    }

    public void debugEditorInstance(View sender)
    {
        Intent intent = new Intent(this, TeamEditorActivity.class);
        startActivity(intent);
    }
}
