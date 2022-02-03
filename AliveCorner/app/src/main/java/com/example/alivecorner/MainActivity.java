package com.example.alivecorner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.alivecorner.data.DeviceDataClass;
import com.example.alivecorner.data.NewsDataClass;
import com.example.alivecorner.data.ScheduleDataClass;
import com.example.alivecorner.ui.fragments.AddFragment;
import com.example.alivecorner.ui.fragments.DeviceFragment;
import com.example.alivecorner.ui.fragments.HomeFragment;
import com.example.alivecorner.ui.fragments.SettingsFragment;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Основная активность, в ней реализована навигация по меню,
 * а также в ней находятся основные константы и глобальные переменные
 *
 * @author Гизатуллин Акрам
 */
public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_COOLDOWN_MS = 1500;
    public static final int REFRESH_NEXT_FEEDING_DELAY_MS = 1500;
    public static final int FAB_MARGIN = 15;
    public static final int MIN_PORTION_SIZE_SEC = 2;
    public static final int MAX_PORTION_SIZE_SEC = 60;
    public static final int WARNING_FOOD_LVL = 30;
    public static final int CRITICAL_FOOD_LVL = 10;
    public static final int UP_FAB_POSITION_SHOW = 10;
    public static final String PUBLIC_KEY = "228wza0h1Rh99ss1sy";

    public static final String MAIN_URL = "http://51.15.137.121:5000/api";
    public static final String FEED_NOW_URL = "/feed_now";
    public static final String SETUP_DEVICES_URL = "/setup_devices";
    public static final String SET_SCHEDULE_URL = "/set_schedule";
    public static final String STARTUP_REQUEST_URL = "/check_updates";
    public static final String REGISTER_NEW_DEVICE_URL = "/reg_new_device";

    public static final int MAX_COUNT_OF_NEWS_PER_DEVICE = 10;

    public static final String APP_PREFERENCES = "devicesDataAC";
    public static String APP_LANGUAGE;

    public static String dGMT;
    public static ArrayList<DeviceDataClass> devicesList = new ArrayList<DeviceDataClass>();
    public static ArrayList<NewsDataClass> allNewsList = new ArrayList<NewsDataClass>();
    public static ArrayList<ScheduleDataClass> scheduleDataClass_tmp = new ArrayList<ScheduleDataClass>();
    public static JSONObject settingsDataTmp = new JSONObject();
    public static Menu menu;
    public static DrawerLayout drawerLayout;
    public static File path;
    public static SharedPreferences sharedPref;
    public static boolean isInSplash;
    public static boolean allowOnBackPressed;
    public static boolean isStartRequestOk;
    public static int currentDevNumber = 0;
    static NavigationView navigationView;
    DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;

    public static void NullAllSelectedMenuItems() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(APP_LANGUAGE.toLowerCase()));
        res.updateConfiguration(conf, dm);

        GlobalApplication.setAppContext(this);

        setContentView(R.layout.activity_main);

        isInSplash = false;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        menu = navigationView.getMenu();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        setupDrawerContent(navigationView);

        if (!isStartRequestOk) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.sending_request_warning, Snackbar.LENGTH_LONG).show();
        }

        sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        StorageToolsClass.clearOldNews();

        StorageToolsClass.writeDevices();
        StorageToolsClass.writeNews();
        StorageToolsClass.writeSchedule();

        MenuItem menuItem;
        Fragment fragment;
        if (devicesList.size() == 0) {
            fragment = new AddFragment();
            menuItem = menu.getItem(2);
        } else {
            for (int i = 0; i < devicesList.size(); i++) {
                menu.add(R.id.dev_list, Menu.NONE, 0, devicesList.get(i).getName()).setIcon(R.drawable.catface).setCheckable(true);
            }
            fragment = new HomeFragment();
            menuItem = menu.getItem(0);
        }

        menuItem.setChecked(true);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.nav_host_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        drawerLayout.closeDrawer(GravityCompat.START);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_add_new)
                .setDrawerLayout(drawer)
                .build();
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Fragment fragment = null;
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        NullAllSelectedMenuItems();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                fragment = new HomeFragment();
                                break;
                            case R.id.nav_settings:
                                fragment = new SettingsFragment();
                                break;
                            case R.id.nav_add_new:
                                fragment = new AddFragment();
                                break;
                            default:
                                fragment = new DeviceFragment();
                                break;
                        }
                        allowOnBackPressed = false;
                        Migrate(fragment, menuItem);
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void Migrate(Fragment fragment, MenuItem menuItem) {
        if (fragment != null) {
            menuItem.setChecked(true);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (allowOnBackPressed) super.onBackPressed();
    }
}