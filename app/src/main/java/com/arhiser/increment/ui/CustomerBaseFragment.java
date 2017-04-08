package com.arhiser.increment.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by arhis on 09.01.2017.
 */

public abstract class CustomerBaseFragment extends Fragment implements ConnectionStateListener {

    private ArrayList<Subscription> subscriptions = new ArrayList<>();
    private ArrayList<UIController> controllers = new ArrayList<>();

    private RestorableManager restorableManager = new RestorableManager();

    public abstract int getFragmentTitleResource();
    public abstract int getLayoutResource();

    protected void registerController(UIController controller) {
        controllers.add(controller);
    }

    public void unsubscribeAll() {
        Utils.forEach(subscriptions, subscription -> subscription.unsubscribe());
    }

    public void unsubscribe(Subscription subscription) {
        subscription.unsubscribe();
        subscriptions.remove(subscription);
    }

    protected void registerSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ConnectionManager.getInstance().addConnectionStateListener(this);
    }

    @Override
    public void onDetach() {
        ConnectionManager.getInstance().removeConnectionStateListener(this);
        for(Subscription subscription: subscriptions) {
            subscription.unsubscribe();
        }
        forEachController(UIController::onDestroy);
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }

    @Override
    public void onConnectionStateChanged(ConnectionManager.ConnectionState connectionState) {

    }

    protected boolean isOnline() {
        return ConnectionManager.getInstance().isOnline();
    }

    public CustomerBaseActivity getHostActivity() {
        return (CustomerBaseActivity)super.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        restorableManager.restore(savedInstanceState);
        View root = null;
        if (getLayoutResource() > 0) {
            root = inflater.inflate(getLayoutResource(), container, false);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        forEachController(controller -> controller.onViewCreated(savedInstanceState));
    }

    @Override
    public void onStart() {
        super.onStart();
        forEachController(UIController::onStart);
    }

    @Override
    public void onStop() {
        forEachController(UIController::onStop);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        forEachController(UIController::onResume);
    }

    @Override
    public void onPause() {
        forEachController(UIController::onPause);
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        forEachController(controller -> controller.onActivityResult(requestCode, resultCode, data));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        forEachController(controller -> controller.onRequestPermissionsResult(requestCode, permissions, grantResults));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        restorableManager.save(outState);
        forEachController(controller -> controller.onSaveInstanceState(outState));
    }

    private void forEachController(Action1<UIController> action) {
        for (UIController controller: controllers) {
            action.call(controller);
        }
    }

    protected void registerPersistent(Restorable object) {
        restorableManager.putRestorable(object);
    }

    protected RestorableManager getRestorableManager() {
        return restorableManager;
    }


    private <T> void subscribeRaw(Observable<T> observable, Subscriber<T> subscriber) {
        registerSubscription(observable.subscribe(subscriber));
    }

    public  <T> void subscribe(Observable<T> observable, Subscriber<T> subscriber) {
        registerSubscription(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public  <T> void subscribe(Observable<T> observable, Action1<T> onSuccess) {
        ProgressSubscriber<T> subscriber = new ProgressSubscriber<T>(getActivity()) {
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

        subscriber.showProgress();
        registerSubscription(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public void onClick(View view, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(view, onClickListener);
    }

    public void onClick(int viewResource, View.OnClickListener onClickListener) {
        DelayedClickListenerWrapper.wrap(getView().findViewById(viewResource), onClickListener);
    }

    public void onLoginStatusChanged() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }
}
