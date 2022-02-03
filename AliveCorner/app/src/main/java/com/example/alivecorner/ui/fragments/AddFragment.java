package com.example.alivecorner.ui.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.other.LoadingDialog;
import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.snackbar.Snackbar;

/**
 * Фрагмент, используемый на странице с ручным добавлением автокормушки;
 *
 * @author Гизатуллин Акрам
 */
public class AddFragment extends Fragment {
    private EditText nameOfDevice, deviceID, devicePassword;
    private boolean isAddingProcessRunning = false;
    private String inpID, inpName, inpPassword;
    private String requestResTmp;
    private LoadingDialog loadingDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        MainActivity.allowOnBackPressed = false;

        MainActivity.menu.getItem(2).setChecked(true);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.menu_add));

        loadingDialog = new LoadingDialog(getActivity());

        nameOfDevice = (EditText) root.findViewById(R.id.dev_name);
        deviceID = (EditText) root.findViewById(R.id.dev_id);
        devicePassword = (EditText) root.findViewById(R.id.dev_passwd);
        Button okBtn = root.findViewById(R.id.ok_button);
        Button cancelBtn = root.findViewById(R.id.cnl_button);
        Button qrBtn = root.findViewById(R.id.qr_button);

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MigrateQR();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                inpName = nameOfDevice.getText().toString();
                inpID = deviceID.getText().toString();
                inpPassword = devicePassword.getText().toString();
                if (!inpName.equals("") && !inpID.equals("") && !inpPassword.equals("")) {
                    if (StorageToolsClass.isSuchDevExistsByName(inpName) || StorageToolsClass.isSuchDevExistsByID(inpID)) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                                R.string.device_duplicate_warning, Snackbar.LENGTH_SHORT).show();
                    } else {
                        if (!isAddingProcessRunning) {
                            AddDevTask add_dev = new AddDevTask();
                            add_dev.execute();
                            isAddingProcessRunning = true;
                        }
                    }
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.all_data_required_warning, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MigrateHome();
            }
        });

        return root;
    }

    /**
     * Функция для перемещения на Главную страницу
     */
    private void MigrateHome() {
        Fragment fragment = new HomeFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Функция для перемещения на страницу автокормушки
     */
    private void MigrateDevice() {
        Fragment fragment = new DeviceFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Функция для перемещения на страницу с QR-код ридером
     */
    private void MigrateQR() {
        Fragment fragment = new AddQRFragment();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Класс для отправки запроса на регистрацию в фоне, а также для создания загрузочного диалога на время процесса регистрации
     */
    class AddDevTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.createLoading();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {
            try {
                requestResTmp = HttpApiAC.Registrate_dev(inpName, inpID, inpPassword);
            } catch (Exception e) {
                e.printStackTrace();
                requestResTmp = getString(R.string.sending_request_warning);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    requestResTmp, Snackbar.LENGTH_SHORT).show();
            if ("Ok".equals(requestResTmp)) {
                MenuItem menuItem = MainActivity.menu.add(R.id.dev_list, Menu.NONE, 0, inpName).setIcon(R.drawable.catface).setCheckable(true);
                menuItem.setChecked(true);
                MigrateDevice();
            }
            loadingDialog.removeLoading();
            isAddingProcessRunning = false;
        }
    }
}