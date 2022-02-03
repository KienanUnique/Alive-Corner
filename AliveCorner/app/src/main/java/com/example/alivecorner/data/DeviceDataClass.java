package com.example.alivecorner.data;

import android.annotation.SuppressLint;

import com.example.alivecorner.GlobalApplication;
import com.example.alivecorner.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

/**
 * Класс автокормушки, предназначен для хранения и обработки данных об автокормушке
 *
 * @author Гизатуллин Акрам
 */

public class DeviceDataClass {

    private String id;
    private String password;
    private String status;

    private String name;
    private Calendar nextFeeding;
    private int foodLvl;
    private boolean isReadyFeedNow;
    private ArrayList<NewsDataClass> news = new ArrayList<NewsDataClass>();
    private ArrayList<ScheduleDataClass> schedule = new ArrayList<ScheduleDataClass>();

    public DeviceDataClass(String _name, String _id, String _password) {
        name = _name;
        id = _id;
        password = _password;
        status = "102";
        foodLvl = 0;
        isReadyFeedNow = true;
    }

    /**
     * Конструктор - создание нового объекта с уже известными значениямии, используется во всех остальных случаях
     */
    public DeviceDataClass(String _name, String _id, String _password, String _status, int _foodLvl) {
        name = _name;
        id = _id;
        password = _password;
        status = _status;
        foodLvl = _foodLvl;
        isReadyFeedNow = true;
    }

    /**
     * Функция вычисления времени следующего кормления. Результат вычислений сразу приравнивается к полю nextFeeding
     */
    public void calcNextTime() {

        ArrayList<Calendar> allCalendars = new ArrayList<Calendar>();
        Calendar nowCalendar = Calendar.getInstance();
        nextFeeding = null;

        int curDayNum = nowCalendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i).getIsEnabled()) {
                for (int n = 0; n < schedule.get(i).getIntDaysFeed().size(); n++) {
                    int dayNum = -1;
                    switch (schedule.get(i).getIntDaysFeed().get(n)) {
                        case 1:
                            dayNum = Calendar.MONDAY;
                            break;
                        case 2:
                            dayNum = Calendar.TUESDAY;
                            break;
                        case 3:
                            dayNum = Calendar.WEDNESDAY;
                            break;
                        case 4:
                            dayNum = Calendar.THURSDAY;
                            break;
                        case 5:
                            dayNum = Calendar.FRIDAY;
                            break;
                        case 6:
                            dayNum = Calendar.SATURDAY;
                            break;
                        case 7:
                            dayNum = Calendar.SUNDAY;
                            break;
                    }
                    Calendar nextCalendar = new GregorianCalendar(nowCalendar.get(Calendar.YEAR), nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH), schedule.get(i).getHour(), schedule.get(i).getMinute());
                    if (curDayNum > dayNum) {
                        nextCalendar.add(Calendar.DATE, curDayNum - dayNum);
                    } else if ((curDayNum < dayNum) || (curDayNum == dayNum && nextCalendar.compareTo(nowCalendar) <= 0)) {
                        nextCalendar.add(Calendar.DATE, (dayNum - curDayNum) + 7);
                    }
                    allCalendars.add(nextCalendar);
                }
            }
        }

        if (allCalendars.size() > 0) {
            Collections.sort(allCalendars);
            nextFeeding = allCalendars.get(0);
        }
    }


    /**
     * Функция добавления новости к полю news
     */
    public void addNews(NewsDataClass newsDataClass) {
        news.add(newsDataClass);
    }

    /**
     * Функция добавления элемента расписания к полю schedule
     */
    public void addNewSchedule(ScheduleDataClass scheduleDataClass) {
        schedule.add(scheduleDataClass);
    }

    /**
     * Функция удаления всех включенных элементов расписания
     * Используется при замене включенных элементов расписания на более свежие, полученные с сервера
     */
    public void removeAllEnabledTime() {
        for (int i = 0; i < schedule.size(); i++) {
            if (schedule.get(i).getIsEnabled()) {
                schedule.remove(i);
                removeAllEnabledTime();
                break;
            }
        }
    }

    /**
     * Функция удаления всех элементов расписания с тем же временем, что и у mainScheduleItem
     * Используется при замене включенных элементов расписания на более свежие, полученные с сервера
     *
     * @param mainScheduleItem - элемент расписания для сравнения
     */
    public void deleteRepeatingTime(ScheduleDataClass mainScheduleItem) {
        for (int i = 0; i < schedule.size(); i++)
            if (schedule.get(i).compareTo(mainScheduleItem) == 0)
                schedule.remove(i);
    }

    /**
     * Функция сортировки листа news (от старых записей к новым)
     */
    public void sortNews() {
        Collections.sort(news);
    }

    /**
     * Функция сортировки листа schedule (по возрастанию времени)
     */
    public void sortSchedule() {
        Collections.sort(schedule);
    }

    /**
     * Функция удаления всех элементов из листа schedule
     */
    public void clearSchedule() {
        schedule.clear();
    }

    /**
     * Функция получения названия устройства
     *
     * @return возвращает название устройства
     */
    public String getName() {
        return name;
    }

    /**
     * Функция задания названия устройства
     *
     * @param _name - новое название
     */
    public void setName(String _name) {
        name = _name;
    }

    /**
     * Функция получения id устройства
     *
     * @return возвращает id устройства
     */
    public String getID() {
        return id;
    }

    /**
     * Функция задания id устройства
     *
     * @param _id - новый id
     */
    public void setID(String _id) {
        id = _id;
    }

    /**
     * Функция получения приватного ключа устройства
     *
     * @return возвращает приватный ключ устройства
     */
    public String getPassword() {
        return password;
    }

    /**
     * Функция задания приватного ключа устройства
     *
     * @param _password - новый ключ
     */
    public void setPassword(String _password) {
        password = _password;
    }

    /**
     * Функция получения статуса устройства
     *
     * @return возвращает статус устройства
     */
    public String getStatus() {
        return status;
    }

    /**
     * Функция задания статуса устройства
     *
     * @param _status - новый статус
     */
    public void setStatus(String _status) {
        status = _status;
    }

    /**
     * Функция получения уровня корма в устройстве
     *
     * @return возвращает уровень корма в устройстве
     */
    public int getFoodLvl() {
        return foodLvl;
    }

    /**
     * Функция задания уровня корма в устройстве
     *
     * @param _food_lvl - новый уровень корма
     */
    public void setFoodLvl(int _food_lvl) {
        foodLvl = _food_lvl;
    }

    /**
     * Функция получения объекта класса Calendar с датой и временем следующего кормления
     *
     * @return возвращает объект класса Calendar с датой и временем следующего кормления
     */
    public Calendar getCalendarNextFeeding() {
        return nextFeeding;
    }

    /**
     * Функция получения времени следующего кормления в формате час:минута день.месяц.год.
     * В случае, если расписание не задано возрващается сообщение соотв. содержания
     *
     * @return возвращает время следующего кормления
     */
    @SuppressLint("SimpleDateFormat")
    public String getNextFeeding() {
        if (nextFeeding != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            return sdf.format(nextFeeding.getTime());
        } else {
            return GlobalApplication.getAppContext().getString(R.string.no_schedule);
        }
    }

    /**
     * Функция задания следующего времени кормления
     *
     * @param _next_feeding - новое время следующего кормления
     */
    public void setNextFeeding(Calendar _next_feeding) {
        nextFeeding = _next_feeding;
    }

    /**
     * Функция получения листа с новостями устройства
     *
     * @return возвращает лист с новостями устройства
     */
    public ArrayList<NewsDataClass> getNews() {
        return news;
    }

    /**
     * Функция получения готовности устройства выполнить комманду "Покормить сейчас"
     *
     * @return возвращает true, если устройство готово, и false в обратном случае
     */
    public boolean getReadyFeedNow() {
        return isReadyFeedNow;
    }

    /**
     * Функция задания готовности устройства выполнить комманду "Покормить сейчас"
     *
     * @param _ready_feed_now - новое значение готовности
     */
    public void setReadyFeedNow(boolean _ready_feed_now) {
        isReadyFeedNow = _ready_feed_now;
    }

    /**
     * Функция получения листа с расписанием кормления
     *
     * @return возвращает лист с расписанием кормления
     */
    public ArrayList<ScheduleDataClass> getSchedule() {
        return schedule;
    }

    /**
     * Функция задания листа с расписанием
     *
     * @param _schedule - лист с новым расписанием
     */
    public void setSchedule(ArrayList<ScheduleDataClass> _schedule) {
        schedule = _schedule;
    }

    /**
     * Функция получения определенного элемента расписания
     *
     * @param num - индекс искомого элемента в листе schedule
     * @return элемент расписания
     */
    public ScheduleDataClass getScheduleElement(int num) {
        return schedule.get(num);
    }

    /**
     * Функция получения определенной записи из листа с новостями
     *
     * @param num - индекс искомого элемента в листе news
     * @return элемент расписания
     */
    public NewsDataClass getNewsElement(int num) {
        return news.get(num);
    }

    /**
     * Функция удаления определенного элемента расписания из листа schedule
     *
     * @param num - индекс записи в листе schedule, которую необходимо удалить
     */
    public void removeScheduleElement(int num) {
        schedule.remove(num);
    }

    /**
     * Функция удаления определенной новости из листа news
     *
     * @param num - индекс записи в листе news, которую необходимо удалить
     */
    public void removeNewsElement(int num) {
        news.remove(num);
    }
}