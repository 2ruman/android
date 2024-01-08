package truman.android.example.expandablelistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MyDataAdapter extends BaseExpandableListAdapter {

    private final GroupDataManager<MyData> mGroupDataManager;
    private final LayoutInflater mLayoutInflater;
    private final Optional<OnCheckedListener> mCallback;

    public MyDataAdapter(Context context, OnCheckedListener callback) {
        mGroupDataManager = new GroupDataManager<>();
        mLayoutInflater = LayoutInflater.from(context);
        mCallback = Optional.of(callback);
    }

    public void add(String group, MyData data) {
        mGroupDataManager.add(group, data);
    }

    public int getGroupPosition(String group) {
        return mGroupDataManager.getIndexOf(group);
    }

    public Map<String, List<MyData>> getDataAsMap() {
        return mGroupDataManager.getDataAsMap();
    }

    @Override
    public int getGroupCount() {
        return mGroupDataManager.getGroupCount();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroupDataManager.getGroupDataCountAt(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return getGroupAt(groupPosition);
    }

    public String getGroupAt(int groupPosition) {
        return mGroupDataManager.getGroupAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return getChildAt(groupPosition, childPosition);
    }

    public MyData getChildAt(int groupPosition, int childPosition) {
        return mGroupDataManager.getDataAt(groupPosition, childPosition);
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
        String group = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_group, null);
        }
        TextView tvGroup = convertView.findViewById(R.id.tv_group);
        tvGroup.setText(group);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final MyData data = (MyData) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_child, null);
        }

        CheckBox cbChild = convertView.findViewById(R.id.cb_child);
        cbChild.setOnCheckedChangeListener(null);
        cbChild.setChecked(data.getState());
        cbChild.setOnCheckedChangeListener((v, isChecked) -> {
            data.setState(isChecked);
            mCallback.ifPresent(cb -> {
                String group = (String) getGroup(groupPosition);
                cb.onChecked(group, data);
            });
        });
        cbChild.setVisibility(data.isStateful() ? View.VISIBLE : View.INVISIBLE);

        TextView tvChild = convertView.findViewById(R.id.tv_child);
        tvChild.setText(data.get());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @FunctionalInterface
    public interface OnCheckedListener {
        void onChecked(String group, MyData data);
    }
}
