package com.example.alivecorner.ui.fragments;

import static com.example.alivecorner.MainActivity.CAMERA_COOLDOWN_MS;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.other.LoadingDialog;
import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import org.json.JSONObject;

/**
 * Фрагмент, используемый на странице с добавлением автокормушки при помощи QR-кода;
 *
 * @author Гизатуллин Акрам
 */

public class AddQRFragment extends Fragment {
    private String inpId, inpName, inpPassword;
    private String requestResTmp;
    private boolean isAddingProcessRunning = false;


    private View root;
    private CodeScanner mCodeScanner;
    private LoadingDialog loadingDialog;

    /**
     * Функция для проверки являются ли символы в строке числом
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int intNum = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_qr, container, false);

        MainActivity.allowOnBackPressed = true;

        MainActivity.menu.getItem(2).setChecked(true);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.menu_add));

        loadingDialog = new LoadingDialog(getActivity());
        startScanning();

        return root;
    }

    private void startScanning() {
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(getContext(), scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final JSONObject parse = new JSONObject(result.getText());
                            if (isNumeric((String) parse.get("id")) && !"".equals(parse.get("password")) && !"".equals(parse.get("name"))) {
                                inpName = (String) parse.get("name");
                                inpId = (String) parse.get("id");
                                inpPassword = (String) parse.get("password");

                                if (StorageToolsClass.isSuchDevExistsByName(inpName) || StorageToolsClass.isSuchDevExistsByID(inpId)) {
                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                            getString(R.string.device_duplicate_warning), Snackbar.LENGTH_SHORT).show();
                                    new CountDownTimer(CAMERA_COOLDOWN_MS, 500) {
                                        public void onFinish() {
                                            mCodeScanner.startPreview();
                                        }

                                        public void onTick(long millisUntilFinished) {
                                        }
                                    }.start();
                                } else {
                                    if (!isAddingProcessRunning) {
                                        AddDevTask add_dev = new AddDevTask();
                                        add_dev.execute();
                                        isAddingProcessRunning = true;
                                    }
                                }

                            } else {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        R.string.invalid_qr_warning, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    R.string.error_reading_qr_warning, Snackbar.LENGTH_SHORT).show();
                            new CountDownTimer(CAMERA_COOLDOWN_MS, 500) {
                                public void onFinish() {
                                    mCodeScanner.startPreview();
                                }

                                public void onTick(long millisUntilFinished) {
                                }
                            }.start();
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.camera_permission_granted, Snackbar.LENGTH_LONG).show();
                startScanning();
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        R.string.camera_permission_denied, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    public void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
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
     * Класс для отправки запроса на регистрацию в фоне, а также для создания загрузочного диалога на время процесса регистрации
     */
    @SuppressLint("StaticFieldLeak")
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
                requestResTmp = HttpApiAC.Registrate_dev(inpName, inpId, inpPassword);
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
            } else {
                new CountDownTimer(CAMERA_COOLDOWN_MS, 500) {
                    public void onFinish() {
                        mCodeScanner.startPreview();
                    }

                    public void onTick(long millisUntilFinished) {
                    }
                }.start();
            }
            loadingDialog.removeLoading();
            isAddingProcessRunning = false;
        }
    }
}