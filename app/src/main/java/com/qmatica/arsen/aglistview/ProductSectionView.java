package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductSectionView extends LinearLayout {

    private View v;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    public ProductSectionView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.product_section, this, true);
        tv1 = (TextView) v.findViewById(R.id.ps_name);
        tv2 = (TextView) v.findViewById(R.id.ps_unitSize);
        tv3 = (TextView) v.findViewById(R.id.ps_caseSize);
    }

    public void setProduct(Product product)
    {
        tv1.setText(product.getName());
        tv2.setText(product.getUnitSize());
        tv3.setText(product.getCaseSize());
    }
}
