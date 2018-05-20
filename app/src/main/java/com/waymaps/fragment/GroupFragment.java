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
import com.waymaps.adapter.GroupAdapter;
import com.waymaps.adapter.TrackerListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 09.04.2018.
 */

public class GroupFragment extends AbstractFragment {

    private GetGroup[] getGroups;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected AbstractFragment nextFragment;

    @BindView(R.id.progress_layout)
    View progressBar;

    @BindView(R.id.content)
    View content;

    @BindView(R.id.group_list)
    ListView groupList;

    public GroupFragment(AbstractFragment nextFragment) {
        this.nextFragment = nextFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        ButterKnife.bind(this,view);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());

        getGroupList();
        return view;
    }

    private void getGroupList() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_GROUPS);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getFirm_id());


        showProgress(true , content , progressBar);
        Call<GetGroup[]> group = RetrofitService.getWayMapsService().getGroup(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());

        group.enqueue(new Callback<GetGroup[]>() {
            @Override
            public void onResponse(Call<GetGroup[]> call, Response<GetGroup[]> response) {
                getGroups = response.body();
                populateList();
                showProgress(false , content , progressBar);
            }

            @Override
            public void onFailure(Call<GetGroup[]> call, Throwable t) {
                ApplicationUtil.showToast(getContext(),getString(R.string.somethin_went_wrong));
                showProgress(false , content , progressBar);
            }
        });


    }

    private void populateList() {
        if (getGroups == null){
            getGroups = new GetGroup[0];
        }
        GroupAdapter groupAdapter = new GroupAdapter(getContext(), Arrays.asList(getGroups));
        groupList.setAdapter(groupAdapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<GetGroup> list = Arrays.asList(getGroups);
                moveTo(list.get(position));
            }
        });
    }

    public void moveTo(GetGroup getGroup){
        Bundle bundle = new Bundle();
        try{
            ApplicationUtil.setValueToBundle(bundle,"group", getGroup);
            ApplicationUtil.setValueToBundle(bundle,"user", authorizedUser);
        }catch (JsonProcessingException e){
            logger.debug("Error while trying write to bundle");
        }
        nextFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack("Groups");
        ft.replace(R.id.content_main, nextFragment);
        ft.commit();
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.group_list);
    }
}
