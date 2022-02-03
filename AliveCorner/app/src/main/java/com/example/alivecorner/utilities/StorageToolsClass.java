package com.example.alivecorner.utilities;

import static com.example.alivecorner.MainActivity.MAX_COUNT_OF_NEWS_PER_DEVICE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.StartActivity;
import com.example.alivecorner.data.DeviceDataClass;
import com.example.alivecorner.data.NewsDataClass;
import com.example.alivecorner.data.ScheduleDataClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Класс, используемый для записи и чтения данных из SharedPreferences и памяти
 *
 * @author Гизатуллин Акрам
 */
public class StorageToolsClass {
    /**
     * Конастанты для работы с SharedPreferences
     */
    private static final String APP_PREFERENCES_DEVICES = "JsonDevicesDataPrefAC";
    private static final String APP_PREFERENCES_NEWS = "JsonNewsDataPrefAC";
    private static final String APP_PREFERENCES_SCHEDULE = "JsonScheduleDataPrefAC";
    private static final String APP_PREFERENCES_SETTINGS = "JsonSettingsDataPrefAC";

    /**
     * Функция для записи настроек в SharedPreferences
     */
    public static void writeSettings() {
        try {
            JSONObject jsSettings = new JSONObject();
            jsSettings.put("dGMT", MainActivity.dGMT);
            jsSettings.put("language", MainActivity.APP_LANGUAGE);
            SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
            editor.putString(APP_PREFERENCES_SETTINGS, jsSettings.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция для записи данных об автокормушках в SharedPreferences
     */
    public static void writeDevices() {
        if (MainActivity.devicesList.size() > 0) {
            JSONArray dev_data = new JSONArray();
            try {
                JSONObject device;
                for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                    device = new JSONObject();
                    device.put("name", MainActivity.devicesList.get(i).getName());
                    device.put("id", MainActivity.devicesList.get(i).getID());
                    device.put("password", MainActivity.devicesList.get(i).getPassword());
                    device.put("status", MainActivity.devicesList.get(i).getStatus());
                    device.put("food_lvl", MainActivity.devicesList.get(i).getFoodLvl());
                    dev_data.put(device);
                }
                SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
                editor.putString(APP_PREFERENCES_DEVICES, dev_data.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
            editor.putString(APP_PREFERENCES_DEVICES, "");
            editor.apply();
        }
    }

    /**
     * Функция для записи данных о новостях в SharedPreferences
     */
    public static void writeNews() {
        if (MainActivity.allNewsList.size() > 0) {
            JSONArray news_data = new JSONArray();
            try {
                JSONObject news;
                for (int i = 0; i < MainActivity.allNewsList.size(); i++) {
                    news = new JSONObject();
                    news.put("name", MainActivity.allNewsList.get(i).getName());
                    news.put("id", MainActivity.allNewsList.get(i).getDevID());
                    news.put("type", MainActivity.allNewsList.get(i).getType());
                    news.put("DateAndTime", MainActivity.allNewsList.get(i).getDateAndTime());
                    news.put("image", MainActivity.allNewsList.get(i).getImage());
                    news.put("error_type", MainActivity.allNewsList.get(i).getErrorType());
                    news_data.put(news);
                }
                SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
                editor.putString(APP_PREFERENCES_NEWS, news_data.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
            editor.putString(APP_PREFERENCES_NEWS, "");
            editor.apply();
        }
    }

    /**
     * Функция для записи данных о расписании в SharedPreferences
     */
    public static void writeSchedule() {
        JSONArray schedules_data = new JSONArray();
        try {
            JSONObject schedules;
            for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                for (int n = 0; n < MainActivity.devicesList.get(i).getSchedule().size(); n++) {
                    schedules = new JSONObject();
                    schedules.put("hour", MainActivity.devicesList.get(i).getSchedule().get(n).getHour());
                    schedules.put("minute", MainActivity.devicesList.get(i).getSchedule().get(n).getMinute());
                    schedules.put("portion_size", MainActivity.devicesList.get(i).getSchedule().get(n).getPortionSize());
                    schedules.put("is_enabled", MainActivity.devicesList.get(i).getSchedule().get(n).getIsEnabled());
                    schedules.put("dev_id", MainActivity.devicesList.get(i).getSchedule().get(n).getDeviceID());
                    schedules.put("days_feed", MainActivity.devicesList.get(i).getSchedule().get(n).getEnDaysFeed());
                    schedules_data.put(schedules);
                }
            }
            SharedPreferences.Editor editor = StartActivity.sharedPrefSA.edit();
            editor.putString(APP_PREFERENCES_SCHEDULE, schedules_data.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция для получения данных о настройках в SharedPreferences
     */
    public static void getSettingsFromSP() {
        if (StartActivity.sharedPrefSA.contains(APP_PREFERENCES_SETTINGS)) {
            String strJsonData = StartActivity.sharedPrefSA.getString(APP_PREFERENCES_SETTINGS, "0");
            if (!"".equals(strJsonData) && !"0".equals(strJsonData)) {
                try {
                    JSONObject settingsInfo = new JSONObject(strJsonData);
                    MainActivity.dGMT = settingsInfo.get("dGMT").toString();
                    MainActivity.APP_LANGUAGE = settingsInfo.get("language").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("SharedPref", "Error");
                }
            } else {
                Log.i("SharedPref", "No data founded");
                setDefaultSettings();
            }
        } else {
            Log.i("SharedPref", "No data founded");
            setDefaultSettings();
        }
    }

    /**
     * Функция для получения данных о расписаниях в SharedPreferences
     */
    public static void getScheduleFromSP() {
        if (StartActivity.sharedPrefSA.contains(APP_PREFERENCES_SCHEDULE)) {
            String strJson = StartActivity.sharedPrefSA.getString(APP_PREFERENCES_SCHEDULE, "0");
            if (!"".equals(strJson) && !"0".equals(strJson)) {
                try {
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        MainActivity.devicesList.get(i).clearSchedule();
                    }
                    JSONArray all_schedules_info = new JSONArray(strJson);
                    for (int i = 0; i < all_schedules_info.length(); i++) {
                        JSONObject cur_schedule = all_schedules_info.getJSONObject(i);
                        for (int n = 0; n < MainActivity.devicesList.size(); n++) {
                            if (cur_schedule.getString("dev_id").equals(MainActivity.devicesList.get(n).getID())) {
                                ScheduleDataClass scheduleDataClass = new ScheduleDataClass(cur_schedule.getInt("hour"),
                                        cur_schedule.getInt("minute"),
                                        cur_schedule.getInt("portion_size"),
                                        cur_schedule.getBoolean("is_enabled"),
                                        cur_schedule.getString("dev_id"),
                                        cur_schedule.getString("days_feed"));
                                MainActivity.devicesList.get(n).addNewSchedule(scheduleDataClass);
                            }
                        }
                    }
                    for (int i = 0; i < MainActivity.devicesList.size(); i++)
                        MainActivity.devicesList.get(i).sortSchedule();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("SharedPref", "Error");
                }
            }
        } else {
            Log.i("SharedPref", "No data founded");
        }
    }

    /**
     * Функция для получения данных о автокормушках в SharedPreferences
     */
    public static void getDevicesFromSP() {
        if (StartActivity.sharedPrefSA.contains(APP_PREFERENCES_DEVICES)) {
            String strJson = StartActivity.sharedPrefSA.getString(APP_PREFERENCES_DEVICES, "0");
            if (!"".equals(strJson) && !"0".equals(strJson)) {
                try {
                    MainActivity.devicesList.clear();
                    JSONArray all_device_info = new JSONArray(strJson);
                    for (int i = 0; i < all_device_info.length(); i++) {
                        JSONObject cur_dev = all_device_info.getJSONObject(i);
                        DeviceDataClass deviceDataClass = new DeviceDataClass(cur_dev.getString("name"),
                                cur_dev.getString("id"),
                                cur_dev.getString("password"),
                                cur_dev.getString("status"),
                                cur_dev.getInt("food_lvl"));
                        MainActivity.devicesList.add(deviceDataClass);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("SharedPref", "Error");
                }
            }
        } else {
            Log.i("SharedPref", "No data founded");
        }
    }

    /**
     * Функция для получения данных о записях в SharedPreferences
     */
    public static void getNewsFromSP() {
        if (StartActivity.sharedPrefSA.contains(APP_PREFERENCES_NEWS)) {
            String strJson = StartActivity.sharedPrefSA.getString(APP_PREFERENCES_NEWS, "0");
            if (!"".equals(strJson) && !"0".equals(strJson)) {
                try {
                    MainActivity.allNewsList.clear();
                    JSONArray all_news_info = new JSONArray(strJson);
                    for (int i = 0; i < all_news_info.length(); i++) {
                        JSONObject cur_news = all_news_info.getJSONObject(i);
                        NewsDataClass newsDataClass = new NewsDataClass(cur_news.getString("name"),
                                cur_news.getString("id"),
                                cur_news.getString("type"),
                                cur_news.getString("DateAndTime"),
                                cur_news.getString("image"),
                                cur_news.getString("error_type"));
                        if (!"Error".equals(newsDataClass.getType())) {
                            newsDataClass.setPreloadedImg(loadImg(Integer.parseInt(newsDataClass.getImage())));
                        }
                        addNewsByID(cur_news.getString("id"), newsDataClass);
                        MainActivity.allNewsList.add(newsDataClass);
                    }
                    Collections.sort(MainActivity.allNewsList);
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        MainActivity.devicesList.get(i).sortNews();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("SharedPref", "Error");
                }
            }
        } else {
            Log.i("SharedPref", "No data founded");
        }
    }

    /**
     * Функция для добавления новости к данным автокормушки по ее id
     *
     * @param id   - id автокормушки, к которой нужно добавить запись
     * @param news - сама запись для добавления
     */
    public static void addNewsByID(String id, NewsDataClass news) {
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            if (id.equals(MainActivity.devicesList.get(i).getID())) {
                MainActivity.devicesList.get(i).addNews(news);
            }
        }
    }

    /**
     * Функция для поиска индекса автокормушки по ее id
     *
     * @param search_id - id автокормушки, которую мы ищем
     * @return индекс автокормушки в общем массиве
     */
    public static int getElementByID(String search_id) {
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            if (search_id.equals(MainActivity.devicesList.get(i).getID())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Функция для поиска индекса автокормушки по ее названию
     *
     * @param search_name - название автокормушки, которую мы ищем
     * @return индекс автокормушки в общем массиве
     */
    public static int getElementByName(String search_name) {
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            if (search_name.equals(MainActivity.devicesList.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Функция для проверки существования автокормушки с заданным id
     *
     * @param search_id - название автокормушки, которую мы ищем
     * @return true, если существует; и false, если нет
     */
    public static boolean isSuchDevExistsByID(String search_id) {
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            if (search_id.equals(MainActivity.devicesList.get(i).getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция для проверки существования автокормушки с заданным названием
     *
     * @param search_name - название автокормушки, которую мы ищем
     * @return true, если существует; и false, если нет
     */
    public static boolean isSuchDevExistsByName(String search_name) {
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            if (search_name.equals(MainActivity.devicesList.get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Функция для получения незанятого суффикса для названия изображения
     *
     * @return свободный суффикс
     */
    public static int getFreeCounter() {
        int res = -1;
        for (int i = 0; i <= MainActivity.allNewsList.size(); i++) {
            boolean is_used = false;
            for (int n = 0; n < MainActivity.allNewsList.size(); n++) {
                if (!"Error".equals(MainActivity.allNewsList.get(n).getType())) {
                    if (Integer.parseInt(MainActivity.allNewsList.get(n).getImage()) == i) {
                        is_used = true;
                    }
                }
            }
            if (!is_used) {
                res = i;
                break;
            }
        }
        return res;
    }

    /**
     * Функция для удаления устройства из SharedPreferences
     *
     * @param dev_num - индекс устройства в общем массиве
     */
    public static void deleteDevice(int dev_num) {
        for (int i = 0; i < MainActivity.devicesList.get(dev_num).getNews().size(); i++) {
            if (!MainActivity.devicesList.get(dev_num).getNews().get(i).getType().equals("Error")) {
                deleteImg(Integer.parseInt(MainActivity.devicesList.get(dev_num).getNews().get(i).getImage()));
            }
        }
        for (int i = 0; i < MainActivity.allNewsList.size(); i++) {
            if (MainActivity.allNewsList.get(i).getDevID().equals(MainActivity.devicesList.get(dev_num).getID())) {
                MainActivity.allNewsList.remove(i);
                i--;
            }
        }
        for (int i = 0; i < MainActivity.menu.size(); i++) {
            if (MainActivity.menu.getItem(i).isChecked()) {
                MainActivity.menu.getItem(i).setVisible(false);
                MainActivity.menu.removeItem(i);
                break;
            }
        }
        MainActivity.devicesList.remove(dev_num);
        writeDevices();
        writeNews();
        writeSchedule();
    }

    /**
     * Функция для удаления ихображения из памяти
     *
     * @param counter - суффикс названия удаляемого изображения
     */
    public static void deleteImg(int counter) {
        try {
            File myExternalFile = new File(MainActivity.path, "imageAC" + counter + ".png");
            if (myExternalFile.exists()) {
                if (myExternalFile.delete()) {
                    Log.i("Deleted", "deleted successful");
                } else {
                    Log.i("Deleted", "deleted unsuccessful");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция для сохранения ихображения в память
     *
     * @param counter - суффикс названия сохраняемого изображения
     * @param bytes   - данные самого изображения
     */
    public static void saveImg(byte[] bytes, int counter) {
        File myExternalFile = new File(MainActivity.path, "imageAC" + counter + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Функция для загрузки данных ихображения из память
     *
     * @param counter - суффикс названия изображения
     * @return Bitmap запрашиваемого изображения
     */
    public static Bitmap loadImg(int counter) {
        return BitmapFactory.decodeFile(MainActivity.path + "/imageAC" + counter + ".png");
    }

    /**
     * Функция для удаления лишних новостей из SharedPreferences
     */
    public static void clearOldNews() {
        int needSize = MainActivity.devicesList.size() * MAX_COUNT_OF_NEWS_PER_DEVICE;
        if (MainActivity.allNewsList.size() > needSize) {
            while (MainActivity.allNewsList.size() >= needSize) {
                int num_tmp = getElementByID(MainActivity.allNewsList.get(0).getDevID());
                if (!MainActivity.devicesList.get(num_tmp).getNewsElement(0).getType().equals("Error")) {
                    deleteImg(Integer.parseInt(MainActivity.devicesList.get(num_tmp).getNewsElement(0).getImage()));
                }
                MainActivity.devicesList.get(num_tmp).removeNewsElement(0);
                MainActivity.allNewsList.remove(0);
            }
        }
    }

    /**
     * Функция для установки стандартных настроек
     */
    @SuppressLint("SimpleDateFormat")
    public static void setDefaultSettings() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z");
        MainActivity.dGMT = date.format(currentLocalTime);
        MainActivity.APP_LANGUAGE = Resources.getSystem().getConfiguration().locale.getLanguage();
        if (!(MainActivity.APP_LANGUAGE.equals("ru") || MainActivity.APP_LANGUAGE.equals("en")))
            MainActivity.APP_LANGUAGE = "en";
        writeSettings();
    }
}
