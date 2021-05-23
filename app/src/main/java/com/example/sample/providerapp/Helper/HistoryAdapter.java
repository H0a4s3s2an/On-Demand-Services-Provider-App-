package com.example.sample.providerapp.Helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.providerapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Hassan Javaid on 9/13/2018.
 */

public class HistoryAdapter extends RecyclerView.ViewHolder {
   public  TextView tv_trip_car;
   public TextView tv_trip_total;
   public ImageView iv_driver_photo;
   public TextView tv_trip_date;
    public HistoryAdapter(final View itemView) {
        super(itemView);
        tv_trip_car = itemView.findViewById(R.id.tv_trip_car);
        tv_trip_total = itemView.findViewById(R.id.tv_trip_total);
        iv_driver_photo = itemView.findViewById(R.id.iv_driver_photo);
        tv_trip_date = itemView.findViewById(R.id.tv_trip_date);
    }
}
