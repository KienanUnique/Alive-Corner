package com.example.alivecorner.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

/**
 * Фрагмент, для страницы с общими настройками приложения
 *
 * @author Гизатуллин Акрам
 */
public class SettingsFragment extends Fragment {

    private Switch onOffAll;
    private boolean isChangedOnOffAll = false;
    private boolean isChangedTZ = false;
    private boolean isChangedLanguage = false;

    private String selectedTZ;
    private String selectedLanguage;
    private boolean currentOnOffAll;

    private TextView timeZoneText, languageText;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity.menu.getItem(1).setChecked(true);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.menu_settings));

        LinearLayout timeZone = root.findViewById(R.id.timeZoneLayout);
        onOffAll = root.findViewById(R.id.turnOnOff);
        timeZoneText = root.findViewById(R.id.timeZoneText);
        languageText = root.findViewById(R.id.languageText);
        LinearLayout language = root.findViewById(R.id.languageLayout);
        Button accept_but = root.findViewById(R.id.accpt_button);
        Button cancel_but = root.findViewById(R.id.cnl_button);
        Switch onOffNotifications = root.findViewById(R.id.turnOnOffNotifications);
        loadSettings();

        timeZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTimeZoneDialog();
            }
        });
        onOffNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.stub_for_the_future, Snackbar.LENGTH_SHORT).show();
            }
        });
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLanguageDialog();
            }
        });

        accept_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isChangedOnOffAll || isChangedTZ || isChangedLanguage) {
                    createApplyDialog();
                } else {
                    MigrateHome();
                }
            }
        });

        cancel_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MigrateHome();
            }
        });

        onOffAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChangedOnOffAll = isChecked != currentOnOffAll;
            }
        });

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void loadSettings() {
        isChangedTZ = false;
        isChangedOnOffAll = false;
        isChangedLanguage = false;

        if (MainActivity.devicesList.size() == 0) {
            onOffAll.setEnabled(false);
        } else {
            for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                if (!(MainActivity.devicesList.get(i).getStatus().charAt(0) == '2')) {
                    currentOnOffAll = false;
                    break;
                }
            }
            onOffAll.setChecked(currentOnOffAll);
        }

        timeZoneText.setText("GMT" + MainActivity.dGMT.substring(0, 3) + ":" + MainActivity.dGMT.substring(3));
        selectedTZ = MainActivity.dGMT;

        selectedLanguage = MainActivity.APP_LANGUAGE;
        if (selectedLanguage.equals("ru"))
            languageText.setText(getString(R.string.ru_language));
        else
            languageText.setText(getString(R.string.en_language));
    }

    private void MigrateHome() {
        Fragment fragment = new HomeFragment();
        MainActivity.menu.getItem(0).setChecked(true);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void createTimeZoneDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_timezone, null);
        dialogBuilder.setView(dialogView);

        final NumberPicker npSign = dialogView.findViewById(R.id.npSign);
        final String[] signVal = new String[]{"+", "-"};
        npSign.setMaxValue(signVal.length - 1);
        npSign.setMinValue(0);
        npSign.setDisplayedValues(signVal);
        for (int i = 0; i < signVal.length; i++) {
            if (selectedTZ.substring(0, 1).equals(signVal[i])) {
                npSign.setValue(i);
                break;
            }
        }

        final NumberPicker npHour = dialogView.findViewById(R.id.npHour);
        final String[] hourVal = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        npHour.setMaxValue(hourVal.length - 1);
        npHour.setMinValue(0);
        npHour.setDisplayedValues(hourVal);
        for (int i = 0; i < hourVal.length; i++) {
            if (String.valueOf(Integer.parseInt(selectedTZ.substring(1, 3))).equals(hourVal[i])) {
                npHour.setValue(i);
                break;
            }
        }

        final NumberPicker npMinute = dialogView.findViewById(R.id.npMinute);
        final String[] minuteVal = new String[]{"00", "30"};
        npMinute.setMaxValue(minuteVal.length - 1);
        npMinute.setMinValue(0);
        npMinute.setDisplayedValues(minuteVal);
        for (int i = 0; i < minuteVal.length; i++) {
            if (selectedTZ.substring(3).equals(minuteVal[i])) {
                npMinute.setValue(i);
                break;
            }
        }

        npHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == hourVal.length - 1)
                    npMinute.setMaxValue(0);
                else if (oldVal == hourVal.length - 1)
                    npMinute.setMaxValue(1);
            }
        });


        dialogBuilder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                selectedTZ = signVal[npSign.getValue()];
                if (hourVal[npHour.getValue()].length() < 2) selectedTZ += "0";
                selectedTZ += hourVal[npHour.getValue()] + minuteVal[npMinute.getValue()];

                isChangedTZ = !selectedTZ.equals(MainActivity.dGMT);

                timeZoneText.setText("GMT" + selectedTZ.substring(0, 3) + ":" + selectedTZ.substring(3));
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void createLanguageDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_language, null);
        dialogBuilder.setView(dialogView);

        final RadioGroup rgSign = dialogView.findViewById(R.id.rgLanguage);

        if (selectedLanguage.equals("ru"))
            ((RadioButton) dialogView.findViewById(R.id.rbRu)).setChecked(true);
        else
            ((RadioButton) dialogView.findViewById(R.id.rbEn)).setChecked(true);


        dialogBuilder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                int selectedIndex = rgSign.getCheckedRadioButtonId();
                switch (selectedIndex) {
                    case R.id.rbEn:
                        selectedLanguage = "en";
                        break;
                    case R.id.rbRu:
                        selectedLanguage = "ru";
                        break;
                }

                isChangedLanguage = !selectedLanguage.equals(MainActivity.APP_LANGUAGE);
                if (selectedLanguage.equals("ru"))
                    languageText.setText(getString(R.string.ru_language));
                else
                    languageText.setText(getString(R.string.en_language));

                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void createApplyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.clarification_apply_changes).setTitle(getString(R.string.clarification));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {

                if (isChangedLanguage) {
                    MainActivity.APP_LANGUAGE = selectedLanguage;
                    StorageToolsClass.writeSettings();
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.restart_request, Snackbar.LENGTH_SHORT).show();
                }
                if (isChangedTZ) {
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        for (int n = 0; n < MainActivity.devicesList.get(i).getSchedule().size(); n++)
                            MainActivity.devicesList.get(i).getSchedule().get(n).convertTZ(MainActivity.dGMT, selectedTZ);
                        for (int n = 0; n < MainActivity.devicesList.get(i).getNews().size(); n++)
                            MainActivity.devicesList.get(i).getNews().get(n).convertTZ(MainActivity.dGMT, selectedTZ);
                        MainActivity.devicesList.get(i).sortSchedule();
                        MainActivity.devicesList.get(i).sortNews();
                        MainActivity.devicesList.get(i).calcNextTime();
                    }
                    MainActivity.dGMT = selectedTZ;
                    StorageToolsClass.writeSettings();
                    StorageToolsClass.writeNews();
                    StorageToolsClass.writeSchedule();
                    isChangedTZ = false;
                }

                if (isChangedOnOffAll) {
                    String new_status_begin = "";
                    if (onOffAll.isChecked()) {
                        new_status_begin = "2";
                    } else {
                        new_status_begin = "1";
                    }
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        String status_to_set = new_status_begin;
                        status_to_set += MainActivity.devicesList.get(i).getStatus().substring(1);
                        MainActivity.devicesList.get(i).setStatus(status_to_set);
                    }
                    try {
                        if (!HttpApiAC.sendStatuses()) {
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    R.string.sending_request_warning, Snackbar.LENGTH_LONG).show();
                        }
                        isChangedOnOffAll = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                MigrateHome();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                loadSettings();
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}