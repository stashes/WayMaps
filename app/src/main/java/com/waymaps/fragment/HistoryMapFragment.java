package com.waymaps.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
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
import com.waymaps.data.responseEntity.Report;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.DateTimeUtil;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.MapProvider;
import com.waymaps.util.SystemUtil;
import com.waymaps.util.TilesProvider;

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

    private static final int POLYLINECOLOR = Color.RED;
    private static final int POLYLINECOLOR2 = Color.YELLOW;


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
    private ArrayList<Handler> handlers = new ArrayList<>();
    private Report report;

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

    @BindView(R.id.history_maxspeed_view)
    LinearLayout maxSpeedView;

    @BindView(R.id.history_parkingcount_view)
    LinearLayout parkingCountView;

    @BindView(R.id.history_maxspeed)
    TextView maxSpeed;

    @BindView(R.id.history_parkingcount)
    TextView historyParkingCount;

    @BindView(R.id.history_limit_view)
    LinearLayout limitView;

    @BindView(R.id.history_limit)
    TextView limit;

    @BindView(R.id.menu)
    ImageView menu;

    @BindView(R.id.history_engine_view)
    LinearLayout engineView;

    @BindView(R.id.history_ignition_view)
    LinearLayout ignitionView;


    BottomSheetBehavior sheetBehavior;

    BasicTrackInfoLayout trackLayout;

    BasicTrackInfoLayout pointLayout;

    static class BackButtonLayout {
        @BindView(R.id.back_to_all_sec)
        ImageView backToAllSec;
    }

    static class BasicTrackInfoLayout {
        @BindView(R.id.history_burn_fuel)
        TextView historyBurnFuel;

        @BindView(R.id.history_burn_fuel_view)
        LinearLayout historyBurnFuelView;

        @BindView(R.id.history_fuelexpense_view)
        LinearLayout fuelExpenseView;

        @BindView(R.id.history_fuelexpense)
        TextView fuelExpense;

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
        if (isAdded() && getActivity() != null) {

            showProgress(true, content, progres);

            //customize button
            historyShowOverSpeed.setText(tracker.getMaxspeed());

            limit.setText(tracker.getMaxspeed() + " " + getResources().getString(R.string.kmperhour));
            maxSpeed.setText(report.getMax_speed() + " " + getResources().getString(R.string.kmperhour));

            //Back button block
            BackButtonLayout backButtonLayoutPoint = new BackButtonLayout();
            BackButtonLayout backButtonLayoutParking = new BackButtonLayout();

            ButterKnife.bind(backButtonLayoutPoint, pointHistoyHeader);
            ButterKnife.bind(backButtonLayoutParking, parkingHistoryHeader);

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

            ButterKnife.bind(pointLayout, pointHistoryInfo);
            ButterKnife.bind(trackLayout, trackHistoryInfo);


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
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    if (isAdded() && getActivity() != null) {
                        mMap = googleMap;
                        TilesProvider.setTile(googleMap, getContext());
                        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {
                                checkMarkerForVisibility();
                            }
                        });
                        mMap.getUiSettings().setRotateGesturesEnabled(false);
                        ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(), getResources().getString(R.string.downloading_data));
                        final Call<GetTrack[]> getTrack = RetrofitService.getWayMapsService().getTrack(procedure.getAction(), procedure.getName(),
                                procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());
                        getTrack.enqueue(new Callback<GetTrack[]>() {
                            @Override
                            public void onResponse(Call<GetTrack[]> call, Response<GetTrack[]> response) {
                                ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(), getResources().getString(R.string.preprocessing_data));
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
                   //             LatLng latLng = new LatLng(build.southwest.latitude, build.southwest.longitude + ((build.southwest.longitude - build.northeast.longitude) / 2.7));
                     //           LatLngBounds latLngBounds = new LatLngBounds(latLng, build.northeast);
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(build, ((int)(SystemUtil.getIntWidth(getActivity()))),
                                        ((int)(SystemUtil.getIntHeight(getActivity()))), 0);
                                googleMap.moveCamera(cu);
                                LatLng target = googleMap.getCameraPosition().target;
                                float zoom = googleMap.getCameraPosition().zoom;
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(target.latitude,(target.longitude*1 + build.southwest.longitude*2)/3),(zoom-1.4f)));
                                ApplicationUtil.showToast(HistoryMapFragment.this.getActivity(), getResources().getString(R.string.draw_way));
                                drawMarkers();
                                fillBasicInfo(trackLayout, getTracks.get(getTracks.size() - 1));

                                //period
                                String fromDate;
                                String toDate;
                                try {
                                    fromDate = DateTimeUtil.dateFormatHistory.format(from);
                                } catch (Exception e) {
                                    fromDate = "-";
                                }
                                try {
                                    toDate = DateTimeUtil.dateFormatHistory.format(to);
                                } catch (Exception e) {
                                    toDate = "-";
                                }


                                trackLayout.historyPeriodFrom.setText(fromDate);
                                trackLayout.historyPeriodTo.setText(toDate);

                                linearLayout.setVisibility(View.VISIBLE);
                                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }

                            @Override
                            public void onFailure(Call<GetTrack[]> call, Throwable t) {
                                showProgress(false, content, progres);
                            }
                        });

                        Procedure procedureGetParking = configureGetParkingProcedure();
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
                                historyParkingCount.setText(getParkingResponse.length + " " + getResources().getString(R.string.pieces) + ".");
                                drawParking();
                            }

                            @Override
                            public void onFailure(Call<GetParking[]> call, Throwable t) {
                                showProgress(false, content, progres);
                            }
                        });

                    }
                }
            });

            menu.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_menu)
                    , getResources().getColor(R.color.light_blue_tr), PorterDuff.Mode.SRC_IN));

            sheetBehavior = BottomSheetBehavior.from(linearLayout);

            historyCarName.setText(getCurrent.getTracker_title());
            historyCarUserName.setText(getCurrent.getDriver());

        }
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
                        .anchor(0.5f, 0.5f).zIndex(100));
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
                if (marker != markers[0] && marker != markers[markers.length - 1])
                    marker.setVisible(visibility);
            }
            currentMarkerVisibility = visibility;
        }
    }

    private void drawMarkers() {
        int numMarkers = getTracks.size();
        markers = new Marker[numMarkers];
        Bitmap bitmap = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_circle_1), Color.BLACK, POINTHEIGHT, POINTWIDTH);
        Bitmap flagStart = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.marker_green), FLAGSIZE, FLAGSIZE);
        Bitmap flagEnd = ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.marker_red), FLAGSIZE, FLAGSIZE);
        if (isAdded() && getActivity() != null)
            for (int i = 0; i < numMarkers; i++) {
                markers[i] = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(getTracks.get(i).getLat())
                                , Double.parseDouble(getTracks.get(i).getLon())))
                        .anchor(0.5f, 0.5f));
                if (i == 0) {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(flagStart));
                    markers[i].setVisible(true);
                } else if (i == (numMarkers - 1)) {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(flagEnd));
                    markers[i].setVisible(true);
                } else {
                    markers[i].setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    markers[i].setVisible(false);
                }
                markers[i].setTag(getTracks.get(i));
            }
        currentMarkerVisibility = false;
        if (isAdded() && getActivity() != null) {
            drawWay();
            showProgress(false, content, progres);


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
                    PolylineOptions overSpeed = new PolylineOptions().width(9.0f).geodesic(true).color(Color.RED).visible(false).zIndex(20);
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
                        , (int) (PARKINGHEIGHT * 1.5), (int) (PARKINGWIDTH * 1.5))));
        String dateFrom = tag.getStart_date();
        if (dateFrom != null) {
            try {
                parkingFrom.setText(DateTimeUtil.toBottomSheetFormat(dateFrom));
            } catch (ParseException e) {
                parkingFrom.setText("");
            }
        } else
            parkingFrom.setText("");

        String dateTo = tag.getEnd_date();
        if (dateTo != null) {
            try {
                parkingTo.setText(DateTimeUtil.toBottomSheetFormat(dateTo));
            } catch (ParseException e) {
                parkingTo.setText("");
            }
        } else
            parkingTo.setText(getResources().getString(R.string.continues));

        String duration = tag.getDuration();
        if (duration != null) {
            parkingDuration.setText(DateTimeUtil.longMinToStringDate(Long.parseLong(duration), getContext()));
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
        if (power == null || "0".equals(power)) {
            power = getResources().getString(R.string.battery);
            historyVoltage.setTextColor(getResources().getColor(R.color.darkyellow));
        } else {
            power = getResources().getString(R.string.network);
            historyVoltage.setTextColor(getResources().getColor(R.color.success));
        }
        voltage = new DecimalFormat("0.0").format(Double.parseDouble(voltage));
        historyVoltage.setText(power
                + " (" + voltage + getResources().getString(R.string.v) + ")");


        //ignition
        String ignition = point.getIgnition();
        if ("-1".equals(getCurrent.getIgnition())) {
            ignitionView.setVisibility(View.GONE);
        } else {
            ignitionView.setVisibility(View.VISIBLE);
            if ("1".equals(ignition)) {
                ignition = getResources().getString(R.string.on);
                historyIgnition.setTextColor(getResources().getColor(R.color.success));
            } else {
                ignition = getResources().getString(R.string.off);
                historyIgnition.setTextColor(getResources().getColor(R.color.fail));
            }
            historyIgnition.setText(ignition);
        }

        //engine
        String engine = point.getMotor();
        if ("-1".equals(getCurrent.getMotor())) {
            engineView.setVisibility(View.GONE);
        } else {
            historyEngine.setVisibility(View.VISIBLE);
            if ("1".equals(engine)) {
                engine = getResources().getString(R.string.on);
                historyEngine.setTextColor(getResources().getColor(R.color.success));
            } else {
                engine = getResources().getString(R.string.off);
                historyEngine.setTextColor(getResources().getColor(R.color.fail));
            }
            historyEngine.setText(engine);
        }

        //speed
        String speed = point.getSpeed();
        if (speed == null || speed == "-1") speed = "0";
        speed += (" " + getResources().getString(R.string.kmperhour));
        historySpeed.setText(speed);


        //period
        String toDate;
        try {
            toDate = DateTimeUtil.toReportFormat(getTrack.getDate());
        } catch (ParseException e) {
            toDate = "-";
        }
        pointLayout.historyPeriodTo.setText(toDate);

        fillBasicInfo(pointLayout, getTrack);
    }

    private void fillBasicInfo(BasicTrackInfoLayout layout, GetTrack getTrack) {
        //burnFuel
        String burnFuel = getTrack.getCount_dff();
        String burnFuelStart = getTracks.get(0).getCount_dff();
        Double b1 = -1d;
        Double b2 = -1d;
        if (burnFuel != null && burnFuelStart != null) {
            layout.historyBurnFuelView.setVisibility(View.VISIBLE);
            try {
                b1 = Double.parseDouble(burnFuel);
                b2 = Double.parseDouble(burnFuelStart);
                layout.historyBurnFuel.setText(((b1 - b2) / 10) + " " + getResources().getString(R.string.l));
            } catch (Exception e) {
                layout.historyBurnFuelView.setVisibility(View.GONE);
                layout.historyBurnFuel.setText("-");
            }
        } else {
            layout.historyBurnFuelView.setVisibility(View.GONE);
            layout.historyBurnFuel.setText("-");
        }

        //distance
        String dEnd = getTrack.getOdometr();
        String dStart = getTracks.get(0).getOdometr();
        Double d1 = -1d;
        Double d2 = -1d;
        if (dEnd != null && dStart != null) {
            try {
                d1 = Double.parseDouble(dEnd);
                d2 = Double.parseDouble(dStart);
                layout.historyDistance.setText((new DecimalFormat("0.0").format((d1 - d2) / 1000)) + " " + getResources().getString(R.string.km));
            } catch (Exception e) {
                layout.historyDistance.setText("-");
            }
        } else {
            layout.historyDistance.setText("-");
        }

        if ((d1 != -1) && (d2 != -1) && (b1 != -1) && (b2 != -1)) {
            try {
                layout.fuelExpense.setText(new DecimalFormat("0.0").format((((b1 - b2) / 10) * 100) / ((d1 - d2) / 1000)) + " " + getResources().getString(R.string.l) +
                        "/" + "100" + getResources().getString(R.string.km));
                layout.fuelExpenseView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                layout.fuelExpenseView.setVisibility(View.GONE);
                layout.fuelExpense.setText("-");
            }
        }
    }


    @OnClick(R.id.back_to_all)
    protected void back() {
        clearMemory();
        getActivity().onBackPressed();
    }

    @OnClick(R.id.history_show_overspeed)
    protected void overSpeed() {
        removeBgTasks();
        updatePolyline(!isOverSpeed);
/*
        for (Polyline polyline : overSpeeds) {
            polyline.setVisible(!isOverSpeed);
        }
*/
        isOverSpeed = !isOverSpeed;
        if (isOverSpeed) {
            historyShowOverSpeed.setBackground(getContext().getResources().getDrawable(R.drawable.ic_overspeed_new));
            maxSpeedView.setVisibility(View.VISIBLE);
            limitView.setVisibility(View.VISIBLE);
            historyShowOverSpeed.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            historyShowOverSpeed.setBackground(getContext().getResources().getDrawable(R.drawable.ic_overspeed));
            maxSpeedView.setVisibility(View.GONE);
            limitView.setVisibility(View.GONE);
            historyShowOverSpeed.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void updatePolyline(boolean show) {
        final Handler handler = new Handler();

        for (Polyline polyline : overSpeeds) {
            polyline.setVisible(show);
        }

        final int delay = 25; //milliseconds
        registerUpdateHandler(handler);
        final int[] currentColor = new int[1];
        currentColor[0] = POLYLINECOLOR;
        final int[] i = new int[1];
        i[0] = 0;
        final boolean[] side = new boolean[1];
        side[0] = true;
        int green = Color.green(POLYLINECOLOR);
        int green1 = Color.green(POLYLINECOLOR2);
        int blue = Color.blue(POLYLINECOLOR);
        int blue1 = Color.blue(POLYLINECOLOR2);
        int red = Color.red(POLYLINECOLOR);
        int red1 = Color.red(POLYLINECOLOR2);
        final float deltag = (green - green1) / 80;
        final float deltar = (red - red1) / 80;
        final float deltab = (blue - blue1) / 80;

        final float[] rgb = new float[3];
        rgb[0] = red;
        rgb[1] = green;
        rgb[2] = blue;


        handler.postDelayed(new Runnable() {
            public void run() {

                if (i[0] == 80) {
                    i[0] = 0;
                    side[0] = !side[0];
                }

                if (side[0]) {
                    rgb[0] -= deltar;
                    rgb[1] -= deltag;
                    rgb[2] -= deltab;
                    for (Polyline polyline : overSpeeds) {
                        polyline.setColor(Color.rgb((int) rgb[0], (int) rgb[1], (int) rgb[2]));
                    }
                } else {
                    rgb[0] += deltar;
                    rgb[1] += deltag;
                    rgb[2] += deltab;
                    for (Polyline polyline : overSpeeds) {
                        polyline.setColor(Color.rgb((int) rgb[0], (int) rgb[1], (int) rgb[2]));
                    }
                }
                i[0]++;
                handler.postDelayed(this, delay);
            }
        }, delay);
    }


    @OnClick(R.id.history_show_parking)
    protected void setShowParking() {
        for (Marker marker : stopMarkers) {
            marker.setVisible(!isShowParking);
        }
        isShowParking = !isShowParking;
        if (isShowParking) {
            parkingCountView.setVisibility(View.VISIBLE);
            historyShowParking.setImageDrawable(getResources().getDrawable(R.drawable.park2));
        } else {
            parkingCountView.setVisibility(View.GONE);
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
            if (!tag.equals(getTracks.get(0)) && !tag.equals(getTracks.get(getTracks.size() - 1)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_circle_1), Color.BLACK, POINTHEIGHT, POINTWIDTH)));
            else if (tag.equals(getTracks.get(getTracks.size() - 1)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.marker_red), FLAGSIZE, FLAGSIZE)));
            else if (tag.equals(getTracks.get(0)))
                currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.marker_green), FLAGSIZE, FLAGSIZE)));
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
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (POINT.equals(to)) {
            pointHistoryInfo.setVisibility(View.VISIBLE);
            pointHistoyHeader.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (PARKING.equals(to)) {
            parkingHistoryHeader.setVisibility(View.VISIBLE);
            parkingHistoryInfo.setVisibility(View.VISIBLE);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
            report = ApplicationUtil.getObjectFromBundle(getArguments(), "report", Report.class);
            from = ApplicationUtil.getObjectFromBundle(getArguments(), "from", Date.class);
            to = ApplicationUtil.getObjectFromBundle(getArguments(), "to", Date.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }


    @Override
    public void onStop() {
        //clearMemory();
        removeBgTasks();
        super.onStop();
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

    @OnClick(R.id.menu)
    public void onMenuClick() {
        ((MainActivity) getActivity()).getDrawer().openDrawer(Gravity.LEFT);
    }

    public void registerUpdateHandler(Handler handler) {
        handlers.add(handler);
    }

    public void removeBgTasks() {
        for (Handler handler : handlers) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}


