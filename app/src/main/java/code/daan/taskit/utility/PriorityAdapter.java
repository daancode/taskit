package code.daan.taskit.utility;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import code.daan.taskit.R;
import code.daan.taskit.database.Priority;


public class PriorityAdapter extends ArrayAdapter<Priority> {

    private static class ViewHolder {
        TextView title;
        ImageView icon;
    }

    public PriorityAdapter(Context context, List<Priority> list) {
        super(context, R.layout.priority_item, list);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Priority priority = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.priority_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.priorityItemTitle);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.imgPriorityIcon);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(priority.title);

        int iconColor;
        switch (priority.value) {
            case 0 : iconColor = R.color.no_priority; break;
            case 1 : iconColor = R.color.low; break;
            case 2 : iconColor = R.color.medium; break;
            case 3 : iconColor = R.color.high; break;
            default: iconColor = R.color.no_priority; break;
        }
        viewHolder.icon.setColorFilter(ContextCompat.getColor(getContext(), iconColor));

        return convertView;
    }
}
