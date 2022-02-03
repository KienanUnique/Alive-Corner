package com.example.alivecorner.ui.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.alivecorner.R;

/**
 * Класс для создания всплывающего окна с анимацией загрузки
 *
 * @author Гизатуллин Акрам
 */
public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity needActivity) {
        activity = needActivity;
    }

    public void createLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void removeLoading() {
        alertDialog.dismiss();
    }
}
