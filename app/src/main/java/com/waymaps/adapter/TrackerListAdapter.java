package com.waymaps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.TrackerList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nazar on 23.02.2018.
 */

public class TrackerListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    private List<TrackerList> objects;


    @BindView(R.id.tracker_name)
    TextView trackerName;

    public TrackerListAdapter(Context context, List<TrackerList> tracker){
        this.context = context;
        objects = tracker;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }




    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.tracker_item, parent, false);
        }
        ButterKnife.bind(this,view);
        TrackerList tracker = getTrackerList(position);
        trackerName.setText(tracker.getTitle());
        return view;
    }


    public TrackerList getTrackerList(int position) {
        return (TrackerList) getItem(position);
    }
}
