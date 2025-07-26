package truman.android.example.example_base;

import static android.view.Gravity.BOTTOM;
import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.VERTICAL;

import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class TestableActivity extends AppCompatActivity implements Ui.Out {

   private static final float BUTTON_CONTAINER_PERCENT_HEIGHT = 0.3F;

   private TextView tvStatus;

   protected abstract List<Button> getButtons();

   protected void initViews() {
      initViewsInternal(BUTTON_CONTAINER_PERCENT_HEIGHT);
   }

   private void initViewsInternal(float buttonContainerPercentHeight) {
      if (!ensureButtons()) {
         return;
      }
      disableAllChildren(ensureRoot());
      initTopAndBottomView(buttonContainerPercentHeight);
   }

   private boolean ensureButtons() {
      return !getButtons().isEmpty();
   }

   private ViewGroup ensureRoot() {
      ViewGroup root = findViewById(R.id.main);
      if (!(root instanceof ConstraintLayout)) {
         throw new IllegalStateException();
      }
      return root;
   }

   private void disableAllChildren(ViewGroup parent) {
      for (int i = 0 ; i < parent.getChildCount() ; i++) {
         parent.getChildAt(i).setVisibility(GONE);
      }
   }

   private void initTopAndBottomView(float bottomViewPercentHeight) {
      deployTopAndBottomView(createTopView(), createBottomView(), bottomViewPercentHeight,
              (topView, bottomView) -> {
         ensureRoot().addView(topView);
         ensureRoot().addView(bottomView);
      });
   }

   private View createTopView() {
      TextView tv = new TextView(this);
      tv.setId(View.generateViewId());
      tv.setGravity(BOTTOM);
      tv.setMovementMethod(new ScrollingMovementMethod());
      return tvStatus = tv;
   }

   private View createBottomView() {
      ScrollView scrollView = new ScrollView(this);
      scrollView.setId(View.generateViewId());

      LinearLayout linearLayout = new LinearLayout(this);
      linearLayout.setOrientation(VERTICAL);
      for (Button button : getButtons()) {
         linearLayout.addView(button);
      }
      scrollView.addView(linearLayout);
      return scrollView;
   }

   private void deployTopAndBottomView(View topView, View bottomView, float bvPercentHeight,
                                       BiConsumer<View, View> display) {
      ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
      tvParams.startToStart = PARENT_ID;
      tvParams.endToEnd = PARENT_ID;
      tvParams.topToTop = PARENT_ID;
      tvParams.bottomToTop = bottomView.getId();
      tvParams.height = 0;
      tvParams.matchConstraintPercentHeight = (1.0F - bvPercentHeight);
      topView.setLayoutParams(tvParams);

      ConstraintLayout.LayoutParams bvParams = new ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
      bvParams.startToStart = PARENT_ID;
      bvParams.endToEnd = PARENT_ID;
      bvParams.bottomToBottom = PARENT_ID;
      bvParams.topToBottom = topView.getId();
      bvParams.height = 0;
      bvParams.matchConstraintPercentHeight = bvPercentHeight;
      bottomView.setLayoutParams(bvParams);

      display.accept(topView, bottomView);
   }

   protected Button getButton(String text, Runnable listener) {
      Button button = new Button(this);
      button.setText(text);
      button.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
      if (listener != null) {
         button.setOnClickListener((v) -> listener.run());
      }
      return button;
   }

   @Override
   public void print(String s) {
      runOnUiThread(() -> {
         if (tvStatus != null) {
            tvStatus.append(nullSafe(s));
         }
      });
   }

   @Override
   public void println(String s) {
      runOnUiThread(() -> {
         if (tvStatus != null) {
            tvStatus.append(nullSafe(s) + System.lineSeparator());
         }
      });
   }

   @Override
   public void clear() {
      runOnUiThread(() -> {
         if (tvStatus != null) {
            tvStatus.setText("");
         }
      });
   }

   private static String nullSafe(String s) {
      return s != null ? s : "";
   }
}