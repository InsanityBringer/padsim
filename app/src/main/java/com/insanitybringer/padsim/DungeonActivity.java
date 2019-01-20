package com.insanitybringer.padsim;

import android.app.Application;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.renderer.DungeonGLSurfaceView;

public class DungeonActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //TODO: Do this in a better location
        GameState.GameStateSingleton.getGameState().startGame();
        GLSurfaceView newView = new DungeonGLSurfaceView(this, GameState.GameStateSingleton.getGameState(this.getApplication()));
        setContentView(newView);
        setTheme(R.style.Theme_AppCompat_NoActionBar);
        //setSupportActionBar(null);
    }
}
