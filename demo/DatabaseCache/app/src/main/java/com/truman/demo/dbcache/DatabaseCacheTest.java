package com.truman.demo.dbcache;

import android.content.Context;
import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;

class DatabaseCacheTest {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "DatabaseCacheTest" +  TAG_SUFFIX;

    private static final String BOOLEAN_KEY = "boolean_key";
    private static final String INTEGER_KEY = "integer_key";
    private static final String LONG_KEY = "long_key";
    private static final String STRING_A_KEY = "string_a_key";
    private static final String STRING_B_KEY = "string_b_key";
    private static final String STRING_C_KEY = "string_c_key";
    private static final String STRING_D_KEY = "string_d_key";
    private static final String STRING_E_KEY = "string_e_key";

    private final DatabaseCache mDatabaseCache;

    public DatabaseCacheTest(Context context) {
        mDatabaseCache = new DatabaseCache(context);
    }
    public void test() {
        SecureRandom random = new SecureRandom();

        Log.d(TAG, "Test got Started!");
        mDatabaseCache.dump();

        Log.d(TAG, "Preload Test!");
        mDatabaseCache.preload(3);
        mDatabaseCache.dump();

        Log.d(TAG, "Put Test!");
        mDatabaseCache.putBoolean(1, BOOLEAN_KEY, true);
        mDatabaseCache.putBoolean(2, BOOLEAN_KEY, true);
        mDatabaseCache.putBoolean(3, BOOLEAN_KEY, true);
        mDatabaseCache.putInt(1, INTEGER_KEY, 1001);
        mDatabaseCache.putInt(2, INTEGER_KEY, 1002);
        mDatabaseCache.putInt(3, INTEGER_KEY, 1003);
        mDatabaseCache.putLong(1, LONG_KEY, Long.MAX_VALUE);
        mDatabaseCache.putLong(2, LONG_KEY, Long.MAX_VALUE);
        mDatabaseCache.putLong(3, LONG_KEY, Long.MAX_VALUE);

        mDatabaseCache.putString(1, STRING_A_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(1, STRING_B_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(1, STRING_C_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(1, STRING_D_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(1, STRING_E_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(2, STRING_A_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(2, STRING_B_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(2, STRING_C_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(2, STRING_D_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(2, STRING_E_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(3, STRING_A_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(3, STRING_B_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(3, STRING_C_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(3, STRING_D_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.putString(3, STRING_E_KEY, new BigInteger(128, random).toString());
        mDatabaseCache.dump();

        Log.d(TAG, "DELETE Test!");
        mDatabaseCache.delete(1, BOOLEAN_KEY);
        mDatabaseCache.delete(1, STRING_A_KEY);
        mDatabaseCache.dump();

        Log.d(TAG, "GET Test!");
        Log.d(TAG, "Data for user 0 : " + mDatabaseCache.getBoolean(0, BOOLEAN_KEY, false));
        Log.d(TAG, "Data for user 1 : " + mDatabaseCache.getBoolean(1, BOOLEAN_KEY, false));
        Log.d(TAG, "Data for user 2 : " + mDatabaseCache.getBoolean(2, BOOLEAN_KEY, false));

        Log.d(TAG, "Data for user 0 : " + mDatabaseCache.getInt(0, INTEGER_KEY, -999));
        Log.d(TAG, "Data for user 1 : " + mDatabaseCache.getInt(1, INTEGER_KEY, -999));
        Log.d(TAG, "Data for user 2 : " + mDatabaseCache.getInt(2, INTEGER_KEY, -999));

        Log.d(TAG, "Data for user 0 : " + mDatabaseCache.getLong(0, INTEGER_KEY, Long.MIN_VALUE));
        Log.d(TAG, "Data for user 1 : " + mDatabaseCache.getLong(1, INTEGER_KEY, Long.MIN_VALUE));
        Log.d(TAG, "Data for user 2 : " + mDatabaseCache.getLong(2, INTEGER_KEY, Long.MIN_VALUE));

        Log.d(TAG, "Data for user 0 : " + mDatabaseCache.getString(0, STRING_A_KEY, null));
        Log.d(TAG, "Data for user 1 : " + mDatabaseCache.getString(1, STRING_A_KEY, null));
        Log.d(TAG, "Data for user 2 : " + mDatabaseCache.getString(2, STRING_A_KEY, null));

//        Log.d(TAG, "CLEAR Test!");
//        mDatabaseCache.clear();
//        mDatabaseCache.dump();
    }

    public void reset() {
        mDatabaseCache.dump();
        mDatabaseCache.destroy(1);
        mDatabaseCache.destroy(2);
        mDatabaseCache.destroy(3);
        mDatabaseCache.dump();
    }
}
