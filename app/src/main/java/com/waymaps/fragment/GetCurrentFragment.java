package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.waymaps.adapter.GetCurrentFilterAdapter;
import com.waymaps.adapter.GroupAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 10.04.2018.
 */


// by group example
public class GetCurrentFragment extends AbstractFragment {
    private GetCurrent[] getCurrents;
    private GetGroup currentGroup;
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @BindView(R.id.progress_bar_group_list)
    ProgressBar progressBar;

    @BindView(R.id.group_list)
    ListView groupList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        ButterKnife.bind(this,view);
        getAttrFromBundle();


        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());

        getCurrentList();
        return view;
    }

    private void getCurrentList() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_CURRENT);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());


        showProgress(true , groupList , progressBar);
        final Call<GetCurrent[]> getCurrent = RetrofitService.getWayMapsService().getCurrentProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(),procedure.getUser_id(), procedure.getFormat(), procedure.getParams());

        getCurrent.enqueue(new Callback<GetCurrent[]>() {
            @Override
            public void onResponse(Call<GetCurrent[]> call, Response<GetCurrent[]> response) {
                getCurrents = response.body();
                populateList();
                showProgress(false , groupList , progressBar);
            }

            @Override
            public void onFailure(Call<GetCurrent[]> call, Throwable t) {
                ApplicationUtil.showToast(getContext(),getString(R.string.somethin_went_wrong));
                showProgress(false , groupList , progressBar);
            }
        });


    }

    private void populateList() {
        if (getCurrents == null){
            getCurrents = new GetCurrent[0];
        }
        List<GetCurrent> list = new ArrayList<>();
        for (GetCurrent gc : getCurrents){
            if (!(gc.getLon() == null || gc.getLat()==null)
                    && (currentGroup == null || gc.getGroup_id().equals(currentGroup.getId()))){
                list.add(gc);
            }
        }
        GetCurrentFilterAdapter adapter = new GetCurrentFilterAdapter(getContext(), list);
        groupList.setAdapter(adapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<GetCurrent> list = Arrays.asList(getCurrents);
                moveTo(list.get(position));
            }
        });
    }

    public void moveTo(GetCurrent getCurrent){
        Bundle bundle = new Bundle();
        HistoryFragment historyFragment = new HistoryFragment();
        try{
            ApplicationUtil.setValueToBundle(bundle,"car", getCurrent);
            ApplicationUtil.setValueToBundle(bundle,"user", authorizedUser);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        historyFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, historyFragment);
        ft.addToBackStack("Cars");
        ft.commit();
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.get_tracker);
    }

    private void getAttrFromBundle(){
        try {
            currentGroup = ApplicationUtil.getObjectFromBundle(getArguments(), "group", GetGroup.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }

    }
}
