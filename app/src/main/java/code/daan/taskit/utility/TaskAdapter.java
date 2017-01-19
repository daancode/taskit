package code.daan.taskit.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import code.daan.taskit.MainActivity;
import code.daan.taskit.R;
import code.daan.taskit.SplashActivity;
import code.daan.taskit.database.Task;

public class TaskAdapter extends ArrayAdapter<Task> {

    private boolean details;

    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView time;
        AppCompatCheckBox completed;
    }

    public TaskAdapter(Context context, List<Task> tasks, boolean details) {
        super(context, R.layout.task_item_detail, tasks);
        this.details = details;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if(details) {
                convertView = inflater.inflate(R.layout.task_item_detail, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.text);
                viewHolder.date = (TextView) convertView.findViewById(R.id.tvTaskItemDate);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tvTaskItemTime);
                viewHolder.completed = (AppCompatCheckBox) convertView.findViewById(R.id.checked);
            }
            else {
                convertView = inflater.inflate(R.layout.task_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.text);
                viewHolder.completed = (AppCompatCheckBox) convertView.findViewById(R.id.checked);
            }

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Task task = getItem(position);
                if(task.completed != buttonView.isChecked()) {
                    task.completed = buttonView.isChecked();
                    task.save();
                    ((MainActivity)getContext()).refreshTasks();
                }

                viewHolder.title.setPaintFlags(task.completed ?
                        viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                        viewHolder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
                );
            }
        });

        Task task = getItem(position);
        viewHolder.title.setText(task.title);
        viewHolder.completed.setChecked(task.completed);

        if(details) {
            viewHolder.date.setText(task.getDate());
            viewHolder.time.setText(task.getTime());

            int iconColor;
            switch (task.priority.value) {
                case 1 : iconColor = R.color.low; break;
                case 2 : iconColor = R.color.medium; break;
                case 3 : iconColor = R.color.high; break;
                default: iconColor = R.color.no_priority; break;
            }
            viewHolder.completed.setSupportButtonTintList(ContextCompat.getColorStateList(getContext(), iconColor));
        }
        return convertView;
    }
}