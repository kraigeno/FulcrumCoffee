package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter <Order> {
    private Context context;
    private int resource;
    private ArrayList<Order> list;

    public OrderAdapter(Context context, int resource, ArrayList<Order> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isSection() ? A.TYPE_SECTION : A.TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Order order = list.get(position);
        int type = order.getType();

        if (convertView == null) {
            switch (type) {
                case A.TYPE_SECTION:
                    convertView = new OrderSectionView(getContext());
                    break;
                case A.TYPE_ITEM:
                    convertView = new OrderView(getContext());
                    break;
            }
        }
        switch (type) {
            case A.TYPE_SECTION:
                OrderSectionView orderSectionView = (OrderSectionView) convertView;
                orderSectionView.setFrom(order);
                break;
            case A.TYPE_ITEM:
                OrderView orderView = (OrderView) convertView;
                orderView.setFrom(order);
                break;
        }
        return convertView;
    }
}


