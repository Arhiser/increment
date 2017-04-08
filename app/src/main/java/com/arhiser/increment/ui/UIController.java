package com.arhiser.increment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.arhiser.increment.ui.tools.listener.DelayedClickListenerWrapper;
import com.arhiser.increment.ui.tools.restorable.Restorable;
import com.arhiser.increment.ui.tools.restorable.RestorableManager;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by arhis on 09.01.2017.
 */

public class UIController {

    protected CustomerBaseActivity activity;
    protected CustomerBaseFragment fragment;

    private RestorableManager restorableManager = new RestorableManager();

    public UIController(CustomerBaseActivity activity) {
        this.activity = activity;
    }

    public UIController(CustomerBaseFragment fragment) {
        this.fragment = fragment;
    }

    public void onViewCreated(Bundle bundle) {
        restorableManager.restore(bundle);
        EventBus.getDefault().register(this);
    }

    public void onSaveInstanceState(Bundle bundle){
        restorableManager.save(bundle);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onStop() {

    }

    public void onStart() {

    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public void onCreateOptionsMenu(Menu menu) {

    }

    public void onOptionsItemSelected(MenuItem item) {

    }

    protected View findViewById(int id) {
        if (fragment != null && fragment.getView() != null) {
            return fragment.getView().findViewById(id);
        } else if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }

    protected void unsubscribeAll() {
        if (fragment != null) {
            fragment.unsubscribeAll();
        } else {
            activity.unsubscribeAll();
        }
    }

    protected void unsubscribe(Subscription subscription) {
        if (fragment != null) {
            fragment.unsubscribe(subscription);
        } else {
            activity.unsubscribe(subscription);
        }
    }

    protected void registerSubscrition(Subscription subscription) {
        if (fragment != null) {
            fragment.registerSubscription(subscription);
        } else {
            activity.registerSubscription(subscription);
        }
    }

    public void onClick(View view, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(view, onClickListener);
    }

    public void onClick(int viewResource, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(findViewById(viewResource), onClickListener);
    }

    protected void registerPersistent(Restorable object) {
        restorableManager.putRestorable(object);
    }

    protected RestorableManager getRestorableManager() {
        return restorableManager;
    }

    protected  <T> void subscribeRaw(Observable<T> observable, Subscriber<T> subscriber) {
        activity.subscribeRaw(observable, subscriber);
    }

    protected  <T> Subscription subscribe(Observable<T> observable, Subscriber<T> subscriber) {
        return activity.subscribe(observable, subscriber);
    }

    protected  <T> Subscription subscribe(Observable<T> observable, Action1<T> onSuccess) {
        return activity.subscribe(observable, onSuccess);
    }

    void onLoginStatusChanged() {

    }

}
