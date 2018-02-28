package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.waymaps.R;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MessageFragment extends AbstractFragment implements View.OnClickListener {
    private User authorizedUser;
    private TrackerList tracker;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    TextView mess_person_from;
    EditText mess_text;
    Button btn_send;
    String parameters = "";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout, container, false);
        initViewElement(view);
        getAttrFromBundle();
        mess_person_from.setText(tracker.getTitle());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    private void getAttrFromBundle() {
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
            tracker = ApplicationUtil.getObjectFromBundle(getArguments(), "tracker", TrackerList.class);
        } catch (IOException e) {

        }
    }
    private void initViewElement(View view){
        mess_person_from = view.findViewById(R.id.mess_person_from);
        mess_text = view.findViewById(R.id.mess_text);
        btn_send = view.findViewById(R.id.btn_mess_send);
        btn_send.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        sendMessage();
        Toast toast = Toast.makeText(view.getContext() , "Sended" , Toast.LENGTH_SHORT);
        goToMapfragment();

    }
    private void sendMessage(){

        Procedure procedure = new Procedure(Action.CALL);
        Parameter firmId = new IdParam("251");
        parameters = "251"+ "," + "'" + mess_text.getText().toString() +"'" +"," + tracker.getId();
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TICKET_ADD);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(firmId.getParameters());
        Call<Void> call = RetrofitService.getWayMapsService().sendMessage(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), parameters);
       call.enqueue(new Callback<Void>() {
           @Override
           public void onResponse(Call<Void> call, Response<Void> response) {
/*               response.
                logger.debug("Send message sucessfull");*/
           }

           @Override
           public void onFailure(Call<Void> call, Throwable t) {

           }
       });
    }
    private void goToMapfragment(){
        TrackerListFragment trackerListF =  new TrackerListFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.message_layout, trackerListF);
        ft.commit();
    }
}
