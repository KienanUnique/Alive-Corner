package com.example.alivecorner.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.alivecorner.data.ScheduleDataClass;
import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * Фрагмент, для страницы с настройками для автокормушки
 *
 * @author Гизатуллин Акрам
 */
public class SettingsDeviceFragment extends Fragment {

    private Switch onOffAll;
    private TextView nameText;
    private String selectedName;
    private boolean selectedDisableEnabled;

    private boolean isNameChanged = false;
    private boolean isDisablingChanged = false;
    private boolean isScheduleChanged = false;


    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings_device, container, false);

        MainActivity.allowOnBackPressed = true;

        nameText = root.findViewById(R.id.nameText);
        onOffAll = root.findViewById(R.id.turnOnOff);
        Button accept_but = root.findViewById(R.id.accpt_button);
        Button cancel_but = root.findViewById(R.id.cnl_button);
        LinearLayout schedule = root.findViewById(R.id.changeScheduleLayout);
        LinearLayout name = root.findViewById(R.id.changeNameLayout);
        LinearLayout share = root.findViewById(R.id.shareQRLayout);
        LinearLayout delete = root.findViewById(R.id.deleteDeviceLayout);

        try {
            if (!MainActivity.settingsDataTmp.getString("last_fragment").equals("device_page")) {
                try {
                    isDisablingChanged = MainActivity.settingsDataTmp.getBoolean("is_disabling_changed");
                    isScheduleChanged = MainActivity.settingsDataTmp.getBoolean("is_schedule_changed");
                    isNameChanged = MainActivity.settingsDataTmp.getBoolean("is_name_changed");
                    selectedDisableEnabled = MainActivity.settingsDataTmp.getBoolean("setted_disableEnabled");
                    selectedName = MainActivity.settingsDataTmp.getString("setted_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                selectedDisableEnabled = MainActivity.devicesList.get(MainActivity.currentDevNumber).getStatus().charAt(0) == '2';
                selectedName = MainActivity.devicesList.get(MainActivity.currentDevNumber).getName();
                MainActivity.settingsDataTmp.put("setted_disableEnabled", selectedDisableEnabled);
                MainActivity.settingsDataTmp.put("setted_name", selectedName);
                MainActivity.scheduleDataClass_tmp = new ArrayList<ScheduleDataClass>();
                for (ScheduleDataClass scheduleDataClass : MainActivity.devicesList.get(MainActivity.currentDevNumber).getSchedule()) {
                    MainActivity.scheduleDataClass_tmp.add(new ScheduleDataClass(scheduleDataClass));
                }
            }
            MainActivity.settingsDataTmp.put("last_fragment", "settings_device");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadSettings();

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MigrateSchedulesAll();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNameDialog();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQRDialog();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDeleteDialog();
            }
        });

        accept_but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNameChanged || isDisablingChanged || isScheduleChanged) {
                    createClarDialog();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });

        cancel_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDisableEnabled = MainActivity.devicesList.get(MainActivity.currentDevNumber).getStatus().charAt(0) == '2';
                selectedName = MainActivity.devicesList.get(MainActivity.currentDevNumber).getName();
                isNameChanged = false;
                isScheduleChanged = false;
                isDisablingChanged = false;
                MainActivity.scheduleDataClass_tmp = new ArrayList<ScheduleDataClass>();
                for (ScheduleDataClass scheduleDataClass : MainActivity.devicesList.get(MainActivity.currentDevNumber).getSchedule()) {
                    MainActivity.scheduleDataClass_tmp.add(new ScheduleDataClass(scheduleDataClass));
                }
                getActivity().onBackPressed();
            }
        });

        onOffAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDisablingChanged = true;
                try {
                    MainActivity.settingsDataTmp.put("is_disabling_changed", true);
                    MainActivity.settingsDataTmp.put("setted_disableEnabled", isChecked);
                    selectedDisableEnabled = isChecked;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    public void loadSettings() {
        nameText.setText(selectedName);
        onOffAll.setChecked(selectedDisableEnabled);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyChanges() {
        if (isNameChanged || isDisablingChanged || isScheduleChanged) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.changes_apply_delay, Snackbar.LENGTH_LONG).show();
        }
        if (isNameChanged) {
            for (int i = 0; i < MainActivity.menu.size(); i++) {
                if (MainActivity.menu.getItem(i).getTitle().equals(MainActivity.devicesList.get(MainActivity.currentDevNumber).getName())) {
                    MainActivity.menu.getItem(i).setTitle(selectedName);
                    break;
                }
            }
            for (int i = 0; i < MainActivity.devicesList.get(MainActivity.currentDevNumber).getNews().size(); i++) {
                MainActivity.devicesList.get(MainActivity.currentDevNumber).getNews().get(i).setName(selectedName);
            }
            MainActivity.devicesList.get(MainActivity.currentDevNumber).setName(selectedName);
            StorageToolsClass.writeDevices();
        }
        if (isScheduleChanged) {
            MainActivity.devicesList.get(MainActivity.currentDevNumber).setSchedule(new ArrayList<ScheduleDataClass>());
            for (ScheduleDataClass scheduleDataClass : MainActivity.scheduleDataClass_tmp) {
                MainActivity.devicesList.get(MainActivity.currentDevNumber).addNewSchedule(new ScheduleDataClass(scheduleDataClass));
            }
            try {
                if (!HttpApiAC.SendSchedule()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.sending_request_warning, Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainActivity.devicesList.get(MainActivity.currentDevNumber).calcNextTime();
            StorageToolsClass.writeSchedule();
        }
        if (isDisablingChanged) {
            String status_to_set;
            if (selectedDisableEnabled) {
                status_to_set = "2";
            } else {
                status_to_set = "1";
            }
            status_to_set += MainActivity.devicesList.get(MainActivity.currentDevNumber).getStatus().substring(1);
            MainActivity.devicesList.get(MainActivity.currentDevNumber).setStatus(status_to_set);
            try {
                if (!HttpApiAC.sendStatuses()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.sending_request_warning, Snackbar.LENGTH_LONG).show();
                }
                StorageToolsClass.writeDevices();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        selectedDisableEnabled = MainActivity.devicesList.get(MainActivity.currentDevNumber).getStatus().charAt(0) == '2';
        selectedName = MainActivity.devicesList.get(MainActivity.currentDevNumber).getName();
        try {
            MainActivity.settingsDataTmp.put("setted_disableEnabled", selectedDisableEnabled);
            MainActivity.settingsDataTmp.put("setted_name", selectedName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void MigrateSchedulesAll() {
        Fragment fragment = new SchedulesAllFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();

        try {
            MainActivity.settingsDataTmp.put("is_disabling_changed", selectedDisableEnabled);
            MainActivity.settingsDataTmp.put("is_schedule_changed", isScheduleChanged);
            MainActivity.settingsDataTmp.put("is_name_changed", isNameChanged);
            MainActivity.settingsDataTmp.put("setted_disableEnabled", selectedDisableEnabled);
            MainActivity.settingsDataTmp.put("setted_name", selectedName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        manager.beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void createClarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getString(R.string.clarification_apply_changes)).setTitle(getString(R.string.clarification));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                applyChanges();
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

    public void createDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_device_clarification).setTitle(getString(R.string.clarification));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                StorageToolsClass.deleteDevice(MainActivity.currentDevNumber);
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

    public void createNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_name, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);
        editText.setText(selectedName);

        dialogBuilder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                selectedName = editText.getText().toString();
                if (!selectedName.equals("")) {
                    if (!StorageToolsClass.isSuchDevExistsByName(selectedName)) {
                        nameText.setText(selectedName);
                        isNameChanged = true;
                        try {
                            MainActivity.settingsDataTmp.put("is_name_changed", isNameChanged);
                            MainActivity.settingsDataTmp.put("setted_name", selectedName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.name_duplication_warning, Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.name_invalid_warning, Snackbar.LENGTH_SHORT).show();
                }
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

    public void createQRDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_qr_layout, null);
        dialogBuilder.setView(dialogView);

        try {
            final ImageView qr_image = (ImageView) dialogView.findViewById(R.id.qrImage);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", MainActivity.devicesList.get(MainActivity.currentDevNumber).getName());
            jsonObject.put("id", MainActivity.devicesList.get(MainActivity.currentDevNumber).getID());
            jsonObject.put("password", MainActivity.devicesList.get(MainActivity.currentDevNumber).getPassword());
            QRGEncoder qrgEncoder = new QRGEncoder(jsonObject.toString(), null, QRGContents.Type.TEXT, 30 * 30);
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qr_image.setImageBitmap(bitmap);
        } catch (Exception e) {

        }

        dialogBuilder.setPositiveButton(getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}