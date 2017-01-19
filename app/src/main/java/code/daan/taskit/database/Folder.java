package code.daan.taskit.database;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Folder extends SugarRecord {
    public String title;

    public Folder() {
    }

    public Folder(String title) {
        this.title = title;
    }

    public List<Task> getTasks(boolean completed, boolean orderByPriority) {
        Select select = Select.from(Task.class);
        select.where(Condition.prop("folder").eq(String.valueOf(getId())));
        if(!completed) {
            select.and(Condition.prop("completed").eq("0"));
        }
        if(orderByPriority) {
            select.orderBy("priority");
        }
        return select.list();
    }
}
