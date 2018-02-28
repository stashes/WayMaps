package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.waymaps.R;
import com.waymaps.adapter.TicketListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.TicketList;
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
 * Created by nazar on 28.02.2018.
 */

public class TicketListFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView ticket;

    private User authorizedUser;
    private TicketList[] ticketList;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ticket_list_layout, container, false);
        getAttrFromBundle();
        getTickers();
        ticket = view.findViewById(R.id.ticket_table);
        ticket.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getTickers(){
        Procedure procedure = new Procedure(Action.CALL);
        Parameter firmId = new IdParam("251");
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TICKET_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(firmId.getParameters());
        Call<TicketList[]> call = RetrofitService.getWayMapsService().tickerListProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id() , procedure.getFormat(), firmId.getParameters());
        call.enqueue(new Callback<TicketList[]>() {
            @Override
            public void onResponse(Call<TicketList[]> call, Response<TicketList[]> response) {
                ticketList = response.body();
                populateTable();
            }

            @Override
            public void onFailure(Call<TicketList[]> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    private void getAttrFromBundle(){
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }
    public void populateTable() {
        if (ticketList == null){
            ticketList = new TicketList[0];
        }
        TicketListAdapter trackerAdapter = new TicketListAdapter(getContext(), Arrays.asList(ticketList));
        ListView lvMain = (ListView) getActivity().findViewById(R.id.ticket_table);
        lvMain.setAdapter(trackerAdapter);
    }
}
