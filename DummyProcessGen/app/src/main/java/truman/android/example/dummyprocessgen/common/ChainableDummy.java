package truman.android.example.dummyprocessgen.common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface ChainableDummy extends Chainable {

   String IS_CHAINING_KEY = "is-chaining";
   String CHAIN_ACTIVITIES_KEY = "chain-activities";
   String CHAIN_SERVICES_KEY = "chain-services";
   String KILL_AFTER_KEY = "kill-after";

   boolean DEBUG = true;

   default String getTag() {
      return this.getClass().getSimpleName()+".2ruman";
   }

   default String getNextChainName() {
      String nextChainName = "";
      String regex = "^([^0-9]+)(\\d+)$";
      String input = getClass().getSimpleName();
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(input);

      if (matcher.groupCount() > 1 && matcher.matches()) {
         String prefix = matcher.group(1);
         String suffix = matcher.group(2);

         int suffixNum = Integer.parseInt(suffix);
         nextChainName = createChainName(prefix, ++suffixNum);
      }
      return nextChainName;
   }

   default String createChainName(String prefix, int suffixNum) {
      return getClass().getPackageName() + "." + prefix + suffixNum;
   }

   default void startNextChain(Context context, Consumer<Intent> intentConsumer) {
      String nextChainName = getNextChainName();
      if (DEBUG) {
         Log.d(getTag(), "nextChainName : " + nextChainName);
      }
      try {
         Intent intent = new Intent(context, Class.forName(nextChainName))
                 .putExtra(IS_CHAINING_KEY, true)
                 .putExtra(KILL_AFTER_KEY, shouldKillAfterChain());
         intentConsumer.accept(intent);
      } catch (ClassNotFoundException ignored) {
         if (DEBUG) {
            Log.d(getTag(), "Class not found: " + nextChainName);
         }
      }
   }

   default boolean shouldKillAfterChain() {
      return false;
   }
}
