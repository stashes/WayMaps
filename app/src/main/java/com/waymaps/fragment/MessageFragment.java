package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;

import java.io.IOException;

import butterknife.BindView;


public class MessageFragment extends Fragment {
    private User authorizedUser;
    private TrackerList tracker;

    TextView mess_person_from;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_layout, container, false);
        mess_person_from = view.findViewById(R.id.mess_person_from);
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
      //      authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
            tracker = ApplicationUtil.getObjectFromBundle(getArguments(), "tracker", TrackerList.class);
        } catch (IOException e) {

        }
    }

}
