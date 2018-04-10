package com.waymaps.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.GetCurrentAdapter;
import com.waymaps.adapter.GroupAdapter;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.components.BottomSheetListView;
import com.waymaps.components.MaxHeightLinearView;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.DateTimeUtil;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private GetCurrent[] getCurrentsResponse;
    private List<GetCurrent> getCurrents;
    private Marker[] markers;
    private Procedure procedure;
    private Marker currentMarker;
    private GetGroup pickedGroup;
    private boolean filtered;
    private boolean locked;
    private Boolean isActive;

    @BindView(R.id.bottom_sheet_map_tracker_list)
    MaxHeightLinearView linearLayout;

    @BindView(R.id.bottom_sheet_car)
    MaxHeightLinearView linearLayoutCar;

    @BindView(R.id.bottom_sheet_map_content)
    LinearLayout linearLayoutContent;

    @BindView(R.id.get_current_table)
    BottomSheetListView listView;

    //Car details
    @BindView(R.id.get_current_image)
    ImageView carImage;

    @BindView(R.id.get_current_car_name)
    TextView carName;

    @BindView(R.id.get_current_car_user_name)
    TextView carUser;

    @BindView(R.id.car_state)
    TextView carState;

    @BindView(R.id.car_state_view)
    LinearLayout carStateView;

    @BindView(R.id.car_actual)
    TextView carActual;

    @BindView(R.id.car_actual_view)
    LinearLayout carActualView;

    @BindView(R.id.car_stop)
    TextView carStop;

    @BindView(R.id.car_stop_view)
    LinearLayout carStopView;

    @BindView(R.id.car_speed)
    TextView carSpeed;

    @BindView(R.id.car_speed_view)
    LinearLayout carSpeedView;

    @BindView(R.id.car_ignition)
    TextView carIgnition;

    @BindView(R.id.car_ignition_view)
    LinearLayout carIgnitionView;

    @BindView(R.id.car_engine)
    TextView carEngine;

    @BindView(R.id.car_engine_view)
    LinearLayout carEngineView;

    @BindView(R.id.car_voltage)
    TextView carVoltage;

    @BindView(R.id.car_voltage_view)
    LinearLayout carVoltageView;

    @BindView(R.id.car_volume_fuel)
    TextView carVolumeFuel;

    @BindView(R.id.car_volume_fuel_view)
    LinearLayout carVolumeFuelView;

    @BindView(R.id.car_fuel_consumption)
    TextView carFuelConsumption;

    @BindView(R.id.car_fuel_consumption_view)
    LinearLayout carFuelConsumptionView;

    @BindView(R.id.car_gps_satellite)
    TextView carGpsSatellite;

    @BindView(R.id.car_gps_satellite_view)
    LinearLayout carGpsSatelliteView;

    @BindView(R.id.car_gps_signal)
    TextView carGpsSignal;

    @BindView(R.id.car_gps_signal_view)
    LinearLayout carGpsSignalView;

    @BindView(R.id.car_note)
    TextView carNote;

    @BindView(R.id.car_note_view)
    LinearLayout carNoteView;

    @BindView(R.id.back_to_all)
    ImageView backTaAll;

    @BindView(R.id.filter_car)
    ImageView filterCar;

    @BindView(R.id.lock_car)
    ImageView lockCar;

    @BindView(R.id.filter_all)
    LinearLayout filterAll;

    @BindView(R.id.filter_active)
    LinearLayout filterActive;

    @BindView(R.id.filter_inactive)
    LinearLayout filterInActive;


    BottomSheetBehavior sheetBehavior;

    BottomSheetBehavior sheetBehaviorCar;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_button) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            GroupFragment fragment= new GroupFragment(new GMapFragment());
            try {
                fragment.setArguments(ApplicationUtil.setValueToBundle
                        (new Bundle(),"user",authorizedUser));
            } catch (JsonProcessingException e) {
                logger.error("Error writing user {}",authorizedUser.toString());
            }
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        getAttrFromBundle();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(fragmentName());

        setHasOptionsMenu(true);
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
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                LatLng location = getLatLng();
                Float zoom = getZoom();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
                Call<GetCurrent[]> getCurrent = RetrofitService.getWayMapsService().getCurrentProcedure(procedure.getAction(), procedure.getName(),
                        procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
                getCurrent.enqueue(new Callback<GetCurrent[]>() {
                    @Override
                    public void onResponse(Call<GetCurrent[]> call, Response<GetCurrent[]> response) {
                        getCurrentsResponse = response.body();
                        getCurrents = new ArrayList<>();
                        if (MainActivity.isGroupAvaible==null){
                            MainActivity.isGroupAvaible = new Boolean(false);
                        }
                        for (GetCurrent getCurrent1 : getCurrentsResponse) {
                            if ((!(getCurrent1.getLat() == null || getCurrent1.getLon() == null)
                                    && (pickedGroup == null || pickedGroup.getId().equals(getCurrent1.getGroup_id())))) {
                                getCurrents.add(getCurrent1);
                                if (getCurrent1.getGroup_id()!=null){
                                    MainActivity.isGroupAvaible = new Boolean(true);
                                }
                            }
                        }
                        if (MainActivity.isGroupAvaible == true){
                            getActivity().getMenuInflater().inflate(R.menu.main, toolbar.getMenu());
                        }
                        updateMarkers();
                    }

                    @Override
                    public void onFailure(Call<GetCurrent[]> call, Throwable t) {

                    }
                });
            }
        });

        //updateMethod();
        return rootView;
    }

    private void updateMethod() {
        final Handler handler = new Handler();
        final int delay = 1000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                if (currentMarker != null){
                    GetCurrent tag = (GetCurrent) currentMarker.getTag();
                    updateMarkerState(tag);
                } else {
                    updateListState();
                    if (isActive == null){
                        all();
                    } else if (isActive == true){
                        active();
                    } else {
                        inActive();
                    }
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    protected String fragmentName() {
        return "";
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TypedValue out = new TypedValue();
        getResources().getValue(R.dimen.bottom_sheet_height, out, true);
        float floatResource = out.getFloat();
        //if  (linearLayout.getHeight() >= SystemUtil.getIntHeight(getActivity()) * floatResource)
        linearLayoutCar.setMaxHeightDp((int) (SystemUtil.getIntHeight(getActivity()) * floatResource));
        linearLayout.setMaxHeightDp((int) (SystemUtil.getIntHeight(getActivity()) * floatResource));
    }

    private void updateMarkers() {
        int numMarkers = getCurrents.size();
        markers = new Marker[numMarkers];
        int active = 0;
        int inActive = 0;
        if (isAdded() && getActivity() != null)
            for (int i = 0; i < numMarkers; i++) {
                    markers[i] = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(getCurrents.get(i).getLat())
                                    , Double.parseDouble(getCurrents.get(i).getLon()))));
                    double speed = 0;
                    if (getCurrents.get(i).getSpeed() != null) {
                        speed = Double.parseDouble(getCurrents.get(i).getSpeed());
                    } else
                        speed = 0;

                    if (getCurrents.get(i).getStatus().equals("1")) {
                        active++;
                    } else {
                        inActive++;
                    }

                    String marker = getCurrents.get(i).getMarker();
                    String color = getCurrents.get(i).getColor();

                    Bitmap markerIcon = ApplicationUtil.pickImage(getContext(),speed, marker, color);

                    if (speed > 5) {
                        float vector = 0;
                        if (getCurrents.get(i).getVector() != null) {
                            vector = Float.parseFloat(getCurrents.get(i).getVector());
                        }
                        markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                        if (speed > 5) {
                            markers[i].setRotation(vector);
                        }

                    } else {
                        markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                    }

                    markers[i].setTag(getCurrents.get(i));
            }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                onMarkerOrListViewClick(marker);
                return true;
            }
        });

        sheetBehaviorCar = BottomSheetBehavior.from(linearLayoutCar);
        sheetBehavior = BottomSheetBehavior.from(linearLayout);
        updateListState();
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                mapView.setPadding(0, 0, 0, SystemUtil.getIntHeight(getActivity()) - SystemUtil.getStatusBarHeight(getActivity()) - bottomSheet.getTop());

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mapView.setPadding(0, 0, 0, SystemUtil.getIntHeight(getActivity()) - SystemUtil.getStatusBarHeight(getActivity()) - bottomSheet.getTop());

            }
        });
        sheetBehaviorCar.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                mapView.setPadding(0, 0, 0, SystemUtil.getIntHeight(getActivity()) - SystemUtil.getStatusBarHeight(getActivity()) - bottomSheet.getTop());

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mapView.setPadding(0, 0, 0, SystemUtil.getIntHeight(getActivity()) - SystemUtil.getStatusBarHeight(getActivity()) - bottomSheet.getTop());

            }
        });

        TextView text = (TextView) filterAll.getChildAt(0);
        TextView count = (TextView) filterAll.getChildAt(1);
        text.setText(getResources().getString(R.string.all) + ": ");
        count.setText(Integer.toString(active + inActive));

        text = (TextView) filterInActive.getChildAt(0);
        count = (TextView) filterInActive.getChildAt(1);
        text.setText(getResources().getString(R.string.statusoffline) + ": ");
        count.setText(Integer.toString(inActive));
        count.setTextColor(getResources().getColor(R.color.fail));

        text = (TextView) filterActive.getChildAt(0);
        count = (TextView) filterActive.getChildAt(1);
        text.setText(getResources().getString(R.string.statusonline) + ": ");
        count.setText(Integer.toString(active));
        count.setTextColor(getResources().getColor(R.color.success));

        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all();


            }
        });

        filterInActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActive();
            }
        });

        filterActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active();


            }
        });

    }

    private void all() {
        isActive = null;
        List<GetCurrent> getCurrents = new ArrayList<>();
        for (Marker m : markers) {
            m.setVisible(true);
            GetCurrent tag = (GetCurrent) m.getTag();
            getCurrents.add(tag);
        }
        listView.setAdapter(new GetCurrentAdapter(getContext(), getCurrents));
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void active() {
        isActive = new Boolean(true);
        List<GetCurrent> getCurrents = new ArrayList<>();
        for (Marker m : markers) {
            m.setVisible(true);
            GetCurrent tag = (GetCurrent) m.getTag();
            if ("0".equals(tag.getStatus())) {
                m.setVisible(false);
            } else {
                getCurrents.add(tag);
            }
        }
        listView.setAdapter(new GetCurrentAdapter(getContext(), getCurrents));
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void inActive() {
        isActive = new Boolean(false);
        List<GetCurrent> getCurrents = new ArrayList<>();
        for (Marker m : markers) {
            m.setVisible(true);
            GetCurrent tag = (GetCurrent) m.getTag();
            if (!"0".equals(tag.getStatus())) {
                m.setVisible(false);
            } else {
                getCurrents.add(tag);
            }

        }
        listView.setAdapter(new GetCurrentAdapter(getContext(), getCurrents));
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void updateListState() {
        final GetCurrentAdapter getCurrentAdapter = new GetCurrentAdapter(getContext(), getCurrents);
        listView.setAdapter(getCurrentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetCurrent getCurrent = (GetCurrent) listView.getItemAtPosition(position);
                for (Marker m : markers) {
                    if (m.getTag() == getCurrent) {
                        markerClick(m);
                    }
                }
            }
        });
    }

    private void onMarkerOrListViewClick(Marker marker) {
        markerClick(marker);
    }

    private void markerClick(Marker marker) {
        if (currentMarker != marker) {
            currentMarker = marker;
            locked = false;
            lockCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlock));
        }
        GetCurrent tag = (GetCurrent) currentMarker.getTag();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(tag.getLat())
                ,Double.parseDouble(tag.getLon()))));
        updateMarkerState(tag);


        backTaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.VISIBLE);
                linearLayoutCar.setVisibility(View.GONE);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehaviorCar.setState(BottomSheetBehavior.STATE_COLLAPSED);
                filtered = false;
                locked = false;
                currentMarker = null;
                for (Marker m : markers) {
                    if (m!=null){
                        m.remove();
                    }
                }
                updateMarkers();
            }
        });

        filterCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtered = !filtered;
                if (filtered) {
                    for (Marker m : markers) {
                        m.setVisible(false);
                    }
                    currentMarker.setVisible(true);
                } else {
                    for (Marker m : markers) {
                        m.setVisible(true);
                    }
                }
            }
        });

        lockCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locked = !locked;
                if (locked) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                    lockCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock));
                } else {
                    lockCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlock));
                }
            }
        });

        linearLayout.setVisibility(View.GONE);
        linearLayoutCar.setVisibility(View.VISIBLE);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehaviorCar.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void updateMarkerState(GetCurrent tag) {
        //speed
        String speed = tag.getSpeed();
        if (speed == null || speed == "-1") speed = "0";
        speed += (" " + getResources().getString(R.string.kmperhour));
        carSpeed.setText(speed);

        //status
        String status = tag.getStatus();
        if ("1".equals(status)) {
            status = getResources().getString(R.string.statusonline);
            carState.setTextColor(getResources().getColor(R.color.success));
        } else {
            status = getResources().getString(R.string.statusoffline);
            carState.setTextColor(getResources().getColor(R.color.fail));
        }
        carState.setText(status);

        //engine
        String engine = tag.getMotor();
        if ("1".equals(engine)) {
            engine = getResources().getString(R.string.on);
            carEngine.setTextColor(getResources().getColor(R.color.success));
        } else {
            engine = getResources().getString(R.string.off);
            carEngine.setTextColor(getResources().getColor(R.color.fail));
        }
        carEngine.setText(engine);

        //fuelConsumption
        String fuelConsumption = tag.getQ_dff();
        if ("-1".equals(fuelConsumption)) {
            carFuelConsumptionView.setVisibility(View.GONE);
        } else {
            fuelConsumption += (" " + getResources().getString(R.string.lperkm));
            carFuelConsumption.setText(fuelConsumption);
            carFuelConsumptionView.setVisibility(View.VISIBLE);
        }

        //volumeFuel
        String fuelVolume = tag.getDff();
        if ("-1".equals(fuelVolume)) {
            carVolumeFuelView.setVisibility(View.GONE);
        } else {
            fuelVolume += (" " + getResources().getString(R.string.l));
            carVolumeFuel.setText(fuelVolume);
            carVolumeFuelView.setVisibility(View.VISIBLE);
        }

        //note
        String note = tag.getNote();
        if (note == null || "-1".equals(note)) {
            carNoteView.setVisibility(View.GONE);
        } else {
            carNote.setText(note);
            carNote.setTextColor(getResources().getColor(R.color.fail));
            carNoteView.setVisibility(View.VISIBLE);
        }

        //gps signal
        String gpsSignal = tag.getGsm();
        carGpsSignalView.setVisibility(View.VISIBLE);
        if ("4".equals(gpsSignal)) {
            carGpsSignal.setText(getResources().getString(R.string.very_well));
            carGpsSignal.setTextColor(getResources().getColor(R.color.success));
        } else if ("3".equals(gpsSignal)) {
            carGpsSignal.setText(getResources().getString(R.string.good));
            carGpsSignal.setTextColor(getResources().getColor(R.color.success));
        } else if ("2".equals(gpsSignal)) {
            carGpsSignal.setText(getResources().getString(R.string.normal));
            carGpsSignal.setTextColor(getResources().getColor(R.color.warning));
        } else if ("1".equals(gpsSignal)) {
            carGpsSignal.setText(getResources().getString(R.string.bad));
            carGpsSignal.setTextColor(getResources().getColor(R.color.fail));
        } else if ("0".equals(gpsSignal)) {
            carGpsSignal.setText(getResources().getString(R.string.absent));
            carGpsSignal.setTextColor(getResources().getColor(R.color.fail));
        } else {
            carGpsSignalView.setVisibility(View.GONE);
        }

        //gsp satellite
        String satellite = tag.getSat();
        carGpsSatelliteView.setVisibility(View.VISIBLE);
        try {
            int sat = Integer.parseInt(satellite);
            if (sat >= 9) {
                carGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                carGpsSatellite.setTextColor(getResources().getColor(R.color.success));
            } else if (sat >= 6) {
                carGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                carGpsSatellite.setTextColor(getResources().getColor(R.color.warning));
            } else if (sat >= 0) {
                carGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                carGpsSatellite.setTextColor(getResources().getColor(R.color.fail));
            } else {
                carGpsSatelliteView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            carGpsSatelliteView.setVisibility(View.GONE);
        }

        //voltage
        String voltage = tag.getVoltage();
        carVoltageView.setVisibility(View.VISIBLE);
        if ("-1".equals(voltage)) {
            carVoltageView.setVisibility(View.GONE);
        } else {
            String power = tag.getPower();
            if (power != null || "1".equals(power)) {
                power = getResources().getString(R.string.network);
            } else
                power = getResources().getString(R.string.battery);
            carVoltage.setText(power
                    + " (" + voltage + getResources().getString(R.string.v) + ")");
            carVoltage.setTextColor(getResources().getColor(R.color.success));
        }

        //ignition
        String ignition = tag.getIgnition();
        if ("1".equals(ignition)) {
            ignition = getResources().getString(R.string.on);
            carIgnition.setTextColor(getResources().getColor(R.color.success));
        } else {
            ignition = getResources().getString(R.string.off);
            carIgnition.setTextColor(getResources().getColor(R.color.fail));
        }
        carIgnition.setText(ignition);

        //stop
        String stop = tag.getLast_parking_start();
        final Date stopDate;
        if (stop == null) {
            carStopView.setVisibility(View.GONE);
        } else {
            try {
                stopDate = ApplicationUtil.simpleDateFormat.parse(stop);
                Date currentDate = new Date();
                String diff = DateTimeUtil.getDiffBetweenDate(currentDate, stopDate, getContext());
                carStop.setText(diff);
                carStopView.setVisibility(View.VISIBLE);

            } catch (ParseException e) {
                carStopView.setVisibility(View.GONE);
            }
        }

        //actual
        String actual = tag.getL_date();
        final Date updateDate;
        try {
            updateDate = ApplicationUtil.simpleDateFormat.parse(actual);
            Date currentDate = new Date();
            String diff = DateTimeUtil.getDiffBetweenDate(currentDate, updateDate, getContext());
            carActual.setText(diff);
            carActualView.setVisibility(View.VISIBLE);

        } catch (ParseException e) {
            carActualView.setVisibility(View.GONE);
        }


        //username
        String userName = "";
        if (tag.getDriver() != null)
            userName = tag.getDriver();
        carUser.setText(userName);

        //car name
        String cName = tag.getTracker_title();
        carName.setText(cName);

        //icon
        Drawable drawable = carImage.getDrawable();
        int color = ApplicationUtil.changeColorScaleTo16Int(tag.getColor());
        Bitmap bitmap = ApplicationUtil.changeIconColor(drawable, color);
        carImage.setImageBitmap(bitmap);
    }


    private Procedure configureProcedure() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_CURRENT);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());
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

    @Override
    public void onStop() {
        super.onStop();
        mMap.clear();
    }

    private void getAttrFromBundle(){
        try {
            pickedGroup = ApplicationUtil.getObjectFromBundle(getArguments(), "group", GetGroup.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }
}
