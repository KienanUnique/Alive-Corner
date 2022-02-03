package com.example.alivecorner.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alivecorner.GlobalApplication;
import com.example.alivecorner.MainActivity;
import com.example.alivecorner.R;
import com.example.alivecorner.data.NewsDataClass;
import com.example.alivecorner.ui.fragments.DeviceFragment;
import com.example.alivecorner.ui.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.alivecorner.MainActivity.CRITICAL_FOOD_LVL;
import static com.example.alivecorner.MainActivity.UP_FAB_POSITION_SHOW;
import static com.example.alivecorner.MainActivity.WARNING_FOOD_LVL;

/**
 * Адаптер для вывода списка всех новостей
 *
 * @author Гизатуллин Акрам
 */
public class NewsAdapterAll extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context ctx;
    private final ArrayList<NewsDataClass> newsList = new ArrayList<NewsDataClass>();

    /**
     * Конструктор
     * @param context - контекст
     */
    public NewsAdapterAll(Context context) {
        ctx = context;

        newsList.addAll(MainActivity.allNewsList);
        Collections.sort(newsList);
        Collections.reverse(newsList);
    }

    /**
     * Переопределяем метод onCreateViewHolder, для выбора нужного ViewHolder-а
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_devices_all, parent, false);
            return new HeaderViewHolder(itemView);
        } else return null;
    }

    /**
     * Переопределяем метод onBindViewHolder для настройки каждого элемента в зависимости от типа ViewHolder-а
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (position > UP_FAB_POSITION_SHOW) {
            HomeFragment.upFab.show();
        } else {
            HomeFragment.upFab.hide();
        }

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            String devName, devNextFeeding;
            int devFoodLvl, devStatus;
            final int devNum;

            devNum = position;
            devName = MainActivity.devicesList.get(devNum).getName();
            devStatus = Integer.parseInt(MainActivity.devicesList.get(devNum).getStatus());
            devNextFeeding = MainActivity.devicesList.get(devNum).getNextFeeding();
            devFoodLvl = MainActivity.devicesList.get(devNum).getFoodLvl();

            boolean is_enabled;
            String prefix = "";
            if (devStatus >= 200) {
                is_enabled = false;
                prefix = GlobalApplication.getAppContext().getString(R.string.disable) + " | ";
            } else {
                is_enabled = true;
                prefix = GlobalApplication.getAppContext().getString(R.string.enable) + " | ";
            }

            if (devStatus % 10 == 0) {
                headerViewHolder.tvStatus.setText(prefix + GlobalApplication.getAppContext().getString(R.string.ok_text));
            } else if (devStatus % 10 == 1) {
                headerViewHolder.tvStatus.setText(prefix + GlobalApplication.getAppContext().getString(R.string.device_error_out_of_food));
                if (is_enabled) {
                    headerViewHolder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.colorError));
                }
            } else {
                headerViewHolder.tvStatus.setText(prefix + GlobalApplication.getAppContext().getString(R.string.device_unknown_error));
                if (is_enabled) {
                    headerViewHolder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.colorError));
                }
            }

            headerViewHolder.tvName.setText(devName);
            headerViewHolder.tvDevNextFeeding.setText(devNextFeeding);
            headerViewHolder.tvDevFoodLvl.setText(devFoodLvl + "%");

            if (!is_enabled) {
                headerViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorDisableDevice));
            }

            if (devFoodLvl > WARNING_FOOD_LVL) {
                if (is_enabled) {
                    headerViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorAccent));
                }
            } else if (devFoodLvl > CRITICAL_FOOD_LVL) {
                if (is_enabled) {
                    headerViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorWarn));
                }
                headerViewHolder.tvDevFoodLvl.setTextColor(ContextCompat.getColor(ctx, R.color.colorWarn));
            } else {
                if (is_enabled) {
                    headerViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(ctx, R.color.colorError));
                }
                headerViewHolder.tvDevFoodLvl.setTextColor(ContextCompat.getColor(ctx, R.color.colorError));
            }

            headerViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < MainActivity.menu.size(); i++) {
                        if (MainActivity.menu.getItem(i).getTitle().equals(MainActivity.devicesList.get(devNum).getName())) {
                            MainActivity.menu.getItem(i).setChecked(true);
                            break;
                        }
                    }
                    MainActivity activity = (MainActivity) v.getContext();
                    DeviceFragment deviceFragment = new DeviceFragment();
                    FragmentManager manager = activity.getSupportFragmentManager();
                    manager.beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.nav_host_fragment, deviceFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                    MainActivity.drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            NewsDataClass p = newsList.get(position - MainActivity.devicesList.size());
            if (p.getType().equals("Error")) {
                if (p.getErrorType().equals("No food"))
                    itemViewHolder.tvName.setText(GlobalApplication.getAppContext().getString(R.string.device_error_out_of_food));
                else
                    itemViewHolder.tvName.setText(p.getType() + " - " + p.getErrorType());
                itemViewHolder.ivImage.setImageResource(R.drawable.nofood);
            } else {
                if (p.getType().equals("Feed filling"))
                    itemViewHolder.tvName.setText(R.string.feed_filling);
                else if (p.getType().equals("Eating"))
                    itemViewHolder.tvName.setText(R.string.eating);
                else
                    itemViewHolder.tvName.setText(p.getType());
                itemViewHolder.ivImage.setImageBitmap(p.getPreloadedImg());
            }
            itemViewHolder.tvNameDevice.setText(p.getName());
            itemViewHolder.tvDate.setText(p.getDateAndTime());
        }
    }

    /**
     * Переопределяем метод getItemCount() для отображения нужного количества элементов в RecyclerView
     */
    @Override
    public int getItemCount() {
        int news_count = 0;
        for (int i = 0; i < MainActivity.devicesList.size(); i++) {
            news_count += MainActivity.devicesList.get(i).getNews().size();
        }
        return MainActivity.devicesList.size() + news_count;
    }

    /**
     * Переопределяем метод getItemViewType() для выбора нужного типа в зависимости от позиции
     */
    @Override
    public int getItemViewType(int position) {
        if (position < MainActivity.devicesList.size()) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    /**
     * ViewHolder для заголовков
     */
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDevID, tvDevNextFeeding, tvDevFoodLvl, tvStatus;
        CardView cardView;

        public HeaderViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvDevID = view.findViewById(R.id.tvDevID);
            tvDevNextFeeding = view.findViewById(R.id.tvDevNextFeeding);
            tvDevFoodLvl = view.findViewById(R.id.tvDevFoodLvl);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvDevFoodLvl = view.findViewById(R.id.tvDevFoodLvl);
            cardView = view.findViewById(R.id.card);
        }
    }

    /**
     * ViewHolder для записей
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNameDevice, tvDate;
        ImageView ivImage;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvNameDevice = itemView.findViewById(R.id.tvNameDevice);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

}