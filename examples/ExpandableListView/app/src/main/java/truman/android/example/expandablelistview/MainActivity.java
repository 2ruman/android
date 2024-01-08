package truman.android.example.expandablelistview;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        mTvStatus.setMovementMethod(new ScrollingMovementMethod());

        mBtTest = findViewById(R.id.bt_test);
        mBtTest.setOnClickListener(v -> test());

        mBtFunc = findViewById(R.id.bt_func);
        mBtFunc.setOnClickListener(v -> func());

        MyDataAdapter.OnCheckedListener callback = (group, data) ->
                print(String.format("%s from %s is %s!",
                        data.get(), group, data.getState() ? "selected" : "unselected"));

        mAdapter = new MyDataAdapter(this, callback);

        mExpandableListView = findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(mAdapter);
        mExpandableListView.setOnGroupExpandListener(groupPosition -> {
            String group = mAdapter.getGroupAt(groupPosition);
            print(String.format("%s expanded!", group));
        });
        mExpandableListView.setOnGroupCollapseListener(groupPosition -> {
            String group = mAdapter.getGroupAt(groupPosition);
            print(String.format("%s collapsed!", group));
        });
        mExpandableListView.setOnChildClickListener((p, v, groupPosition, childPosition, id) -> {
            MyData data = mAdapter.getChildAt(groupPosition, childPosition);
            print(String.format("%s clicked!", data.get()));
            return false;
        });

        Tester.fillData(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void print(String s) {
        runOnUiThread(() -> mTvStatus.setText(s));
    }

    private void test() {
        String group = "E Class";
        String data = "Who are you?";
        runOnUiThread(() -> {
            mAdapter.add(group, new MyData(data, true));
            mAdapter.notifyDataSetChanged();

            int position = mAdapter.getGroupPosition(group);
            mExpandableListView.expandGroup(position, true);
        });
    }

    private void func() {
        Map<String, List<MyData>> dataMap = mAdapter.getDataAsMap();
        runOnUiThread(() -> {
            mTvStatus.setText("");
            dataMap.forEach((group, groupData) -> {
                mTvStatus.append(String.format("[ %s ]", group));
                mTvStatus.append(System.lineSeparator());
                groupData.forEach(data ->
                        mTvStatus.append(String.format(" - %s (%s)%s",
                                data.get(),
                                data.getState() ? "v" : " ",
                                System.lineSeparator())));
            });
        });
    }
}
