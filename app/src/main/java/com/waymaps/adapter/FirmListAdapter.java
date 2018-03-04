package com.waymaps.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.FirmList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nazar on 04.03.2018.
 */

public class FirmListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    List<FirmList> objects;

    @BindView(R.id.firm_name)
    TextView firmName;


    public FirmListAdapter(Context context, List<FirmList> firmLists) {
        this.context = context;
        objects = firmLists;
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
            view = lInflater.inflate(R.layout.item_firm_list, parent, false);
        }
        ButterKnife.bind(this,view);
        FirmList firmList = getFirm(position);
        firmName.setText(firmList.getTitle_firm());

        return view;
    }

    public FirmList getFirm(int position) {
        return (FirmList) getItem(position);
    }



}
