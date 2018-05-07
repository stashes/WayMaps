package com.waymaps.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.components.MaxHeightLinearView;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.ComplexParameters;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.requestEntity.parameters.StartEndDate;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetParking;
import com.waymaps.data.responseEntity.GetTrack;
import com.waymaps.data.responseEntity.PointData;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.dialog.ParkingDialog;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.DateTimeUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 25.03.2018.
 */

public class HistoryMapFragment extends AbstractFragment {

    public static final int POINTHEIGHT = 30;
    public static final int POINTWIDTH = 30;

    public static final int PARKINGWIDTH = 60;
    public static final int PARKINGHEIGHT = 60;

    public static final int FLAGSIZE = 50;

    public static final String TRACK = "TRACK";
    public static final String POINT = "POINT";
    public static final String PARKING = "PARKING";

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private GoogleMap mMap;
    private MapView mapView;
    private TrackCount trackCount;
    private GetCurrent getCurrent;
    private GetTrack[] getTrackResponse;
    private List<GetTrack> getTracks;
    private GetParking[] getParkingResponse;
    private List<GetParking> getParkings;
    private Marker[] markers;
    private Marker[] stopMarkers;
    private Procedure procedure;
    private Marker currentMarker;
    private Date from;
    private Date to;
    private List<Polyline> overSpeeds;
    private TrackerList tracker;
    private boolean isOverSpeed;
    private boolean isShowParking;
    private boolean currentMarkerVisibility;

    @BindView(R.id.rootView)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_sheet_history)
    MaxHeightLinearView linearLayout;

    @BindView(R.id.history_gps_signal)
    TextView historyGpsSignal;

    @BindView(R.id.history_gps_satellite)
    TextView historyGpsSatellite;

    @BindView(R.id.history_voltage)
    TextView historyVoltage;

    @BindView(R.id.history_engine)
    TextView historyEngine;

    @BindView(R.id.history_ignition)
    TextView historyIgnition;

    @BindView(R.id.history_speed)
    TextView historySpeed;

    @BindView(R.id.history_car_name)
    TextView historyCarName;

    @BindView(R.id.history_car_user_name)
    TextView historyCarUserName;

    @BindView(R.id.back_to_all)
    ImageView backTaAll;

    @BindView(R.id.history_show_overspeed)
    TextView historyShowOverSpeed;

    @BindView(R.id.history_show_parking)
    ImageView historyShowParking;

    @BindView(R.id.parking_from)
    TextView parkingFrom;

    @BindView(R.id.parking_to)
    TextView parkingTo;

    @BindView(R.id.parking_duration)
    TextView parkingDuration;

    @BindView(R.id.parking_odometr_view)
    LinearLayout parkingOdometrView;

    @BindView(R.id.parking_distance)
    TextView parkingDistance;

    @BindView(R.id.track_history_header)
    LinearLayout trackHistoryHeader;

    @BindView(R.id.parking_history_header)
    LinearLayout parkingHistoryHeader;

    @BindView(R.id.point_history_header)
    LinearLayout pointHistoyHeader;

    @BindView(R.id.point_history_info)
    LinearLayout pointHistoryInfo;

    @BindView(R.id.parking_history_info)
    LinearLayout parkingHistoryInfo;

    @BindView(R.id.track_history_info)
    LinearLayout trackHistoryInfo;

    @BindView(R.id.history_map_content)
    View content;

    @BindView(R.id.progress_layout)
    View progres;

    BottomSheetBehavior sheetBehavior;

    BasicTrackInfoLayout trackLayout;

    BasicTrackInfoLayout pointLayout;

    static class BackButtonLayout{
        @BindView(R.id.back_to_all_sec)
        ImageView backToAllSec;
    }

    static class BasicTrackInfoLayout{
        @BindView(R.id.history_burn_fuel)
        TextView historyBurnFuel;

        @BindView(R.id.history_distance)
        TextView historyDistance;

        @BindView(R.id.history_period_to)
        TextView historyPeriodTo;

        @BindView(R.id.history_period_from)
        TextView historyPeriodFrom;
    }

    @Override
    protected String fragmentName() {
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history_map, container, false);
        getAttrFromBundle();
        ButterKnife.bind(this, rootView);

        //customize button
        historyShowOverSpeed.setText(tracker.getMaxspeed());

        //Back button block
        BackButtonLayout backButtonLayoutPoint = new BackButtonLayout();
        BackButtonLayout backButtonLayoutParking = new BackButtonLayout();

        ButterKnife.bind(backButtonLayoutPoint,pointHistoyHeader);
        ButterKnife.bind(backButtonLayoutParking,parkingHistoryHeader);

        View.OnClickListener backButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };

        backButtonLayoutParking.backToAllSec.setOnClickListener(backButton);
        backButtonLayoutPoint.backToAllSec.setOnClickListener(backButton);

        //Info layout block
        pointLayout = new BasicTrackInfoLayout();
        trackLayout = new BasicTrackInfoLayout();

        ButterKnife.bind(pointLayout,pointHistoryInfo);
        ButterKnife.bind(trackLayout,trackHistoryInfo);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(fragmentName());

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            logger.error("Error occurs while map initializing");
        }

        procedure = configureProcedure();
        showProgress(true,content,progres);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        checkMarkerForVisibility();
                    }
                });
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(),getResources().getString(R.string.downloading_data));
                final Call<GetTrack[]> getTrack = RetrofitService.getWayMapsService().getTrack(procedure.getAction(), procedure.getName(),
                        procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());
                getTrack.enqueue(new Callback<GetTrack[]>() {
                    @Override
                    public void onResponse(Call<GetTrack[]> call, Response<GetTrack[]> response) {
                        ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(),getResources().getString(R.string.preprocessing_data));
                        getTrackResponse = response.body();
                        getTracks = new ArrayList<>();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (GetTrack getTrack : getTrackResponse) {
                            if (!(getTrack.getLat() == null || getTrack.getLon() == null)) {
                                getTracks.add(getTrack);
                                builder.include(new LatLng(Double.parseDouble(getTrack.getLat())
                                        , Double.parseDouble(getTrack.getLon())));
                            }
                        }
                        LatLngBounds build = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(build, SystemUtil.getIntWidth(getActivity()),
                                SystemUtil.getIntHeight(getActivity()), 10);
                        googleMap.animateCamera(cu);
                        ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(),getResources().getString(R.string.draw_way));
                        drawMarkers();
                        fillBasicInfo(trackLayout, getTracks.get(getTracks.size() - 1));
                        linearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<GetTrack[]> call, Throwable t) {
                        showProgress(false,content,progres);
                    }
                });

                Procedure procedureGetParking = configureGetParkingProcedure();
                ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(),getResources().getString(R.string.prepare_stop_info));
                Call<GetParking[]> getParking = RetrofitService.getWayMapsService().getParking(procedureGetParking.getAction(), procedureGetParking.getName(),
                        procedureGetParking.getIdentficator(), procedureGetParking.getFormat(), procedureGetParking.getParams());
                getParking.enqueue(new Callback<GetParking[]>() {
                    @Override
                    public void onResponse(Call<GetParking[]> call, Response<GetParking[]> response) {
                        getParkingResponse = response.body();
                        getParkings = new ArrayList<>();
                        for (GetParking getP : getParkingResponse) {
                            if (!(getP.getParking_lat() == null || getP.getParking_lon() == null)) {
                                getParkings.add(getP);
                            }
                        }
                        drawParking();
                    }

                    @Override
                    public void onFailure(Call<GetParking[]> call, Throwable t) {
                        showProgress(false,content,progres);
                    }
                });

            }
        });

        sheetBehavior = BottomSheetBehavior.from(linearLayout);

        historyCarName.setText(getCurrent.getTracker_title());
        historyCarUserName.setText(getCurrent.getDriver());

        return rootView;
    }

    private Procedure configureGetParkingProcedure() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_PARKING);
        procedure.setUser_id(authorizedUser.getId());
        Parameter params = new ComplexParameters(new IdParam(getCurrent.getId()), new StartEndDate(from, to));
        procedure.setParams(params.getParameters());
        return procedure;
    }

    private void drawParking() {
        int numMarkers = getParkings.size();
        stopMarkers = new Marker[numMarkers];
        Bitmap bitmap = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.park1), PARKINGHEIGHT, PARKINGWIDTH);
        if (isAdded() && getActivity() != null)
            for (int i = 0; i < numMarkers; i++) {
                stopMarkers[i] = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(getParkings.get(i).getParking_lat())
                                , Double.parseDouble(getParkings.get(i).getParking_lon())))
                        .anchor(0.5f, 0.5f));
                stopMarkers[i].setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                stopMarkers[i].setVisible(false);
                stopMarkers[i].setTag(getParkings.get(i));
            }
    }

    private void checkMarkerForVisibility() {
        CameraPosition cameraPosition = mMap.getCameraPosition();
        makeMarkerVisible(cameraPosition.zoom > 15.0 ? true : false);
    }

    private void makeMarkerVisible(boolean visibility) {
        if (currentMarkerVisibility != visibility) {
            for (Marker marker : markers) {
                marker.setVisible(visibility);
            }
            currentMarkerVisibility = visibility;
        }
    }

    private void drawMarkers() {
        int numMarkers = getTracks.size();
        markers = new Marker[numMarkers];
        Bitmap bitmap = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_circle_1), Color.BLACK, POINTHEIGHT, POINTWIDTH);
        Bitmap flagStart = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.accent_begin_track),FLAGSIZE,FLAGSIZE);
        Bitmap flagEnd = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.accent_end_track),FLAGSIZE,FLAGSIZE);
        if (isAdded() && getActivity() != null)
            for (int i = 0; i < numMarkers; i++) {
                markers[i] = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(getTracks.get(i).getLat())
                                , Double.parseDouble(getTracks.get(i).getLon())))
                        .anchor(0.5f, 0.5f));
                if (i == 0) {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(flagStart));
                } else if (i == (numMarkers - 1)) {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(flagEnd));
                } else {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                }
                markers[i].setVisible(false);
                markers[i].setTag(getTracks.get(i));
            }
        currentMarkerVisibility = false;
        drawWay();
        showProgress(false,content,progres);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (currentMarker != null) {
                    changeMarkerToDefault();
                }
                currentMarker = marker;
                if (marker.getTag() instanceof GetTrack) {
                    onMarkerOrListViewClick(marker);
                    changeView(POINT);
                } else {
                    onParkingClick(marker);
                    changeView(PARKING);
                }
                return true;
            }
        });
    }

    private void drawWay() {
        overSpeeds = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions().width(5.0f).geodesic(true).
                color(ApplicationUtil.changeColorScaleTo16Int(getCurrent.getColor()));
        for (int i = 0; i < markers.length; i++) {
            GetTrack tag = (GetTrack) markers[i].getTag();
            polylineOptions.add(new LatLng(Double.parseDouble(tag.getLat()),
                    Double.parseDouble(tag.getLon()))).zIndex(1);
            if ("1".equals(tag.getOver_speed())) {
                if (i >= 1 && i < markers.length - 1) {
                    PolylineOptions overSpeed = new PolylineOptions().width(5.0f).geodesic(true).color(Color.RED).visible(false).zIndex(10);
                    GetTrack tag0 = (GetTrack) markers[i - 1].getTag();
                    GetTrack tag2 = (GetTrack) markers[i + 1].getTag();

                    overSpeed.add(new LatLng(Double.parseDouble(tag0.getLat()),
                            Double.parseDouble(tag0.getLon())));
                    overSpeed.add(new LatLng(Double.parseDouble(tag.getLat()),
                            Double.parseDouble(tag.getLon())));
                    overSpeed.add(new LatLng(Double.parseDouble(tag2.getLat()),
                            Double.parseDouble(tag2.getLon())));

                    overSpeeds.add(mMap.addPolyline(overSpeed));
                }
            }
        }
        Polyline polyline = mMap.addPolyline(polylineOptions);

    }

    private void changeOverSpeedVisibility() {
        for (Polyline polyline : overSpeeds) {
            polyline.setVisible(!polyline.isVisible());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    /*private void onParkingClick(Marker marker) {
        GetParking tag = (GetParking) marker.getTag();
        Dialog dialog = new ParkingDialog(getContext(),tag);
        dialog.show();
    }*/

    private void onParkingClick(Marker marker) {
        GetParking tag = (GetParking) marker.getTag();
        currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.park2)
                        ,(int) (PARKINGHEIGHT*1.5), (int)(PARKINGWIDTH*1.5))));
        String dateFrom = tag.getStart_date();
        if (dateFrom != null) {
            parkingFrom.setText(dateFrom);
        } else
            parkingFrom.setText("");

        String dateTo = tag.getEnd_date();
        if (dateTo != null) {
            parkingTo.setText(dateTo);
        } else
            parkingTo.setText("");

        String duration = tag.getDuration();
        if (duration != null) {
            parkingDuration.setText(duration + " " + getContext().getString(R.string.minute));
        } else
            parkingDuration.setText("0 " + getContext().getString(R.string.minute));

        String odometr = tag.getP_odometr();
        if (odometr != null) {
            parkingOdometrView.setVisibility(View.VISIBLE);
            parkingDistance.setText(new DecimalFormat("0.0").format(Double.parseDouble(odometr) / 1000) + " "
                    + getContext().getString(R.string.km));
        } else {
            parkingOdometrView.setVisibility(View.GONE);
        }
    }

    private void onMarkerOrListViewClick(Marker marker) {
        final GetTrack getTrack = (GetTrack) marker.getTag();
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(
                ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_circle_1))));
        Call<PointData[]> loadPoint = RetrofitService.getWayMapsService().getPoint(Action.CALL, Action.POINT_DATA,
                SystemUtil.getWifiMAC(getContext()), WayMapsService.DEFAULT_FORMAT, getTrack.getId());
        loadPoint.enqueue(new Callback<PointData[]>() {
            @Override
            public void onResponse(Call<PointData[]> call, Response<PointData[]> response) {
                PointData[] body = response.body();
                if (body == null || body.length > 1) {
                    ApplicationUtil.showToast(getContext(), getResources().getString(R.string.error_loading_point));
                } else {
                    PointData point = body[0];
                    fillBottomSheetPoint(getTrack, point);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PointData[]> call, Throwable t) {
                ApplicationUtil.showToast(getContext(), getResources().getString(R.string.error_loading_point));
            }
        });
    }

    private void fillBottomSheetPoint(GetTrack getTrack, PointData point) {

        //gps signal
        String gpsSignal = point.getGsm();
        if ("4".equals(gpsSignal)) {
            historyGpsSignal.setText(getResources().getString(R.string.very_well));
            historyGpsSignal.setTextColor(getResources().getColor(R.color.success));
        } else if ("3".equals(gpsSignal)) {
            historyGpsSignal.setText(getResources().getString(R.string.good));
            historyGpsSignal.setTextColor(getResources().getColor(R.color.success));
        } else if ("2".equals(gpsSignal)) {
            historyGpsSignal.setText(getResources().getString(R.string.normal));
            historyGpsSignal.setTextColor(getResources().getColor(R.color.warning));
        } else if ("1".equals(gpsSignal)) {
            historyGpsSignal.setText(getResources().getString(R.string.bad));
            historyGpsSignal.setTextColor(getResources().getColor(R.color.fail));
        } else if ("0".equals(gpsSignal)) {
            historyGpsSignal.setText(getResources().getString(R.string.absent));
            historyGpsSignal.setTextColor(getResources().getColor(R.color.fail));
        }
        //gsp satellite
        String satellite = point.getSat();
        try {
            int sat = Integer.parseInt(satellite);
            if (sat >= 9) {
                historyGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                historyGpsSatellite.setTextColor(getResources().getColor(R.color.success));
            } else if (sat >= 6) {
                historyGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                historyGpsSatellite.setTextColor(getResources().getColor(R.color.warning));
            } else if (sat >= 0) {
                historyGpsSatellite.setText(satellite + " " + getResources().getString(R.string.pieces));
                historyGpsSatellite.setTextColor(getResources().getColor(R.color.fail));
            }
        } catch (Exception e) {
            historyGpsSatellite.setText(" ");
        }

        //voltage
        String voltage = point.getVoltage();
        if (voltage == null || "-1".equals(voltage)) {
            voltage = "0";
        }
        String power = point.getPower();
        if (power != null || "1".equals(power)) {
            power = getResources().getString(R.string.network);
        } else
            power = getResources().getString(R.string.battery);
        voltage = new DecimalFormat("0.0").format(Double.parseDouble(voltage));
        historyVoltage.setText(power
                + " (" + voltage + getResources().getString(R.string.v) + ")");
        historyVoltage.setTextColor(getResources().getColor(R.color.success));


        //ignition
        String ignition = point.getIgnition();
        if ("1".equals(ignition)) {
            ignition = getResources().getString(R.string.on);
            historyIgnition.setTextColor(getResources().getColor(R.color.success));
        } else {
            ignition = getResources().getString(R.string.off);
            historyIgnition.setTextColor(getResources().getColor(R.color.fail));
        }
        historyIgnition.setText(ignition);

        //engine
        String engine = point.getMotor();
        if ("1".equals(engine)) {
            engine = getResources().getString(R.string.on);
            historyEngine.setTextColor(getResources().getColor(R.color.success));
        } else {
            engine = getResources().getString(R.string.off);
            historyEngine.setTextColor(getResources().getColor(R.color.fail));
        }
        historyEngine.setText(engine);

        //speed
        String speed = point.getSpeed();
        if (speed == null || speed == "-1") speed = "0";
        speed += (" " + getResources().getString(R.string.kmperhour));
        historySpeed.setText(speed);


        fillBasicInfo(pointLayout,getTrack);
    }

    private void fillBasicInfo(BasicTrackInfoLayout layout,GetTrack getTrack) {
        //burnFuel
        String burnFuel = getTrack.getCount_dff();
        String burnFuelStart = getTracks.get(0).getCount_dff();
        if (burnFuel != null && burnFuelStart != null) {
            try {
                Double b1 = Double.parseDouble(burnFuel);
                Double b2 = Double.parseDouble(burnFuelStart);
                layout.historyBurnFuel.setText(((b1 - b2) / 10) + " " + getResources().getString(R.string.l));
            } catch (Exception e) {
                layout.historyBurnFuel.setText("-");
            }
        } else {
            layout.historyBurnFuel.setText("-");
        }

        //distance
        String dEnd = getTrack.getOdometr();
        String dStart = getTracks.get(0).getOdometr();
        if (dEnd != null && dStart != null) {
            try {
                Double d1 = Double.parseDouble(dEnd);
                Double d2 = Double.parseDouble(dStart);
                layout.historyDistance.setText((new DecimalFormat("0.0").format((d1 - d2) / 1000)) + " " + getResources().getString(R.string.km));
            } catch (Exception e) {
                layout.historyDistance.setText("-");
            }
        } else {
            layout.historyDistance.setText("-");
        }

        //period
        String fromDate;
        String toDate;
        try {
            fromDate = DateTimeUtil.toReportFormat(getTracks.get(0).getDate());
        } catch (ParseException e) {
            fromDate = "-";
        }
        try {
            toDate = DateTimeUtil.toReportFormat(getTrack.getDate());
        } catch (ParseException e) {
            toDate = "-";
        }
        layout.historyPeriodFrom.setText(fromDate);
        layout.historyPeriodTo.setText(toDate);

    }


    @OnClick(R.id.back_to_all)
    protected void back() {
        clearMemory();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.history_show_overspeed)
    protected void overSpeed() {
        for (Polyline polyline : overSpeeds) {
            polyline.setVisible(!isOverSpeed);
        }
        isOverSpeed = !isOverSpeed;
        if (isOverSpeed){
            historyShowOverSpeed.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            historyShowOverSpeed.setTypeface(null, Typeface.NORMAL);
        }
    }

    @OnClick(R.id.history_show_parking)
    protected void setShowParking() {
        for (Marker marker : stopMarkers) {
            marker.setVisible(!isShowParking);
        }
        isShowParking = !isShowParking;
        if (isShowParking){
            historyShowParking.setImageDrawable(getResources().getDrawable(R.drawable.park2));
        } else {
            historyShowParking.setImageDrawable(getResources().getDrawable(R.drawable.park1));
        }
    }

    @Override
    public boolean onBackPressed() {
        if (pointHistoyHeader.getVisibility() == View.VISIBLE
                || parkingHistoryHeader.getVisibility() == View.VISIBLE) {
            changeView(TRACK);
            changeMarkerToDefault();
            return true;
        } else
            return super.onBackPressed();
    }

    private void changeMarkerToDefault() {
        if (currentMarker.getTag() instanceof GetParking) {
            currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.park1), PARKINGHEIGHT, PARKINGWIDTH)));
        } else if (currentMarker.getTag() instanceof GetTrack) {
            GetTrack tag = (GetTrack) currentMarker.getTag();
            if (!tag.equals(getTracks.get(0)) && !tag.equals(getTracks.get(getTracks.size()-1)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_circle_1), Color.BLACK, POINTHEIGHT, POINTWIDTH)));
            else if (tag.equals(getTracks.get(getTracks.size()-1)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.accent_end_track),FLAGSIZE,FLAGSIZE)));
            else if (tag.equals(getTracks.get(0)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.accent_begin_track),FLAGSIZE,FLAGSIZE)));
        }
    }

    private void changeView(String to) {
        trackHistoryHeader.setVisibility(View.GONE);
        parkingHistoryHeader.setVisibility(View.GONE);
        pointHistoyHeader.setVisibility(View.GONE);
        pointHistoryInfo.setVisibility(View.GONE);
        parkingHistoryInfo.setVisibility(View.GONE);
        trackHistoryInfo.setVisibility(View.GONE);
        if (TRACK.equals(to)) {
            trackHistoryHeader.setVisibility(View.VISIBLE);
            trackHistoryInfo.setVisibility(View.VISIBLE);
        } else if (POINT.equals(to)) {
            pointHistoryInfo.setVisibility(View.VISIBLE);
            pointHistoyHeader.setVisibility(View.VISIBLE);
        } else if (PARKING.equals(to)) {
            parkingHistoryHeader.setVisibility(View.VISIBLE);
            parkingHistoryInfo.setVisibility(View.VISIBLE);
        }
    }

    private void showPointNotLoadedToast() {
        Toast toast = Toast.makeText(getContext(),
                getResources().getString(R.string.error_loading_point),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private Procedure configureProcedure() {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.GET_TRACK);
        procedure.setUser_id(authorizedUser.getId());
        Parameter params = new ComplexParameters(new IdParam(getCurrent.getId()), new StartEndDate(from, to));
        procedure.setParams(params.getParameters());
        return procedure;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TypedValue out = new TypedValue();
        getResources().getValue(R.dimen.bottom_sheet_height, out, true);
        float floatResource = out.getFloat();
        //if  (linearLayout.getHeight() >= SystemUtil.getIntHeight(getActivity()) * floatResource)
        linearLayout.setMaxHeightDp((int) (SystemUtil.getIntHeight(getActivity()) * floatResource));
    }

    private void getAttrFromBundle() {
        try {
            trackCount = ApplicationUtil.getObjectFromBundle(getArguments(), "trackCount", TrackCount.class);
            getCurrent = ApplicationUtil.getObjectFromBundle(getArguments(), "getCurrent", GetCurrent.class);
            tracker = ApplicationUtil.getObjectFromBundle(getArguments(), "tracker", TrackerList.class);
            from = ApplicationUtil.getObjectFromBundle(getArguments(), "from", Date.class);
            to = ApplicationUtil.getObjectFromBundle(getArguments(), "to", Date.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        clearMemory();
    }

    private void clearMemory() {
        getTrackResponse = new GetTrack[0];
        getTracks = new ArrayList<>();
        getParkingResponse = new GetParking[0];
        getParkings = new ArrayList<>();
        markers = new Marker[0];
        stopMarkers = new Marker[0];
        mMap.clear();
        System.gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindDrawables(coordinatorLayout);
        System.gc();
    }

}
