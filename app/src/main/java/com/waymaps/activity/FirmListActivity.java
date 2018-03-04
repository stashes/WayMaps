package com.waymaps.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waymaps.R;
import com.waymaps.adapter.FirmListAdapter;
import com.waymaps.adapter.TrackerListAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.FirmList;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;
import com.waymaps.fragment.AbstractFragment;
import com.waymaps.util.JSONUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 04.03.2018.
 */

public class FirmListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    FirmList[] firmLists;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    ListView listView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firm_list);
        listView = findViewById(R.id.firm_table);
        progressBar = findViewById(R.id.progress_bar_firm_list);
        getFirm();
        listView.setOnItemClickListener(this);
    }

    private void populateTable(){
        if (firmLists == null){
            firmLists = new FirmList[0];
        }
        FirmListAdapter firmListAdapter = new FirmListAdapter(getApplicationContext(), Arrays.asList(firmLists));
        listView.setAdapter(firmListAdapter);
    }

    private void getFirm() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(this));
        procedure.setName(Action.FIRM_LIST);
        procedure.setUser_id(MainActivity.authorisedUser.getId());
        showProgress(true);
        Call<FirmList[]> call = RetrofitService.getWayMapsService().getFirmList(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), procedure.getUser_id());
        call.enqueue(new Callback<FirmList[]>() {
            @Override
            public void onResponse(Call<FirmList[]> call, Response<FirmList[]> response) {
                firmLists = response.body();
                populateTable();
                showProgress(false);
            }

            @Override
            public void onFailure(Call<FirmList[]> call, Throwable t) {
                logger.debug("Failed while trying to load balance.");
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            listView.setVisibility(show ? View.GONE : View.VISIBLE);
            listView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            listView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, MainActivity.class);
        MainActivity.firmId = Arrays.asList(firmLists).get(i).getId_firm();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}
