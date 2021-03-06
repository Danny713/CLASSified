package com.classified.classified;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by home_folder on 11/14/15.
 */
public class CustomAdapter extends ArrayAdapter<ClassInfo> implements Filterable {
    private Context context;
    private List<ClassInfo> filteredClassInfoList;

    public CustomAdapter(Context context, List<ClassInfo> filteredClassInfoList) {
        super(context, R.layout.custom_rowlayout, filteredClassInfoList); //bookmarkList = fullList if no bookmarks exist
        this.filteredClassInfoList = filteredClassInfoList;

        this.context = context;
    }

    // view implementation
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_rowlayout, parent, false);

        ClassInfo classInfo = filteredClassInfoList.get(position);

        TextView courseName = (TextView) customView.findViewById(R.id.courseText);
        TextView courseTitle = (TextView) customView.findViewById(R.id.courseTitle);

        courseName.setText(classInfo.getCourseCode());
        courseTitle.setText(classInfo.getCourseTitle());

        return customView;
    }

    @Override
    public int getCount() {
        return filteredClassInfoList.size();
    }

    @NonNull
    @Override
    public android.widget.Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                //String filterQuery = charSequence.toString().toLowerCase();
                FilterResults results = new FilterResults();

                // TODO: there is something wrong here..what if you return something of size 0?
                results.count = filteredClassInfoList.size();
                results.values = filteredClassInfoList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //notifyDataSetChanged();
            }
        };
        return filter;
    }

    @Override
    public ClassInfo getItem(int position) {
        return filteredClassInfoList.get(position);
    }
}