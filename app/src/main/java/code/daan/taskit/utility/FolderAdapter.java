package code.daan.taskit.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import code.daan.taskit.R;
import code.daan.taskit.database.Folder;

public class FolderAdapter extends ArrayAdapter<Folder> {

    private static class ViewHolder {
        TextView title;
    }

    public FolderAdapter(Context context, List<Folder> folders) {
        super(context, R.layout.folder_item, folders);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Folder folder = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.folder_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.folderItemTitle);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(folder.title);
        return convertView;
    }
}