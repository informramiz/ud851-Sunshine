package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.example.android.sunshine.data.WeatherContract;

// COMPLETED (9) Create a class called SunshineSyncUtils
    //  COMPLETED (10) Create a public static void method called startImmediateSync
    //  COMPLETED (11) Within that method, start the SunshineSyncIntentService
public final class SunshineSyncUtils {
    private static boolean sInitialized = false;

    @AnyThread
    public static void startImmediateSync(Context context) {
        Intent startSyncServiceIntent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(startSyncServiceIntent);
    }

    @AnyThread
    public static void initialize(final Context context) {
        AppExecutors.getInstance().diskIO.execute(() -> {
            if (!isAlreadyInitialized(context)) {
                startImmediateSync(context);
                sInitialized = true;
            }
        });
    }

    @WorkerThread
    private static boolean isAlreadyInitialized(Context context) {
        if (sInitialized) return true;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        int rowsCount = cursor != null ? cursor.getCount() : 0;
        if (cursor != null) cursor.close();
        return rowsCount > 0;
    }
}