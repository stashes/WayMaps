package com.waymaps.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.data.responseEntity.Report;
import com.waymaps.data.responseEntity.TrackCount;
import com.waymaps.data.responseEntity.TrackerList;
import com.waymaps.data.responseEntity.User;
import com.waymaps.util.DateTimeUtil;
import com.waymaps.util.SystemUtil;

import java.text.DecimalFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 22.03.2018.
 */

public class ReportDialog extends Dialog {

    @BindView(R.id.report_object_view)
    LinearLayout reportObjectView;

    @BindView(R.id.report_object)
    TextView reportObject;

    @BindView(R.id.report_period_view)
    LinearLayout reportPeriodView;

    @BindView(R.id.report_date_from)
    TextView reportDateFrom;

    @BindView(R.id.report_date_to)
    TextView reportDateTo;

    @BindView(R.id.report_driver_view)
    LinearLayout reportDriverView;

    @BindView(R.id.report_driver)
    TextView reportDriver;

    @BindView(R.id.report_totaltime_view)
    LinearLayout reportTotalTimeView;

    @BindView(R.id.report_totaltime)
    TextView reportTotalTime;

    @BindView(R.id.report_traffic_view)
    LinearLayout reportTrafficView;

    @BindView(R.id.report_traffic_time)
    TextView reportTraffic;

    @BindView(R.id.report_total_distance_view)
    LinearLayout reportTotalDistanceView;

    @BindView(R.id.report_total_distance)
    TextView reportTotalDistance;

    @BindView(R.id.report_average_speed_view)
    LinearLayout reportAverageSpeedView;

    @BindView(R.id.report_average_speed)
    TextView reportAverageSpeed;

    @BindView(R.id.report_average_speed_include_parking_view)
    LinearLayout reportAverageSpeedIncludeParkingView;

    @BindView(R.id.report_average_speed_include_parking)
    TextView reportAverageSpeedIncludeParking;

    @BindView(R.id.report_parking_count_view)
    LinearLayout reportParkingCountView;

    @BindView(R.id.report_parking_count)
    TextView reportParkingCount;

    @BindView(R.id.report_parking_time_view)
    LinearLayout reportParkingTimeView;

    @BindView(R.id.report_parking_time)
    TextView reportParkingTime;

    @BindView(R.id.report_max_speed_view)
    LinearLayout reportMaxSpeedView;

    @BindView(R.id.report_max_speed)
    TextView reportMaxSpeed;

    @BindView(R.id.report_speed_limit_view)
    LinearLayout reportSpeedLimitView;

    @BindView(R.id.report_speed_limit)
    TextView reportSpeedLimit;

    @BindView(R.id.report_excesses_total_count_view)
    LinearLayout reportExcessesTotalCountView;

    @BindView(R.id.report_excesses_total_count)
    TextView reportExcessesTotalCount;

    @BindView(R.id.report_excesses_total_time_view)
    LinearLayout reportExcessesTotalTimeView;

    @BindView(R.id.report_excesses_total_time)
    TextView reportExcessesTotalTime;

    @BindView(R.id.report_excesses_percentage_view)
    LinearLayout reportExcessesPercentageView;

    @BindView(R.id.report_excesses_percentage)
    TextView reportExcessesPercentage;

    @BindView(R.id.report_ok_button_view)
    LinearLayout reportOkButtonView;

    @BindView(R.id.report_ok_button)
    Button reportOkButton;

    @BindView(R.id.report_min_stop_time_view)
    LinearLayout reportMinStopTimeView;

    @BindView(R.id.report_min_stop_time)
    TextView reportMinStopTime;

    private User authorisedUser;
    private GetCurrent getCurrent;
    private Report report;
    TrackCount trackCount;
    private TrackerList tracker;

    public static DecimalFormat format = new DecimalFormat("0.0");

    private Date dateFrom;
    private Date dateTo;

    public ReportDialog(@NonNull Context context, User authorisedUser, GetCurrent getCurrent, TrackerList tracker,Report report, TrackCount trackCount, Date dateFrom, Date dateTo) {
        super(context);
        this.authorisedUser = authorisedUser;
        this.getCurrent = getCurrent;
        this.report = report;
        this.trackCount = trackCount;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.tracker = tracker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_report);
        ButterKnife.bind(this);

        String title = getCurrent.getTracker_title();
        if (title != null) {
            reportObject.setText(title);
        } else {
            reportObject.setText("");
        }

        String driver = getCurrent.getDriver();
        if (driver != null) {
            reportDriver.setText(driver);
        } else {
            reportDriver.setText("");
        }

        String dateF = DateTimeUtil.dateToStringForReport(dateFrom);
        if (dateF != null) {
            reportDateFrom.setText(dateF);
        } else {
            reportDateFrom.setText("");
        }

        String dateT = DateTimeUtil.dateToStringForReport(dateTo);
        if (dateT != null) {
            reportDateTo.setText(dateT);
        } else {
            reportDateTo.setText("");
        }

        String avgspeed = report.getSpeed_action();
        if (avgspeed != null) {
            try{
                avgspeed = format.format(Double.parseDouble(avgspeed));
            } finally {
                reportAverageSpeed.setText(avgspeed + " " + getContext().getResources().getString(R.string.kmperhour));
            }
        } else {
            reportAverageSpeed.setText("");
        }

        String avgspeedip = report.getSpeed_avg();
        if (avgspeedip != null) {
            try{
                avgspeedip = format.format(Double.parseDouble(avgspeedip));
            } finally {
                reportAverageSpeedIncludeParking.setText(avgspeedip + " " + getContext().getResources().getString(R.string.kmperhour));
            }
        } else {
            reportAverageSpeedIncludeParking.setText("");
        }

        String excPer = report.getOverspeed_percent();
        if (excPer != null) {
            reportExcessesPercentage.setText(excPer);
        } else {
            reportExcessesPercentage.setText("");
        }

        String excC = report.getOverspeed_count();
        if (excC != null) {
            reportExcessesTotalCount.setText(excC);
        } else {
            reportExcessesTotalCount.setText("");
        }

        String excT = report.getOverspeed_seconds();
        if (excT != null) {
            excT =  DateTimeUtil.longToStringDate(Long.parseLong(excT),getContext());
            reportExcessesTotalTime.setText(excT);
        } else {
            reportExcessesTotalTime.setText("");
        }

        String max_speed = report.getMax_speed();
        if (max_speed != null) {
            reportMaxSpeed.setText(max_speed + " " + getContext().getString(R.string.kmperhour));
        } else {
            reportMaxSpeed.setText("");
        }

        String speed_limit = tracker.getMaxspeed();
        if (speed_limit != null) {
            reportSpeedLimit.setText(speed_limit + " " + getContext().getString(R.string.kmperhour));
        } else {
            reportSpeedLimit.setText("");
        }

        String minstop = report.getStop_time();
        if (minstop != null) {
            minstop = DateTimeUtil.longMinToStringDate(Long.parseLong(minstop),getContext());
            reportMinStopTime.setText(minstop);
        } else {
            reportMinStopTime.setText("");
        }

        String stop = report.getParking_count();
        if (stop != null) {
            reportParkingCount.setText(stop);
        } else {
            reportParkingCount.setText("");
        }

        String parktime = report.getParking_time();
        if (parktime != null) {
            parktime = DateTimeUtil.longMinToStringDate(Long.parseLong(parktime),getContext());
            reportParkingTime.setText(parktime);
        } else {
            reportParkingTime.setText("");
        }

        String dist = report.getTotal_odometr();
        if (dist != null) {
            try{
                dist = format.format((Double.parseDouble(dist)/1000));
            } finally {
                reportTotalDistance.setText(dist + " " + getContext().getString(R.string.km));
            }
        } else {
            reportTotalDistance.setText("");
        }

        String totalTime = report.getTotal_period();
        if (totalTime != null) {
            totalTime = DateTimeUtil.longMinToStringDate(Long.parseLong(totalTime),getContext());
            reportTotalTime.setText(totalTime);
        } else {
            reportTotalTime.setText("");
        }

        String trtime = report.getAction_time();
        if (trtime != null) {
            trtime= DateTimeUtil.longMinToStringDate(Long.parseLong(trtime),getContext());
            reportTraffic.setText(trtime);
        } else {
            reportTraffic.setText("");
        }

    }

    @OnClick(R.id.report_ok_button)
    protected void ok(){
        dismiss();
    }
}
