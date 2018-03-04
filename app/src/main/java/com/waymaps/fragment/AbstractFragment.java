package com.waymaps.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
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
    /**
     * Shows the progress UI and hides the login form.
     * main view [0]
     * progress bar [1]
     * others [...]
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show , final View...view) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            for(int i = 0; i<view.length ; i++) {
                view[i].setVisibility(show ? View.GONE : View.VISIBLE);
            }
                view[0].animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        for (int i = 0 ; i<view.length ; i++) {
                            view[i].setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    }
                });


            view[0].animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    for (int i = 0 ; i<view.length ; i++) {
                        view[i].setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                }
            });

            view[1].setVisibility(show ? View.VISIBLE : View.GONE);
            view[1].animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view[1].setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            view[0].setVisibility(show ? View.VISIBLE : View.GONE);
            view[1].setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public void setAuthorizedUser(User authorizedUser) {
        this.authorizedUser = authorizedUser;
    }
}
