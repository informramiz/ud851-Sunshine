package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import java.net.URL;

//  COMPLETED (1) Create a class called SunshineSyncTask
//  COMPLETED (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
//      COMPLETED (3) Within syncWeather, fetch new weather data
//      COMPLETED (4) If we have valid results, delete the old data and insert the new
public final class SunshineSyncTask {
    @WorkerThread
    synchronized public static void syncWeather(Context context) {
        try {
            final URL weatherRequestUrl = NetworkUtils.getUrl(context);
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            final ContentValues[] weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse);
            if (isDataValid(weatherValues)) {
                deleteAllData(context);
                insertData(context, weatherValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isDataValid(@Nullable final ContentValues[] weatherValues) {
        return weatherValues != null && weatherValues.length > 0;
    }

    private static void deleteAllData(Context context) {
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(
                WeatherContract.WeatherEntry.CONTENT_URI,
                null,
                null
        );
    }

    private static void insertData(Context context, final ContentValues[] contentValues) {
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
    }
}