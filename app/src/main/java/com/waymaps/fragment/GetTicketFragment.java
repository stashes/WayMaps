package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class GetTicketFragment extends Fragment implements AdapterView.OnItemClickListener{

    ListView ticketsListView;

    private User authorizedUser;
    private Ticket[] tickets;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();
    int ticketId;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_ticket, container, false);
        getAttrFromBundle();
        getTickets();
        ticketsListView = view.findViewById(R.id.get_ticket_table);
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
        Call<Ticket[]> call = RetrofitService.getWayMapsService().getTicket(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id() , procedure.getFormat(), String.valueOf(ticketId));
        call.enqueue(new Callback<Ticket[]>() {
            @Override
            public void onResponse(Call<Ticket[]> call, Response<Ticket[]> response) {
                tickets = response.body();
                populateTable();
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
}
