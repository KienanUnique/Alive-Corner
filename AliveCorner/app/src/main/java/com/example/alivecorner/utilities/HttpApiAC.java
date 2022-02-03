package com.example.alivecorner.utilities;

import static com.example.alivecorner.MainActivity.FEED_NOW_URL;
import static com.example.alivecorner.MainActivity.MAIN_URL;
import static com.example.alivecorner.MainActivity.PUBLIC_KEY;
import static com.example.alivecorner.MainActivity.REGISTER_NEW_DEVICE_URL;
import static com.example.alivecorner.MainActivity.SETUP_DEVICES_URL;
import static com.example.alivecorner.MainActivity.SET_SCHEDULE_URL;
import static com.example.alivecorner.MainActivity.STARTUP_REQUEST_URL;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.alivecorner.GlobalApplication;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.data.DeviceDataClass;
import com.example.alivecorner.data.NewsDataClass;
import com.example.alivecorner.data.ScheduleDataClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Класс, предназначенный для связи с сервером;
 *
 * @author Гизатуллин Акрам
 */
public class HttpApiAC {

    /**
     * Функция для отправки JSONObject на сервер
     *
     * @param urlAddress - адресс для отправки запроса
     * @param jsonParam  - данные для отправки
     * @return ответ сервера в виде JSONArray
     */
    public static JSONArray sendPost(final String urlAddress, final JSONObject jsonParam) throws JSONException {
        final String[] answer_tmp = new String[1];
        answer_tmp[0] = "[{\"status_request\": \"wait\"}]";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlAddress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    StringBuffer inputLine = new StringBuffer();
                    String tmp;
                    while ((tmp = dis.readLine()) == null) {
                    }
                    inputLine.append(tmp);
                    while ((tmp = dis.readLine()) != null) {
                        inputLine.append(tmp);
                    }
                    answer_tmp[0] = inputLine.toString();
                    dis.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    answer_tmp[0] = "[{\"status_request\": \"Server is unavailable\"}]";
                }
            }
        });
        thread.start();
        while (thread.isAlive()) ;
        JSONArray jsonResult = new JSONArray(answer_tmp[0]);
        return jsonResult;
    }

    /**
     * Функция для отправки JSONArray на сервер
     *
     * @param urlAddress - адресс для отправки запроса
     * @param jsonParam  - данные для отправки
     * @return ответ сервера в виде JSONArray
     */
    public static JSONArray sendPost(final String urlAddress, final JSONArray jsonParam) throws JSONException {
        final String[] answer_tmp = new String[1];
        answer_tmp[0] = "[{\"status_request\": \"wait\"}]";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlAddress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG", conn.getResponseMessage());
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    StringBuffer inputLine = new StringBuffer();
                    String tmp;
                    while ((tmp = dis.readLine()) == null) {
                    }
                    inputLine.append(tmp);
                    while ((tmp = dis.readLine()) != null) {
                        inputLine.append(tmp);
                    }
                    answer_tmp[0] = inputLine.toString();
                    dis.close();
                    conn.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    answer_tmp[0] = "[{\"status_request\": \"Server is unavailable\"}]";
                }
            }
        });
        thread.start();
        while (thread.isAlive()) ;
        JSONArray jsonResult = new JSONArray(answer_tmp[0]);
        return jsonResult;
    }

    /**
     * Функция для регистрации автокормушки
     *
     * @param name  - имя регистрируемой автокормушки
     * @param id    - id регистрируемой автокормушки
     * @param passw - приватный ключ регистрируемой автокормушки
     * @return результат регистрации
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String Registrate_dev(String name, String id, String passw) throws JSONException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        final String urlAddress = MAIN_URL + REGISTER_NEW_DEVICE_URL;

        String idEnc = AES.encrypt(id, PUBLIC_KEY);
        String idEncPass = AES.encrypt(id, passw);

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("id", idEnc);
        jsonParam.put("cr_pass", idEncPass);

        JSONArray jsonRes = sendPost(urlAddress, jsonParam);

        String enc_res;

        if (!jsonRes.getJSONObject(0).getString("status_request").equals("Server is unavailable")) {

            enc_res = AES.decrypt(jsonRes.getJSONObject(0).getString("status_request"), PUBLIC_KEY);
            if (enc_res.equals("Ok")) {
                DeviceDataClass deviceDataClass = new DeviceDataClass(name, id, passw);
                MainActivity.devicesList.add(deviceDataClass);
                int dev_num = StorageToolsClass.getElementByID(id);

                MainActivity.devicesList.get(dev_num).setStatus(AES.decrypt(jsonRes.getJSONObject(0).getString("status"), passw));
                MainActivity.devicesList.get(dev_num).setFoodLvl(Integer.parseInt(AES.decrypt(jsonRes.getJSONObject(0).getString("food_lvl"), passw)));

                JSONArray JSONschedule = jsonRes.getJSONObject(0).getJSONArray("schedule");
                for (int n = 0; n < JSONschedule.length(); n++) {
                    int hour, minute;
                    String dec_time = AES.decrypt(JSONschedule.getJSONObject(n).getString("time"), passw);
                    hour = Integer.parseInt(dec_time.substring(0, 2));
                    minute = Integer.parseInt(dec_time.substring(3, 5));
                    ScheduleDataClass scheduleDataClass = new ScheduleDataClass(hour,
                            minute,
                            Integer.parseInt(AES.decrypt(JSONschedule.getJSONObject(n).getString("portion_size"), passw)),
                            true,
                            id,
                            AES.decrypt(JSONschedule.getJSONObject(n).getString("days_feed"), passw));
                    scheduleDataClass.convertGlobalToLocalTZ();
                    MainActivity.devicesList.get(dev_num).addNewSchedule(scheduleDataClass);
                }

                JSONArray news_data = jsonRes.getJSONObject(0).getJSONArray("news");
                String dec_type, dec_TD, dec_pic, dec_error;
                for (int n = 0; n < news_data.length(); n++) {
                    dec_type = AES.decrypt(news_data.getJSONObject(n).getString("type"), passw);
                    dec_TD = AES.decrypt(news_data.getJSONObject(n).getString("TD"), passw);
                    dec_pic = AES.decrypt(news_data.getJSONObject(n).getString("image"), passw);
                    dec_error = AES.decrypt(news_data.getJSONObject(n).getString("error"), passw);
                    if (!"Error".equals(dec_type)) {
                        if (dec_type == null) {
                            Log.e("Type error", "NULL");
                        } else {
                            Log.e("Type error", dec_type);
                        }
                        dec_pic = dec_pic.substring(2, dec_pic.length() - 1);
                    }
                    NewsDataClass news = new NewsDataClass(id, dec_type, dec_TD, dec_pic, dec_error);
                    news.convertDateTimeToLocalTZ();
                    MainActivity.allNewsList.add(news);
                    MainActivity.devicesList.get(dev_num).addNews(news);
                }

                MainActivity.devicesList.get(dev_num).sortNews();
                MainActivity.devicesList.get(dev_num).sortSchedule();
                MainActivity.devicesList.get(dev_num).calcNextTime();
                Collections.sort(MainActivity.allNewsList);

                StorageToolsClass.writeDevices();
                StorageToolsClass.writeNews();
                StorageToolsClass.writeSchedule();
            } else {
                enc_res = GlobalApplication.getAppContext().getString(R.string.invalid_device_data_warning);
            }
        } else {
            enc_res = GlobalApplication.getAppContext().getString(R.string.server_is_unavailable_warning);
        }
        return enc_res;
    }

    /**
     * Функция для проверки изменений в расписании устройств и новых записей
     *
     * @return true, если запрос удался и false, если произошла ошибка
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean longPollRequest() throws JSONException {
        boolean is_ok = false;
        if (MainActivity.devicesList.size() != 0) {
            final String urlAddress = MAIN_URL + STARTUP_REQUEST_URL;
            JSONObject sendJson = new JSONObject();

            String fromDateTime;
            if (MainActivity.allNewsList.size() == 0) fromDateTime = "all";
            else
                fromDateTime = MainActivity.allNewsList.get(MainActivity.allNewsList.size() - 1).getStringGlobalDateAndTime();

            sendJson.put("fromDT", AES.encrypt(fromDateTime, PUBLIC_KEY));

            JSONArray all_dev_data = new JSONArray();
            for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                JSONObject dev_data = new JSONObject();
                dev_data.put("id", AES.encrypt(MainActivity.devicesList.get(i).getID(), PUBLIC_KEY));
                dev_data.put("id_enc", AES.encrypt(MainActivity.devicesList.get(i).getID(), MainActivity.devicesList.get(i).getPassword()));
                all_dev_data.put(dev_data);
            }
            sendJson.put("devices", all_dev_data);

            JSONArray jsonRes = sendPost(urlAddress, sendJson);
            if (!jsonRes.getJSONObject(0).getString("status_request").equals("Server is unavailable")) {
                try {
                    if (AES.decrypt(jsonRes.getJSONObject(0).getString("status_request"), PUBLIC_KEY).equals("Error - Bad ids encryption")) {
                        is_ok = false;
                    } else {
                        is_ok = true;
                    }
                } catch (JSONException e) {
                    Log.i("Status", "Ok");

                }
                if (is_ok) {
                    String dec_id, dev_pass, dec_type, dec_TD, dec_pic, dec_error, dec_status, dec_food_lvl;
                    int dev_num;
                    for (int i = 0; i < jsonRes.length(); i++) {
                        JSONObject device_data = jsonRes.getJSONObject(i);
                        dec_id = AES.decrypt(device_data.getString("id"), PUBLIC_KEY);
                        if (MainActivity.isInSplash) {
                            dev_num = StorageToolsClass.getElementByID(dec_id);
                        } else {
                            dev_num = StorageToolsClass.getElementByID(dec_id);
                        }

                        dev_pass = MainActivity.devicesList.get(dev_num).getPassword();

                        dec_status = AES.decrypt(device_data.getString("status"), dev_pass);
                        dec_food_lvl = AES.decrypt(device_data.getString("food_lvl"), dev_pass);
                        MainActivity.devicesList.get(dev_num).setFoodLvl(Integer.parseInt(dec_food_lvl));
                        MainActivity.devicesList.get(dev_num).setStatus(dec_status);

                        JSONArray news_data = device_data.getJSONArray("news");
                        for (int n = 0; n < news_data.length(); n++) {
                            dec_type = AES.decrypt(news_data.getJSONObject(n).getString("type"), dev_pass);
                            dec_TD = AES.decrypt(news_data.getJSONObject(n).getString("TD"), dev_pass);
                            dec_pic = AES.decrypt(news_data.getJSONObject(n).getString("image"), dev_pass);
                            dec_error = AES.decrypt(news_data.getJSONObject(n).getString("error"), dev_pass);
                            if (!"Error".equals(dec_type)) {
                                dec_pic = dec_pic.substring(2, dec_pic.length() - 1);
                            }
                            NewsDataClass news = new NewsDataClass(dec_id, dec_type, dec_TD, dec_pic, dec_error);
                            news.convertDateTimeToLocalTZ();
                            MainActivity.allNewsList.add(news);
                            MainActivity.devicesList.get(dev_num).addNews(news);
                        }

                        MainActivity.devicesList.get(dev_num).removeAllEnabledTime();

                        JSONArray JSONschedule = jsonRes.getJSONObject(i).getJSONArray("schedule");
                        for (int n = 0; n < JSONschedule.length(); n++) {
                            int hour, minute;
                            String dec_time = AES.decrypt(JSONschedule.getJSONObject(n).getString("time"), dev_pass);
                            hour = Integer.parseInt(dec_time.substring(0, 2));
                            minute = Integer.parseInt(dec_time.substring(3, 5));
                            ScheduleDataClass scheduleDataClass = new ScheduleDataClass(hour,
                                    minute,
                                    Integer.parseInt(AES.decrypt(JSONschedule.getJSONObject(n).getString("portion_size"), dev_pass)),
                                    true,
                                    dec_id,
                                    AES.decrypt(JSONschedule.getJSONObject(n).getString("days_feed"), dev_pass));
                            scheduleDataClass.convertGlobalToLocalTZ();
                            MainActivity.devicesList.get(dev_num).deleteRepeatingTime(scheduleDataClass);
                            MainActivity.devicesList.get(dev_num).addNewSchedule(scheduleDataClass);
                        }
                        MainActivity.devicesList.get(dev_num).calcNextTime();
                    }

                    Collections.sort(MainActivity.allNewsList);
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        MainActivity.devicesList.get(i).sortSchedule();
                        MainActivity.devicesList.get(i).sortNews();
                    }
                    StorageToolsClass.writeDevices();
                    StorageToolsClass.writeNews();
                    StorageToolsClass.writeSchedule();
                }
            }
        } else {
            is_ok = false;
        }
        return is_ok;
    }

    /**
     * Функция для проверки изменений в расписании устройств
     *
     * @return true, если запрос удался и false, если произошла ошибка
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean SendSchedule() throws JSONException {
        final String urlAddress = MAIN_URL + SET_SCHEDULE_URL;

        JSONArray sendJson = new JSONArray();

        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            JSONObject device_data = new JSONObject();
            JSONArray schedule_data = new JSONArray();
            for (int n = 0; n < MainActivity.devicesList.get(i).getSchedule().size(); n++) {
                if (MainActivity.devicesList.get(i).getSchedule().get(n).getIsEnabled()) {
                    ScheduleDataClass scheduleDataClassSend = MainActivity.devicesList.get(i).getSchedule().get(n).getGlobalSchedule();
                    JSONObject schedules = new JSONObject();
                    schedules.put("time", AES.encrypt(scheduleDataClassSend.getStringTime(), MainActivity.devicesList.get(i).getPassword()));
                    schedules.put("portion_size", AES.encrypt(Integer.toString(scheduleDataClassSend.getPortionSize()), MainActivity.devicesList.get(i).getPassword()));
                    schedules.put("days_feed", AES.encrypt(scheduleDataClassSend.getEnDaysFeed(), MainActivity.devicesList.get(i).getPassword()));
                    schedule_data.put(schedules);
                }
            }

            device_data.put("schedule", schedule_data);
            device_data.put("id", AES.encrypt(MainActivity.devicesList.get(i).getID(), PUBLIC_KEY));
            device_data.put("id_enc", AES.encrypt(MainActivity.devicesList.get(i).getID(), MainActivity.devicesList.get(i).getPassword()));
            sendJson.put(device_data);
        }

        JSONArray jsonRes = sendPost(urlAddress, sendJson);

        boolean is_ok;
        if (!jsonRes.getJSONObject(0).getString("status_request").equals("Server is unavailable")) {
            is_ok = true;
            for (int i = 0; i < jsonRes.length(); i++) {
                if (!AES.decrypt(jsonRes.getJSONObject(i).getString("status_request"), PUBLIC_KEY).equals("Ok")) {
                    is_ok = false;
                }
            }
        } else {
            is_ok = false;
        }
        return is_ok;
    }

    /**
     * Функция для проверки изменений в статусах устройств
     *
     * @return true, если запрос удался и false, если произошла ошибка
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean sendStatuses() throws JSONException {
        final String urlAddress = MAIN_URL + SETUP_DEVICES_URL;

        JSONArray sendJson = new JSONArray();

        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            JSONObject device_data = new JSONObject();

            device_data.put("id", AES.encrypt(MainActivity.devicesList.get(i).getID(), PUBLIC_KEY));
            device_data.put("id_enc", AES.encrypt(MainActivity.devicesList.get(i).getID(), MainActivity.devicesList.get(i).getPassword()));
            device_data.put("status", AES.encrypt(MainActivity.devicesList.get(i).getStatus(), MainActivity.devicesList.get(i).getPassword()));
            sendJson.put(device_data);
        }
        JSONArray jsonRes = sendPost(urlAddress, sendJson);
        boolean is_ok;
        if (!jsonRes.getJSONObject(0).getString("status_request").equals("Server is unavailable")) {
            is_ok = AES.decrypt(jsonRes.getJSONObject(0).getString("status_request"), PUBLIC_KEY).equals("Ok");
        } else {
            is_ok = false;
        }
        return is_ok;
    }

    /**
     * Функция для запроса для кормления сейчас
     *
     * @return true, если запрос удался и false, если произошла ошибка
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String requestFeedNow(int dev_num, int portion_size) throws JSONException {
        final String urlAddress = MAIN_URL + FEED_NOW_URL;
        Timer mTimer;
        MyTimerTask mMyTimerTask;

        JSONObject sendJson = new JSONObject();
        sendJson.put("id", AES.encrypt(MainActivity.devicesList.get(dev_num).getID(), PUBLIC_KEY));
        sendJson.put("id_enc", AES.encrypt(MainActivity.devicesList.get(dev_num).getID(), MainActivity.devicesList.get(dev_num).getPassword()));
        sendJson.put("portion_size", AES.encrypt(Integer.toString(portion_size), MainActivity.devicesList.get(dev_num).getPassword()));
        JSONArray jsonRes = sendPost(urlAddress, sendJson);
        String res;
        if (!jsonRes.getJSONObject(0).getString("status_request").equals("Server is unavailable")) {
            res = AES.decrypt(jsonRes.getJSONObject(0).getString("status_request"), PUBLIC_KEY);
            Log.i("Status feeding", res);
            if (res.equals("Ok") || res.equals("Cooldown")) {
                MainActivity.devicesList.get(dev_num).setReadyFeedNow(false);
                mTimer = new Timer();
                mMyTimerTask = new MyTimerTask(dev_num);
                mTimer.schedule(mMyTimerTask, 85000);
            }
        } else {
            res = "Server is unavailable";
        }
        return res;
    }

    /**
     * Класс, необходимый для ограничения частого вызова покормить сейчас
     *
     * @return true, если запрос удался и false, если произошла ошибка
     */
    static class MyTimerTask extends TimerTask {

        int dev_num;

        public MyTimerTask(int _dev_num) {
            this.dev_num = _dev_num;
        }

        @Override
        public void run() {
            MainActivity.devicesList.get(dev_num).setReadyFeedNow(true);
        }
    }
}