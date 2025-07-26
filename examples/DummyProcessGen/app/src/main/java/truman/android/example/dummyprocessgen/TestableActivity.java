package truman.android.example.dummyprocessgen;

import static android.view.Gravity.BOTTOM;
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

public abstract class TestableActivity extends AppCompatActivity {

   private static final float BUTTON_CONTAINER_PERCENT_HEIGHT = 0.4F;

   private TextView tvStatus;

   protected abstract List<Button> getTestButtons();

   private ViewGroup ensureRoot() {
      ViewGroup root = findViewById(R.id.main);
      if (!(root instanceof ConstraintLayout)) {
         throw new IllegalStateException();
      }
      return root;
   }

   private void disableAllChildren(ViewGroup parent) {
      for (int i = 0 ; i < parent.getChildCount() ; i++) {
         parent.getChildAt(i).setVisibility(View.GONE);
      }
   }

   protected void initViews() {
      initViews(false);
   }

   protected void initViews(boolean scrollable) {
      if (scrollable) {
         initViewsInternal(1.0F - BUTTON_CONTAINER_PERCENT_HEIGHT,
                 BUTTON_CONTAINER_PERCENT_HEIGHT);
      } else {
         initViewsInternal(0.0F, 0.0F);
      }
   }

   private void initViewsInternal(float topPercentHeight, float bottomPercentHeight) {
      if (!ensureButtons()
              || !ensurePercentHeight(topPercentHeight)
              || !ensurePercentHeight(bottomPercentHeight)) {
         return;
      }
      disableAllChildren(ensureRoot());
      initHalfAndHalf(topPercentHeight, bottomPercentHeight);
   }

   private boolean ensureButtons() {
      return !getTestButtons().isEmpty();
   }

   private boolean ensurePercentHeight(float percentHeight) {
      return (percentHeight >= 0.0F && percentHeight < 1.0F);
   }

   private void initHalfAndHalf(float topPercentHeight, float bottomPercentHeight) {
      int topId = View.generateViewId();
      int bottomId = View.generateViewId();
      View topView = initTop(topId, bottomId, topPercentHeight);
      View bottomView = initBottom(topId, bottomId, bottomPercentHeight);
      ensureRoot().addView(topView);
      ensureRoot().addView(bottomView);
   }

   private View initTop(int topId, int bottomId, float percentHeight) {
      return initStatusView(topId, bottomId, percentHeight);
   }

   private View initStatusView(int topId, int bottomId, float percentHeight) {
      TextView tv = new TextView(this);
      tv.setId(topId);
      tv.setGravity(BOTTOM);
      tv.setMovementMethod(new ScrollingMovementMethod());
      ConstraintLayout.LayoutParams tvParams = new ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
      tvParams.startToStart = PARENT_ID;
      tvParams.endToEnd = PARENT_ID;
      tvParams.topToTop = PARENT_ID;
      tvParams.bottomToTop = bottomId;
      tvParams.height = 0;
      if (percentHeight > 0.0F) {
         tvParams.matchConstraintPercentHeight = percentHeight;
      }
      tv.setLayoutParams(tvParams);
      return tvStatus = tv;
   }

   private View initBottom(int topId, int bottomId, float percentHeight) {
      View buttonContainer = initButtonContainer(topId, bottomId, getTestButtons());
      return (percentHeight > 0.0F) ? scrollWrapped(buttonContainer, percentHeight) : buttonContainer;
   }

   private View initButtonContainer(int topId, int bottomId, List<Button> buttons) {
      LinearLayout linearLayout = new LinearLayout(this);
      linearLayout.setOrientation(VERTICAL);
      linearLayout.setId(bottomId);
      ConstraintLayout.LayoutParams llParams = new ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
      llParams.startToStart = PARENT_ID;
      llParams.endToEnd = PARENT_ID;
      llParams.bottomToBottom = PARENT_ID;
      llParams.topToBottom = topId;
      linearLayout.setLayoutParams(llParams);
      for (Button button : buttons) {
         linearLayout.addView(button);
      }
      return linearLayout;
   }

   private View scrollWrapped(View innerView, float percentHeight) {
      int origViewId = innerView.getId();
      innerView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
      innerView.setId(View.generateViewId());

      ScrollView scrollView = new ScrollView(this);
      scrollView.setId(origViewId);
      ConstraintLayout.LayoutParams svParams = new ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
      svParams.startToStart = PARENT_ID;
      svParams.endToEnd = PARENT_ID;
      svParams.bottomToBottom = PARENT_ID;
      svParams.height = 0;
      svParams.matchConstraintPercentHeight = percentHeight;
      scrollView.setLayoutParams(svParams);
      scrollView.addView(innerView);
      return scrollView;
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

   protected void print(String s) {
      runOnUiThread(() -> {
         if (tvStatus != null) {
            tvStatus.append(nullSafe(s));
         }
      });
   }

   protected void println(String s) {
      runOnUiThread(() -> {
         if (tvStatus != null) {
            tvStatus.append(nullSafe(s) + System.lineSeparator());
         }
      });
   }

   protected void clear() {
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