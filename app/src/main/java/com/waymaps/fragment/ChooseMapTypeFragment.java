package com.waymaps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.MapProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChooseMapTypeFragment extends AbstractFragment {
    private GetGroup[] getGroups;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @BindView(R.id.radiogroup)
    RadioGroup content;

    @BindView(R.id.gmap)
    RadioButton gMap;

    @BindView(R.id.osmap)
    RadioButton osMap;

    @BindView(R.id.gmaphybrid)
    RadioButton gMapHybrid;

    @BindView(R.id.gmapsatellite)
    RadioButton gMapSatellite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_choose_map_type, container, false);
        ButterKnife.bind(this, view);

        gMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonClick(v);
            }
        });

        osMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonClick(v);
            }
        });

        gMapHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonClick(v);
            }
        });

        gMapSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonClick(v);
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());
        setRadioButtonActive();
        return view;
    }

    private void setRadioButtonActive() {
        String mapProvider = LocalPreferenceManager.getMapProvider(getContext());
        if (MapProvider.valueOf(mapProvider) == MapProvider.Google) {
            gMap.toggle();
        } else if (MapProvider.valueOf(mapProvider) == MapProvider.GOOGLE_HYBRID) {
            gMapHybrid.toggle();
        } else if (MapProvider.valueOf(mapProvider) == MapProvider.GOOGLE_SATELLITE) {
            gMapSatellite.toggle();
        } else {
            osMap.toggle();
        }
    }

    private void radioButtonClick(View v) {
        RadioButton radioButton = (RadioButton) v;
        switch (radioButton.getId()) {
            case R.id.osmap:
                LocalPreferenceManager.setMapProvider(getContext(), MapProvider.OSM.name());
                break;
            case R.id.gmap:
                LocalPreferenceManager.setMapProvider(getContext(), MapProvider.Google.name());
                break;

            case R.id.gmaphybrid:
                LocalPreferenceManager.setMapProvider(getContext(), MapProvider.GOOGLE_HYBRID.name());
                break;

            case R.id.gmapsatellite:
                LocalPreferenceManager.setMapProvider(getContext(), MapProvider.GOOGLE_SATELLITE.name());
                break;
        }
        ((MainActivity) getActivity()).onBackPressed();
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.map_type);
    }
}

