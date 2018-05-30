package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.BalanceAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11.02.2018.
 */

public class BalanceFragment extends AbstractFragment {

    private FinGet[] finGets;
    private double balance;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @BindView(R.id.balance_info)
    TextView info;
    @BindView(R.id.balance_contain)
    View balanceMain;
    @BindView(R.id.progress_layout)
    View progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_balance, container, false);
        ButterKnife.bind(this,view);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());


        getBalance();
        return view;
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.balance);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    private void getBalance() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.FIN_GET);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getFirm_id());
        showProgress(true,balanceMain,progressBar);
        Call<FinGet[]> call = RetrofitService.getWayMapsService().finGetProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<FinGet[]>() {
            @Override
            public void onResponse(Call<FinGet[]> call, Response<FinGet[]> response) {
                finGets = response.body();
                showProgress(false,balanceMain,progressBar);
                logger.debug("Balance load successfully.");
                populateTable();
            }

            @Override
            public void onFailure(Call<FinGet[]> call, Throwable t) {
                showProgress(false , balanceMain,progressBar);
                logger.debug("Failed while trying to load balance.");
            }
        });

    }

    public void populateTable() {
        if (finGets == null){
            finGets = new FinGet[0];
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.###,00");
        String html1 = null;
        String html2 = null;
        String saldo = null;
        String accNo = "15" + String.format("%05d",new Integer(authorizedUser.getFirm_id()));
        String text = getResources().getString(R.string.personal_account) + " " + "№" + accNo +
                " - " + getString(R.string.balancesmall) + " ";
        if (finGets.length == 0){
            balance = 0;
            saldo = decimalFormat.format(balance);
            saldo += " " + getString(R.string.uah);
            html1 = "<font>" + text + "</font>";
        } else {
            balance = new Double(finGets[0].getBalance());
            saldo = decimalFormat.format(balance);
            saldo += " " + getString(R.string.uah);
            html1 = "<font>" + text + "</font>";
        }
        if (balance > 0) {
            html2 = "<font color=#12b90f>" + saldo + "</font>";
        } else {
            html2 = "<font color=#e11a24>" + saldo + "</font>";
        }

        info.setText(Html.fromHtml((html1 + html2)));
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
