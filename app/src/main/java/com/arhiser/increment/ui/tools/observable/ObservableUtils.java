package com.arhiser.increment.ui.tools.observable;

import android.graphics.Bitmap;
import android.view.View;

import com.arhiser.increment.tools.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by arhis on 31.01.2017.
 */

public class ObservableUtils {

    public static Observable<Bitmap> getImageLoadingObservable(String avatarPath) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                try {
                    if (!Utils.isEmpty(avatarPath)) {
                        DisplayImageOptions.Builder optionsBuilder = new DisplayImageOptions.Builder();
                        optionsBuilder.cacheOnDisk(true);
                        optionsBuilder.cacheInMemory(true);
                        ImageLoader.getInstance().loadImage(avatarPath, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                subscriber.onError(failReason.getCause());
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                subscriber.onNext(loadedImage);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    } else {
                        throw new RuntimeException("Avatar is empty");
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
