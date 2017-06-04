package com.classified.classified;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by home_folder on 12/22/15.
 */
public class PreceptAdapter extends ArrayAdapter<String[]> {
    Context context;

    public PreceptAdapter(Context context, ArrayList<String[]> list) {
        super(context, R.layout.custom_rowlayout, list);
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_preceptlayout, parent, false);

        String[] array = getItem(position);

        TextView section = (TextView) customView.findViewById(R.id.section);
        TextView days = (TextView) customView.findViewById(R.id.days);
        TextView time = (TextView) customView.findViewById(R.id.time);
        TextView enrolled = (TextView) customView.findViewById(R.id.enrolled);
        TextView limit = (TextView) customView.findViewById(R.id.limit);
        TextView status = (TextView) customView.findViewById(R.id.status);

        section.setText(array[0]);
        days.setText(array[1]);
        time.setText(array[2]);
        enrolled.setText(array[3]);
        limit.setText(array[4]);

        if (array[5] == null)
            status.setText("Open");
        else {
            status.setText(array[5]);
        }
        return customView;
    }
}
