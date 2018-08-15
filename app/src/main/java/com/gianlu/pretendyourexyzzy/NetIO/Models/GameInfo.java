package com.gianlu.pretendyourexyzzy.NetIO.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameInfo {
    public final Game game;
    public final List<Player> players;

    public GameInfo(JSONObject obj) throws JSONException {
        game = new Game(obj.getJSONObject("gi"));
        players = Player.list(obj.getJSONArray("pi"));
    }

    @Nullable
    public Player player(@NonNull String nick) {
        synchronized (players) {
            for (Player player : players)
                if (Objects.equals(player.name, nick)) return player;

            return null;
        }
    }

    public void removePlayer(@NonNull String nick) {
        game.players.remove(nick);
    }

    public void newPlayer(@NonNull Player player) {
        game.players.add(player.name);
    }

    public void newSpectator(@NonNull String nick) {
        game.spectators.add(nick);
    }

    public void removeSpectator(@NonNull String nick) {
        game.spectators.remove(nick);
    }

    public enum PlayerStatus {
        HOST("sh" /* Wait for players then click Start Game. */),
        IDLE("si" /* Waiting for players..." */),
        JUDGE("sj" /* You are the CardcastCard Czar. */),
        JUDGING("sjj" /* Select a winning card. */),
        PLAYING("sp" /* "Select a card to play. */),
        WINNER("sw" /* You have won! */),
        SPECTATOR("sv" /* You are just spectating. */);

        private final String val;

        PlayerStatus(String val) {
            this.val = val;
        }

        public static PlayerStatus parse(String val) {
            for (PlayerStatus status : values())
                if (Objects.equals(status.val, val))
                    return status;

            throw new IllegalArgumentException("Cannot find status with value: " + val);
        }
    }

    public static class Player {
        public final String name;
        public final int score;
        public PlayerStatus status;

        public Player(JSONObject obj) throws JSONException {
            name = obj.getString("N");
            score = obj.getInt("sc");
            status = PlayerStatus.parse(obj.getString("st"));
        }

        public Player(String name, int score, PlayerStatus status) {
            this.name = name;
            this.score = score;
            this.status = status;
        }

        @NonNull
        public static List<Player> list(JSONArray array) throws JSONException {
            List<Player> list = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++)
                list.add(new Player(array.getJSONObject(i)));
            return list;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return name.equals(player.name);
        }
    }
}
