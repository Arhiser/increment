package com.arhiser.increment.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import retrofit.RetrofitError;
import rx.Subscriber;

/**
 * Created by arhis on 15.01.2017.
 */

public class BaseSubscriber<T> extends Subscriber<T> {

    protected Activity activity;

    public BaseSubscriber(Activity activity) {
        this.activity = activity;
    }

    private Activity getContext() {
        return activity;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) throwable;
            handleError(error, false);
        }
    }

    private void handleError(RetrofitError responseError, boolean verbose) {
        handleError(activity, responseError, verbose);
    }

    public static void handleError(Activity activity, RetrofitError responseError, boolean verbose) {
        String textError = responseError.getMessage();
        int status = 0;

        if (responseError.getResponse() != null) {
            status = responseError.getResponse().getStatus();

            try {
               // errorRetrofit = (ErrorRetrofit) responseError.getBodyAs(ErrorRetrofit.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (activity == null || activity.isFinishing()) {
            return;
        }

        Context context = activity;

        showErrorMessage(context, textError);
    }

    public static void showErrorMessage(Context context, String textError) {
        new AlertDialog.Builder(context)
                .setMessage(textError)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
