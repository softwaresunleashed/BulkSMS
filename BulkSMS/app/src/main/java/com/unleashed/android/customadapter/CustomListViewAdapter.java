package com.unleashed.android.customadapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.unleashed.android.bulksms2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gupta on 7/15/2015.
 */
public class CustomListViewAdapter extends ArrayAdapter<PhoneBookRowItem> {

    private LayoutInflater inflater;
    private int mResource;
    private ViewGroup mParent;
    private PhoneBookRowItem rowItem;


    public ArrayList<PhoneBookRowItem> phoneBookRowItems;
    public ArrayList<PhoneBookRowItem> orig;



    public static class Holder{

        TextView phone_user_name;
        TextView phone_user_number;
        CheckBox phone_user_selected;
        boolean state;
    }

    public CustomListViewAdapter(Context context, int resource, ArrayList<PhoneBookRowItem> objects) {
        super(context, resource, objects);


        this.phoneBookRowItems = objects;

        // Activity class is inherited from Context. So we can typecase context object to Activity
        Activity activity = (Activity) context;
        inflater = activity.getWindow().getLayoutInflater();

        // Keep a record of Resource passed from initialising class
        mResource = resource;

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<PhoneBookRowItem> results = new ArrayList<PhoneBookRowItem>();
                if (orig == null){
                    // Make a copy of phonebook list at a different memory addres
                    // called only once.
                    orig = (ArrayList<PhoneBookRowItem>) phoneBookRowItems.clone();
                }

                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final PhoneBookRowItem g : orig) {
                            if (g.getPhoneUserName().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                }

                oReturn.values = results;

                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                // Applying these operations to adapter
                clear();
                addAll((ArrayList<PhoneBookRowItem>) filterResults.values);

                // Now update the Adapter
                notifyDataSetChanged();
            }
        };
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }



    // getView() method is called every time a row is to be added in the ListView.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        rowItem = getItem(position);
        mParent = parent;

        final Holder holder;

        if (convertView == null) {
            // = createNewView();      //inflater.inflate(mResource, parent, false);
            // Instantiate view
            convertView = inflater.inflate(mResource, mParent, false);


            holder = new Holder();
            holder.phone_user_name = (TextView)convertView.findViewById(R.id.tv_phonename);
            holder.phone_user_name.setText(rowItem.getPhoneUserName());

            holder.phone_user_number = (TextView)convertView.findViewById(R.id.tv_phonenumber);
            holder.phone_user_number.setText(rowItem.getPhoneNumber());

            holder.state = rowItem.getState();

            holder.phone_user_selected = (CheckBox)convertView.findViewById(R.id.checkBox_phonebookcontact);
            holder.phone_user_selected.setChecked(holder.state);


            convertView.setTag(holder);
        }else{
            // Gets the holder pointing to the views
            holder = (Holder) convertView.getTag();

            holder.phone_user_name.setText(rowItem.getPhoneUserName());
            holder.phone_user_number.setText(rowItem.getPhoneNumber());
            holder.state = rowItem.getState();
            holder.phone_user_selected.setChecked(holder.state);
        }

        // Set the color of the list view
        convertView.setBackgroundColor(holder.state ? Color.LTGRAY : Color.WHITE);



        return convertView;
    }



}
