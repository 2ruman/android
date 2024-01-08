package truman.android.example.expandablelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyDataAdapter extends BaseExpandableListAdapter {

    private final Context mContext;
    private final List<String> mGroup;
    private final Map<String, List<MyData>> mGroupData;
    private final OnCheckedListener mCallback;

    public MyDataAdapter(Context context, OnCheckedListener callback) {
        mContext = context;
        mGroup = new ArrayList<>();
        mGroupData = new LinkedHashMap<>();
        mCallback = callback;
    }

    public void add(String groupName, MyData data) {
        ensureGroup(groupName);
        addToGroup(groupName, data);
    }

    private void ensureGroup(String groupName) {
        if (!mGroup.contains(groupName)) {
            mGroup.add(groupName);
            mGroupData.put(groupName, new ArrayList<>());
        }
    }

    private void addToGroup(String groupName, MyData data) {
        List<MyData> groupData = getGroupData(groupName);
        for (MyData md : groupData) {
            if (md.getData() == data.getData()) {
                return;
            }
        }
        groupData.add(data);
    }

    public MyData getData(int groupPosition, int childPosition) {
        MyData ret = null;
        try {
            ret = (MyData) getChild(groupPosition, childPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Map<String, List<MyData>> getDataAsMap() {
        return Collections.unmodifiableMap(mGroupData);
    }

    private List<MyData> getGroupData(int groupPosition) {
        String groupName = convPositionToGroup(groupPosition);
        return getGroupData(groupName);
    }

    private String convPositionToGroup(int position) {
        String ret;
        try {
            ret = mGroup.get(position);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Group doesn't exist at " + position);
        }
        return ret;
    }

    public int convGroupToPosition(String groupName) {
        return mGroup.indexOf(groupName);
    }

    private List<MyData> getGroupData(String groupName) {
        return mGroupData.get(groupName);
    }

    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<MyData> groupData = getGroupData(groupPosition);
        return groupData.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getGroupName(groupPosition);
    }

    public String getGroupName(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getGroupData(groupPosition).get(childPosition);
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String groupName = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_group, null);
        }
        TextView tvGroup = convertView.findViewById(R.id.tv_group);
        tvGroup.setText(groupName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final MyData data = (MyData) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_child, null);
        }
        CheckBox cbChild = convertView.findViewById(R.id.cb_child);
        cbChild.setOnCheckedChangeListener(null);
        cbChild.setChecked(data.getState());
        cbChild.setOnCheckedChangeListener((v, isChecked) -> {
            data.setState(isChecked);
            if (getCallback() != null) {
                String groupName = getGroupName(groupPosition);
                getCallback().onChecked(groupName, data);
            }
        });

        TextView tvChild = convertView.findViewById(R.id.tv_child);
        tvChild.setText(data.getData());
        return convertView;
    }

    private OnCheckedListener getCallback() {
        return mCallback;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @FunctionalInterface
    public interface OnCheckedListener {
        void onChecked(String groupName, MyData data);
    }
}
