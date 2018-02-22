package com.waymaps.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.waymaps.R;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.fragment.BalanceFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Admin on 12.02.2018.
 */

public class BalanceAdapter extends BaseAdapter {
    Context context;
    LayoutInflater lInflater;
    List<FinGet> objects;

    @BindView(R.id.balance_balance)
    TextView balance;
    @BindView(R.id.balance_date)
    TextView date;
    @BindView(R.id.balance_debet)
    TextView debet;
    @BindView(R.id.balance_credit)
    TextView credit;

    public BalanceAdapter(Context context, List<FinGet> finGets) {
        this.context = context;
        objects = finGets;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_balance_fragment, parent, false);
        }
        ButterKnife.bind(this,view);
        FinGet finGet = getFinGet(position);

        balance.setText(finGet.getBalance()== null ? "" : finGet.getBalance());
        debet.setText(finGet.getDebet() == null ? "" : finGet.getDebet());
        date.setText(finGet.getDate() == null ? "" : finGet.getDate());
        credit.setText(finGet.getCredit() == null ? "" : finGet.getCredit());

        if (Double.valueOf(finGet.getBalance()) >=  0){
            balance.setTextColor(context.getResources().getColor(R.color.success));
        } else {
            balance.setTextColor(context.getResources().getColor(R.color.fail));
        }

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public FinGet getFinGet(int position) {
        return (FinGet) getItem(position);
    }
}
