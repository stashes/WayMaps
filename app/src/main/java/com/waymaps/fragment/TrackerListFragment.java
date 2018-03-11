package com.waymaps.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
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


public class TrackerListFragment extends AbstractFragment implements AdapterView.OnItemClickListener {
    ListView trackerList;

    private TrackerList[] tracker;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private HashMap trackerId = new HashMap();
    ProgressBar progressBar;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tracker_list, container, false);
        trackerList = view.findViewById(R.id.tracker_table);
        progressBar = view.findViewById(R.id.progress_bar_tracker_list);
        getTracker();
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.tracker_list_actionbar_title);
        trackerList.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getTracker(){
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TRACKER_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId()); //MainActivity.authorisedUser.getId()
        showProgress(true , trackerList , progressBar);
        Call<TrackerList[]> call = RetrofitService.getWayMapsService().trackerProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<TrackerList[]>() {
            @Override
            public void onResponse(Call<TrackerList[]> call, Response<TrackerList[]> response) {
                tracker = response.body();
                populateTable();
                showProgress(false , trackerList , progressBar);
            }

            @Override
            public void onFailure(Call<TrackerList[]> call, Throwable t) {
                logger.debug("Failed while trying to load balance.");
            }
        });


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
        Bundle bundle = new Bundle();
        List<TrackerList> list = Arrays.asList(tracker);
        try{
            ApplicationUtil.setValueToBundle(bundle,"tracker", list.get(i));
            ApplicationUtil.setValueToBundle(bundle,"user", authorizedUser);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        logger.debug("the select item is: " + String.valueOf(i));
        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, messageFragment);
        ft.addToBackStack("TrackerList");
        ft.commit();
    }

    @Override
    protected void showProgress(boolean show, View... view) {
        super.showProgress(show, view);
    }
}
