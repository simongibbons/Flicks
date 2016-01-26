package com.simongibbons.flicks;

import android.app.Application;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class FlicksApplication extends Application {

    private static final String LOG_TAG = FlicksApplication.class.getSimpleName();

    private OkHttpClient okHttpClient;

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup global okHttp instance so that we can cache responses.
        int cacheSize = 10 * 1024 * 1024; //10 MiB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        // Setup the global picasso instance to use this okHttpClient
        Picasso.Builder picassoBuilder = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(okHttpClient));

        Picasso picasso = picassoBuilder.build();

        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException e) {
            Log.d(LOG_TAG, "Unable to set singleton instance for Picasso");
        }
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
