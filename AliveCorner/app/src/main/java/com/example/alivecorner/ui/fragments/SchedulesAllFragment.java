package com.example.alivecorner.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.adapters.ScheduleAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

/**
 * Фрагмент, для страницы с просмотром всех элементов расписания автокормушки
 *
 * @author Гизатуллин Акрам
 */
public class SchedulesAllFragment extends Fragment {

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_schedules_all, container, false);

        try {
            MainActivity.settingsDataTmp.put("last_fragment", "schedules_all");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getContext());

        RelativeLayout relativeLayout = (RelativeLayout) root.findViewById(R.id.relativeLayout);

        FloatingActionButton addFab = new FloatingActionButton(getContext());
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rel.setMargins(15, 15, 15, 15);
        rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addFab.setLayoutParams(rel);
        addFab.setImageResource(R.drawable.ic_add_24dp);
        addFab.setSize(FloatingActionButton.SIZE_NORMAL);
        addFab.setBackgroundColor(R.color.colorAccent);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.scheduleDataClass_tmp.size() < 10) {
                    ScheduleSettingsFragment settingsDeviceFragment = new ScheduleSettingsFragment();
                    try {
                        MainActivity.settingsDataTmp.put("is_new", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                    R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.nav_host_fragment, settingsDeviceFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            R.string.excess_number_of_schedule_items, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        relativeLayout.addView(addFab);

        ListView lvMain = (ListView) root.findViewById(R.id.lvMain);
        lvMain.setAdapter(scheduleAdapter);

        return root;
    }

}
