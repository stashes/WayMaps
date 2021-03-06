package com.waymaps.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.FirmListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.FirmList;
import com.waymaps.intent.MainActivityIntent;
import com.waymaps.util.JSONUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FirmListFragment extends AbstractFragment implements AdapterView.OnItemClickListener {
    private FirmList[] firms;
    private ListView listView;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private View progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        SystemUtil.hideKeyboard(getActivity());
        View view = inflater.inflate(R.layout.activity_firm_list, container, false);
        listView = view.findViewById(R.id.firm_table);
        progressBar = view.findViewById(R.id.progress_layout);
        listView.setOnItemClickListener(this);
        closeKeyBoard();
        getFirms();
        return view;
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.get_firms_actionbar_title);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getFirms() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.FIRM_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());
        showProgress(true ,listView,progressBar );
        Call<FirmList[]> call = RetrofitService.getWayMapsService().getFirmList(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<FirmList[]>() {
            @Override
            public void onResponse(Call<FirmList[]> call, Response<FirmList[]> response) {
                firms = response.body();
                populateTable();
                showProgress(false , listView,progressBar);
                getFragmentManager().popBackStackImmediate("maps",0);
            }

            @Override
            public void onFailure(Call<FirmList[]> call, Throwable t) {
                showProgress(false , listView,progressBar);
                logger.debug("Failed while trying to load balance.");
            }
        });
    }

    public void populateTable() {
        if (firms == null){
            firms = new FirmList[0];
        }
        FirmListAdapter firmListAdapter = new FirmListAdapter(getContext(), Arrays.asList(firms));
        listView.setAdapter(firmListAdapter);
    }

    @Override
    protected void showProgress(boolean show, View... view) {
        super.showProgress(show, view);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String id_firm = Arrays.asList(firms).get(i).getId_firm();
        String id_user = Arrays.asList(firms).get(i).getId_user();
        String title_user = Arrays.asList(firms).get(i).getTitle_user();
        String firm_title = Arrays.asList(firms).get(i).getTitle_firm();
        MainActivityIntent mainActivityIntent = new MainActivityIntent(getContext());
        authorizedUser.setFirm_id(id_firm);
        authorizedUser.setId(id_user);
        authorizedUser.setUser_title(title_user);
        authorizedUser.setFirm_title(firm_title);
        try {
            mainActivityIntent.putExtra("user", JSONUtil.getObjectMapper().writeValueAsString(authorizedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}",authorizedUser.toString());
        }
        startActivity(mainActivityIntent);
    }

    private void closeKeyBoard() {
        View view = getActivity().getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromInputMethod(view.getWindowToken(),0);
        }
    }


}
