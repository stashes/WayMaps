package com.waymaps.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.waymaps.R;
import com.waymaps.activity.MainActivity;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.parameters.ComplexParameters;
import com.waymaps.data.requestEntity.parameters.IdParam;
import com.waymaps.data.requestEntity.parameters.Parameter;
import com.waymaps.data.requestEntity.parameters.StartEndDate;
import com.waymaps.data.requestEntity.parameters.StringParam;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.data.responseEntity.GetTrack;
import com.waymaps.data.responseEntity.Report;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.dialog.ReportDialog;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.SystemUtil;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 11.03.2018.
 */

public class HistoryFragment extends AbstractFragment {

    @BindView(R.id.history_distance)
    EditText distance;

    @BindView(R.id.date_from)
    Button dateFrom;

    @BindView(R.id.date_to)
    Button dateTo;

    @BindView(R.id.history_calculate)
    Button calcuate;

    @BindView(R.id.history_show_report)
    Button showReport;

    @BindView(R.id.history_show_track)
    Button showTrack;

    @BindView(R.id.history_calculate_view)
    LinearLayout historyCalculateView;

    @BindView(R.id.history_show_info_view)
    LinearLayout hisoryShowInfoView;

    @BindView(R.id.history_driver)
    TextView driver;

    @BindView(R.id.history_object)
    TextView object;

    private static final int DIALOG_DATE_FROM = 1;
    private static final int DIALOG_DATE_TO = 2;

    private int year_from;
    private int month_from;
    private int day_from;
    private int hour_from;
    private int minute_from;

    private int year_to;
    private int month_to;
    private int day_to;
    private int hour_to;
    private int minute_to;

    private TrackCount trackCount;
    private Report report;
    private GetCurrent getCurrent;
    private TrackerList tracker;

    private Date dateFromD;
    private Date dateToD;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);
        getFromBundle();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        DrawerLayout drawer = ((MainActivity) getActivity()).getDrawer();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle(fragmentName());

        driver.setText(getCurrent.getDriver());
        object.setText(getCurrent.getTracker_title());
        distance.setText("");

        setDefaultDate();
        updateButtonText();
        return rootView;
    }

    private void getFromBundle() {
        try {
            getCurrent = ApplicationUtil.getObjectFromBundle(getArguments(), "car", GetCurrent.class);
            tracker = ApplicationUtil.getObjectFromBundle(getArguments(), "tracker", TrackerList.class);
        } catch (IOException e) {
            logger.error("Error while trying to parse parameters {}", this.getClass());
        }
    }

    private void setDefaultDate() {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        year_from = calendar.get(Calendar.YEAR);
        year_to = calendar.get(Calendar.YEAR);
        month_from = calendar.get(Calendar.MONTH);
        month_to = calendar.get(Calendar.MONTH);
        day_to = calendar.get(Calendar.DAY_OF_MONTH);
        day_from = calendar.get(Calendar.DAY_OF_MONTH);
        hour_to = calendar.get(Calendar.HOUR_OF_DAY);
        minute_to = calendar.get(Calendar.MINUTE);
    }

    @Override
    protected String fragmentName() {
        return getResources().getString(R.string.history);
    }

    @OnClick(R.id.history_show_track)
    protected void showTrack() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(HistoryFragment.class.getName());
        HistoryMapFragment fragment = new HistoryMapFragment();
        try {
            Bundle bundle = ApplicationUtil.setValueToBundle(new Bundle(), "user", authorizedUser);
            ApplicationUtil.setValueToBundle(bundle, "trackCount", trackCount);
            ApplicationUtil.setValueToBundle(bundle, "getCurrent", getCurrent);
            ApplicationUtil.setValueToBundle(bundle, "tracker", tracker);
            ApplicationUtil.setValueToBundle(bundle, "report", report);
            ApplicationUtil.setValueToBundle(bundle, "from", dateFromD);
            ApplicationUtil.setValueToBundle(bundle, "to", dateToD);
            fragment.setArguments(bundle);
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorizedUser.toString());
        }
        ft.add(R.id.content_main, fragment);
        ft.hide(HistoryFragment.this);
        ft.commit();
    }

    @OnClick(R.id.history_show_report)
    protected void report() {
        ReportDialog reportDialog = new ReportDialog(getContext(), authorizedUser, getCurrent, tracker, report, trackCount, dateFromD, dateToD);
        reportDialog.getWindow().setLayout((int) (SystemUtil.getIntWidth(getActivity()) * 1), (int) (SystemUtil.getIntHeight(getActivity()) * 1));
        reportDialog.show();
    }


    private void loadReport() {
        Procedure procedure = new Procedure(Action.CALL);
        Parameter parameter = new IdParam(getCurrent.getId());
        dateToD = new GregorianCalendar(year_to, month_to, day_to, hour_to, minute_to).getTime();
        dateFromD = new GregorianCalendar(year_from, month_from, day_from, hour_from, minute_from).getTime();
        StartEndDate startEndDate = new StartEndDate(dateFromD,
                dateToD);
        Parameter parameters = new ComplexParameters(parameter, startEndDate);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setName(Action.REPORT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setParams(parameters.getParameters());
        RetrofitService.getWayMapsService().getReport(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), procedure.getParams()).enqueue(new Callback<Report[]>() {
            @Override
            public void onResponse(Call<Report[]> call, Response<Report[]> response) {
                Report[] reports = response.body();
                if (reports.length == 0) {
                    Toast toast = Toast.makeText(getContext(),
                            getResources().getString(R.string.somethin_went_wrong),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    report = reports[0];
                    historyCalculateView.setVisibility(View.GONE);
                    hisoryShowInfoView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Report[]> call, Throwable t) {
                Toast toast = Toast.makeText(getContext(),
                        getResources().getString(R.string.somethin_went_wrong),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    @OnClick(R.id.date_from)
    protected void dateFromOnClick() {
        showDialog(DIALOG_DATE_FROM);
    }

    @OnClick(R.id.history_calculate)
    protected void calculate() {
        Procedure procedure = new Procedure(Action.CALL);
        //todo change
        Parameter parameter = new IdParam(getCurrent.getId());
        //
        StartEndDate startEndDate = new StartEndDate(new GregorianCalendar(year_from, month_from, day_from, hour_from, minute_from).getTime(),
                new GregorianCalendar(year_to, month_to, day_to, hour_to, minute_to).getTime());
        Parameter parameters = new ComplexParameters(parameter, startEndDate);
        procedure.setUser_id(authorizedUser.getId());
        procedure.setName(Action.TRACK_COUNT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(getActivity()));
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setParams(parameters.getParameters());
        RetrofitService.getWayMapsService().getTrackCount(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getFormat(), procedure.getParams()).enqueue(new Callback<TrackCount[]>() {
            @Override
            public void onResponse(Call<TrackCount[]> call, Response<TrackCount[]> response) {
                TrackCount[] trackCounts = response.body();
                if (trackCounts.length == 0) {
                    Toast toast = Toast.makeText(getContext(),
                            getResources().getString(R.string.somethin_went_wrong),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (trackCounts[0].getOdo() != null) {
                        trackCount = trackCounts[0];
                        String distanceKm = new DecimalFormat("0.0").format(
                                Double.parseDouble(trackCounts[0].getOdo()) / 1000);
                        distance.setText(distanceKm + " " + getString(R.string.km));
                        loadReport();
                    } else {
                        distance.setText("0.0 " + getString(R.string.km));
                        Toast toast = Toast.makeText(getContext(),
                                getResources().getString(R.string.no_data_found),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TrackCount[]> call, Throwable t) {
                distance.setText("0.0 " + getString(R.string.km));
                Toast toast = Toast.makeText(getContext(),
                        getResources().getString(R.string.somethin_went_wrong),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    @OnClick(R.id.date_to)
    protected void dateToOnClick() {
        showDialog(DIALOG_DATE_TO);
    }

    private void updateButtonText() {
        String message = (hour_to < 10 ? "0" : "") + hour_to + ":" + (minute_to < 10 ? "0" : "") + minute_to + " " +
                (day_to < 10 ? "0" : "") + day_to + "." + ((month_to + 1) < 10 ? "0" : "") + (month_to + 1) + "." + year_to;
        dateTo.setText(message);
        message = (hour_from < 10 ? "0" : "") + hour_from + ":" + (minute_from < 10 ? "0" : "") + minute_from + " " +
                (day_from < 10 ? "0" : "") + day_from + "." + ((month_from + 1) < 10 ? "0" : "") + (month_from + 1) + "." + year_from;
        dateToD = new GregorianCalendar(year_to, month_to, day_to, hour_to, minute_to).getTime();
        dateFromD = new GregorianCalendar(year_from, month_from, day_from, hour_from, minute_from).getTime();
        dateFrom.setText(message);
        distance.setText("");
        historyCalculateView.setVisibility(View.VISIBLE);
        hisoryShowInfoView.setVisibility(View.GONE);
    }

    private void showDialog(int dialog_id) {
        DatePickerDialog datePickerDialog = null;
        if (dialog_id == DIALOG_DATE_FROM) {
            DatePickerDialog.OnDateSetListener onDateSetListenerDateFrom = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    year_from = year;
                    month_from = month;
                    day_from = dayOfMonth;
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            hour_from = hourOfDay;
                            minute_from = minute;
                            updateButtonText();
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(), onTimeSetListener, hour_from, minute_from, true);
                    timePickerDialog.show();
                }
            };
            datePickerDialog = new DatePickerDialog(getContext(),
                    onDateSetListenerDateFrom, year_from, month_from, day_from);
        } else if (dialog_id == DIALOG_DATE_TO) {
            DatePickerDialog.OnDateSetListener onDateSetListenerDateTo = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    year_to = year;
                    month_to = month;
                    day_to = dayOfMonth;
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            hour_to = hourOfDay;
                            minute_to = minute;
                            updateButtonText();
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(), onTimeSetListener, hour_from, minute_from, true);
                    timePickerDialog.show();

                }
            };
            datePickerDialog = new DatePickerDialog(getContext(),
                    onDateSetListenerDateTo, year_to, month_to, day_to);
        }
        datePickerDialog.show();

    }

}
