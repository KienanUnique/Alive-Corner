package com.example.alivecorner.data;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.utilities.StorageToolsClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Класс новостной записи, предназначен для хранения и обработки данных о записях
 *
 * @author Гизатуллин Акрам
 */

public class NewsDataClass implements Comparable<NewsDataClass> {
    private String dateAndTime;
    private final String id;
    private String name;
    private final String image;
    private final String type;
    private String error_type;
    private Date TDclass;
    private Bitmap preloadedImg;

    /**
     * Конструктор - создание нового объекта исключительно с основными данными, используется при регистрации автокормушки
     */
    public NewsDataClass(String _id, String _type, String _dateAndTime, String _image, String _error_type) {
        String _name;
        int founded_id = StorageToolsClass.getElementByID(_id);
        if (!(founded_id == -1)) _name = MainActivity.devicesList.get(founded_id).getName();
        else _name = _id;

        name = _name;
        id = _id;
        type = _type;
        dateAndTime = _dateAndTime;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        try {
            TDclass = sdf.parse(dateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        error_type = _error_type;
        if ("Error".equals(_type)) {
            image = "No image";
        } else {
            int counter = StorageToolsClass.getFreeCounter();
            image = Integer.toString(counter);
            byte[] bytes = Base64.decode(_image, Base64.DEFAULT);
            StorageToolsClass.saveImg(bytes, counter);
            preloadedImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            error_type = "No errors";
        }
    }

    /**
     * Конструктор - создание нового объекта с основными данными и именем, используется во всех других случаях
     */
    public NewsDataClass(String _name, String _id, String _type,
                         String _dateAndTime, String _image, String _error_type) {
        name = _name;
        id = _id;
        type = _type;
        dateAndTime = _dateAndTime;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        try {
            TDclass = sdf.parse(dateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        error_type = _error_type;
        if ("Error".equals(_type)) {
            image = "No image";
        } else {
            image = _image;
            error_type = "No errors";
        }
    }

    /**
     * Функция получения названия автокормушки, к которой принадлежит данная запись
     *
     * @return название автокормушки
     */
    public String getName() {
        return name;
    }

    /**
     * Функция установки названия автокормушки, к которому принадлежит запись
     *
     * @param _name - новое название
     */
    public void setName(String _name) {
        name = _name;
    }

    /**
     * Функция получения id автокормушки, к которой принадлежит данная запись
     *
     * @return id автокормушки
     */
    public String getDevID() {
        return id;
    }

    /**
     * Функция получения даты и времени записи
     *
     * @return дата и время записи в формате час:минута день.месяц.год
     */
    public String getDateAndTime() {
        return dateAndTime;
    }

    /**
     * Функция получения префикса картинки
     *
     * @return префикс картинки
     */
    public String getImage() {
        return image;
    }

    /**
     * Функция получения типа ошибки записи
     *
     * @return тип ошибки
     */
    public String getErrorType() {
        return error_type;
    }

    /**
     * Функция получения типа записи
     *
     * @return типа записи
     */
    public String getType() {
        return type;
    }

    /**
     * Функция получения Bitmap-а поодгруженного изображения
     *
     * @return Bitmap с изображением
     */
    public Bitmap getPreloadedImg() {
        return preloadedImg;
    }

    /**
     * Функция установки Bitmap-а картинки
     *
     * @param _preloadedImg - новый Bitmap
     */
    public void setPreloadedImg(Bitmap _preloadedImg) {
        preloadedImg = _preloadedImg;
    }

    /**
     * Функция получения даты и времени записи, но в переводе на GMT
     *
     * @return время записи по Гринвичу в формате час:минута день.месяц.год
     */
    @SuppressLint("SimpleDateFormat")
    public String getStringGlobalDateAndTime() {
        Calendar calendarTmp = Calendar.getInstance();
        calendarTmp.setTime(TDclass);
        int dHourGMT = Integer.parseInt(MainActivity.dGMT) / 100 * -1;
        int dMinuteGMT = Integer.parseInt(MainActivity.dGMT) % 100 * -1;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");

        calendarTmp.add(Calendar.HOUR_OF_DAY, dHourGMT);
        calendarTmp.add(Calendar.MINUTE, dMinuteGMT);

        return sdf.format(calendarTmp.getTime());
    }

    /**
     * Функция перевода даты и времени записи из GMT в локльное
     */
    public void convertDateTimeToLocalTZ() {
        convertTZ("+0000", MainActivity.dGMT);
    }

    /**
     * Функция для конвертации даты и времени записи из одного часового пояса в другой
     *
     * @param fromGMT - изначальный часовой пояс записи
     * @param toGMT   - часовой пояс, в который нужно перевести
     */

    public void convertTZ(String fromGMT, String toGMT) {
        Calendar calendarTmp = Calendar.getInstance();
        calendarTmp.setTime(TDclass);
        int dHourGMT, dMinuteGMT;
        int dHourFrom, dMinuteFrom, dHourTo, dMinuteTo;
        dHourFrom = Integer.parseInt(fromGMT) / 100;
        dMinuteFrom = Integer.parseInt(fromGMT) % 100;
        dHourTo = Integer.parseInt(toGMT) / 100;
        dMinuteTo = Integer.parseInt(toGMT) % 100;

        dHourGMT = dHourTo - dHourFrom;
        dMinuteGMT = dMinuteTo - dMinuteFrom;

        calendarTmp.add(Calendar.HOUR_OF_DAY, dHourGMT);
        calendarTmp.add(Calendar.MINUTE, dMinuteGMT);
        TDclass = calendarTmp.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        dateAndTime = sdf.format(calendarTmp.getTime());
    }

    /**
     * Функция сравнения другого объекта класса NewsDataClass с текущим
     *
     * @param o - объект для сравнения
     * @return результат сравнения
     */
    @Override
    public int compareTo(NewsDataClass o) {
        return TDclass.compareTo(o.TDclass);
    }
}