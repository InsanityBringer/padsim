package com.insanitybringer.padsim;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.insanitybringer.padsim.game.GameState;

public class SplashScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LoadGameTask task = new LoadGameTask(getApplication());
        task.execute((Void)null);
    }

    private static class LoadGameTask extends AsyncTask<Void, Integer, Void>
    {
        Application application;
        public LoadGameTask(Application application)
        {
            this.application = application;
        }
        @Override
        protected Void doInBackground(Void... voids)
        {
            //If the user comes in from here, init the game state while the splash is shown
            GameState.GameStateSingleton.getGameState(application);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            Intent test = new Intent(application, MainMenu.class);
            test.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(test);
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);

        }
    }
}
