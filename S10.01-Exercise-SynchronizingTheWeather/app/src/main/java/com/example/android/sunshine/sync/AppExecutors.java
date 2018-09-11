package com.example.android.sunshine.sync;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Ramiz Raja on 11/09/2018.
 */
public class AppExecutors {
    public final Executor network;
    public Executor diskIO;
    public Executor mainThread;

    private static AppExecutors instance;

    private AppExecutors(Executor network, Executor diskIO, Executor mainThread) {
        this.network = network;
        this.diskIO = diskIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            instance = new AppExecutors(
                    Executors.newFixedThreadPool(3),
                    Executors.newSingleThreadExecutor(),
                    new MainThreadExecutor()
            );
        }

        return instance;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
