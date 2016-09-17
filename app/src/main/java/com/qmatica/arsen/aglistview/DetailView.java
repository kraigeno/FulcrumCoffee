package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailView extends LinearLayout {

        private View v;
        private TextView tvName;
        private TextView tvUnit;
        private TextView tvQuantity;

        private Boolean checked = false;

        public DetailView(Context context)
        {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.detail_item, this, true);
            tvName = (TextView) v.findViewById(R.id.di_name);
            tvUnit = (TextView) v.findViewById(R.id.di_unit);
            tvQuantity = (TextView) v.findViewById(R.id.di_quantity);
        }

        public void setFrom(Detail detail)
        {
            tvName.setText(detail.getProductName());
            tvUnit.setText(detail.getUnit());
            tvQuantity.setText(String.valueOf(detail.getQuantity()));
        }

}
