package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderView extends LinearLayout {

    private View v;
    private TextView tvName;
    private TextView tvQuantity1;
    private TextView tvQuantity2;
    private TextView tvQuantity3;
    private TextView tvQuantity4;
    private TextView tvQuantity;
    private Button b1;
    private Button b2;

//    private Boolean checked = false;

    public OrderView(Context context)
    {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.order_item, this, true);
        tvName = (TextView) v.findViewById(R.id.oi_name);
        tvQuantity1 = (TextView) v.findViewById(R.id.oi_quantyty1);
        tvQuantity2 = (TextView) v.findViewById(R.id.oi_quantyty2);
        tvQuantity3 = (TextView) v.findViewById(R.id.oi_quantyty3);
        tvQuantity4 = (TextView) v.findViewById(R.id.oi_quantyty4);
        tvQuantity  = (TextView) v.findViewById(R.id.oi_quantyty);
        b1 = (Button) v.findViewById(R.id.oi_minus);
        b2 = (Button) v.findViewById(R.id.oi_plus);
    }

    public void setFrom(Order order)
    {
        tvName.setText(order.getProductName() + " " + order.getUnit());
        if(order.getID() == 0)  // TODO Colors and name+unit vs separate fields
            tvName.setTextColor(A.COLOR_NEW);
        else
            tvName.setTextColor(A.COLOR_OLD);

        tvQuantity1.setText(String.valueOf(order.getQuantity(1)));
        tvQuantity2.setText(String.valueOf(order.getQuantity(2)));
        tvQuantity3.setText(String.valueOf(order.getQuantity(3)));
        tvQuantity4.setText(String.valueOf(order.getQuantity(4)));
        tvQuantity.setText(String.valueOf(order.getQuantity()));
    }

}


/*
        v.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events
                String DEBUG_TAG = " Gesture";

                int action = MotionEventCompat.getActionMasked(event);
                switch(action) {
                    case (MotionEvent.ACTION_DOWN):
                        Log.d(DEBUG_TAG, "Action was DOWN");
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        Log.d(DEBUG_TAG, "Action was MOVE");
                        return true;
                    case (MotionEvent.ACTION_UP):
                        Log.d(DEBUG_TAG, "Action was UP");
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                        Log.d(DEBUG_TAG, "Action was CANCEL");
                        return true;
                    case (MotionEvent.ACTION_OUTSIDE):
                        Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                                "of current screen element");
                        return true;
                    default:
//                        return super.onTouchEvent(event);
                }

                return true;
            }
        });

 */