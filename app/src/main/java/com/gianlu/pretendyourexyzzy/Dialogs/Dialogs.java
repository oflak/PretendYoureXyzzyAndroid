package com.gianlu.pretendyourexyzzy.Dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.SuperTextView;
import com.gianlu.commonutils.Toaster;
import com.gianlu.pretendyourexyzzy.NetIO.Models.FirstLoad;
import com.gianlu.pretendyourexyzzy.NetIO.Models.Game;
import com.gianlu.pretendyourexyzzy.NetIO.Pyx;
import com.gianlu.pretendyourexyzzy.NetIO.PyxException;
import com.gianlu.pretendyourexyzzy.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import okhttp3.HttpUrl;

public final class Dialogs {

    @SuppressLint("InflateParams")
    public static AlertDialog addServer(@NonNull final Context context, @Nullable final Pyx.Server server, @NonNull final OnAddServer listener) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_add_server, null, false);

        TextInputLayout nameField = layout.findViewById(R.id.addServer_name);
        CommonUtils.clearErrorOnEdit(nameField);
        TextInputLayout urlField = layout.findViewById(R.id.addServer_url);
        CommonUtils.clearErrorOnEdit(urlField);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.addServer)
                .setView(layout)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(server == null ? R.string.add : R.string.apply, null);

        if (server != null) {
            CommonUtils.setText(nameField, server.name);
            CommonUtils.setText(urlField, server.url.toString());
            builder.setNeutralButton(R.string.remove, (dialog, which) -> {
                Pyx.Server.removeUserServer(server);
                listener.removeItem(server);
            });
        }

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String nameStr = CommonUtils.getText(nameField);
            if (nameStr.isEmpty() || (server != null && !Objects.equals(server.name, nameStr) && Pyx.Server.hasServer(nameStr))) {
                nameField.setError(context.getString(R.string.invalidServerName));
                return;
            }

            String urlStr = CommonUtils.getText(urlField);
            HttpUrl url = Pyx.Server.parseUrl(urlStr);
            if (url == null) {
                urlField.setError(context.getString(R.string.invalidServerUrl));
                return;
            }

            try {
                Pyx.Server.addUserServer(new Pyx.Server(url, null, nameStr, true));
                listener.loadServers();
            } catch (JSONException ex) {
                Toaster.with(context).message(R.string.failedAddingServer).ex(ex).show();
            }

            dialogInterface.dismiss();
        }));

        dialog.setOnDismissListener(d -> listener.startTests());
        return dialog;
    }

    @NonNull
    @SuppressLint("InflateParams")
    public static AlertDialog.Builder gameOptions(@NonNull Context context, @NonNull Game.Options options, @NonNull FirstLoad firstLoad) {
        ScrollView layout = (ScrollView) LayoutInflater.from(context).inflate(R.layout.dialog_game_options, null, false);

        SuperTextView scoreLimit = layout.findViewById(R.id.gameOptions_scoreLimit);
        scoreLimit.setHtml(R.string.scoreLimit, options.scoreLimit);

        SuperTextView playerLimit = layout.findViewById(R.id.gameOptions_playerLimit);
        playerLimit.setHtml(R.string.playerLimit, options.playersLimit);

        SuperTextView spectatorLimit = layout.findViewById(R.id.gameOptions_spectatorLimit);
        spectatorLimit.setHtml(R.string.spectatorLimit, options.spectatorsLimit);

        SuperTextView timerMultiplier = layout.findViewById(R.id.gameOptions_timerMultiplier);
        timerMultiplier.setHtml(R.string.timerMultiplier, options.timerMultiplier);

        SuperTextView cardSets = layout.findViewById(R.id.gameOptions_cardSets);
        cardSets.setHtml(R.string.cardSets, options.cardSets.isEmpty() ? "<i>none</i>" : CommonUtils.join(firstLoad.createCardSetNamesList(options.cardSets), ", "));

        SuperTextView blankCards = layout.findViewById(R.id.gameOptions_blankCards);
        blankCards.setHtml(R.string.blankCards, options.blanksLimit);

        SuperTextView password = layout.findViewById(R.id.gameOptions_password);
        if (options.password == null || options.password.isEmpty())
            password.setVisibility(View.GONE);
        else
            password.setHtml(R.string.password, options.password);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.gameOptions)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null);

        return builder;
    }

    @NonNull
    @SuppressLint("InflateParams")
    public static AlertDialog.Builder notEnoughCards(@NonNull Context context, @NonNull PyxException ex) throws JSONException {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_cannot_start_game, null, false);

        int wcr = ex.obj.getInt("wcr");
        int bcr = ex.obj.getInt("bcr");
        int wcp = ex.obj.getInt("wcp");
        int bcp = ex.obj.getInt("bcp");

        ((TextView) layout.findViewById(R.id.cannotStartGame_wcr)).setText(String.valueOf(wcr));
        ((TextView) layout.findViewById(R.id.cannotStartGame_bcr)).setText(String.valueOf(bcr));
        ((TextView) layout.findViewById(R.id.cannotStartGame_wcp)).setText(String.valueOf(wcp));
        ((TextView) layout.findViewById(R.id.cannotStartGame_bcp)).setText(String.valueOf(bcp));
        ((ImageView) layout.findViewById(R.id.cannotStartGame_checkBc)).setImageResource(bcp >= bcr ? R.drawable.baseline_done_24 : R.drawable.baseline_clear_24);
        ((ImageView) layout.findViewById(R.id.cannotStartGame_checkWc)).setImageResource(wcp >= wcr ? R.drawable.baseline_done_24 : R.drawable.baseline_clear_24);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.cannotStartGame)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, null);

        return builder;
    }

    @NonNull
    public static AlertDialog.Builder askText(@NonNull Context context, @NonNull final OnText listener) {
        final EditText text = new EditText(context);

        return new AlertDialog.Builder(context)
                .setTitle(R.string.setBlankCardText)
                .setView(text)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> listener.onText(text.getText().toString())).setNegativeButton(android.R.string.cancel, null);
    }

    @NonNull
    public static AlertDialog.Builder confirmation(@NonNull Context context, @NonNull final OnConfirmed listener) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.areYouSurePlayCard)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> listener.onConfirmed()).setNegativeButton(android.R.string.no, null);
    }

    @NonNull
    public static AlertDialog.Builder askDefinitionWord(@NonNull Context context, @NonNull final OnText listener) {
        final EditText text = new EditText(context);

        return new AlertDialog.Builder(context)
                .setTitle(R.string.definition)
                .setView(text)
                .setPositiveButton(R.string.search, (dialog, which) -> listener.onText(text.getText().toString())).setNegativeButton(android.R.string.cancel, null);
    }

    public interface OnAddServer {
        void loadServers();

        void removeItem(@NonNull Pyx.Server server);

        void startTests();
    }

    public interface OnConfirmed {
        void onConfirmed();
    }

    public interface OnText {
        void onText(@NonNull String text);
    }
}
