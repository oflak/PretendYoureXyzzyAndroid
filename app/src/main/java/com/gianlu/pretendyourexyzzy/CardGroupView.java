package com.gianlu.pretendyourexyzzy;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.LinearLayout;

import com.gianlu.pretendyourexyzzy.Adapters.PyxCard;
import com.gianlu.pretendyourexyzzy.NetIO.Models.Card;

import java.util.List;

public class CardGroupView extends LinearLayout {
    private List<Card> cards;

    public CardGroupView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setWillNotDraw(false);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;

        removeAllViews();
        for (Card card : cards)
            addView(new PyxCard(getContext(), card));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setWinning() { // FIXME
        for (int i = 0; i < getChildCount(); i++) {
            PyxCard child = (PyxCard) getChildAt(i);
            removeViewAt(i);
            child.setWinning();
            addView(child, i);
        }

        invalidate();
    }
}
