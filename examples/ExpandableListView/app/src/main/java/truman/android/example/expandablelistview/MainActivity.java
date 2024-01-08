package truman.android.example.expandablelistview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ExpandableListView mExpandableListView;
    private MyDataAdapter mAdapter;
    private TextView mTvStatus;
    private Button mBtTest;
    private Button mBtFunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvStatus = findViewById(R.id.tv_status);

        mBtTest = findViewById(R.id.bt_test);
        mBtTest.setOnClickListener((v) -> test());

        mBtFunc = findViewById(R.id.bt_func);
        mBtFunc.setOnClickListener((v) -> func());

        MyDataAdapter.OnCheckedListener callback = (groupName, data) -> {
            print(String.format(
                    "%s from %s is %s!\n",
                    data.getData(), groupName, data.getState() ? "selected" : "unselected"));
        };
        mAdapter = new MyDataAdapter(this, callback);

        mExpandableListView = findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setOnGroupExpandListener((groupPosition) -> {
            String groupName = mAdapter.getGroupName(groupPosition);
            print(String.format(
                    "%s expanded!\n", groupName));
        });
        mExpandableListView.setOnGroupCollapseListener((groupPosition) -> {
            String groupName = mAdapter.getGroupName(groupPosition);
            print(String.format(
                    "%s collapsed!\n", groupName));
        });
        mExpandableListView.setOnChildClickListener((p, v, groupPosition, childPosition, id) -> {
            Toast.makeText(this, "Wow!!!", Toast.LENGTH_SHORT).show();
            String groupName = mAdapter.getGroupName(groupPosition);
            MyData data = mAdapter.getData(groupPosition, childPosition);
            print(String.format(
                    "%s from %s clicked!\n", data.getData(), groupName));
            return false;
        });
        Tester.fillData(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void print(String s) {
        runOnUiThread(() -> {
            mTvStatus.setText(s);
        });
    }

    private void test() {
        String groupName = "D Class";
        String data = "Who are you?";
        runOnUiThread(() -> {
            mAdapter.add(groupName, new MyData(data, true));
            mAdapter.notifyDataSetChanged();

            int position = mAdapter.convGroupToPosition(groupName);
            mExpandableListView.expandGroup(position, true);
        });
    }

    private void func() {
        Map<String, List<MyData>> dataMap = mAdapter.getDataAsMap();
        runOnUiThread(() -> {
            mTvStatus.setText("");
            dataMap.forEach((group, groupData) -> {
                mTvStatus.append(String .format("[ %s ]\n", group));
                groupData.forEach((data) -> {
                    mTvStatus.append(String.format(" - %s (%s)\n",
                            data.getData(),
                            data.getState() ? "v" : " "));
                });
            });
        });
    }
}