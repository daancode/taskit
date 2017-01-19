package code.daan.taskit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import code.daan.taskit.database.Folder;
import code.daan.taskit.database.Priority;
import code.daan.taskit.database.Task;
import code.daan.taskit.utility.PriorityAdapter;
import code.daan.taskit.utility.TaskReminder;

public class TaskActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Layout layout;
    DatePickerDialog datePicker;
    TimePickerDialog timePicker;
    Task task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        layout = new Layout();

        setSupportActionBar(layout.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layout.priority.setAdapter(new PriorityAdapter(this, Priority.listAll(Priority.class)));

        task = loadTaskFromDatabase(getIntent().getLongExtra("taskId", -1));

        setDatePicker(layout.date);
        setTimePicker(layout.time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add_task) {
            if(!layout.title.getText().toString().equals("")) {
                task.title = layout.title.getText().toString();
                task.folder = Folder.findById(Folder.class, getIntent().getIntExtra("folderId", -1));
                task.description = layout.desc.getText().toString();
                task.completed = layout.completed.isChecked();
                task.priority = Priority.findById(Priority.class, layout.priority.getSelectedItemId() + 1);
                task.isDateSet = !layout.date.getText().equals("No date");
                task.isTimeSet = !layout.time.getText().equals("No time");
                task.isReminderSet = layout.reminder.isChecked();
                boolean overdue = System.currentTimeMillis() > task.calendar.getTimeInMillis();
                if(layout.reminder.isChecked() && !overdue) {
                    setAlarm(true);
                }
                else {
                    if(overdue) {
                        Toast.makeText(getApplicationContext(), "Remainder is already overdue !", Toast.LENGTH_SHORT).show();
                    }
                    task.isReminderSet = false;
                    setAlarm(false);
                }
                task.save();
                onBackPressed();
            }
            else layout.title.setError("Required");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        task.calendar.set(year, monthOfYear, dayOfMonth);
        task.isDateSet = true;
        layout.date.setText(task.getDate());
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        task.calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        task.calendar.set(Calendar.MINUTE, minute);
        task.isTimeSet = true;
        layout.time.setText(task.getTime());
    }

    private Task loadTaskFromDatabase(long taskId) {
        if(taskId != -1) {
            Task taskRef = Task.findById(Task.class, taskId);
            layout.title.setText(taskRef.title);
            layout.desc.setText(taskRef.description);
            layout.title.setSelection(layout.title.getText().length());
            layout.completed.setChecked(taskRef.completed);
            layout.priority.setSelection(taskRef.priority.value);
            layout.date.setText(taskRef.getDate());
            layout.time.setText(taskRef.getTime());
            layout.reminder.setChecked(taskRef.isReminderSet);
            return taskRef;
        }
        else return null;
    }

    private void setDatePicker(final TextView tvDate) {
        datePicker = DatePickerDialog.newInstance(
                TaskActivity.this,
                task.calendar.get(Calendar.YEAR),
                task.calendar.get(Calendar.MONTH),
                task.calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.setThemeDark(true);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getFragmentManager(), "Set date");
            }
        });

        tvDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tvDate.setText("No date");
                return true;
            }
        });
    }

    private void setTimePicker(final TextView tvTime) {
        timePicker = TimePickerDialog.newInstance(
                TaskActivity.this,
                task.calendar.get(Calendar.HOUR_OF_DAY),
                task.calendar.get(Calendar.MINUTE),
                true);
        timePicker.setThemeDark(true);

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show(getFragmentManager(), "Set time");
            }
        });

        tvTime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tvTime.setText("No time");
                return true;
            }
        });
    }

    void setAlarm(boolean enable) {
        Intent intent = new Intent(getApplicationContext(), TaskReminder.class);
        intent.putExtra("taskId", task.getId().intValue());
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(), task.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(enable) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.calendar.getTimeInMillis(), pendingIntent);
        }
        else alarmManager.cancel(pendingIntent);
    }

    private class Layout
    {
        TextView date, time;
        EditText title, desc;
        Toolbar toolbar;
        CheckBox completed;
        Spinner priority;
        Switch reminder;

        public Layout() {
            date  = (TextView)findViewById(R.id.tvTaskDate);
            time  = (TextView)findViewById(R.id.tvTaskTime);
            priority = (Spinner) findViewById(R.id.spinPriority);
            title = (EditText)findViewById(R.id.editTaskTitle);
            desc  = (EditText)findViewById(R.id.editTaskDesc);
            toolbar = (Toolbar)findViewById(R.id.toolbarTask);
            completed = (CheckBox)findViewById(R.id.cbTaskCompleted);
            reminder = (Switch) findViewById(R.id.swReminder);
        }
    }
}
