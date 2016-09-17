package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailSectionView extends LinearLayout {

    private View v;
    private TextView tvName;
    private TextView tvUnit;
    private TextView tvQuantity;
    private TextView tvSpacer;

    public DetailSectionView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.detail_section, this, true);
        tvName = (TextView) v.findViewById(R.id.ds_name);
        tvUnit = (TextView) v.findViewById(R.id.ds_unit);
        tvQuantity = (TextView) v.findViewById(R.id.ds_quantity);
        tvSpacer = (TextView) v.findViewById(R.id.ds_spacer);
    }

    public void setFrom(Detail detail)
    {
        if(detail.getProductName().isEmpty()) {
            tvName.setText("");
            tvUnit.setText("");
            tvQuantity.setText("");
            tvName.setBackgroundColor(A.SECTION_END_COLOR);
            tvUnit.setBackgroundColor(A.SECTION_END_COLOR);
            tvQuantity.setBackgroundColor(A.SECTION_END_COLOR);
            tvSpacer.setBackgroundColor(A.SECTION_END_COLOR);
        }
        else {
            tvName.setText(detail.getCategory());
            tvUnit.setText("Unit [Case]");
            tvQuantity.setText("Qty");
            tvName.setBackgroundColor(A.SECTION_BG_COLOR);
            tvUnit.setBackgroundColor(A.SECTION_BG_COLOR);
            tvQuantity.setBackgroundColor(A.SECTION_BG_COLOR);
            tvSpacer.setBackgroundColor(A.SECTION_BG_COLOR);
        }
    }
}
