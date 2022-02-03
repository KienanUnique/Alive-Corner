package com.example.alivecorner;

/**
 * класс, используемый для хранения контекста MainActivity.
 * Данный контекст необходим для получения текстовых данных, зависящих от установленного языка
 * <p>
 * Источник: https://www.dev2qa.com/android-get-application-context-from-anywhere-example/
 * Мною был добавлен дополнительный метод setAppContext
 */

import android.app.Application;
import android.content.Context;

public class GlobalApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context _appContext) {
        appContext = _appContext;
    }
}
