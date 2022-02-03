package com.example.alivecorner.ui.fragments;

import android.annotation.SuppressLint;
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

import com.example.alivecorner.utilities.HttpApiAC;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.ui.adapters.NewsAdapterAll;
import com.example.alivecorner.R;
import com.example.alivecorner.ui.other.SpaceItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.alivecorner.MainActivity.FAB_MARGIN;
import static com.example.alivecorner.MainActivity.REFRESH_NEXT_FEEDING_DELAY_MS;

/**
 * Фрагмент, с домашней страницей
 *
 * @author Гизатуллин Акрам
 */

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    public static FloatingActionButton upFab;
    private NextTimeCheckerAllDev nextTimeCheckerAllDev;
    private NewsAdapterAll newsAdapterAll;

    @SuppressLint("ResourceAsColor")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RelativeLayout relativeLayout = root.findViewById(R.id.relativeLayout);
        final RecyclerView mRecyclerView = root.findViewById(R.id.rvNews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MainActivity.menu.getItem(0).setChecked(true);
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.menu_home));

        mSwipeRefreshLayout = root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() { Longpoll longpoll = new Longpoll(mRecyclerView); longpoll.execute(); }
        });

        if(MainActivity.devicesList.size() == 0){
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.no_devices_warning, Snackbar.LENGTH_LONG).show();
        }
        else if(MainActivity.allNewsList.size() == 0){
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    R.string.no_news_warning, Snackbar.LENGTH_LONG).show();
        }

        mRecyclerView.addItemDecoration(
                new SpaceItemDecoration(getActivity(), R.dimen.list_space,
                        true, true));
        newsAdapterAll = new NewsAdapterAll(getContext());
        mRecyclerView.setAdapter(newsAdapterAll);

        upFab = new FloatingActionButton(getContext());
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rel.setMargins(FAB_MARGIN, FAB_MARGIN, FAB_MARGIN, FAB_MARGIN);
        rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        upFab.setLayoutParams(rel);
        upFab.setImageResource(R.drawable.ic_arrow_upward_24dp);
        upFab.setSize(FloatingActionButton.SIZE_NORMAL);
        upFab.setBackgroundColor(R.color.colorAccent);

        upFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.smoothScrollToPosition(0);
                upFab.hide();
            }
        });

        relativeLayout.addView(upFab);

        Timer mTimer = new Timer();
        nextTimeCheckerAllDev = new NextTimeCheckerAllDev(mRecyclerView);
        mTimer.schedule(nextTimeCheckerAllDev, 0, REFRESH_NEXT_FEEDING_DELAY_MS);

        return root;
    }

    class Longpoll extends AsyncTask<Void, Void, Void> {

        RecyclerView mRecyclerView;

        public Longpoll(RecyclerView _mRecyclerView){
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
                if(!HttpApiAC.longPollRequest()){
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
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
            newsAdapterAll = new NewsAdapterAll(getContext());
            mRecyclerView.swapAdapter(newsAdapterAll, false);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class NextTimeCheckerAllDev extends TimerTask {

        RecyclerView mRecyclerView;

        public NextTimeCheckerAllDev(RecyclerView _mRecyclerView){
            mRecyclerView = _mRecyclerView;
        }

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Calendar nowCalendar = Calendar.getInstance();
                    for (int i = 0; i < MainActivity.devicesList.size(); i++) {
                        if(MainActivity.devicesList.get(i).getCalendarNextFeeding() != null) {
                            if (MainActivity.devicesList.get(i).getCalendarNextFeeding().compareTo(nowCalendar) <= 0) {
                                MainActivity.devicesList.get(i).calcNextTime();
                                newsAdapterAll = new NewsAdapterAll(getContext());
                                mRecyclerView.swapAdapter(newsAdapterAll, false);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        nextTimeCheckerAllDev.cancel();
        super.onDestroyView();
    }

}
