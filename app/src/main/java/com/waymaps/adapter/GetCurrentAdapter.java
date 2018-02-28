package com.waymaps.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.waymaps.R;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.GetCurrent;
import com.waymaps.util.ApplicationUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by StanislavCheslavskyi on 28.02.2018.
 */

public class GetCurrentAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater lInflater;
    private List<GetCurrent> objects;

    @BindView(R.id.get_current_name)
    TextView name;
    @BindView(R.id.get_current_status)
    TextView status;
    @BindView(R.id.get_current_image)
    ImageView icon;

    public GetCurrentAdapter(Context context, List<GetCurrent> getCurrents) {
        this.context = context;
        objects = getCurrents;
        if (context!=null)
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
            view = lInflater.inflate(R.layout.item_get_current_fragment, parent, false);
        }
        ButterKnife.bind(this,view);
        GetCurrent getCurrent = getGetCurrent(position);

        Drawable drawable = icon.getDrawable();
        int color = ApplicationUtil.changeColorScaleTo16Int(getCurrent.getColor());

        Bitmap bitmap = ApplicationUtil.changeIconColor(drawable,color);

        icon.setImageBitmap(bitmap);
        name.setText(getCurrent.getTracker_title());
        status.setText(getCurrent.getStatus());

        return view;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public GetCurrent getGetCurrent(int position) {
        return (GetCurrent) getItem(position);
    }
}
