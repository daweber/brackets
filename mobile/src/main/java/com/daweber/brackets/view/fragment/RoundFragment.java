package com.daweber.brackets.view.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daweber.brackets.R;
import com.daweber.brackets.view.GameListAdapter;
import com.daweber.brackets.vo.Game;
import com.daweber.brackets.vo.Game_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Round Fragment class
 */
public class RoundFragment extends Fragment {

    private static final String TAG = "b64.RoundFragment";
    private static final String ARG_ROUND_NUMBER = "round_number";

    private int roundNumber;

    public RoundFragment() {
    }

    public static RoundFragment newInstance(int round) {
        RoundFragment fragment = new RoundFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUND_NUMBER, round);
        fragment.roundNumber = round;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "OnCreating...");
        //TODO:deal with bundle

        View rootView = inflater.inflate(R.layout.fragment_round_list, container, false);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerView roundList = (RecyclerView) rootView
                .findViewById(R.id.round_games_list);
        roundList.setHasFixedSize(true);
        roundList.setLayoutManager(llm);
        roundList.setAdapter(new GameListAdapter(container.getContext(),
                getRoundGameList(roundNumber)));

        return rootView;
    }

    private List<Game> getRoundGameList(int round) {
        return SQLite
                .select()
                .from(Game.class)
                .where(Game_Table.bracketRound.eq(round))
                .and(Game_Table.bracketId.eq(1)).queryList();

        //TODO:hard-coded '1' above should reflect shardPrefs value and/or Activity Member
    }

}