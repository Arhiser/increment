package com.arhiser.increment;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.arhiser.increment.managers.connection.ConnectionManager;
import com.arhiser.increment.tools.CustomLogger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    @Override
    public void onCreate()
    {
        MultiDex.install(this);
        super.onCreate();
        ConnectionManager.init(this);
        CustomLogger.initInstance(this);

        //Fabric.with(this, new Crashlytics());

        // init Universal Image Loader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);


        Configuration configuration = new Configuration();
        configuration.locale = new Locale("ru", "RU");
        Locale.setDefault(configuration.locale);
        getResources().updateConfiguration(configuration, null);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onLowMemory() {
        ImageLoader.getInstance().clearMemoryCache();
        super.onLowMemory();
    }
}
