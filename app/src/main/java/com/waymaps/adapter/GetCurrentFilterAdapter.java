package com.waymaps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 10.04.2018.
 */

public class GetCurrentFilterAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    List<GetCurrent> objects;

    @BindView(R.id.group_name)
    TextView groupName;


    public GetCurrentFilterAdapter(Context context, List<GetCurrent> groupList) {
        this.context = context;
        objects = groupList;
        if (context!=null)
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
            view = lInflater.inflate(R.layout.item_get_group, parent, false);
        }
        ButterKnife.bind(this,view);

        GetCurrent object = getCurrent(position);
        groupName.setText(object.getTracker_title());

        return view;
    }

    public GetCurrent getCurrent(int position) {
        return (GetCurrent) getItem(position);
    }

}
