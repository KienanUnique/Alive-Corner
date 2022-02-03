package com.example.alivecorner.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.fragments.ScheduleSettingsFragment;

import org.json.JSONException;

/**
 * Адаптер для вывода расписания конкретного устройства для редактирования
 *
 * @author Гизатуллин Акрам
 */

public class ScheduleAdapter extends BaseAdapter {

    private LayoutInflater lInflater;

    /**
     * Конструктор
     *
     * @param _ctx - контекст
     */
    public ScheduleAdapter(Context _ctx) {
        lInflater = (LayoutInflater) _ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Переопределяем метод getCount() для отображения нужного количества элементов в ListView
     */
    @Override
    public int getCount() {
        return MainActivity.scheduleDataClass_tmp.size();
    }

    /**
     * Переопределяем метод getItem() для получния нужных объектов в зависимости от позиции
     */
    @Override
    public Object getItem(int position) {
        return MainActivity.scheduleDataClass_tmp.get(position);
    }

    /**
     * Переопределяем метод getItem() для получния нужных индексов в зависимости от позиции
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Переопределяем метод getView для настройки каждого элемента
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = lInflater.inflate(R.layout.item_schedule, parent, false);
        ((TextView) view.findViewById(R.id.timeText)).setText(MainActivity.scheduleDataClass_tmp.get(position).getStringTime());
        ((Switch) view.findViewById(R.id.enabledDisabled)).setChecked(MainActivity.scheduleDataClass_tmp.get(position).getIsEnabled());
        ((TextView) view.findViewById(R.id.timeInfo)).setText(MainActivity.scheduleDataClass_tmp.get(position).getLocalDaysFeed());

        ((Switch) view.findViewById(R.id.enabledDisabled)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.scheduleDataClass_tmp.get(position).setIsEnabled(isChecked);
                try {
                    MainActivity.settingsDataTmp.put("is_schedule_changed", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ((LinearLayout) view.findViewById(R.id.scheduleItemLayout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) v.getContext();
                ScheduleSettingsFragment settingsDeviceFragment = new ScheduleSettingsFragment();
                try {
                    MainActivity.settingsDataTmp.put("is_new", false);
                    MainActivity.settingsDataTmp.put("schedule_num", position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FragmentManager manager = activity.getSupportFragmentManager();
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.nav_host_fragment, settingsDeviceFragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        return view;
    }
}
