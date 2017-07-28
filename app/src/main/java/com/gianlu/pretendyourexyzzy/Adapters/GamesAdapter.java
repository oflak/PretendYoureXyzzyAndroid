package com.gianlu.pretendyourexyzzy.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Sorting.NotFilterable;
import com.gianlu.commonutils.Sorting.OrderedRecyclerViewAdapter;
import com.gianlu.commonutils.SuperTextView;
import com.gianlu.pretendyourexyzzy.NetIO.Models.Game;
import com.gianlu.pretendyourexyzzy.R;

import java.util.Comparator;
import java.util.List;

public class GamesAdapter extends OrderedRecyclerViewAdapter<GamesAdapter.ViewHolder, Game, GamesAdapter.SortBy, NotFilterable> {
    private final Context context;
    private final IAdapter handler;
    private final LayoutInflater inflater;

    public GamesAdapter(Context context, List<Game> objs, IAdapter handler) {
        super(objs, SortBy.NUM_PLAYERS);
        this.context = context;
        this.handler = handler;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return objs.get(position).gid;
    }

    @Nullable
    @Override
    protected RecyclerView getRecyclerView() {
        return handler != null ? handler.getRecyclerView() : null;
    }

    @Override
    protected boolean matchQuery(Game item, @Nullable String query) {
        return query == null || item.name.contains(query);
    }

    @Override
    protected void shouldUpdateItemCount(int count) {
    }

    @NonNull
    @Override
    public Comparator<Game> getComparatorFor(SortBy sorting) {
        switch (sorting) {
            case NAME:
                return new Game.NameComparator();
            default:
            case NUM_PLAYERS:
                return new Game.NumPlayersComparator();
            case NUM_SPECTATORS:
                return new Game.NumSpectatorsComparator();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Game game = objs.get(position);
        holder.name.setText(game.name);
        holder.players.setHtml(R.string.players, game.players.size(), game.options.playersLimit);
        holder.spectators.setHtml(R.string.spectators, game.spectators.size(), game.options.spectatorLimit);
        holder.goal.setHtml(R.string.goal, game.options.scoreLimit);
        holder.locked.setImageResource(game.hasPassword ? R.drawable.ic_lock_outline_black_48dp : R.drawable.ic_lock_open_black_48dp);
        holder.status.setImageResource(game.status == Game.Status.LOBBY ? R.drawable.ic_hourglass_empty_black_48dp : R.drawable.ic_casino_black_48dp);

        CommonUtils.setCardTopMargin(context, holder);
    }

    public enum SortBy {
        NAME,
        NUM_PLAYERS,
        NUM_SPECTATORS
    }

    public interface IAdapter {
        @Nullable
        RecyclerView getRecyclerView();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final SuperTextView players;
        final SuperTextView spectators;
        final SuperTextView goal;
        final ImageView locked;
        final ImageView status;
        final Button spectate;
        final Button join;

        public ViewHolder(ViewGroup parent) {
            super(inflater.inflate(R.layout.game_item, parent, false));

            name = (TextView) itemView.findViewById(R.id.gameItem_name);
            status = (ImageView) itemView.findViewById(R.id.gameItem_status);
            players = (SuperTextView) itemView.findViewById(R.id.gameItem_players);
            locked = (ImageView) itemView.findViewById(R.id.gameItem_locked);
            spectators = (SuperTextView) itemView.findViewById(R.id.gameItem_spectators);
            goal = (SuperTextView) itemView.findViewById(R.id.gameItem_goal);
            spectate = (Button) itemView.findViewById(R.id.gameItem_spectate);
            join = (Button) itemView.findViewById(R.id.gameItem_join);
        }
    }
}
