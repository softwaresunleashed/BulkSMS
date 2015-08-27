package com.unleashed.android.expandablelistview;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.unleashed.android.bulksms1.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gupta on 8/5/2015.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private int[] groupStatus;


    public ExpandableListAdapter(Context context,
                                 ExpandableListView pExpandableListView,
                                 List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

        groupStatus = new int[_listDataHeader.size()];

        pExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int position) {
                groupStatus[position] = 1;
            }

        });

        pExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int position) {
                groupStatus[position] = 0;
            }
        });

    }


    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    class GroupHolder {
        ImageView img;
        TextView title;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        GroupHolder groupHolder;

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_group, null);

            groupHolder = new GroupHolder();
            groupHolder.img = (ImageView) convertView.findViewById(R.id.tag_img);
            groupHolder.title = (TextView) convertView.findViewById(R.id.lblListHeader);

            convertView.setTag(groupHolder);
        }else{
            groupHolder =(GroupHolder)convertView.getTag();
        }

        // Change the arrow icon according to group's expand / collapse state
        if (groupStatus[groupPosition] == 0) {
            groupHolder.img.setImageResource(R.drawable.group_down);
        } else {
            groupHolder.img.setImageResource(R.drawable.group_up);
        }

        groupHolder.title.setText(headerTitle);


        return convertView;
    }

    class ChildHolder {
        TextView title;
    }
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder;

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_item, null);

            childHolder = new ChildHolder();
            childHolder.title  = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            convertView.setTag(childHolder);

        }else{
            childHolder = (ChildHolder)convertView.getTag();
        }

        childHolder.title.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
