package com.waymaps.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.adapter.GetTicketAdapter;
import com.waymaps.adapter.TicketListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.Ticket;
import com.waymaps.data.responseEntity.TicketList;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 01.03.2018.
 */

public class GetTicketFragment extends AbstractFragment implements AdapterView.OnItemClickListener{

    ListView ticketsListView;
    ProgressBar progressBar;
    private User authorizedUser;
    private Ticket[] tickets;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();
    int ticketId;
    EditText text;
    Button btnSend;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_ticket, container, false);
        getAttrFromBundle();
        ticketsListView = view.findViewById(R.id.get_ticket_table);
        progressBar = view.findViewById(R.id.progress_bar_get_ticket);
        text = view.findViewById(R.id.text_comment_ticket);
        btnSend = view.findViewById(R.id.send_ticket_comment);
        getTickets();
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.get_ticket_actionbar_title);
        ticketsListView.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getTickets(){
        Procedure procedure = new Procedure(Action.CALL);
        Parameter firmId = new IdParam("251");
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TICKET_GET);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(firmId.getParameters());
        showProgress(true , ticketsListView , progressBar , text , btnSend);
        Call<Ticket[]> call = RetrofitService.getWayMapsService().getTicket(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id() , procedure.getFormat(), String.valueOf(ticketId));
        call.enqueue(new Callback<Ticket[]>() {
            @Override
            public void onResponse(Call<Ticket[]> call, Response<Ticket[]> response) {
                tickets = response.body();
                populateTable();
                showProgress(false , ticketsListView , progressBar , text , btnSend);
            }

            @Override
            public void onFailure(Call<Ticket[]> call, Throwable t) {

            }
        });
    }

    private void getAttrFromBundle(){
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
            ticketId = (Integer) ApplicationUtil.getObjectFromBundle(getArguments(), "get_ticket_id", Integer.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }
    public void populateTable() {
        if (tickets == null){
            tickets = new Ticket[0];
        }
        if (tickets.length==0){
            goTonotFoundFragment();
        }else {
            GetTicketAdapter getTicketAdapter = new GetTicketAdapter(getContext(), Arrays.asList(tickets));
            ticketsListView.setAdapter(getTicketAdapter);
        }
    }

    private void goTonotFoundFragment(){
        Bundle bundle = new Bundle();
        try{
            ApplicationUtil.setValueToBundle(bundle,"user", authorizedUser);
            ApplicationUtil.setValueToBundle(bundle,"get_ticket_id", ticketId);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        NoFoundFragment noFoundFragment = new NoFoundFragment();
        noFoundFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, noFoundFragment);
        ft.commit();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    protected void showProgress(boolean show, View... view) {
        super.showProgress(show, view);
    }
}
