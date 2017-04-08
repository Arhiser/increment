package com.arhiser.increment.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.adjust.sdk.Adjust;
import com.arhiser.increment.managers.connection.ConnectionManager;
import com.arhiser.increment.managers.connection.ConnectionStateListener;
import com.arhiser.increment.network.ProgressSubscriber;
import com.arhiser.increment.tools.Utils;
import com.arhiser.increment.ui.tools.listener.DelayedClickListenerWrapper;
import com.arhiser.increment.ui.tools.restorable.Restorable;
import com.arhiser.increment.ui.tools.restorable.RestorableManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by arhis on 30.12.2016.
 */

public abstract class CustomerBaseActivity extends AppCompatActivity implements ConnectionStateListener {

    private ArrayList<Subscription> subscriptions = new ArrayList<>();

    protected ArrayList<UIController> controllers = new ArrayList<>();

    private RestorableManager restorableManager = new RestorableManager();

    protected abstract int getLayoutResource();

    protected void onCreateControllers() {

    }

    protected void registerController(UIController controller) {
        controllers.add(controller);
    }

    protected void registerSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public void unsubscribeAll() {
        Utils.forEach(subscriptions, subscription -> subscription.unsubscribe());
    }

    protected void unsubscribe(Subscription subscription) {
        subscription.unsubscribe();
        subscriptions.remove(subscription);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        restorableManager.restore(savedInstanceState);
        onCreateControllers();
        forEachController(controller -> controller.onViewCreated(savedInstanceState));
        ConnectionManager.getInstance().addConnectionStateListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        forEachController(UIController::onStart);
    }

    @Override
    protected void onStop() {
        forEachController(UIController::onStop);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Adjust.onResume();
        forEachController(UIController::onResume);
    }

    @Override
    protected void onPause() {
        Adjust.onPause();
        forEachController(UIController::onPause);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        forEachController(controller -> controller.onActivityResult(requestCode, resultCode, data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        forEachController(controller -> controller.onRequestPermissionsResult(requestCode, permissions, grantResults));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        forEachController(controller -> controller.onCreateOptionsMenu(menu));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        forEachController(controller -> controller.onOptionsItemSelected(item));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        restorableManager.save(outState);
        forEachController(controller -> controller.onSaveInstanceState(outState));
    }

    @Override
    protected void onDestroy() {
        for(Subscription subscription: subscriptions) {
            subscription.unsubscribe();
        }
        ConnectionManager.getInstance().removeConnectionStateListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void forEachController(Action1<UIController> action) {
        for (UIController controller: controllers) {
            action.call(controller);
        }
    }

    @Override
    public void onConnectionStateChanged(ConnectionManager.ConnectionState connectionState) {

    }

    protected boolean isOnline() {
        return ConnectionManager.getInstance().isOnline();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected void registerPersistent(Restorable object) {
        restorableManager.putRestorable(object);
    }

    protected  <T> void subscribeRaw(Observable<T> observable, Subscriber<T> subscriber) {
        registerSubscription(observable.subscribe(subscriber));
    }

    public <T> Subscription subscribe(Observable<T> observable, Subscriber<T> subscriber) {
        Subscription subscription;
        registerSubscription(subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
        return subscription;
    }

    protected  <T> Subscription subscribe(Observable<T> observable, Action1<T> onSuccess) {
        ProgressSubscriber<T> subscriber = new ProgressSubscriber<T>(this) {
            @Override
            public void onNext(T t) {
                super.onNext(t);
                if (onSuccess != null) {
                    onSuccess.call(t);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };

        Subscription subscription;
        subscriber.showProgress();
        registerSubscription(subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
        return subscription;
    }

    protected RestorableManager getRestorableManager() {
        return restorableManager;
    }

    public void onClick(View view, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(view, onClickListener);
    }

    public void onClick(int viewResource, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(findViewById(viewResource), onClickListener);
    }

    void onLoginStatusChanged() {

    }
}
