package truman.android.example.example_base;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

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
        Ui.setOut(this);
    }

    @Override
    protected List<Button> getTestButtons() {
        return List.of(
//                getButton("Test - 1", this::test1),
//                getButton("Test - 2", this::test2),
//                getButton("Test - 3", this::test3),
//                getButton("Test - 1", this::test1),
//                getButton("Test - 2", this::test2),
//                getButton("Test - 3", this::test3),
//                getButton("Test - 1", this::test1),
//                getButton("Test - 2", this::test2),
//                getButton("Test - 3", this::test3),
                getButton("Test - 1", this::test1),
                getButton("Test - 2", this::test2),
                getButton("Test - 3", this::test3)
        );
    }

    private void test1() {
        println("test1()");
    }

    private void test2() {
        println("test2()");
    }

    private void test3() {
        Ui.println("test3()");
    }
}