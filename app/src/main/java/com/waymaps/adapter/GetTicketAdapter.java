package com.waymaps.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.data.responseEntity.Ticket;
import com.waymaps.data.responseEntity.TicketList;
import com.waymaps.util.ApplicationUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nazar on 01.03.2018.
 */

public class GetTicketAdapter extends BaseAdapter {private Context context;
    private LayoutInflater lInflater;
    private List<Ticket> objects;

    @BindView(R.id.ticketDate)
    TextView ticketDate;

    @BindView(R.id.ticketUsr)
    TextView ticketUsr;

    @BindView(R.id.ticketMessage)
    TextView ticketMessage;

    @BindView(R.id.ticket_mail)
    ImageView ticketMail;

//    @BindView(R.id.ticketReadDate)
//    TextView ticketReadDate;

    public GetTicketAdapter(Context context, List<Ticket> tickets){
        this.context = context;
        objects = tickets;
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
            view = lInflater.inflate(R.layout.item_get_ticket, parent, false);
        }
        ButterKnife.bind(this,view);
        Ticket ticket = getTickets(position);

        if (ticket.getReadDate() == null) {
            ticketMail.setImageBitmap(ApplicationUtil.drawToBitmap(context.getResources().getDrawable(R.drawable.arrowtp)));
        } else {
            ticketMail.setImageBitmap(ApplicationUtil.drawToBitmap(context.getResources().getDrawable(R.drawable.arrowfl)));
        }


        if (ticket.getUserTitle() != null && ticket.getUserTitle().equals(MainActivity.authorisedUser.getUser_title())){
            ticketMail.setImageBitmap(ApplicationUtil.drawToBitmap(ticketMail.getDrawable()
                    , context.getResources().getColor(R.color.light_green)
                    , PorterDuff.Mode.SRC_IN));
            ticketMail.setRotation(180f);

            //ticketMessage.setTypeface(null, Typeface.BOLD);
        } else {
            ticketMail.setImageBitmap(ApplicationUtil.drawToBitmap(ticketMail.getDrawable()
                    , context.getResources().getColor(R.color.light_blue)
                    , PorterDuff.Mode.SRC_IN));
        }
        ticketDate.setText("[" + ticket.getCreatedDate() + "]");
        ticketUsr.setText(ticket.getUserTitle());
        ticketMessage.setText(ticket.getText());
        //       ticketReadDate.setText("Створено" + "[" + ticket.getCreated_date() +"]");
        return view;
    }

    public Ticket getTickets(int position) {
        return (Ticket) getItem(position);
    }
}