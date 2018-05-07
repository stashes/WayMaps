package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waymaps.R;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;

import java.io.IOException;

/**
 * Created by nazar on 03.03.2018.
 */

public class NoFoundFragment extends Fragment {
    User user;
    Integer ticketId;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_not_found, container, false);
        getAttrFromBundle();
        return view;
    }

    private void getAttrFromBundle(){
        try {
            user = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
            ticketId = ApplicationUtil.getObjectFromBundle(getArguments(), "get_ticket_id", Integer.class);
        } catch (IOException e) {
        }
    }
}
