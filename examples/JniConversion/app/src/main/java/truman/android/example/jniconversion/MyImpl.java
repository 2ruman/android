package truman.android.example.jniconversion;

import android.util.Log;

import java.util.ArrayList;

public final class MyImpl {

    private static final String TAG = MyImpl.class.getSimpleName() +  ".2ruman";

    public MyImpl() {
    }

    public void test() {
        getWelcomeMessage_Test();
        getStrList_Test();
        getDataList_Test();
        printStrList_Test();
    }

    public void getWelcomeMessage_Test() {
        Log.d(TAG, "getWelcomeMessage() - Result : " + MyImplNative.getWelcomeMessage());
    }

    public void getStrList_Test() {
        ArrayList<String> list = MyImplNative.getStrList();
        if (list != null) {
            for (String s : list) {
                Log.d(TAG, "getStrList() - Result : " + s);
            }
        }
    }

    public void getDataList_Test() {
        ArrayList<BasicData> list = MyImplNative.getDataList();
        if (list != null) {
            for (BasicData d : list) {
                Log.d(TAG, "getDataList() - Result : [ "
                        + d.iVal1 + ", " + d.iVal2 + ", " + d.lVal1 + ", " + d.lVal2 + " ]");
            }
        }
    }

    public void printStrList_Test() {
        Log.d(TAG, "printStrList(null) - Result : " + MyImplNative.printStrList(null));
        ArrayList<String> alphabets = new ArrayList<>();
        alphabets.add("ABC"); alphabets.add("DEF"); alphabets.add("GHI");
        alphabets.add("JKL"); alphabets.add("MNO"); alphabets.add("PQR");
        alphabets.add("STU");alphabets.add("VWX");alphabets.add("YZ");
        Log.d(TAG, "printStrList(alphabets) - Result : " + MyImplNative.printStrList(alphabets));
    }
}
