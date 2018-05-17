package com.gianlu.pretendyourexyzzy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.SuperTextView;
import com.gianlu.pretendyourexyzzy.NetIO.Models.Game;
import com.gianlu.pretendyourexyzzy.NetIO.Models.WhoisResult;

import java.util.Date;

public class UserInfoDialog extends DialogFragment {

    @NonNull
    public static UserInfoDialog get(@NonNull WhoisResult user) {
        UserInfoDialog dialog = new UserInfoDialog();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireActivity(), R.style.TitledDialog);
        WhoisResult user = getUser();
        if (user != null) dialog.setTitle(user.nickname);
        return dialog;
    }

    @Nullable
    private WhoisResult getUser() {
        WhoisResult user;
        Bundle args = getArguments();
        if (args == null || (user = (WhoisResult) args.getSerializable("user")) == null)
            return null;
        else return user;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_user_info, container, false);

        WhoisResult user = getUser();
        if (user == null) return null;

        SuperTextView sigil = layout.findViewById(R.id.userInfoDialog_sigil);
        sigil.setHtml(R.string.sigil, user.sigil.getFormal(getContext()));

        SuperTextView idCode = layout.findViewById(R.id.userInfoDialog_idCode);
        if (user.idCode == null || user.idCode.isEmpty()) {
            idCode.setVisibility(View.GONE);
        } else {
            idCode.setVisibility(View.VISIBLE);
            idCode.setHtml(R.string.idCode, user.idCode);
        }

        SuperTextView connAt = layout.findViewById(R.id.userInfoDialog_connAt);
        connAt.setHtml(R.string.connectedAt, CommonUtils.getFullDateFormatter().format(new Date(user.connectedAt)));

        SuperTextView idleFrom = layout.findViewById(R.id.userInfoDialog_idle);
        idleFrom.setHtml(R.string.idleFor, CommonUtils.timeFormatter(user.idle / 1000));

        SuperTextView ipAddr = layout.findViewById(R.id.userInfoDialog_ipAddr);
        if (user.ipAddress() == null) {
            ipAddr.setVisibility(View.GONE);
        } else {
            ipAddr.setVisibility(View.VISIBLE);
            ipAddr.setHtml(R.string.ipAddress, user.ipAddress());
        }

        SuperTextView client = layout.findViewById(R.id.userInfoDialog_client);
        if (user.clientName() == null) {
            client.setVisibility(View.GONE);
        } else {
            client.setVisibility(View.VISIBLE);
            client.setHtml(R.string.clientName, user.clientName());
        }

        Button viewGame = layout.findViewById(R.id.userInfoDialog_viewGame);
        SuperTextView game = layout.findViewById(R.id.userInfoDialog_game);
        final Game gameInfo = user.game();
        if (gameInfo == null) {
            game.setVisibility(View.GONE);
            viewGame.setVisibility(View.GONE);
        } else {
            game.setVisibility(View.VISIBLE);
            game.setHtml(R.string.gameHost, gameInfo.host);

            viewGame.setVisibility(View.VISIBLE);
            viewGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = getActivity();
                    if (activity instanceof OnViewGame) {
                        ((OnViewGame) activity).viewGame(gameInfo.gid, gameInfo.hasPassword());
                        dismiss();
                    }
                }
            });
        }

        return layout;
    }

    public interface OnViewGame {
        void viewGame(int gid, boolean locked);
    }
}
