package com.example.alivecorner.data;

import com.example.alivecorner.GlobalApplication;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс элемента расписания, предназначен для хранения и обработки данных о расписании
 *
 * @author Гизатуллин Акрам
 */

public class ScheduleDataClass implements Comparable<ScheduleDataClass> {
    private int hour;
    private int minute;
    private int portionSize;
    private boolean isEnabled;
    private final String deviceID;
    private String repeatDays;

    /**
     * Конструктор - создание нового объекта
     */
    public ScheduleDataClass(int _hour, int _minute, int _portionSize, boolean _is_enabled, String _devID, String _EnDaysFeed) {
        hour = _hour;
        minute = _minute;
        portionSize = _portionSize;
        isEnabled = _is_enabled;
        deviceID = _devID;
        repeatDays = _EnDaysFeed;
    }

    /**
     * Конструктор - дублирование элемента расписания
     */
    public ScheduleDataClass(ScheduleDataClass _scheduleDataClass) {
        this.hour = _scheduleDataClass.getHour();
        this.minute = _scheduleDataClass.getMinute();
        this.portionSize = _scheduleDataClass.getPortionSize();
        this.isEnabled = _scheduleDataClass.getIsEnabled();
        this.deviceID = _scheduleDataClass.getDeviceID();
        this.repeatDays = _scheduleDataClass.getEnDaysFeed();
    }

    /**
     * Функция установки дней недели для повтора
     *
     * @param _days_feed - новое значение дней для повтора
     */
    public void setEnglishDaysFeed(String _days_feed) {
        repeatDays = _days_feed;
    }

    /**
     * Функция получения часа
     *
     * @return час
     */
    public int getHour() {
        return hour;
    }

    /**
     * Функция установки часа
     *
     * @param _hour - новое значение часа
     */
    public void setHour(int _hour) {
        hour = _hour;
    }

    /**
     * Функция получения минуты
     *
     * @return минута
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Функция установки минут
     *
     * @param _minute - новое значение минут
     */
    public void setMinute(int _minute) {
        minute = _minute;
    }

    /**
     * Функция получения размера порции
     *
     * @return размер порции
     */
    public int getPortionSize() {
        return portionSize;
    }

    /**
     * Функция установки размера порции
     *
     * @param _portion_size - новое размера порции
     */
    public void setPortionSize(int _portion_size) {
        portionSize = _portion_size;
    }

    /**
     * Функция получения статуса
     *
     * @return статус
     */
    public boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * Функция установки статуса
     *
     * @param _is_enabled - новое значение статуса
     */
    public void setIsEnabled(boolean _is_enabled) {
        isEnabled = _is_enabled;
    }

    /**
     * Функция получения id автокормушки, к которой принадлежит данный элемент расписания
     *
     * @return час
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Функция получения дней недели для повтора
     *
     * @return дни недели для повтора
     */
    public String getEnDaysFeed() {
        return repeatDays;
    }

    /**
     * Функция получения дней недели для повтора на выбранном в приложении языке
     *
     * @return дни недели для повтора
     */
    public String getLocalDaysFeed() {
        String res = "";
        if (repeatDays.contains("Mon"))
            res += GlobalApplication.getAppContext().getString(R.string.monday_abbreviated) + " ";
        if (repeatDays.contains("Tue"))
            res += GlobalApplication.getAppContext().getString(R.string.tuesday_abbreviated) + " ";
        if (repeatDays.contains("Wed"))
            res += GlobalApplication.getAppContext().getString(R.string.wednesday_abbreviated) + " ";
        if (repeatDays.contains("Thu"))
            res += GlobalApplication.getAppContext().getString(R.string.thursday_abbreviated) + " ";
        if (repeatDays.contains("Fri"))
            res += GlobalApplication.getAppContext().getString(R.string.friday_abbreviated) + " ";
        if (repeatDays.contains("Sat"))
            res += GlobalApplication.getAppContext().getString(R.string.saturday_abbreviated) + " ";
        if (repeatDays.contains("Sun"))
            res += GlobalApplication.getAppContext().getString(R.string.sunday_abbreviated) + " ";
        return res.substring(0, res.length() - 1);
    }

    /**
     * Функция получения дней недели для повтора в виде листа с целочисленными значениями
     * Например, Mon Fri --> [1,5]
     *
     * @return дни недели для повтора в виде листа с целочисленными значениями
     */
    public ArrayList<Integer> getIntDaysFeed() {
        return convertEnglishDaysStringToArrayList(repeatDays);
    }

    /**
     * Функция получения времени в формате час:минута
     *
     * @return время в формате час:минута
     */
    public String getStringTime() {
        String res;
        if (hour > 9) {
            res = hour + ":";
        } else {
            res = "0" + hour + ":";
        }
        if (minute > 9) {
            res += Integer.toString(minute);
        } else {
            res += "0" + minute;
        }
        return res;
    }

    /**
     * Функция сравнения указанного времени со временем данного элемента расписания
     * Используется во избежание возникновения одинаковых элментов расписания у одного устройства
     *
     * @return совпало ли время
     */
    public boolean isEqual(int compareWithHour, int compareWithMinute) {
        boolean is_equal = true;

        if (compareWithHour != this.hour) {
            is_equal = false;
        } else if (compareWithMinute != this.minute) {
            is_equal = false;
        }

        return is_equal;
    }

    /**
     * Функция сравнения указанного элемента расписания с данным
     *
     * @param o - элемент расписания для сравнения
     * @return разница во времени
     */
    @Override
    public int compareTo(ScheduleDataClass o) {
        int compareage = o.getHour() * 60 + o.getMinute();
        return getHour() * 60 + getMinute() - compareage;
    }

    /**
     * Функция перевода данного элемента расписания из часового пояса по Гринвичу в локльный
     */
    public void convertGlobalToLocalTZ() {
        convertTZ("+0000", MainActivity.dGMT);
    }

    /**
     * Функция для конвертации времени и дней недели для повтора из одного часового пояса в другой
     *
     * @param fromGMT - изначальный часовой пояс записи
     * @param toGMT   - часовой пояс, в который нужно перевести
     */
    public void convertTZ(String fromGMT, String toGMT) {
        int dHourGMT, dMinuteGMT, dDays = 0;
        int dHourFrom, dMinuteFrom, dHourTo, dMinuteTo;
        dHourFrom = Integer.parseInt(fromGMT) / 100;
        dMinuteFrom = Integer.parseInt(fromGMT) % 100;
        dHourTo = Integer.parseInt(toGMT) / 100;
        dMinuteTo = Integer.parseInt(toGMT) % 100;
        dHourGMT = dHourTo - dHourFrom;
        dMinuteGMT = dMinuteTo - dMinuteFrom;
        minute += dMinuteGMT;
        if (minute >= 60) {
            hour += 1;
            minute -= 60;
        } else if (minute < 0) {
            hour -= 1;
            minute += 60;
        }

        hour += dHourGMT;
        if (hour >= 24) {
            dDays = 1;
            hour -= 24;
        } else if (hour < 0) {
            dDays = -1;
            hour += 24;
        }

        ArrayList<Integer> intDays = convertEnglishDaysStringToArrayList(repeatDays);

        for (int i = 0; i < intDays.size(); i++) {
            intDays.set(i, intDays.get(i) + dDays);
            if (intDays.get(i) == 0) intDays.set(i, 7);
            else if (intDays.get(i) == 8) intDays.set(i, 1);
        }
        Collections.sort(intDays);
        repeatDays = convertDaysArrayListToString(intDays);
    }

    /**
     * Функция получения копии данного элемента расписания, но уже с глобальным временем
     *
     * @return копия данного элемента расписания но уже со временем по Гринвичу
     */
    public ScheduleDataClass getGlobalSchedule() {
        int dHourGMT, dMinuteGMT, dDays = 0;
        int mHour = hour, mMinute = minute;
        String mDaysFeed = repeatDays;

        dHourGMT = Integer.parseInt(MainActivity.dGMT) / 100 * -1;
        dMinuteGMT = Integer.parseInt(MainActivity.dGMT) % 100 * -1;

        mMinute += dMinuteGMT;
        if (mMinute >= 60) {
            mHour += 1;
            mMinute -= 60;
        } else if (mMinute < 0) {
            mHour -= 1;
            mMinute += 60;
        }

        mHour += dHourGMT;
        if (mHour >= 24) {
            dDays = 1;
            mHour -= 24;
        } else if (mHour < 0) {
            dDays = -1;
            mHour += 24;
        }

        ArrayList<Integer> mIntDays = convertEnglishDaysStringToArrayList(mDaysFeed);

        for (int i = 0; i < mIntDays.size(); i++) {
            mIntDays.set(i, mIntDays.get(i) + dDays);
            if (mIntDays.get(i) == 0) mIntDays.set(i, 7);
            else if (mIntDays.get(i) == 8) mIntDays.set(i, 1);
        }
        Collections.sort(mIntDays);
        mDaysFeed = convertDaysArrayListToString(mIntDays);

        return new ScheduleDataClass(mHour, mMinute, portionSize, true, deviceID, mDaysFeed);
    }

    /**
     * Функция конвертации строки с днями повтора в лист с целочисленными значениями
     * Например, Mon Fri --> [1,5]
     *
     * @param days - строка с днями повтора
     * @return лист с днями повтора
     */
    private ArrayList<Integer> convertEnglishDaysStringToArrayList(String days) {
        ArrayList<Integer> intDays = new ArrayList<>();
        String[] daysMas = days.split(" ");
        for (String daysMa : daysMas) {
            switch (daysMa) {
                case "Mon":
                    intDays.add(1);
                    break;
                case "Tue":
                    intDays.add(2);
                    break;
                case "Wed":
                    intDays.add(3);
                    break;
                case "Thu":
                    intDays.add(4);
                    break;
                case "Fri":
                    intDays.add(5);
                    break;
                case "Sat":
                    intDays.add(6);
                    break;
                case "Sun":
                    intDays.add(7);
                    break;
                default:
                    break;
            }
        }
        return intDays;
    }

    /**
     * Функция конвертации листа с днями повтора в строку
     * Например, [1,5] --> Mon Fri
     *
     * @param intDays - лист с днями повтора
     * @return строка с днями повтора
     */
    private String convertDaysArrayListToString(ArrayList<Integer> intDays) {
        StringBuilder strDays = new StringBuilder();
        for (Integer intDay : intDays) {
            switch (intDay) {
                case 1:
                    strDays.append("Mon ");
                    break;
                case 2:
                    strDays.append("Tue ");
                    break;
                case 3:
                    strDays.append("Wed ");
                    break;
                case 4:
                    strDays.append("Thu ");
                    break;
                case 5:
                    strDays.append("Fri ");
                    break;
                case 6:
                    strDays.append("Sat ");
                    break;
                case 7:
                    strDays.append("Sun ");
                    break;
            }
        }
        strDays.deleteCharAt(strDays.length() - 1);
        return strDays.toString();
    }
}
