package home.truman.demo.sdpdatabasedemo;

import android.os.Binder;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class AppUtils {

    public static String getCurrentDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static String getCurrentDateSeq() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    public static String getCurrentFuncName() {
        String funcName = new Exception()
                .getStackTrace()[1]
                .getMethodName();
        return funcName;
    }

    public static String resultToStr(boolean res) {
        if (res) {
            return "Success!";
        } else {
            return "Failed...";
        }
    }

    public static String getPackageName() {
        return AppMain.getContext().getPackageName();
    }

    public static int getUserId() {
        return Binder.getCallingUid() / 100000;
    }
}
