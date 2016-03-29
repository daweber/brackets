package com.daweber.brackets.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daweber.brackets.R;
import com.daweber.brackets.view.activity.BracketActivity;
import com.daweber.brackets.vo.Game;
import com.daweber.brackets.vo.Game_Table;
import com.daweber.brackets.vo.Pick;
import com.daweber.brackets.vo.Pick_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Game List Adapter class
 */
public class GameListAdapter extends RecyclerView.Adapter<GameViewHolder> {
    private static final String TAG = "b64.GameListAdapter";

    private List<Game> gameList;
    private Context mContext;
    private int mPickset;

    public GameListAdapter(Context context, List<Game> gameList, int picksetId) {
        this.mContext = context;
        this.gameList = gameList;
        this.mPickset = picksetId;
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    @Override
    public void onBindViewHolder(GameViewHolder gameCard, int i) {

        Game g = gameList.get(i);
        int gameId = g.getGameId();

        gameCard.picksetID = mPickset;
        gameCard.gameID = gameId;
        gameCard.viewGameDetails.setText(g.getGameDetails());
        gameCard.viewBracketRegion.setText(Game.REGION[g.getBracketRegion()].toUpperCase());

        String teamOne = g.getTeamOne();
        String teamTwo = g.getTeamTwo();

        // TODO: get pickedTeam name if team name is a number
        if (teamOne.length() <= 2) {

            if (true)
                gameCard.viewTeamOne
                        .setText(SQLite.select().from(Game.class)
                                .where(Game_Table.gameId.eq(Integer.parseInt(teamOne))).querySingle()
                                .getTeamOne());

            gameCard.viewTeamOne
                    .setText(String.format("%s\u00A0%s", mContext
                            .getString(R.string.unpicked_team_prefix), teamOne));
        } else
            gameCard.viewTeamOne.setText(teamOne);

        if (teamTwo.length() <= 2)
            gameCard.viewTeamTwo
                    .setText(String.format("%s\u00A0%s", mContext
                            .getString(R.string.unpicked_team_prefix), teamTwo));
        else
            gameCard.viewTeamTwo.setText(teamTwo);

        Pick gamePick = SQLite.select()
                .from(Pick.class)
                .where(Pick_Table.picksetId.eq(mPickset))
                .and(Pick_Table.gameId.eq(gameId)).querySingle();

        if (gamePick != null) {

            gameCard.pickID = gamePick.getPickId();

            switch (gamePick.getPickedWinner()) {
                case 1:
                    gameCard.pickTeamOne.setVisibility(View.VISIBLE);
                    gameCard.pickTeamTwo.setVisibility(View.GONE);
                    break;
                case 2:
                    gameCard.pickTeamOne.setVisibility(View.GONE);
                    gameCard.pickTeamTwo.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    gameCard.pickTeamOne.setVisibility(View.GONE);
                    gameCard.pickTeamTwo.setVisibility(View.GONE);
                    break;
            }
        } else {

            gameCard.pickTeamOne.setVisibility(View.GONE);
            gameCard.pickTeamTwo.setVisibility(View.GONE);
        }
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View gameCard = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_game, parent, false);

        return new GameViewHolder(gameCard, new GameViewHolder.GameCardClickListener() {
            @Override
            public void setPicked(ResizeTextView picked, int game, int pickset, int pick) {
                if (picked.getId() == R.id.team_one) {
                    BracketActivity.updatePicked(pick, pickset, game, 1);
                    notifyDataSetChanged();
                } else {
                    BracketActivity.updatePicked(pick, pickset, game, 2);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void moreInfo(View touched, int game) {
                Log.d(TAG, "Touched Game " + game + " view: " + touched.toString());
            }
        });
    }
}
