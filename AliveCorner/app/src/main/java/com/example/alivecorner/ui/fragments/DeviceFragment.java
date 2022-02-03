package com.example.alivecorner.ui.fragments;

import static com.example.alivecorner.MainActivity.FAB_MARGIN;
import static com.example.alivecorner.MainActivity.REFRESH_NEXT_FEEDING_DELAY_MS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.adapters.NewsAdapterSingle;
import com.example.alivecorner.ui.other.SpaceItemDecoration;
import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.utilities.StorageToolsClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Фрагмент, используемый на странице автокормушки;
 *
 * @author Гизатуллин Акрам
 */

public class DeviceFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static FloatingActionButton addFb;
    private NextTimeCheckerSingleDev nextTimeCheckerSingleDev;
    private NewsAdapterSingle newsAdapterSingle;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_deivce, container, false);

        RelativeLayout relativeLayout = root.findViewById(R.id.relativeLayout);
        final RecyclerView mRecyclerView = root.findViewById(R.id.rvNews);

        MainActivity.allowOnBackPressed = false;

        String title = "";
        for (int i = 0; i < MainActivity.menu.size(); i++) {
            if (MainActivity.menu.getItem(i).isChecked()) {
                if (StorageToolsClass.getElementByName(MainActivity.menu.getItem(i).getTitle().toString()) != -1) {
                    title = MainActivity.menu.getItem(i).getTitle().toString();
                    MainActivity.currentDevNumber = StorageToolsClass.getElementByName(MainActivity.menu.getItem(i).getTitle().toString());
                }
                break;
            }
        }

        ((MainActivity) getActivity()).setActionBarTitle(title);

        try {
            MainActivity.settingsDataTmp = new JSONObject();
            MainActivity.settingsDataTmp.put("last_fragment", "device_page");
            MainActivity.settingsDataTmp.put("device_num", StorageToolsClass.getElementByName(title));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() {
                Longpoll longpoll = new Longpoll(mRecyclerView);
                longpoll.execute();
            }
        });

        if (MainActivity.allNewsList.size() == 0) {
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    getString(R.string.no_news_warning), Snackbar.LENGTH_LONG).show();
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.addItemDecoration(
                new SpaceItemDecoration(getActivity(), R.dimen.list_space,
                        false, true));
        newsAdapterSingle = new NewsAdapterSingle(getContext(), MainActivity.currentDevNumber);
        mRecyclerView.setAdapter(newsAdapterSingle);

        addFb = new FloatingActionButton(getContext());
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rel.setMargins(FAB_MARGIN, FAB_MARGIN, FAB_MARGIN, FAB_MARGIN);
        rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addFb.setLayoutParams(rel);
        addFb.setImageResource(R.drawable.ic_arrow_upward_24dp);
        addFb.setSize(FloatingActionButton.SIZE_NORMAL);
        addFb.setBackgroundColor(R.color.colorAccent);

        addFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
                addFb.hide();
            }
        });

        relativeLayout.addView(addFb);

        Timer mTimer = new Timer();
        nextTimeCheckerSingleDev = new NextTimeCheckerSingleDev(mRecyclerView);
        mTimer.schedule(nextTimeCheckerSingleDev, 0, REFRESH_NEXT_FEEDING_DELAY_MS);

        return root;
    }

    @SuppressLint("StaticFieldLeak")
    class Longpoll extends AsyncTask<Void, Void, Void> {

        RecyclerView mRecyclerView;

        public Longpoll(RecyclerView _mRecyclerView) {
            this.mRecyclerView = _mRecyclerView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (!HttpApiAC.longPollRequest()) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getString(R.string.sending_request_warning), Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            newsAdapterSingle = new NewsAdapterSingle(getContext(), MainActivity.currentDevNumber);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newsAdapterSingle = new NewsAdapterSingle(getContext(), MainActivity.currentDevNumber);
                    mRecyclerView.swapAdapter(newsAdapterSingle, false);
                }
            });
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public static class FeedNow extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        Context ctx;
        int mDevNum, portion_size;

        public FeedNow(Context _ctx, int _mDevNum, int _portion_size) {
            this.ctx = _ctx;
            this.mDevNum = _mDevNum;
            this.portion_size = _portion_size;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String res = HttpApiAC.requestFeedNow(mDevNum, portion_size);
                if (res.equals("Cooldown")) {
                    Snackbar.make(((MainActivity) ctx).findViewById(android.R.id.content),
                            R.string.task_already_in_progress_warning, Snackbar.LENGTH_LONG).show();
                } else if (res.equals("Ok")) {
                    Snackbar.make(((MainActivity) ctx).findViewById(android.R.id.content),
                            R.string.ok_feeding_request_warning, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(((MainActivity) ctx).findViewById(android.R.id.content),
                            R.string.sending_request_warning, Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class NextTimeCheckerSingleDev extends TimerTask {

        RecyclerView mRecyclerView;

        public NextTimeCheckerSingleDev(RecyclerView _mRecyclerView) {
            mRecyclerView = _mRecyclerView;
        }

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Calendar nowCalendar = Calendar.getInstance();
                    if (MainActivity.devicesList.get(MainActivity.currentDevNumber).getCalendarNextFeeding() != null) {
                        if (MainActivity.devicesList.get(MainActivity.currentDevNumber).getCalendarNextFeeding().compareTo(nowCalendar) <= 0) {
                            MainActivity.devicesList.get(MainActivity.currentDevNumber).calcNextTime();
                            newsAdapterSingle = new NewsAdapterSingle(getContext(), MainActivity.currentDevNumber);
                            mRecyclerView.swapAdapter(newsAdapterSingle, false);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        nextTimeCheckerSingleDev.cancel();
        super.onDestroyView();
    }
}