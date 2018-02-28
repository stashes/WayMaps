package com.waymaps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.TicketList;
import com.waymaps.data.responseEntity.TrackerList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nazar on 28.02.2018.
 */

public class TicketListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater lInflater;
    private List<TicketList> objects;

    @BindView(R.id.ticketDate)
    TextView ticketDate;

    @BindView(R.id.ticketUsr)
    TextView ticketUsr;

    @BindView(R.id.ticketMessage)
    TextView ticketMessage;

    @BindView(R.id.ticketReadDate)
    TextView ticketReadDate;

    public TicketListAdapter(Context context, List<TicketList> ticketLists){
        this.context = context;
        objects = ticketLists;
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
            view = lInflater.inflate(R.layout.item_ticket_list, parent, false);
        }
        ButterKnife.bind(this,view);
        TicketList ticket = getTicketList(position);
        ticketDate.setText("[" + ticket.getCreated_date() + "]");
        ticketUsr.setText(ticket.getTracker_title());
        ticketMessage.setText(ticket.getText());
        ticketReadDate.setText("Створено" + "[" + ticket.getCreated_date() +"]");
        return view;
    }

    public TicketList getTicketList(int position) {
        return (TicketList) getItem(position);
    }
}
