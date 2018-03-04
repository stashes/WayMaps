package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.GetTicketAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.Ticket;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 01.03.2018.
 */

public class GetTicketFragment extends AbstractFragment implements AdapterView.OnClickListener{

    ListView ticketsListView;
    ProgressBar progressBar;
    private Ticket[] tickets;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();
    int ticketId;
    FloatingActionButton fab;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_ticket, container, false);
        getAttrFromBundle();
        ticketsListView = view.findViewById(R.id.get_ticket_table);
        progressBar = view.findViewById(R.id.progress_bar_get_ticket);
        fab = view.findViewById(R.id.fab_comment_dialog);
        fab.setOnClickListener(this);
        getTickets();
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.get_ticket_actionbar_title);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getTickets(){
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TICKET_GET);
        procedure.setUser_id(MainActivity.authorisedUser.getId());
        procedure.setParams(String.valueOf(ticketId));
        showProgress(true , ticketsListView , progressBar , fab);
        Call<Ticket[]> call = RetrofitService.getWayMapsService().getTicket(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id() , procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<Ticket[]>() {
            @Override
            public void onResponse(Call<Ticket[]> call, Response<Ticket[]> response) {
                tickets = response.body();
                populateTable();
                showProgress(false , ticketsListView , progressBar , fab);
            }

            @Override
            public void onFailure(Call<Ticket[]> call, Throwable t) {

            }
        });
    }

    private void getAttrFromBundle(){
        try {
            ticketId = (Integer) ApplicationUtil.getObjectFromBundle(getArguments(), "get_ticket_id", Integer.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }
    public void populateTable() {
        if (tickets == null){
            tickets = new Ticket[0];
        }
            GetTicketAdapter getTicketAdapter = new GetTicketAdapter(getContext(), Arrays.asList(tickets));
            ticketsListView.setAdapter(getTicketAdapter);
    }


    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        try{
            ApplicationUtil.setValueToBundle(bundle,"get_ticket_id", ticketId);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        CommentDialogFragment cdf = new CommentDialogFragment();
        cdf.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        cdf.show(ft,"comment_dialog");
    }
}
