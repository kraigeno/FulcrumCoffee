package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductView extends LinearLayout {

    private View v;
    private TextView tv1;
    private Button b1;
    private Button b2;

    private Boolean checked = false;
    private Boolean unitChecked = false;
    private Boolean caseChecked = false;

    public ProductView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.product_item, this, true);
        tv1 = (TextView) v.findViewById(R.id.pi_name);
        b1 = (Button) v.findViewById(R.id.pi_unit_button);
        b2 = (Button) v.findViewById(R.id.pi_case_button);
    }

    public void setProduct(Product product)
    {
        tv1.setText(product.getName());
        b1.setText(product.getUnitSize());
        b2.setText(product.getCaseSize());
//        b1.setEnabled(product.isUnitCheckable());
//        b2.setEnabled(product.isCaseCheckable());
    }


    public void setChecked(boolean checked)
    {
        if (this.checked != checked) {
            this.checked = checked;
//        TODO colors
            if(this.checked) {
//                setBackgroundColor(A.UNCHECKED_BG_COLOR);
                tv1.setTextColor(A.CHECKED_COLOR);
            }
            else {
//                setBackgroundColor(A.UNCHECKED_BG_COLOR);
                tv1.setTextColor(A.PRODUCT_COLOR);
            }
//            refreshDrawableState();
        }
    }

    public void setUnitChecked(boolean unitChecked)
    {
        if (this.unitChecked != unitChecked) {
            this.unitChecked = unitChecked;
            if(this.unitChecked) {
                b1.setTextColor(A.CHECKED_COLOR);
            }
            else {
                b1.setTextColor(A.PRODUCT_COLOR);
            }
        }
    }
    public void setCaseChecked(boolean caseChecked)
    {
        if (this.caseChecked != caseChecked) {
            this.caseChecked = caseChecked;
            if(this.caseChecked) {
                b2.setTextColor(A.CHECKED_COLOR);
            }
            else {
                b2.setTextColor(A.PRODUCT_COLOR);
            }
        }
    }

}
