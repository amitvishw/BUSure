package com.bussure.college;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
public class ListViewAdapter extends ArrayAdapter<String>
{
    ListViewAdapter(Context context,String[] names)
    {
        super(context,R.layout.listview_item,names);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater= LayoutInflater.from(getContext());
        View view=layoutInflater.inflate(R.layout.listview_item, parent, false);
        String item=getItem(position);
        String[] s=item.split("==");
        TextView stopName=(TextView)view.findViewById(R.id.tvStopName);
        TextView busNo1=(TextView)view.findViewById(R.id.tvBusNo1);
        TextView busNo2=(TextView)view.findViewById(R.id.tvBusNo2);
        TextView busNo3=(TextView)view.findViewById(R.id.tvBusNo3);
        stopName.setText(s[0]);
        busNo1.setText(s[1]);
        busNo2.setText(s[2]);
        busNo3.setText(s[3]);
        return view;
    }
}
