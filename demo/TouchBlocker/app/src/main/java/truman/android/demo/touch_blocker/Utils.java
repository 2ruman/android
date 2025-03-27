package truman.android.demo.touch_blocker;

import android.app.ActivityManager;
import android.content.Context;

public class Utils {

   public static boolean isEven(int number) {
      return (number & 0x01) == 0;
   }

   public static int getProcessImportance(Context context) {
      return getProcessImportance(context, android.os.Process.myPid());
   }

   public static int getProcessImportance(Context context, int pid) {
      ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      if (am != null) {
         for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
               return processInfo.importance;
            }
         }
      }
      return 0;
   }
}