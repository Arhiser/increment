package com.arhiser.increment.ui.tools.listener;

import android.view.View;

/**
 * Created by arhis on 20.03.2017.
 */

public class DelayedClickListenerWrapper implements View.OnClickListener{

    private static final int CLICK_DELAY = 1000;

    long lastClickTime;
    View.OnClickListener onClickListener;

    public static void wrap(View view, View.OnClickListener onClickListener) {
        view.setOnClickListener(new DelayedClickListenerWrapper(onClickListener));
    }

    public DelayedClickListenerWrapper(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClickTime > CLICK_DELAY) {
            onClickListener.onClick(v);
            lastClickTime = System.currentTimeMillis();
        }
    }
}
