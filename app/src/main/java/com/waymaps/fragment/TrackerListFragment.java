package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.adapter.TrackerListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.FinGet;
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
import java.util.Map;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 22.02.2018.
 */


public class TrackerListFragment extends Fragment implements AdapterView.OnItemClickListener {
    ListView trackerList;

    private User authorizedUser;
    private TrackerList[] tracker;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker_list, container, false);
        getAttrFromBundle();
        getTracker();
        trackerList = view.findViewById(R.id.tracker_table);
        trackerList.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getTracker(){
        Procedure procedure = new Procedure(Action.CALL);
        Parameter firmId = new IdParam("255");

        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TRACKER_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(firmId.getParameters());

        Call<TrackerList[]> call = RetrofitService.getWayMapsService().trackerProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<TrackerList[]>() {
            @Override
            public void onResponse(Call<TrackerList[]> call, Response<TrackerList[]> response) {
                tracker = response.body();
                logger.debug("Balance load successfully.");
                populateTable();
            }

            @Override
            public void onFailure(Call<TrackerList[]> call, Throwable t) {
                logger.debug("Failed while trying to load balance.");
            }
        });


    }

    private void getAttrFromBundle() {
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }


    public void populateTable() {
        if (tracker == null){
            tracker = new TrackerList[0];
        }
        TrackerListAdapter trackerAdapter = new TrackerListAdapter(getContext(), Arrays.asList(tracker));
        ListView lvMain = (ListView) getActivity().findViewById(R.id.tracker_table);
        lvMain.setAdapter(trackerAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bundle bundle = null;
        List<TrackerList> list = Arrays.asList(tracker);
        try{
            bundle = ApplicationUtil.setValueToBundle(new Bundle(),"user", authorizedUser);
            bundle = ApplicationUtil.setValueToBundle(new Bundle(),"tracker", list.get(i));
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        logger.debug("the select item is: " + String.valueOf(i));
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.tracker_list_view, messageFragment);
        ft.commit();
    }
}
