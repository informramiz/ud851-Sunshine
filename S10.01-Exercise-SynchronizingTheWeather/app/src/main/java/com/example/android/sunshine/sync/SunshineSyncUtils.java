package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.AnyThread;
import android.support.annotation.WorkerThread;

import com.example.android.sunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

// COMPLETED (9) Create a class called SunshineSyncUtils
    //  COMPLETED (10) Create a public static void method called startImmediateSync
    //  COMPLETED (11) Within that method, start the SunshineSyncIntentService
public final class SunshineSyncUtils {
    private static boolean sInitialized = false;

    private static final String SYNC_JOB_SERVICE_TAG = "weather_sync_job_service";
    private static final int SYNC_INTERVAL_MINUTES = 1 ; // hours to mins
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(2); //mins to seconds
    private static final int SYNC_FLEXTIME_SECONDS = (int) TimeUnit.MINUTES.toSeconds(1); //mins to seconds

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
                scheduleFirebaseJobDispatcherSync(context);
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

    @AnyThread
    private static void scheduleFirebaseJobDispatcherSync(Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job sunshineSyncJob = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setLifetime(Lifetime.FOREVER)
                .setTag(SYNC_JOB_SERVICE_TAG)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setTrigger(Trigger.executionWindow(
                        60,
                        120)
                )
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.schedule(sunshineSyncJob);
    }
}