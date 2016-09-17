package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.view.CollapsibleActionView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderSectionView extends LinearLayout {

    private View v;
    private TextView tvSection;

    public OrderSectionView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.order_section, this, true);
        tvSection = (TextView) v.findViewById(R.id.os_name);
    }

    public void setFrom(Order order)
    {
        if(order.getProductName().isEmpty()) {
            tvSection.setText("");
            tvSection.setBackgroundColor(A.SECTION_END_COLOR);
        }
        else {
            tvSection.setText(order.getProductName());
            tvSection.setBackgroundColor(A.SECTION_BG_COLOR);
        }
    }
}
