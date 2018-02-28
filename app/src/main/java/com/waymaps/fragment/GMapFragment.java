package com.waymaps.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.ComplexParameters;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11.02.2018.
 */

public class GMapFragment extends AbstractFragment {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private GoogleMap mMap;
    private MapView mapView;
    private GetCurrent[] getCurrents;
    private Marker[] markers;
    private Procedure procedure;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            logger.error("Error occurs while map initializing");
        }

        procedure = configureProcedure();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng location = getLatLng();
                Float zoom = getZoom();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
                Call<GetCurrent[]> getCurrent = RetrofitService.getWayMapsService().getCurrentProcedure(procedure.getAction(), procedure.getName(),
                        procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
                getCurrent.enqueue(new Callback<GetCurrent[]>() {
                    @Override
                    public void onResponse(Call<GetCurrent[]> call, Response<GetCurrent[]> response) {
                        getCurrents = response.body();
                        updateMarkers();
                    }

                    @Override
                    public void onFailure(Call<GetCurrent[]> call, Throwable t) {

                    }
                });
            }
        });


        return rootView;
    }

    private void updateMarkers() {
        int numMarkers = getCurrents.length;
        markers = new Marker[numMarkers];
        if (isAdded() && getActivity()!=null)
        for (int i = 0; i < numMarkers; i++) {
            markers[i] = mMap.addMarker(new MarkerOptions().position(
                    new LatLng(Double.parseDouble(getCurrents[i].getLat())
                            , Double.parseDouble(getCurrents[i].getLon()))));

            double speed = 0;
            if (getCurrents[i].getSpeed() != null) {
                speed = Double.parseDouble(getCurrents[i].getSpeed());
            } else
                speed = 0;

            String marker = getCurrents[i].getMarker();
            String color = getCurrents[i].getColor();

            Bitmap markerIcon = pickImage(speed, marker, color);

            float vector = 0;
            if (getCurrents[i].getVector() != null) {
                vector = Float.parseFloat(getCurrents[i].getVector());
            }

            markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
            if (speed>5){
                markers[i].setRotation(vector);
            }
            markers[i].setTag(getCurrents[i]);
        }
    }

    private Bitmap pickImage(double speed, String marker, String color) {
        Bitmap bitmap = null;
        Drawable drawable = null;
        int bitmapColor = 10000;
        if (speed > 5) {
            drawable = getResources().getDrawable(R.drawable.ic_marker_navigation);
        } else {
            if (marker != null) {

            }
            drawable = getResources().getDrawable(R.drawable.ic_marker_stay);
        }

        if (color != null) {
            int intColor = Integer.parseInt(color);
            String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
            bitmapColor = Color.parseColor(hexColor);
        }

        return ApplicationUtil.changeIconColor(drawable, bitmapColor);
    }

    private Procedure configureProcedure() {
        Procedure procedure = new Procedure(Action.CALL);
        Parameter userId = new IdParam("255");
        //@todo change getID()
        //Parameter userId = new IdParam(authorizedUser.getId());
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_CURRENT);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(userId.getParameters());
        return procedure;
    }

    private Float getZoom() {
        return LocalPreferenceManager.getZoom(getActivity());
    }

    private LatLng getLatLng() {
        double latitude = LocalPreferenceManager.getLatitude(getActivity());
        double longitude = LocalPreferenceManager.getLongitude(getActivity());
        return new LatLng(latitude, longitude);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
