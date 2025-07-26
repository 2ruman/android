package truman.android.example.dummyprocessgen.common;

public class Utils {
   public static void killMyself() {
      android.os.Process.killProcess(android.os.Process.myPid());
   }
}
