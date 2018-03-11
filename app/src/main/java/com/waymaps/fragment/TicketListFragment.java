package com.waymaps.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 28.02.2018.
 */

public class TicketListFragment extends AbstractFragment implements AdapterView.OnItemClickListener {
    ListView ticketListView;

    private TicketList[] ticketList;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();
    TrackerList[] tracker;
    Ticket[]tickets;
    FloatingActionButton fab;
    ProgressBar progressBar;


    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.ticket_list_layout, container, false);
        ticketListView = view.findViewById(R.id.ticket_table);
        progressBar = view.findViewById(R.id.progress_bar_ticket_list);
        fab = view.findViewById(R.id.fab);
        getTickers(ticketListView);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.ticket_list_actionbar_title);
        ticketListView.setOnItemClickListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTrackerList();
            }
        });
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getTickers(final View view){
        Procedure procedure = new Procedure(Action.CALL);

        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TICKET_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());
        showProgress(true , ticketListView , progressBar , fab);
        Call<TicketList[]> call = RetrofitService.getWayMapsService().tickerListProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id() , procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<TicketList[]>() {
            @Override
            public void onResponse(Call<TicketList[]> call, Response<TicketList[]> response) {
                ticketList = response.body();
                populateTable();
                showProgress(false , ticketListView , progressBar , fab);
            }

            @Override
            public void onFailure(Call<TicketList[]> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ArrayList list = new ArrayList<>(Arrays.asList(ticketList));
        TicketList item = (TicketList) list.get(i);
        goToGetTicketFragment(Integer.parseInt(item.getId()));

    }
    public void populateTable() {
        if (ticketList == null){
            ticketList = new TicketList[0];
        }
        TicketListAdapter trackerAdapter = new TicketListAdapter(getContext(), Arrays.asList(ticketList));
        ticketListView.setAdapter(trackerAdapter);
    }
    private void goToGetTicketFragment(int ticketId){
        Bundle bundle = new Bundle();
        try{
            ApplicationUtil.setValueToBundle(bundle,"get_ticket_id", ticketId);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        GetTicketFragment getTicketFragment = new GetTicketFragment();
        getTicketFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, getTicketFragment);
        ft.addToBackStack("getTicketList");
        getFragmentManager().popBackStackImmediate("map",0);
        ft.commit();

    }
    private void showTrackerList(){
        TrackerListFragment trackerListFragment = new TrackerListFragment();
        Bundle bundle = null;
        try{
            bundle = ApplicationUtil.setValueToBundle(new Bundle(),"user", authorizedUser);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        trackerListFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack("trackerList");
        getFragmentManager().popBackStackImmediate("getTicketList",0);
        ft.replace(R.id.content_main, trackerListFragment);
        ft.commit();
    }

    @Override
    protected void showProgress(boolean show, View... view) {
        super.showProgress(show, view);
    }
}
