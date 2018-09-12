package com.example.android.sunshine.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


/**
 * Created by Ramiz Raja on 11/09/2018.
 */
public class SunshineFirebaseJobService extends JobService {
    private AsyncTask<Void, Void, Void> mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        Log.i(SunshineFirebaseJobService.class.getSimpleName(), "Running sunshine sync server");
        mBackgroundTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SunshineSyncTask.syncWeather(SunshineFirebaseJobService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(job, false);
            }
        };
        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
