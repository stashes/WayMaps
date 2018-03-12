package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.waymaps.R;
import com.waymaps.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 11.03.2018.
 */

public class HistoryFragment extends AbstractFragment {

    /*@BindView(R.id.date_from)
    Button dateFrom;

    @BindView(R.id.date_to)
    Button dateTo;*/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this,rootView);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());

        return rootView;
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.history);
    }

    /*@OnClick(R.id.date_from)
    protected void dateFromOnClick() {
        dateFrom.setText("Clicked");
    }

    @OnClick(R.id.date_to)
    protected void dateToOnClick() {
        dateTo.setText("Clicked");
    }*/

}
