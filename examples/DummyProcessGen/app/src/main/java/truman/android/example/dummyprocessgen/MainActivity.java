package truman.android.example.dummyprocessgen;

import static truman.android.example.dummyprocessgen.common.ChainableDummy.CHAIN_ACTIVITIES_KEY;
import static truman.android.example.dummyprocessgen.common.ChainableDummy.CHAIN_SERVICES_KEY;
import static truman.android.example.dummyprocessgen.common.ChainableDummy.IS_CHAINING_KEY;
import static truman.android.example.dummyprocessgen.common.ChainableDummy.KILL_AFTER_KEY;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import truman.android.example.dummyprocessgen.act.DummyActivity0;
import truman.android.example.dummyprocessgen.svc.DummyService0;

public class MainActivity extends TestableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews(false);
        run();
    }

    private void run() {
        boolean shouldChainActivities = getIntent().getBooleanExtra(CHAIN_ACTIVITIES_KEY, false);
        boolean shouldChainServices = getIntent().getBooleanExtra(CHAIN_SERVICES_KEY, false);
        boolean shouldKillAfter = getIntent().getBooleanExtra(KILL_AFTER_KEY, false);
        if (shouldChainActivities) {
            chainActivities(shouldKillAfter);
        }
        if (shouldChainServices) {
            chainServices(shouldKillAfter);
        }
    }

    @Override
    protected List<Button> getTestButtons() {
        return List.of(
                getButton("Chain Activities", this::chainActivities),
                getButton("Chain Services", this::chainServices),
                getButton("Chain All", this::chainAll),
                getButton("Terminate All", this::terminateAll)
        );
    }

    private void chainActivities() {
        chainActivities(false);
    }

    private void chainActivities(boolean shouldKillAfter) {
        startActivity(new Intent(this, DummyActivity0.class)
                .putExtra(IS_CHAINING_KEY, true)
                .putExtra(KILL_AFTER_KEY, shouldKillAfter));
    }

    private void chainServices() {
        chainServices(false);
    }

    private void chainServices(boolean shouldKillAfter) {
        startService(new Intent(this, DummyService0.class)
                .putExtra(IS_CHAINING_KEY, true)
                .putExtra(KILL_AFTER_KEY, shouldKillAfter));
    }

    private void chainAll() {
        chainActivities();
        chainServices();
    }

    private void terminateAll() {
        chainActivities(true);
        chainServices(true);
    }
}