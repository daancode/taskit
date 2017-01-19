package code.daan.taskit.database;

import com.orm.SugarRecord;

public class Priority extends SugarRecord {
    public String title;
    public int value;

    public Priority() {}

    public Priority(String title, int value) {
        this.title = title;
        this.value = value;
    }
}
