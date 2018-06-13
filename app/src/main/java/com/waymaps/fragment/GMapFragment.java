package com.waymaps.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.adapter.GetCurrentAdapter;
import com.waymaps.api.CancelableCallback;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.components.BottomSheetListView;
import com.waymaps.components.MaxHeightLinearView;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

/**
 * Created by Admin on 11.02.2018.
 */

public class GMapFragment extends AbstractFragment implements OnMapReadyCallback {


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private GoogleMap mMap;
    private MapView mapView;
    private GetCurrent[] getCurrentsResponse;
    private List<GetCurrent> getCurrents;
    private Marker[] markers;
    private Procedure procedure;
    private volatile Marker currentMarker;
    private volatile Object currentTag;
    private GetGroup pickedGroup;
    private boolean filtered;
    private boolean locked;
    private Boolean isActive;
    private long timesMarkerUpdate;
    private String pickedId;
    public static Drawer drawerSecond;
    private GetGroup[] getGroups;
    private GetCurrentAdapter adapter;


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

    @BindView(R.id.history)
    ImageView historyCar;

    @BindView(R.id.lock_car)
    ImageView lockCar;

    @BindView(R.id.filter_all)
    LinearLayout filterAll;

    @BindView(R.id.filter_active)
    LinearLayout filterActive;

    @BindView(R.id.filter_inactive)
    LinearLayout filterInActive;

    @BindView(R.id.car_group_view)
    LinearLayout carGroupView;

    @BindView(R.id.car_group_exit)
    ImageView carGroupExit;

    @BindView(R.id.car_group)
    TextView carGroup;

    @BindView(R.id.progress_layout)
    View progressLayout;

    @BindView(R.id.map_container)
    View mapContainer;

    @BindView(R.id.group_view)
    LinearLayout groupButtonView;

    @BindView(R.id.group)
    ImageView group;

    @BindView(R.id.menu)
    ImageView menu;

    @BindView(R.id.message)
    ImageView message;


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
            drawerSecond.openDrawer();
            /*FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            GroupFragment fragment= new GroupFragment(new GMapFragment());
            try {
                fragment.setArguments(ApplicationUtil.setValueToBundle
                        (new Bundle(),"user",authorizedUser));
            } catch (JsonProcessingException e) {
                logger.error("Error writing user {}",authorizedUser.toString());
            }
            ft.replace(R.id.content_main, fragment);
            ft.commit();*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        getAttrFromBundle();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(fragmentName());

        setHasOptionsMenu(true);
        mapView = (MapView) rootView.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);
        showProgress(true, mapContainer, progressLayout);
        procedure = configureProcedure();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            logger.error("Error occurs while map initializing");
        }


        mapView.onResume(); // needed to get the map to display immediately

        CancelableCallback.cancelAll();
        ((MainActivity) getActivity()).deleteAllBackgroundTasks();
        timesMarkerUpdate = 0;
        mapView.getMapAsync(this);


        filterCar.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.filter), Color.GRAY));
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        TilesProvider.setTile(googleMap, getContext());
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        LatLng location = getLatLng();
        Float zoom = getZoom();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        updateMarkersPosition();
    }

    private void addSearchGroup() {
        menu.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_menu)
                , getResources().getColor(R.color.light_blue_tr), PorterDuff.Mode.SRC_IN));

        if (MainActivity.isGroupAvaible == true) {
            Procedure procedure = new Procedure(Action.CALL);
            procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
            procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
            procedure.setName(Action.GET_GROUPS);
            procedure.setUser_id(authorizedUser.getId());
            procedure.setParams(authorizedUser.getFirm_id());

            Call<GetGroup[]> group = RetrofitService.getWayMapsService().getGroup(procedure.getAction(), procedure.getName(),
                    procedure.getIdentficator(), procedure.getFormat(), procedure.getParams());

            group.enqueue(new Callback<GetGroup[]>() {
                @Override
                public void onResponse(Call<GetGroup[]> call, Response<GetGroup[]> response) {
                    getGroups = response.body();

                    DrawerBuilder drawerBuilder = new DrawerBuilder(getActivity())
                            .withActionBarDrawerToggle(false)
                            .withDrawerGravity(Gravity.END)
                            .addDrawerItems(new PrimaryDrawerItem().withName(R.string.all).withTag(null), new DividerDrawerItem())
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    if (drawerItem.getTag() == null) {
                                        pickedGroup = null;
                                        carGroupView.setVisibility(View.GONE);
                                    } else {
                                        pickedGroup = (GetGroup) drawerItem.getTag();
                                        carGroup.setText(pickedGroup.getTitle());
                                        carGroupView.setVisibility(View.VISIBLE);
                                    }
                                    updateMarkersPosition();
                                    drawerSecond.closeDrawer();
                                    return true;
                                }
                            });
                    drawerSecond = drawerBuilder.build();
                    for (int i = 0; i < getGroups.length; i++) {
                        drawerSecond.addItem(new SecondaryDrawerItem().withName(getGroups[i].getTitle()).withTag(getGroups[i]));
                    }


                }

                @Override
                public void onFailure(Call<GetGroup[]> call, Throwable t) {
                    ApplicationUtil.showToast(getContext(), getString(R.string.somethin_went_wrong));
                }
            });
            getActivity().getMenuInflater().inflate(R.menu.main, toolbar.getMenu());
            Drawable yourdrawable = toolbar.getMenu().getItem(0).getIcon();
            yourdrawable.mutate();
            yourdrawable.setColorFilter(getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_IN);
            carGroupExit.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_exit)
                    , getResources().getColor(R.color.colorAccent)));
            this.group.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.group_ic)
                    , getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_IN));
            groupButtonView.setVisibility(View.VISIBLE);

        }

        if (((MainActivity) getActivity()).blinkMessageIcon && (!"0".equals(authorizedUser.getUnread_ticket()) && authorizedUser.getUnread_ticket() != null)
                && ("1".equals(authorizedUser.getManager()) || "1".equals(authorizedUser.getDiler()))) {
            message.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.ic_mail)
                    , getResources().getColor(R.color.light_blue), PorterDuff.Mode.SRC_IN));
            message.setVisibility(View.VISIBLE);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(message, View.ALPHA, 1, 0);
            objectAnimator.setRepeatCount(Animation.INFINITE);
            objectAnimator.setRepeatMode(Animation.REVERSE);
            objectAnimator.setDuration(1200);
            objectAnimator.setInterpolator(new LinearInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(objectAnimator);
            animatorSet.start();
        }
    }

    private void updateMarkersPosition() {
        Call<GetCurrent[]> getCurrent = RetrofitService.getWayMapsService().getCurrentProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        getCurrent.enqueue(new CancelableCallback<GetCurrent[]>() {
            @Override
            public void success(Call<GetCurrent[]> call, Response<GetCurrent[]> response) {

                getCurrentsResponse = response.body();
                getCurrents = new ArrayList<>();
                if (MainActivity.isGroupAvaible == null) {
                    MainActivity.isGroupAvaible = new Boolean(false);
                }
                for (GetCurrent getCurrent1 : getCurrentsResponse) {
                    if ((!(getCurrent1.getLat() == null || getCurrent1.getLon() == null)
                            && (pickedGroup == null || pickedGroup.getId().equals(getCurrent1.getGroup_id())))) {
                        getCurrents.add(getCurrent1);
                        if (timesMarkerUpdate == 0) {
                            if (getCurrent1.getGroup_id() != null) {
                                MainActivity.isGroupAvaible = new Boolean(true);
                            }
                        }
                    }
                }
                if (timesMarkerUpdate == 0)
                    addSearchGroup();
                if (markers != null) {
                    for (Marker marker : markers) {
                        marker.remove();
                    }
                }
                updateMarkers();
                filter();
/*                if (timesMarkerUpdate == 0) {
                    updateMethod(0);
                }*/
                showProgress(false, mapContainer, progressLayout);
                timesMarkerUpdate++;
            }

            @Override
            public void failure(Call<GetCurrent[]> call, Throwable t) {

            }
        });

    }


    private void updateMethod(int flag) {
        final Handler handler = new Handler();

        final int delay = 1000; //milliseconds
        ((MainActivity) getActivity()).registerHandler(handler);
        final int[] i = new int[1];
        final int[] j = new int[1];
        j[0] = flag;
        handler.postDelayed(new Runnable() {
            public void run() {
                ((MainActivity) getActivity()).setBackgroundTaskExecuting(true);
                if (drawerSecond != null && getActivity().getSupportFragmentManager().getFragments().size() == 1) {
                    drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                }
                i[0]++;
                if (j[0] == 1) {
                    i[0] = 0;
                    j[0]--;
                }
                if (i[0] % 5 == 0) {
                    updateMarkersPosition();
                }
                if (currentMarker != null) {
                    GetCurrent tag = (GetCurrent) currentMarker.getTag();
                    updateMarkerState((GetCurrent) currentTag);
                } else {
                    updateListState();
                }


                ((MainActivity) getActivity()).setBackgroundTaskExecuting(false);
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
        if (currentMarker != null) {
            for (GetCurrent gc : getCurrents) {
                if (gc.getId().equals(pickedId)) {
                    currentTag = gc;
                    break;
                }
            }
        }
        markers = new Marker[numMarkers];
        int active = 0;
        int inActive = 0;

        if (isAdded() && getActivity() != null)
            for (int i = 0; i < numMarkers; i++) {
                markers[i] = mMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(getCurrents.get(i).getLat())
                                , Double.parseDouble(getCurrents.get(i).getLon())))
                        .anchor(0.5f, 0.5f));
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

                Bitmap markerIcon = ApplicationUtil.pickImage(getContext(), speed, getCurrents.get(i).getLast_parking_start(), marker, color);
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
                markers[i].setVisible(false);
                markers[i].setTag(getCurrents.get(i));
                if (currentMarker != null && ((GetCurrent) currentTag).getTracker_title()
                        .equals(getCurrents.get(i).getTracker_title())) {
                    currentMarker = markers[i];
                    if (locked)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                }
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
                resizeMap(bottomSheet);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                resizeMap(bottomSheet);
            }
        });
        sheetBehaviorCar.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                resizeMap(bottomSheet);

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                resizeMap(bottomSheet);

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
                all(true);


            }
        });

        filterInActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inActive(true);
            }
        });

        filterActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active(true);
            }
        });


    }

    private void resizeMap(@NonNull View bottomSheet) {
        mapView.setPadding(0, 0, 0, SystemUtil.getIntHeight(getActivity()) - SystemUtil.getStatusBarHeight(getActivity()) - bottomSheet.getTop());
    }

    private void all(boolean collapse) {
        if (markers != null && markers.length != 0 && markers[0].getTag() != null) {
            isActive = null;
            List<GetCurrent> getCurrents = new ArrayList<>();

            for (Marker m : markers) {
                m.setVisible(true);
                GetCurrent tag = (GetCurrent) m.getTag();
                getCurrents.add(tag);
            }

            if (listView.getAdapter() == null) {
                adapter = new GetCurrentAdapter(getContext(), getCurrents);
                listView.setAdapter(adapter);
            }
            adapter.updateList((ArrayList<GetCurrent>) getCurrents);
            changeFontFiter();
            if (collapse) {
                changeBSheetState(BottomSheetBehavior.STATE_COLLAPSED, sheetBehavior);
                resizeMap(linearLayout);
            }
        }
    }

    private void changeBSheetState(int stateCollapsed, BottomSheetBehavior sheetBehavior) {
        if (stateCollapsed == BottomSheetBehavior.STATE_COLLAPSED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (stateCollapsed == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (sheetBehavior == this.sheetBehavior) {
            resizeMap(linearLayout);
        }
        if (sheetBehavior == this.sheetBehaviorCar) {
            resizeMap(linearLayoutCar);
        }
    }

    private void active(boolean collapse) {
        if (markers != null && markers.length != 0 && markers[0].getTag() != null) {

            isActive = new Boolean(true);
            List<GetCurrent> getCurrents = new ArrayList<>();
            for (Marker m : markers) {
                GetCurrent tag = (GetCurrent) m.getTag();
                if ("0".equals(tag.getStatus())) {
                    m.setVisible(false);
                } else {
                    m.setVisible(true);
                    getCurrents.add(tag);
                }
            }
            if (listView.getAdapter() == null) {
                adapter = new GetCurrentAdapter(getContext(), getCurrents);
                listView.setAdapter(adapter);
            }
            adapter.updateList((ArrayList<GetCurrent>) getCurrents);
            changeFontFiter();
            if (collapse)
                changeBSheetState(BottomSheetBehavior.STATE_COLLAPSED, sheetBehavior);
            resizeMap(linearLayout);
        }
    }

    private void inActive(boolean collapse) {
        if (markers != null && markers.length != 0 && markers[0].getTag() != null) {
            isActive = new Boolean(false);
            List<GetCurrent> getCurrents = new ArrayList<>();
            for (Marker m : markers) {
                GetCurrent tag = (GetCurrent) m.getTag();
                if (!"0".equals(tag.getStatus())) {
                    m.setVisible(false);
                } else {
                    m.setVisible(true);
                    getCurrents.add(tag);
                }

            }
            if (listView.getAdapter() == null) {
                adapter = new GetCurrentAdapter(getContext(), getCurrents);
                listView.setAdapter(adapter);
            }
            adapter.updateList((ArrayList<GetCurrent>) getCurrents);
            changeFontFiter();
            if (collapse)
                changeBSheetState(BottomSheetBehavior.STATE_COLLAPSED, sheetBehavior);
            resizeMap(linearLayout);
        }
    }

    private void updateListState() {
        /*final GetCurrentAdapter getCurrentAdapter = new GetCurrentAdapter(getContext(), getCurrents);
        listView.setAdapter(getCurrentAdapter);*/
        if (isActive == null) {
            all(false);
        } else if (isActive == true) {
            active(false);
        } else {
            inActive(false);
        }
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
        if (linearLayoutCar.getVisibility() == View.VISIBLE)
            resizeMap(linearLayoutCar);
        if (linearLayout.getVisibility() == View.VISIBLE)
            resizeMap(linearLayout);
    }

    private void onMarkerOrListViewClick(Marker marker) {
        markerClick(marker);
    }

    private void markerClick(Marker marker) {
        if (currentMarker != marker) {
            currentMarker = marker;
            locked = false;
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            lockCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlock));
        }
        GetCurrent tag = (GetCurrent) currentMarker.getTag();
        pickedId = tag.getId();
        currentTag = currentMarker.getTag();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 18f));
        updateMarkerState(tag);


        backTaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToAll();
            }
        });

        filterCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtered = !filtered;
                filter();
            }
        });

        lockCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locked = !locked;
                if (locked) {
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    lockCar.setImageBitmap(ApplicationUtil.drawToBitmap(
                            getResources().getDrawable(R.drawable.ic_locked)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentMarker.getPosition()));
                } else {
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                    lockCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlock));
                }
            }
        });

        linearLayout.setVisibility(View.GONE);
        changeBSheetState(BottomSheetBehavior.STATE_COLLAPSED, sheetBehaviorCar);
        linearLayoutCar.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.history)
    public void history() {
        showProgress(true, mapContainer, progressLayout);

        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setName(Action.TRACKER_LIST);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setParams(authorizedUser.getId());
        Call<TrackerList[]> call = RetrofitService.getWayMapsService().trackerProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<TrackerList[]>() {
            @Override
            public void onResponse(Call<TrackerList[]> call, Response<TrackerList[]> response) {
                ArrayList<TrackerList> trackers = new ArrayList<>(Arrays.asList(response.body()));
                Bundle bundle = new Bundle();
                TrackerList trackerList = null;
                for (TrackerList t : trackers) {
                    if (t.getId().equals(((GetCurrent) currentTag).getId())) {
                        trackerList = t;
                        break;
                    }
                }
                HistoryFragment historyFragment = new HistoryFragment();
                try {
                    ApplicationUtil.setValueToBundle(bundle, "car", currentTag);
                    ApplicationUtil.setValueToBundle(bundle, "user", authorizedUser);
                    ApplicationUtil.setValueToBundle(bundle, "tracker", trackerList);
                } catch (JsonProcessingException e) {
                    logger.debug("Error while trying write to bundle");
                }

                if (drawerSecond != null) {
                    drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

                historyFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (fragments.get(fragments.size() - 1) != f) {
                        ft.remove(f);
                    }
                }
                ft.commit();

                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(this.getClass().getName());
                /*                ft.replace(R.id.content_main, historyFragment);*/
                ft.add(R.id.content_main, historyFragment);
                ft.hide(GMapFragment.this);
                ft.commit();
                if (drawerSecond != null) {
                    drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                showProgress(false, mapContainer, progressLayout);
            }

            @Override
            public void onFailure(Call<TrackerList[]> call, Throwable t) {
                logger.debug("Failed while trying to load tracker list.");
                showProgress(false, mapContainer, progressLayout);
            }
        });
    }

    private void filter() {
        if (filtered) {
            for (Marker m : markers) {
                m.setVisible(false);
            }
            currentMarker.setVisible(true);
            filterCar.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.filterpicked)));
        } else {
            for (Marker m : markers) {
                if (pickedGroup == null || pickedGroup.getId().equals(((GetCurrent) m.getTag()).getGroup_id()))
                    m.setVisible(true);
            }
            filterCar.setImageBitmap(ApplicationUtil.drawToBitmap(getResources().getDrawable(R.drawable.filter), Color.GRAY));
        }
    }

    private void backToAll() {
        linearLayout.setVisibility(View.VISIBLE);
        linearLayoutCar.setVisibility(View.GONE);
        changeBSheetState(BottomSheetBehavior.STATE_COLLAPSED, sheetBehavior);
        filtered = false;
        locked = false;
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        currentMarker = null;
        for (Marker m : markers) {
            if (m != null) {
                m.remove();
            }
        }
        updateMarkers();
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
        if ("-1".equals(engine)) {
            carEngineView.setVisibility(View.GONE);
        } else {
            if ("1".equals(engine)) {
                engine = getResources().getString(R.string.on);
                carEngine.setTextColor(getResources().getColor(R.color.success));
            } else {
                engine = getResources().getString(R.string.off);
                carEngine.setTextColor(getResources().getColor(R.color.fail));
            }
            carEngineView.setVisibility(View.VISIBLE);
            carEngine.setText(engine);
        }


        //fuelConsumption
        String fuelConsumption = tag.getQ_dff();
        if ("-1".equals(fuelConsumption) || fuelConsumption == null || "-1.00".equals(fuelConsumption)) {
            carFuelConsumptionView.setVisibility(View.GONE);
        } else {
            double v = (Double.parseDouble(fuelConsumption)) / 10;
            fuelConsumption = v + (" " + getResources().getString(R.string.lperkm));
            carFuelConsumption.setText(fuelConsumption);
            carFuelConsumptionView.setVisibility(View.VISIBLE);
        }

        //volumeFuel
        String fuelVolume = tag.getDff();
        if ("-1".equals(fuelVolume) || fuelVolume == null || "-1.00".equals(fuelVolume)) {
            carVolumeFuelView.setVisibility(View.GONE);
        } else {
            double v = Double.parseDouble(fuelVolume) / 10;
            String result = new DecimalFormat("0.0").format(v);
            fuelVolume = result + (" " + getResources().getString(R.string.l));
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
        if ("-1".equals(voltage) || voltage == null) {
            carVoltageView.setVisibility(View.GONE);
        } else {
            String power = tag.getPower();
            if (power == null || "0".equals(power)) {
                power = getResources().getString(R.string.battery);
                carVoltage.setTextColor(getResources().getColor(R.color.yellow));
            } else {
                power = getResources().getString(R.string.network);
                carVoltage.setTextColor(getResources().getColor(R.color.success));
            }
            voltage = new DecimalFormat("0.0").format(Double.parseDouble(voltage));
            carVoltage.setText(power
                    + " (" + voltage + getResources().getString(R.string.v) + ")");
        }

        //ignition
        String ignition = tag.getIgnition();
        if ("-1".equals(ignition)) {
            carIgnitionView.setVisibility(View.GONE);
        } else {
            if ("1".equals(ignition)) {
                ignition = getResources().getString(R.string.on);
                carIgnition.setTextColor(getResources().getColor(R.color.success));
            } else {
                ignition = getResources().getString(R.string.off);
                carIgnition.setTextColor(getResources().getColor(R.color.fail));
            }
            carIgnition.setText(ignition);
            carIgnitionView.setVisibility(View.VISIBLE);
        }

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
        Bitmap bitmap = ApplicationUtil.pickImage(getContext(), 0, "1", tag.getMarker(), tag.getColor());
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
        if (drawerSecond != null) {
            drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        ((MainActivity) getActivity()).deleteAllBackgroundTasks();
        CancelableCallback.cancelAll();
        super.onStop();

    }

    private void getAttrFromBundle() {
        try {
            pickedGroup = ApplicationUtil.getObjectFromBundle(getArguments(), "group", GetGroup.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressed() {
        if (drawerSecond != null && drawerSecond.isDrawerOpen()) {
            drawerSecond.closeDrawer();
            return true;
        } else if (linearLayoutCar.getVisibility() == View.VISIBLE) {
            backToAll();
            return true;
        } else
            return super.onBackPressed();
    }

    private void changeFontFiter() {
        if (isActive == null) {
            TextView text = (TextView) filterAll.getChildAt(0);
            TextView count = (TextView) filterAll.getChildAt(1);
            text.setTypeface(null, Typeface.BOLD_ITALIC);
            count.setTypeface(null, Typeface.BOLD_ITALIC);
            makeNoBoldFont(filterActive, filterInActive);
        } else if (isActive == false) {
            TextView text = (TextView) filterInActive.getChildAt(0);
            TextView count = (TextView) filterInActive.getChildAt(1);
            text.setTypeface(null, Typeface.BOLD_ITALIC);
            count.setTypeface(null, Typeface.BOLD_ITALIC);
            makeNoBoldFont(filterActive, filterAll);
        } else if (isActive == true) {
            TextView text = (TextView) filterActive.getChildAt(0);
            TextView count = (TextView) filterActive.getChildAt(1);
            text.setTypeface(null, Typeface.BOLD_ITALIC);
            count.setTypeface(null, Typeface.BOLD_ITALIC);
            makeNoBoldFont(filterAll, filterInActive);
        }

    }

    @OnClick(R.id.car_group_exit)
    public void groupExt() {
        drawerSecond.deselect();
        carGroupView.setVisibility(View.GONE);
        pickedGroup = null;
        updateMarkersPosition();
    }

    private void makeNoBoldFont(LinearLayout first, LinearLayout second) {
        TextView text = (TextView) first.getChildAt(0);
        TextView count = (TextView) first.getChildAt(1);
        text.setTypeface(null, Typeface.NORMAL);
        count.setTypeface(null, Typeface.NORMAL);
        text = (TextView) second.getChildAt(0);
        count = (TextView) second.getChildAt(1);
        text.setTypeface(null, Typeface.NORMAL);
        count.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public void onPause() {
        if (drawerSecond != null) {
            drawerSecond.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        CancelableCallback.cancelAll();
        super.onPause();
    }

    @Override
    public void onResume() {
        updateMethod(1);
        super.onResume();
    }

    @OnClick(R.id.menu)
    public void onMenuClick() {
        ((MainActivity) getActivity()).getDrawer().openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.message)
    public void onMessageClick() {
        ((MainActivity) getActivity()).showTicketList();
    }

    @OnClick(R.id.group)
    public void onGroupClick() {
        if (drawerSecond != null)
            drawerSecond.openDrawer();
    }
}
