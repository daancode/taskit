package code.daan.taskit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Collections;
import java.util.List;

import code.daan.taskit.database.Folder;
import code.daan.taskit.database.Priority;
import code.daan.taskit.database.Task;
import code.daan.taskit.utility.FolderManager;
import code.daan.taskit.utility.TaskAdapter;

public class MainActivity extends AppCompatActivity {

    private boolean showCompleted = false, showDetails = true, sortByPriority = false;

    Layout layout;
    FolderManager folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = new Layout();
        folders = new FolderManager(this, layout.spinner);

        setSupportActionBar(layout.toolbar);

        initializeDatabase();
        folders.refresh();
        loadSettings();
        refreshTasks();
        setListeners();
    }

    @Override
    public void onResume() {
        refreshTasks();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_show_completed).setChecked(showCompleted);
        menu.findItem(R.id.action_sort_by_priority).setChecked(sortByPriority);
        menu.findItem(R.id.action_show_details).setChecked(showDetails);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_completed:
                showCompleted = item.setChecked(!item.isChecked()).isChecked();
                refreshTasks();
                saveSettings();
                return true;

            case R.id.action_show_details:
                showDetails = item.setChecked(!item.isChecked()).isChecked();
                refreshTasks();
                saveSettings();
                return true;

            case R.id.action_add_folder:
                folders.createFolder();
                return true;

            case R.id.action_edit_folder:
                folders.editFolder();
                return true;

            case R.id.action_sort_by_priority:
                sortByPriority = item.setChecked(!item.isChecked()).isChecked();
                refreshTasks();
                saveSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initializeDatabase() {
        if(Folder.count(Folder.class) == 0) {
            new Folder("Inbox").save();
        }
        if(Priority.count(Priority.class) == 0) {
            new Priority("No priority", 0).save();
            new Priority("Low", 1).save();
            new Priority("Medium", 2).save();
            new Priority("High", 3).save();
        }
    }

    public void refreshTasks() {

        List<Task> tasks = folders.getCurrentFolder().getTasks(showCompleted, sortByPriority);
        Collections.reverse(tasks);
        layout.listView.setAdapter(new TaskAdapter(this, tasks, showDetails));
    }

    void saveSettings() {
        SharedPreferences.Editor editor = getPreferences(MainActivity.MODE_PRIVATE).edit();
        editor.putBoolean("completed", showCompleted);
        editor.putBoolean("sortBy", sortByPriority);
        editor.putBoolean("details", showDetails);
        editor.putInt("spinnerId", layout.spinner.getSelectedItemPosition());
        editor.apply();
    }

    void loadSettings() {
        SharedPreferences sharedPref = getPreferences(MainActivity.MODE_PRIVATE);
        showCompleted = sharedPref.getBoolean("completed", false);
        showDetails = sharedPref.getBoolean("details", true);
        sortByPriority = sharedPref.getBoolean("sortBy", false);
        layout.spinner.setSelection(sharedPref.getInt("spinnerId", 0));
    }

    private void setListeners() {
        layout.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                folders.currentId = ((Folder)parent.getItemAtPosition((int)id)).getId().intValue();
                refreshTasks();
                saveSettings();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        layout.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!layout.taskTitle.getText().toString().equals("")) {
                    Task task = new Task(
                            layout.taskTitle.getText().toString(),
                            Folder.findById(Folder.class, folders.currentId)
                    );
                    task.save();
                    layout.taskTitle.setText("");
                    refreshTasks();
                }
                else layout.listView.requestFocus();
            }
        });

        layout.fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!layout.taskTitle.getText().toString().equals("")) {
                    Task task = new Task(layout.taskTitle.getText().toString(), folders.getCurrentFolder());
                    task.save();
                    startActivity(
                            new Intent(MainActivity.this, TaskActivity.class)
                                    .putExtra("folderId", folders.currentId)
                                    .putExtra("taskId", task.getId())
                    );
                    layout.taskTitle.setText("");
                }
                return false;
            }
        });

        layout.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task task = (Task) parent.getItemAtPosition((int)id);
                startActivity(
                        new Intent(MainActivity.this, TaskActivity.class)
                                .putExtra("folderId", folders.currentId)
                                .putExtra("taskId", task.getId())
                );
            }
        });

        layout.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Task task = (Task) parent.getItemAtPosition((int) id);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.deleteTaskDialogTitle))
                        .setMessage(getResources().getString(R.string.deleteTaskDialogMessage))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                task.delete();
                                refreshTasks();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private class Layout
    {
        public Toolbar toolbar;
        public Spinner spinner;
        FloatingActionButton fab;
        EditText taskTitle;
        ListView listView;

        public Layout() {
            toolbar = (Toolbar) findViewById(R.id.toolbarMain);
            spinner = (Spinner) findViewById(R.id.spinFolders);
            fab = (FloatingActionButton) findViewById(R.id.fabAddTask);
            listView = (ListView) findViewById(R.id.listTasks);
            taskTitle = (EditText) findViewById(R.id.editMainTaskTitle);
        }
    }
}
