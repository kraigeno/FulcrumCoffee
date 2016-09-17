package com.qmatica.arsen.aglistview;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<Detail> {

    private Context context;
    private int resource;
    private ArrayList<Detail> list;

    public HistoryAdapter(Context context, int resource, ArrayList<Detail> list)
    {
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

        Detail detail = list.get(position);
        int type = detail.getType();

        if (convertView == null) {
            switch (type) {
                case A.TYPE_SECTION:
                    convertView = new DetailSectionView(getContext());
                    break;
                case A.TYPE_ITEM:
                    convertView = new DetailView(getContext());
                    break;
            }
        }
        switch (type) {
            case A.TYPE_SECTION:
                DetailSectionView detailSectionView = (DetailSectionView) convertView;
                detailSectionView.setFrom(detail);
                break;
            case A.TYPE_ITEM:
                DetailView detailView = (DetailView) convertView;
                detailView.setFrom(detail);
                break;
        }

        return convertView;
    }
}
