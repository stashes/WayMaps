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
import com.waymaps.data.responseEntity.GetParking;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 04.04.2018.
 */

public class ParkingDialog extends Dialog {

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

    @BindView(R.id.parking_ok_button_view)
    LinearLayout parkingOkButtonView;

    @BindView(R.id.parking_ok_button)
    Button okButton;

    private GetParking getParking;

    public ParkingDialog(@NonNull Context context, GetParking parking) {
        super(context);
        this.getParking = parking;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_parking);
        ButterKnife.bind(this);

        String dateFrom = getParking.getStart_date();
        if (dateFrom!=null){
            parkingFrom.setText(dateFrom);
        } else
            parkingFrom.setText("");

        String dateTo = getParking.getEnd_date();
        if (dateTo!=null){
            parkingTo.setText(dateTo);
        } else
            parkingTo.setText("");

        String duration = getParking.getDuration();
        if (duration!= null){
            parkingDuration.setText(duration + " " + getContext().getString(R.string.minute));
        } else
            parkingDuration.setText("0 " + getContext().getString(R.string.minute));

        String odometr = getParking.getP_odometr();
        if (odometr!=null){
            parkingOdometrView.setVisibility(View.VISIBLE);
            parkingDistance.setText(Double.toString(Double.parseDouble(odometr)/1000) + " "
                    + getContext().getString(R.string.km));
        } else {
            parkingOdometrView.setVisibility(View.GONE);
        }

    }


    @OnClick(R.id.parking_ok_button)
    protected void ok(){
        dismiss();
    }
}
