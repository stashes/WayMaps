package com.waymaps.fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
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
import com.waymaps.data.responseEntity.TrackerList;
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
    private GetGroup pickedGroup;
    private GetGroup[] getGroups;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Drawer drawerSecond;
    private int methodCompleted = 0;


    @BindView(R.id.progress_layout)
    View progressBar;

    @BindView(R.id.group_list)
    ListView groupList;

    @BindView(R.id.content)
    View content;

    private ArrayList<TrackerList> trackers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        ButterKnife.bind(this, view);
        getAttrFromBundle();


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());

        showProgress(true, content, progressBar);
        methodCompleted = 0;
        getTracker();
        if (MainActivity.isGroupAvaible) {
            addSearchGroup();
        } else {
            hideProgress();
        }
        getCurrentList();
        return view;
    }

    @Override
    public boolean onBackPressed() {
        if (drawerSecond!=null && drawerSecond.isDrawerOpen()){
            drawerSecond.closeDrawer();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search_button)

        {
            drawerSecond.openDrawer();
        }

        return super.onOptionsItemSelected(item);

    }

    private void getTracker(){
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TRACKER_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());
        Call<TrackerList[]> call = RetrofitService.getWayMapsService().trackerProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<TrackerList[]>() {
            @Override
            public void onResponse(Call<TrackerList[]> call, Response<TrackerList[]> response) {
                trackers =  new ArrayList<>(Arrays.asList(response.body()));
                hideProgress();
            }

            @Override
            public void onFailure(Call<TrackerList[]> call, Throwable t) {
                logger.debug("Failed while trying to load tracker list.");
                hideProgress();
            }
        });
    }

    private void addSearchGroup() {
            Procedure procedure = new Procedure(Action.CALL);
            procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
            procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
            procedure.setName(Action.GET_GROUPS);
            procedure.setUser_id(authorizedUser.getId());
            procedure.setParams(authorizedUser.getFirm_id());

            Call<GetGroup[]> group = RetrofitService.getWayMapsService().getGroup(procedure.getAction(), procedure.getName(),
                    procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());

            group.enqueue(new Callback<GetGroup[]>() {
                @Override
                public void onResponse(Call<GetGroup[]> call, Response<GetGroup[]> response) {
                    getGroups = response.body();
                    hideProgress();

                    drawerSecond = new DrawerBuilder(getActivity())
                            .withActionBarDrawerToggle(false)
                            .withDrawerGravity(Gravity.END)
                            .addDrawerItems(new PrimaryDrawerItem().withName(R.string.all).withTag(null), new DividerDrawerItem())
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    if (drawerItem.getTag() == null) {
                                        pickedGroup = null;
                                    } else {
                                        pickedGroup = (GetGroup) drawerItem.getTag();
                                    }
                                    populateList();
                                    drawerSecond.closeDrawer();
                                    return true;
                                }
                            })
                            .build();

                    for (int i = 0; i < getGroups.length; i++) {
                        drawerSecond.addItem(new SecondaryDrawerItem().withName(getGroups[i].getTitle()).withTag(getGroups[i]));
                    }


                }

                @Override
                public void onFailure(Call<GetGroup[]> call, Throwable t) {
                    hideProgress();
                    ApplicationUtil.showToast(getContext(), getString(R.string.somethin_went_wrong));
                }
            });
            getActivity().getMenuInflater().inflate(R.menu.main, toolbar.getMenu());
        Drawable yourdrawable = toolbar.getMenu().getItem(0).getIcon();
        yourdrawable.mutate();
        yourdrawable.setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_IN);
    }

    private void getCurrentList() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_CURRENT);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());


        final Call<GetCurrent[]> getCurrent = RetrofitService.getWayMapsService().getCurrentProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());

        getCurrent.enqueue(new Callback<GetCurrent[]>() {
            @Override
            public void onResponse(Call<GetCurrent[]> call, Response<GetCurrent[]> response) {
                getCurrents = response.body();
                hideProgress();
                populateList();
            }

            @Override
            public void onFailure(Call<GetCurrent[]> call, Throwable t) {
                hideProgress();
                ApplicationUtil.showToast(getContext(), getString(R.string.somethin_went_wrong));
            }
        });


    }

    private void populateList() {
        if (getCurrents == null) {
            getCurrents = new GetCurrent[0];
        }
        List<GetCurrent> list = new ArrayList<>();
        for (GetCurrent gc : getCurrents){
            if (!(gc.getLon() == null || gc.getLat()==null)
                    && (pickedGroup == null || pickedGroup.getId().equals(gc.getGroup_id()))){
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

    public void moveTo(GetCurrent getCurrent) {
        Bundle bundle = new Bundle();

        TrackerList trackerList = null;
        for (TrackerList t : trackers){
            if (t.getId().equals(getCurrent.getId())){
                trackerList = t;
                break;
            }
        }
        HistoryFragment historyFragment = new HistoryFragment();
        try {
            ApplicationUtil.setValueToBundle(bundle, "car", getCurrent);
            ApplicationUtil.setValueToBundle(bundle, "user", authorizedUser);
            ApplicationUtil.setValueToBundle(bundle,"tracker",trackerList);
        } catch (JsonProcessingException e) {
            logger.debug("Error while trying write to bundle");
        }
        historyFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, historyFragment);
        ft.addToBackStack(this.getClass().getName());
        ft.commit();
    }

    private void hideProgress(){
        methodCompleted++;
        if (methodCompleted==3){
            showProgress(false, content, progressBar);

        }
    }

    @Override
    protected String fragmentName() {
        return "";
    }

    private void getAttrFromBundle() {
        try {
            pickedGroup = ApplicationUtil.getObjectFromBundle(getArguments(), "group", GetGroup.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }

    @Override
    public void onStop() {
        if (drawerSecond!=null){
            drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        super.onStop();
    }
}
