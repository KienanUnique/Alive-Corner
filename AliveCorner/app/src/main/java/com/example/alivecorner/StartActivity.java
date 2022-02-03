package com.example.alivecorner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;

import org.json.JSONException;

/**
 * Стартовая активность, с ее помощью загружаются данные из SharedPreferences
 * и выполняется запрос на сервер для проверки изменений.
 *
 * @author Гизатуллин Акрам
 */
public class StartActivity extends AppCompatActivity {

    private static final String APP_PREFERENCES_Devices = "JsonDevicesDataPrefAC";
    private static final String APP_PREFERENCES_News = "JsonNewsDataPrefAC";
    private static final String APP_PREFERENCES_Schedule = "JsonScheduleDataPrefAC";
    private static final String APP_PREFERENCES_SETTINGS = "JsonSettingsDataPrefAC";
    public static SharedPreferences sharedPrefSA;
    private static String filepath = "SavedImagesAC";

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.CAMERA
        };

        while (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        MainActivity.isInSplash = true;
        MainActivity.path = getExternalFilesDir(filepath);
        if (!MainActivity.path.exists()) {
            MainActivity.path.mkdir();
        }
        sharedPrefSA = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

        StorageToolsClass.getSettingsFromSP();
        StorageToolsClass.getDevicesFromSP();
        StorageToolsClass.getNewsFromSP();
        StorageToolsClass.getScheduleFromSP();

        if (MainActivity.devicesList.size() != 0) {
            try {
                MainActivity.isStartRequestOk = HttpApiAC.longPollRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            MainActivity.isStartRequestOk = true;
        }

        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            MainActivity.devicesList.get(i).calcNextTime();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void WipeAllData() {
        SharedPreferences.Editor editor = sharedPrefSA.edit();
        editor.putString(APP_PREFERENCES_Devices, "");
        editor.putString(APP_PREFERENCES_News, "");
        editor.putString(APP_PREFERENCES_Schedule, "");
        editor.putString(APP_PREFERENCES_SETTINGS, "");
        editor.apply();
        for (int i = 0; i < 20; i++) {
            StorageToolsClass.deleteImg(i);
        }
    }
}
