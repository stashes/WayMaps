package com.waymaps.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.BalanceAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11.02.2018.
 */

public class BalanceFragment extends Fragment {

    private User authorizedUser;
    private FinGet[] finGets;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
        getAttrFromBundle();
        getBalance();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getAttrFromBundle() {
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }

    private void getBalance() {
        Procedure procedure = new Procedure(Action.CALL);
        Parameter firmId = new IdParam("100");
//        Parameter firmId = new IdParam(authorizedUser.getFirm_id());

        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.FIN_GET);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(firmId.getParameters());

        Call<FinGet[]> call = RetrofitService.getWayMapsService().finGetProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<FinGet[]>() {
            @Override
            public void onResponse(Call<FinGet[]> call, Response<FinGet[]> response) {
                finGets = response.body();
                logger.debug("Balance load successfully.");
                populateTable();
            }

            @Override
            public void onFailure(Call<FinGet[]> call, Throwable t) {
                logger.debug("Failed while trying to load balance.");
            }
        });

    }

    public void populateTable() {
        if (finGets == null){
            finGets = new FinGet[0];
        }
        BalanceAdapter balanceAdapter = new BalanceAdapter(getContext(), Arrays.asList(finGets));
        ListView lvMain = (ListView) getActivity().findViewById(R.id.balance_table);
        lvMain.setAdapter(balanceAdapter);
    }
/*
    private TextView fillText(int id){
        TextView tv = new TextView(getActivity());
        tv.setText(getResources().getText(id));
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    private TextView fillText(String text){
        TextView tv = new TextView(getActivity());
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }*/

}