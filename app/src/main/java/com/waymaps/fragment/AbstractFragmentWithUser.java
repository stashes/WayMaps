package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;

import java.io.IOException;

/**
 * Created by Admin on 06.03.2018.
 */

public class AbstractFragmentWithUser extends AbstractFragment {
    protected User authorizedUser;

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
