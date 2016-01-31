package com.simongibbons.flicks;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class FlicksApplication extends Application {

    private static final String LOG_TAG = FlicksApplication.class.getSimpleName();

    private OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        // Setup global okHttp instance so that we can cache responses.

        int cacheSize = 5 * 1024 * 1024; //5 MiB for API responses only
        Cache cache = new Cache(getCacheDir(), cacheSize);

        try {
            cache.initialize();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Could not initialize okHTTP cache");
        }

        okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
