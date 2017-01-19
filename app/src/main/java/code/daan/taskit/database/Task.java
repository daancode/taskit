package code.daan.taskit.database;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Task extends SugarRecord {

    public String title;
    public String description;
    public Folder folder;
    public Priority priority;
    public boolean completed;
    public boolean isDateSet;
    public boolean isTimeSet;
    public boolean isReminderSet;
    public Calendar calendar = Calendar.getInstance();

    public Task() {}

    public Task(String title, Folder folder) {
        this.title = title;
        this.folder = folder;
        priority = Priority.first(Priority.class);
        description = "";
        completed = false;
        isDateSet = false;
        isTimeSet = false;
        isReminderSet = false;
    }

    public String getDate() {
        return isDateSet ? new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.getTime()) : "No date";
    }

    public String getTime() {
        return isTimeSet ? new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(calendar.getTime()) : "No time";
    }
}
