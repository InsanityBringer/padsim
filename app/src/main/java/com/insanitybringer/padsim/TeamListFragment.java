package com.insanitybringer.padsim;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.insanitybringer.padsim.game.GameState;
import com.insanitybringer.padsim.ui.TeamListAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamListFragment.TeamListFragmentListener} interface
 * to handle interaction events.
 * Use the {@link TeamListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamListFragment extends Fragment
{
    public static int lastInstanceNum = 0;
    public int instanceNum = 0;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEAMNUM = "param1";

    // TODO: Rename and change types of parameters
    private int teamNum;

    private TeamListAdapter adapter;
    private TeamListFragmentListener mListener;

    public TeamListFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 The team ID to show for this fragment.
     * @return A new instance of fragment TeamListFragment.
     */
    public static TeamListFragment newInstance(int param1)
    {
        TeamListFragment fragment = new TeamListFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_TEAMNUM, param1);
        args.putInt(ARG_TEAMNUM, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.instanceNum = ++lastInstanceNum;
        System.out.printf("Team %d newInstance, instancenum %d\n", param1, fragment.instanceNum);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        System.out.printf("Team %d onCreate in instance %d\n", teamNum, instanceNum);
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            teamNum = getArguments().getInt(ARG_TEAMNUM);
           //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        System.out.printf("Team %d onCreateView in instance %d\n", teamNum, instanceNum);
        return inflater.inflate(R.layout.fragment_team_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        System.out.printf("Team %d onActivityCreated, called in context of instance %d. My id is %d and tag is %s\n", teamNum, instanceNum, getId(), getTag());
        super.onActivityCreated(savedInstanceState);
        RecyclerView recyclerView = getView().findViewById(R.id.team_recycler_view);
        adapter = new TeamListAdapter(teamNum, GameState.GameStateSingleton.getGameState().getTeam(teamNum), getResources());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (adapter == null)
        {
            System.out.printf("Leaving team %d onActivityCreated with null adapter\n", teamNum);
        }
        if (mListener != null)
        {
            mListener.setHostFragment(this, teamNum);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        System.out.printf("Attached to a %s\n", context.getClass().getCanonicalName());
        if (context instanceof TeamListFragmentListener)
        {
            mListener = (TeamListFragmentListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement TeamListFragmentListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    public void updateList(int teammate)
    {
        System.out.printf("Member %d updateList in instance %d\n", teamNum, instanceNum);
        //Notify the adapter that the user changed this monster. The adapter can be nulled in
        //certain circumstances (the configuration), so try to make sure it's usable.
        //If it is nulled, when it's recreated it should be updated automatically.
        if (adapter != null)
        {
            adapter.notifyItemChanged(teammate);
        }
        else
        {
            System.out.printf("Adapter is nulled in instancenum %d\n", instanceNum);
        }
    }

    public interface TeamListFragmentListener
    {
        void setHostFragment(Fragment fragment, int id);
    }
}
