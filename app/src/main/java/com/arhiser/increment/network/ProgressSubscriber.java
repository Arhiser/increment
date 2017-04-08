package com.arhiser.increment.network;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by arhis on 18.01.2017.
 */

public class ProgressSubscriber<T> extends BaseSubscriber<T> {

    private ProgressDialog progressDialog;

    public ProgressSubscriber(Activity activity) {
        super(activity);
    }

    public ProgressSubscriber<T> showProgress() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("");//activity.getString(R.string.customer_progress_message));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return this;
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCompleted() {
        hideProgress();
    }

    @Override
    public void onError(Throwable throwable) {
        hideProgress();
        super.onError(throwable);
    }
}
