package com.classified.classified;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by home_folder on 11/14/15.
 */
public class CustomAdapter extends ArrayAdapter<String[]> {
    Context context;

    public CustomAdapter(Context context, ArrayList<String[]> list) {
        super(context, R.layout.custom_rowlayout, list);
        this.context = context;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_rowlayout, parent, false);

        String[] array = getItem(position);

        TextView courseName = (TextView) customView.findViewById(R.id.courseText);
        TextView date = (TextView) customView.findViewById(R.id.dateText);
        TextView time = (TextView) customView.findViewById(R.id.timeText);
        TextView courseId = (TextView) customView.findViewById(R.id.courseid);

        courseName.setText(array[0]);
        courseId.setText(array[3]);

        String s;

        if (array[4].contains("R"))
            s = array[4].replace("R", "Th");
        else
            s = array[4];

        date.setText(s);

        if (array[5].equals("---"))
            time.setText("TBA");
        else {
            String[] timeFrame = array[5].split(",");
            int firstLength = timeFrame[0].length();
            int secondLength = timeFrame[1].length();
            String beginningTime = timeFrame[0].substring(0,firstLength-2) + ":"
                    + timeFrame[0].substring(firstLength - 2,firstLength);
            String endingTime = timeFrame[1].substring(0,secondLength-2) + ":"
                    + timeFrame[1].substring(secondLength-2, secondLength);

            time.setText(beginningTime +"-" + endingTime);
        }
        return customView;
    }
}