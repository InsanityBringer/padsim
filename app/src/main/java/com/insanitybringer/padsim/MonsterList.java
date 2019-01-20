package com.insanitybringer.padsim;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.game.Monster;
import com.insanitybringer.padsim.ui.MonsterListAdapter;

import java.util.List;
import java.util.ArrayList;

public class MonsterList extends AppCompatActivity
{
    //private EditText searchBox;
    private AsyncTask<Void, Void, List<Monster>> searchTask;
    private MonsterListAdapter adapter;
    private GameState gameState;
    private List<Monster> localList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monster_list);

        gameState = GameState.GameStateSingleton.getGameState(this.getApplication());
        Intent intent = getIntent();
        //You can only inherit something that is at max awoken level, and has a skill in the first place,
        //so if we're in inherit mode, only show things that have an active and more than 0 awokens
        if (intent.getBooleanExtra(CardEditorActivity.PARAM_LISTMODE, false))
        {
            localList = new ArrayList<>();
            List<Monster> normalList = gameState.getCleanMonsters();
            for (Monster monster : normalList)
            {
                if (monster.getId() == 0 || monster.getCanInherit())
                {
                    localList.add(monster);
                }
            }
        }
        else
        {
            localList = gameState.getCleanMonsters();
        }

        RecyclerView recyclerView = findViewById(R.id.monsterRecyclerView);
        adapter = new MonsterListAdapter(localList, this.getResources());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditText searchBox = findViewById(R.id.text_find_monster);
        searchBox.addTextChangedListener(new SearchTextWatcher());


    }

    @Override
    public void onBackPressed()
    {
        setResult(-1);
        super.onBackPressed();
    }

    private class SearchTextWatcher implements TextWatcher
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override
        public void afterTextChanged(Editable s)
        {
            System.out.println("Text changed called");
            if (s.length() == 0) //empty string?
            {
                //Skip doing the search as needed
                adapter.updateList(localList);
            }
            if (searchTask != null)
            {
                //If there's a search going, stop it
                searchTask.cancel(true);
            }
            //Threaded searching. What really needs to be threaded is the list update...
            searchTask = new SearchTask(s, adapter, gameState, localList);
            searchTask.execute();
        }
    }

    private static class SearchTask extends AsyncTask<Void, Void, List<Monster>>
    {
        private String searchname;
        private MonsterListAdapter adapter;
        private GameState gameState;
        private List<Monster> localList;
        public SearchTask(CharSequence searchname, MonsterListAdapter adapter, GameState gameState, List<Monster> localList)
        {
            this.searchname = searchname.toString().toUpperCase();
            this.adapter = adapter;
            this.gameState = gameState;
            this.localList = localList;
        }

        @Override
        protected List<Monster> doInBackground(Void... voids)
        {
            ArrayList<Monster> monsterList = new ArrayList<>();
            //List<Monster> baseMonsterList = localList;
            for (Monster monster : localList)
            {
                if (isCancelled()) break; //oop we're done here
                if (monster.getIDName().toUpperCase().contains(searchname))
                {
                    monsterList.add(monster);
                }
            }

            return monsterList;
        }

        @Override
        protected void onPostExecute(List<Monster> monsters)
        {
            super.onPostExecute(monsters);
            System.out.printf("Search for string %s completed\n", searchname);
            adapter.updateList(monsters);
        }
    }
}
