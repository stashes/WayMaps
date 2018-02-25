package com.waymaps.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by Admin on 25.02.2018.
 */

public abstract class AbstractFragment extends Fragment {
    protected User authorizedUser;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getAttrFromBundle();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getAttrFromBundle() {
        try {
            authorizedUser = ApplicationUtil.getObjectFromBundle(getArguments(), "user", User.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
            authorizedUser = null;
        }
    }
}
