package truman.android.example.expandablelistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This implementation is not designed to be multi-thread safe.
 */
public class GroupDataManager<T> {
    final List<String> mGroups;
    final Map<String, List<T>> mGroupData;

    public GroupDataManager() {
        mGroups = new ArrayList<>();
        mGroupData = new LinkedHashMap<>();
    }

    public void add(String group, T data) {
        ensureGroup(group);
        addInner(group, data);
    }

    private void ensureGroup(String group) {
        if (group == null) throw new IllegalArgumentException("Invalid group");

        if (!mGroups.contains(group)) {
            mGroups.add(group);
            mGroupData.put(group, new ArrayList<>());
        }
    }

    private void addInner(String group, T data) {
        if (data == null) throw new IllegalArgumentException("Invalid data");

        List<T> groupData = getGroupDataOf(group);
        for (T gd : groupData) {
            if (gd.equals(data)) {
                return;
            }
        }
        groupData.add(data);
    }

    public int getIndexOf(String group) {
        return mGroups.indexOf(group);
    }

    public int getGroupCount() {
        return mGroups.size();
    }

    public String getGroupAt(int index) {
        return indexToGroup(index);
    }

    private String indexToGroup(int index) {
        String ret;
        try {
            ret = mGroups.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Group doesn't exist at " + index);
        }
        return ret;
    }

    public int getGroupDataCountAt(int index) {
        return getGroupDataAt(index).size();
    }

    public List<T> getGroupDataAt(int index) {
        String group = indexToGroup(index);
        return getGroupDataOf(group);
    }

    public List<T> getGroupDataOf(String group) {
        return mGroupData.get(group);
    }

    public T getDataAt(int groupIndex, int dataIndex) {
        List<T> groupData = getGroupDataAt(groupIndex);
        T data;
        try {
            data = groupData.get(dataIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Data doesn't exist at " + dataIndex);
        }
        return data;
    }

    public Map<String, List<T>> getDataAsMap() {
        return Collections.unmodifiableMap(mGroupData);
    }
}
