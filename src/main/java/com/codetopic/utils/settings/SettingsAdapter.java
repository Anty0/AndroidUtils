package com.codetopic.utils.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codetopic.utils.R;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@SuppressWarnings("ALL")
public class SettingsAdapter extends BaseAdapter {

    private final LayoutInflater mLayoutInflater;
    private final ArrayList<SettingsItem> mItems = new ArrayList<>();

    public SettingsAdapter(Context context, List<SettingsItem> items) {
        mLayoutInflater = LayoutInflater.from(context);
        mItems.addAll(items);
    }

    public SettingsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public ArrayList<SettingsItem> getItems() {
        return mItems;
    }

    public void setItems(ArrayList<SettingsItem> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.item_settings, parent, false);// TODO: 25.3.16 use view holder

        SettingsItem item = mItems.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView text = (TextView) convertView.findViewById(R.id.textViewDescription);

        title.setText(item.getTitle());
        text.setText(item.getDescription());

        return convertView;
    }

}
