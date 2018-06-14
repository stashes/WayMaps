package com.waymaps.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nazar on 04.03.2018.
 */

public class CommentDialogFragment extends DialogFragment implements View.OnClickListener {
    EditText text;
    Button addComent;
    int ticketId;
    private String trackerName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("");
        View v = inflater.inflate(R.layout.comment_dialog, null);
        text = v.findViewById(R.id.text_comment_ticket);
        addComent = v.findViewById(R.id.send_ticket_comment);
        getAttrFromBundle();
        addComent.setOnClickListener(this);
        return v;
    }
    private void getAttrFromBundle(){
        try {
            ticketId = (Integer) ApplicationUtil.getObjectFromBundle(getArguments(), "get_ticket_id", Integer.class);
            trackerName = ApplicationUtil.getObjectFromBundle(getArguments(), "tracker_name", String.class);
        } catch (IOException e) {
        }
    }

    @Override
    public void onClick(View view) {
        addComment();
        goToGetTicketFragment();
    }

    private void addComment() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.COMMENT_ADD);
        procedure.setUser_id(MainActivity.authorisedUser.getId());
        String parameter = ticketId + ",'" + text.getText().toString() + "'," + MainActivity.authorisedUser.getId();
        Call<Void> call = RetrofitService.getWayMapsService().addComment(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), parameter);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    private void goToGetTicketFragment(){
        Bundle bundle = new Bundle();
        try{
            ApplicationUtil.setValueToBundle(bundle,"get_ticket_id", ticketId);
            ApplicationUtil.setValueToBundle(bundle,"tracker_name", trackerName);

        }catch (JsonProcessingException e){

        }
        GetTicketFragment getTicketFragment = new GetTicketFragment();
        getTicketFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, getTicketFragment);
        getDialog().dismiss();
        ft.commit();
    }
}
