package com.example.alivecorner.ui.fragments;

import static com.example.alivecorner.MainActivity.MAX_PORTION_SIZE_SEC;
import static com.example.alivecorner.MainActivity.MIN_PORTION_SIZE_SEC;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.alivecorner.GlobalApplication;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.data.ScheduleDataClass;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.Collections;

/**
 * Фрагмент, используемый на странице с редактированием элемента расписания автокормушки
 *
 * @author Гизатуллин Акрам
 */
public class ScheduleSettingsFragment extends Fragment {

    private boolean isChanged = false;
    private boolean isNew;
    private int scheduleNum;
    private SeekBar portionSeekBar;
    private TextView portionText;
    private TimePicker firstTimePicker;
    private TextView repeatText, enabledDisabled;
    private String selectedDays;
    private Button okBut, cancelBut;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule_edit, container, false);

        try {
            isNew = MainActivity.settingsDataTmp.getBoolean("is_new");
            MainActivity.settingsDataTmp.put("last_fragment", "schedule_settings");
            if (!isNew) {
                scheduleNum = MainActivity.settingsDataTmp.getInt("schedule_num");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        okBut = root.findViewById(R.id.ok_button);
        cancelBut = root.findViewById(R.id.cnl_button);
        portionSeekBar = root.findViewById(R.id.portionSeekBar);
        LinearLayout repeatLayout = root.findViewById(R.id.repeatLayout);
        LinearLayout deleteLayout = root.findViewById(R.id.deleteDeviceLayout);
        repeatText = root.findViewById(R.id.repeatText);
        portionText = root.findViewById(R.id.portionText);
        enabledDisabled = root.findViewById(R.id.enabledDisabled);
        firstTimePicker = root.findViewById(R.id.simpleTimePicker);

        portionSeekBar.setMax(MAX_PORTION_SIZE_SEC);

        if (!isNew) {
            if (MainActivity.scheduleDataClass_tmp.get(scheduleNum).getIsEnabled()) {
                enabledDisabled.setText(R.string.enable);
            } else {
                enabledDisabled.setText(R.string.disable);
            }
        } else {
            enabledDisabled.setText(R.string.enable);
        }

        firstTimePicker.setIs24HourView(true);
        firstTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                isChanged = true;
            }
        });

        portionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                isChanged = true;
                if (progress < MIN_PORTION_SIZE_SEC) {
                    seekBar.setProgress(MIN_PORTION_SIZE_SEC);
                    portionText.setText(MIN_PORTION_SIZE_SEC + getString(R.string.seconds));
                } else {
                    portionText.setText(Integer.toString(progress) + getString(R.string.seconds));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDaysDialog();
            }
        });

        okBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean is_unique = true;
                for (int i = 0; i < MainActivity.scheduleDataClass_tmp.size(); i++) {
                    if (i != scheduleNum && MainActivity.scheduleDataClass_tmp.get(i).isEqual(firstTimePicker.getHour(),
                            firstTimePicker.getMinute())) {
                        is_unique = false;
                        break;
                    }
                }
                if (is_unique && (isNew || isChanged)) {
                    applyChanges();
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.schedule_item_duplicate_warning, Snackbar.LENGTH_SHORT).show();
                }
                getActivity().onBackPressed();

            }
        });

        cancelBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanged = false;
                getActivity().onBackPressed();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeleteDialog();
            }
        });

        loadSettings();

        return root;

    }

    private void applyChanges() {
        if (isChanged || isNew) {
            try {
                MainActivity.settingsDataTmp.put("is_schedule_changed", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (isNew) {
            ScheduleDataClass scheduleDataClass = new ScheduleDataClass(firstTimePicker.getHour(),
                    firstTimePicker.getMinute(),
                    portionSeekBar.getProgress(),
                    true,
                    MainActivity.devicesList.get(MainActivity.currentDevNumber).getID(),
                    selectedDays);
            MainActivity.scheduleDataClass_tmp.add(scheduleDataClass);
        } else {
            MainActivity.scheduleDataClass_tmp.get(scheduleNum).setHour(firstTimePicker.getHour());
            MainActivity.scheduleDataClass_tmp.get(scheduleNum).setMinute(firstTimePicker.getMinute());
            MainActivity.scheduleDataClass_tmp.get(scheduleNum).setEnglishDaysFeed(selectedDays);
            MainActivity.scheduleDataClass_tmp.get(scheduleNum).setPortionSize(portionSeekBar.getProgress());
        }
        Collections.sort(MainActivity.scheduleDataClass_tmp);
    }

    private void loadSettings() {
        if (isNew) {
            firstTimePicker.setHour(12);
            firstTimePicker.setMinute(0);
            portionSeekBar.setProgress(MIN_PORTION_SIZE_SEC);
            portionText.setText(MIN_PORTION_SIZE_SEC + getString(R.string.seconds));
            selectedDays = "Mon Tue Wed Thu Fri Sat Sun";
            repeatText.setText(getLocalSelecedDays());
        } else {
            firstTimePicker.setHour(MainActivity.scheduleDataClass_tmp.get(scheduleNum).getHour());
            firstTimePicker.setMinute(MainActivity.scheduleDataClass_tmp.get(scheduleNum).getMinute());
            portionSeekBar.setProgress(MainActivity.scheduleDataClass_tmp.get(scheduleNum).getPortionSize());
            portionText.setText(MainActivity.scheduleDataClass_tmp.get(scheduleNum).getPortionSize() + getString(R.string.seconds));
            repeatText.setText(MainActivity.scheduleDataClass_tmp.get(scheduleNum).getLocalDaysFeed());
            selectedDays = MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed();
        }
        isChanged = false;
    }

    private void createDaysDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_days, null);
        dialogBuilder.setView(dialogView);

        final CheckBox mon = dialogView.findViewById(R.id.monBox);
        final CheckBox tue = dialogView.findViewById(R.id.tueBox);
        final CheckBox wed = dialogView.findViewById(R.id.wedBox);
        final CheckBox thu = dialogView.findViewById(R.id.thuBox);
        final CheckBox fri = dialogView.findViewById(R.id.friBox);
        final CheckBox sat = dialogView.findViewById(R.id.satBox);
        final CheckBox sun = dialogView.findViewById(R.id.sunBox);

        if (selectedDays.contains("Mon")) {
            mon.setChecked(true);
        }
        if (selectedDays.contains("Tue")) {
            tue.setChecked(true);
        }
        if (selectedDays.contains("Wed")) {
            wed.setChecked(true);
        }
        if (selectedDays.contains("Thu")) {
            thu.setChecked(true);
        }
        if (selectedDays.contains("Fri")) {
            fri.setChecked(true);
        }
        if (selectedDays.contains("Sat")) {
            sat.setChecked(true);
        }
        if (selectedDays.contains("Sun")) {
            sun.setChecked(true);
        }

        dialogBuilder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                boolean is_days_changed = false;
                if (!isNew) {
                    if (mon.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Mon")) {
                        is_days_changed = true;
                    } else if (tue.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Tue")) {
                        is_days_changed = true;
                    } else if (wed.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Wed")) {
                        is_days_changed = true;
                    } else if (thu.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Thu")) {
                        is_days_changed = true;
                    } else if (fri.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Fri")) {
                        is_days_changed = true;
                    } else if (sat.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Sat")) {
                        is_days_changed = true;
                    } else if (sun.isChecked() != MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed().contains("Sun")) {
                        is_days_changed = true;
                    }
                } else {
                    is_days_changed = true;
                }
                if (is_days_changed) {
                    isChanged = true;
                    selectedDays = "";
                    if (mon.isChecked()) {
                        selectedDays += "Mon ";
                    }
                    if (tue.isChecked()) {
                        selectedDays += "Tue ";
                    }
                    if (wed.isChecked()) {
                        selectedDays += "Wed ";
                    }
                    if (thu.isChecked()) {
                        selectedDays += "Thu ";
                    }
                    if (fri.isChecked()) {
                        selectedDays += "Fri ";
                    }
                    if (sat.isChecked()) {
                        selectedDays += "Sat ";
                    }
                    if (sun.isChecked()) {
                        selectedDays += "Sun ";
                    }
                    if (!selectedDays.equals("")) {
                        selectedDays = selectedDays.substring(0, selectedDays.length() - 1);
                        repeatText.setText(getLocalSelecedDays());
                    } else {
                        selectedDays = "Mon Tue Wed Thu Fri Sat Sun";
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.no_days_selected_warning, Snackbar.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                } else {
                    dialog.cancel();
                }
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                if (!isNew) {
                    selectedDays = MainActivity.scheduleDataClass_tmp.get(scheduleNum).getEnDaysFeed();
                } else {
                    selectedDays = "Mon Tue Wed Thu Fri Sat Sun";
                }
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private String getLocalSelecedDays() {
        String res = "";
        if (selectedDays.contains("Mon"))
            res += GlobalApplication.getAppContext().getString(R.string.monday_abbreviated) + " ";
        if (selectedDays.contains("Tue"))
            res += GlobalApplication.getAppContext().getString(R.string.tuesday_abbreviated) + " ";
        if (selectedDays.contains("Wed"))
            res += GlobalApplication.getAppContext().getString(R.string.wednesday_abbreviated) + " ";
        if (selectedDays.contains("Thu"))
            res += GlobalApplication.getAppContext().getString(R.string.thursday_abbreviated) + " ";
        if (selectedDays.contains("Fri"))
            res += GlobalApplication.getAppContext().getString(R.string.friday_abbreviated) + " ";
        if (selectedDays.contains("Sat"))
            res += GlobalApplication.getAppContext().getString(R.string.saturday_abbreviated) + " ";
        if (selectedDays.contains("Sun"))
            res += GlobalApplication.getAppContext().getString(R.string.sunday_abbreviated) + " ";
        return res.substring(0, res.length() - 1);
    }

    public void createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_clarification_text).setTitle(R.string.clarification);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                if (!isNew) {
                    try {
                        MainActivity.settingsDataTmp.put("is_schedule_changed", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MainActivity.scheduleDataClass_tmp.remove(scheduleNum);
                }
                dialog.cancel();
                getActivity().onBackPressed();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}