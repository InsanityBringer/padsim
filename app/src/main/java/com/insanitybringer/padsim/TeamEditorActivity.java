package com.insanitybringer.padsim;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class TeamEditorActivity extends AppCompatActivity implements TeamListFragment.TeamListFragmentListener
{
    public final static String MSG_TEAMMATE = "com.insanitybringer.padsim.TEAMMATENUM";
    public final static String MSG_TEAMNUM = "com.insanitybringer.padsim.TEAMNUM";
    TeamEditorFragmentAdapter adapter;
    ViewPager viewPager;

    private Fragment[] fragments = new Fragment[3];
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_editor);
        Toolbar appbar = findViewById(R.id.toolbar);
        setSupportActionBar(appbar);
        adapter = new TeamEditorFragmentAdapter(getSupportFragmentManager(), getResources());
        viewPager = (ViewPager)findViewById(R.id.team_view_pager);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        System.out.printf("I got back %d and %d\n", requestCode, resultCode);
        viewPager = (ViewPager)findViewById(R.id.team_view_pager);
        adapter = (TeamEditorFragmentAdapter)viewPager.getAdapter();

        TeamListFragment frag = (TeamListFragment)fragments[requestCode];
        //TeamListFragment frag = getSupportFragmentManager().
        if (frag != null)
        {
            frag.updateList(resultCode);
        }
    }

    @Override
    public void setHostFragment(Fragment fragment, int id)
    {
        fragments[id] = fragment;
    }

    /*@Override
    public void onWindowFocusChanged (boolean hasFocus)
    {
        if (hasFocus)
        {
            //TODO: Only change updated item
            TeamListFragment frag = (TeamListFragment)adapter.getItem(0);
            frag.updateList();
        }
    }*/

    public static class TeamEditorFragmentAdapter extends FragmentPagerAdapter
    {
        private Fragment[] fragments = new Fragment[3];
        private Resources resources;
        public TeamEditorFragmentAdapter(FragmentManager fm, Resources resources)
        {
            super(fm);
            this.resources = resources;
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            if (position == 0)
                return resources.getString(R.string.desc_team1);
            else if (position == 1)
                return resources.getString(R.string.desc_team2);
            else
                return resources.getString(R.string.desc_team3);
        }


        @Override
        public Fragment getItem(int position)
        {
            fragments[position] = TeamListFragment.newInstance(position);
            return fragments[position];
        }
    }
}
