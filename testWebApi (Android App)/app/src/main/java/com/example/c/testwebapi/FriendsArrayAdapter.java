package com.example.c.testwebapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendsArrayAdapter extends ArrayAdapter<Data.Friend> {
    private final Context context;
    private final List<Data.Friend> values;

    public FriendsArrayAdapter(Context context, List<Data.Friend> values) {
        super(context, R.layout.listviewslayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewslayout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.rowTextview);
        TextView textView2 = (TextView) rowView.findViewById(R.id.rowTextview2);
        TextView textView3 = (TextView) rowView.findViewById(R.id.rowTextview3);


        textView.setText(values.get(position).username);
        textView2.setText(values.get(position).fname);
        textView3.setText(values.get(position).lname);

        return rowView;
    }
}