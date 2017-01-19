package code.daan.taskit.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import code.daan.taskit.MainActivity;
import code.daan.taskit.R;
import code.daan.taskit.database.Folder;
import code.daan.taskit.database.Task;

public class FolderManager {

    public int currentId;
    private Spinner spinner;
    private Context context;

    public FolderManager(Context context, Spinner spinner) {
        this.context = context;
        this.spinner = spinner;
        currentId = Folder.count(Folder.class) == 0 ? 1 : Folder.first(Folder.class).getId().intValue();
    }

    public Folder getCurrentFolder() {
        return Folder.findById(Folder.class, currentId);
    }

    public void createFolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.add_folder, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(R.string.addFolderDialogTitle));
        alert.setView(view);

        final EditText input = (EditText)view.findViewById(R.id.folderTitle);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String folderTitle = input.getText().toString();
                if (folderTitle.equals("")) {
                    input.setError(context.getResources().getString(R.string.addFolderDialogEditError));
                    createFolder();
                }
                else {
                    new Folder(folderTitle).save();
                    refresh();
                    spinner.setSelection((int)Folder.count(Folder.class) - 1);
                }
            }
        });
        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void deleteFolder() {
        if(Folder.count(Folder.class) > 1) {
            Folder folder = Folder.findById(Folder.class, currentId);
            Task.deleteAll(Task.class, "folder = ? ", folder.getId().toString());
            folder.delete();
            currentId = Folder.first(Folder.class).getId().intValue();
            refresh();
        }
        else {
            Toast.makeText(context, context.getResources().getString(R.string.addFolderDialogLastError), Toast.LENGTH_LONG).show();
        }
    }

    public void editFolder() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.edit_folder, null);

        final Folder folder = Folder.findById(Folder.class, currentId);

        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(context.getResources().getString(R.string.editFolderDialogTitle));
        alert.setView(view);

        final EditText input = (EditText) view.findViewById(R.id.editFolderTitleDialog);
        input.setText(folder.title);

        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.deleteFolderDialog);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String folderTitle = input.getText().toString();
                if (folderTitle.equals("")) {
                    Toast.makeText(context, context.getResources().getString(R.string.addFolderDialogEditError), Toast.LENGTH_SHORT).show();
                    editFolder();
                }
                else if(checkBox.isChecked()) {
                    deleteFolder();
                }
                else {
                    folder.title = input.getText().toString();
                    folder.save();
                    refresh();
                    spinner.setSelection((int)Folder.count(Folder.class) - 1);
                }
            }
        });
        alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void refresh() {
        FolderAdapter adapter = new FolderAdapter(context, Folder.listAll(Folder.class));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
