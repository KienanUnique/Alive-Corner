package com.example.alivecorner.ui.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.example.alivecorner.ui.fragments.SettingsDeviceFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.alivecorner.MainActivity.CRITICAL_FOOD_LVL;
import static com.example.alivecorner.MainActivity.MIN_PORTION_SIZE_SEC;
import static com.example.alivecorner.MainActivity.UP_FAB_POSITION_SHOW;
import static com.example.alivecorner.MainActivity.WARNING_FOOD_LVL;

/**
 * Адаптер для вывода списка новостей конкретного устройства
 *
 * @author Гизатуллин Акрам
 */

public class NewsAdapterSingle extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context ctx;
    private ArrayList<NewsDataClass> newsList = new ArrayList<NewsDataClass>();
    private int devNum;

    /**
     * Конструктор
     *
     * @param context - контекст
     * @param _devNum - индекс автокормушки в общем списке устройств
     */
    public NewsAdapterSingle(Context context, int _devNum) {
        this.devNum = _devNum;

        ctx = context;
        newsList = new ArrayList<NewsDataClass>(MainActivity.devicesList.get(devNum).getNews());
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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_device, parent, false);
            return new HeaderViewHolder(itemView);
        } else return null;
    }

    /**
     * Переопределяем метод onBindViewHolder для настройки каждого элемента в зависимости от типа ViewHolder-а
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (position > UP_FAB_POSITION_SHOW) {
            DeviceFragment.addFb.show();
        } else {
            DeviceFragment.addFb.hide();
        }

        String devName, devID, devNextFeeding;
        final int devFoodLvl, devStatus;
        devName = MainActivity.devicesList.get(devNum).getName();
        devID = MainActivity.devicesList.get(devNum).getID();
        devStatus = Integer.parseInt(MainActivity.devicesList.get(devNum).getStatus());
        devNextFeeding = MainActivity.devicesList.get(devNum).getNextFeeding();
        devFoodLvl = MainActivity.devicesList.get(devNum).getFoodLvl();

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.tvName.setText(devName);
            headerViewHolder.tvDevID.setText("#" + devID);
            headerViewHolder.tvDevNextFeeding.setText(devNextFeeding);
            headerViewHolder.tvDevFoodLvl.setText(MainActivity.devicesList.get(devNum).getFoodLvl() + "%");
            headerViewHolder.btEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity activity = (MainActivity) v.getContext();
                    SettingsDeviceFragment settingsDeviceFragment = new SettingsDeviceFragment();
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

            headerViewHolder.btFeedNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.devicesList.get(devNum).getReadyFeedNow() && devStatus == 100) {
                        createPortionSizeDialog();
                    } else if (devStatus != 100) {
                        Snackbar.make(((MainActivity) ctx).findViewById(android.R.id.content),
                                R.string.cant_complete_task_warning, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(((MainActivity) ctx).findViewById(android.R.id.content),
                                R.string.task_already_in_progress_warning, Snackbar.LENGTH_LONG).show();
                    }
                }
            });

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

            if (devFoodLvl <= WARNING_FOOD_LVL && devFoodLvl > CRITICAL_FOOD_LVL) {
                headerViewHolder.tvDevFoodLvl.setTextColor(ContextCompat.getColor(ctx, R.color.colorWarn));
            } else if (devFoodLvl <= CRITICAL_FOOD_LVL) {
                headerViewHolder.tvDevFoodLvl.setTextColor(ContextCompat.getColor(ctx, R.color.colorError));
            }
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            NewsDataClass p = newsList.get(position - 1);
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
        return newsList.size() + 1;
    }

    /**
     * Переопределяем метод getItemViewType() для выбора нужного типа в зависимости от позиции
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    /**
     * Функция создания диалога с выбором порции для кормления сейчас
     */
    public void createPortionSizeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(((MainActivity) ctx));
        LayoutInflater inflater = ((MainActivity) ctx).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_feed_now, null);
        dialogBuilder.setView(dialogView);

        final SeekBar portionSeekBar = dialogView.findViewById(R.id.portionSeekBar);
        final TextView portionText = dialogView.findViewById(R.id.portionText);

        portionSeekBar.setProgress(MIN_PORTION_SIZE_SEC);
        portionText.setText(MIN_PORTION_SIZE_SEC + GlobalApplication.getAppContext().getString(R.string.seconds));

        portionSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < MIN_PORTION_SIZE_SEC) {
                    seekBar.setProgress(MIN_PORTION_SIZE_SEC);
                    portionText.setText(MIN_PORTION_SIZE_SEC + GlobalApplication.getAppContext().getString(R.string.seconds));
                } else {
                    portionText.setText(Integer.toString(progress) + GlobalApplication.getAppContext().getString(R.string.seconds));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialogBuilder.setPositiveButton(GlobalApplication.getAppContext().getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                DeviceFragment.FeedNow feedNow = new DeviceFragment.FeedNow(ctx, devNum, portionSeekBar.getProgress());
                feedNow.execute();
                dialog.cancel();
            }
        });
        dialogBuilder.setNegativeButton(GlobalApplication.getAppContext().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    /**
     * ViewHolder для заголовков
     */
    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDevID, tvDevNextFeeding, tvDevFoodLvl, tvStatus;
        Button btEdit, btFeedNow;

        public HeaderViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvDevID = (TextView) view.findViewById(R.id.tvDevID);
            tvDevNextFeeding = (TextView) view.findViewById(R.id.tvDevNextFeeding);
            tvDevFoodLvl = (TextView) view.findViewById(R.id.tvDevFoodLvl);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvDevFoodLvl = (TextView) view.findViewById(R.id.tvDevFoodLvl);
            btEdit = (Button) view.findViewById(R.id.btEdit);
            btFeedNow = (Button) view.findViewById(R.id.btFeedNow);
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
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvNameDevice = (TextView) itemView.findViewById(R.id.tvNameDevice);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        }
    }

}