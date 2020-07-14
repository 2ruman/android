package com.truman.demo.dbcache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 * Version : 1.0.0
 */
class DatabaseCache {

    private static final String TAG_SUFFIX = ".2ruman"; // For grep
    private static final String TAG = "DatabaseCache" +  TAG_SUFFIX;
    private static final boolean DEBUG = true;

    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "test_table";
    private static final String COLUMN_KEY = "name";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_VALUE = "value";

    private static final int MAX_CACHE_SIZE = 30;
    private static final int INT_CACHE_SIZE = MAX_CACHE_SIZE / 3;

    private final HashMap<String, String> mCache =
            new LinkedHashMap<String, String>(INT_CACHE_SIZE, 0.75f, true) {
        private static final long serialVersionUID = -6538574977717884266L;
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return (size() >= MAX_CACHE_SIZE);
        }
    };

    private final DatabaseHelper mDatabaseHelper;

    public DatabaseCache(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
    }

    public void putBoolean(int userId, String key, boolean value) {
        putInternal(userId, key, value ? "1" : "0");
    }

    public void putInt(int userId, String key, int value) {
        putInternal(userId, key, String.valueOf(value));
    }

    public void putLong(int userId, String key, long value) {
        putInternal(userId, key, String.valueOf(value));
    }

    public void putString(int userId, String key, String value) {
        putInternal(userId, key, value);
    }

    private void putInternal(int userId, String key, String value) {
        // Null value means none of data
        if (key == null || value == null) {
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_KEY, key);
        cv.put(COLUMN_USER, userId);
        cv.put(COLUMN_VALUE, value);

        boolean success = false;
        SQLiteDatabase db = null;
        try {
            db = mDatabaseHelper.getWritableDatabase();

            db.beginTransaction();
            db.delete(TABLE,  COLUMN_KEY + "=? AND " + COLUMN_USER + "=?",
                    new String[] { key, Integer.toString(userId) });
            db.insert(TABLE, null, cv);
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            // Runtime exception such as SQLException could be thrown due to file system problem if
            // the software is used for system database, then it could be critical, as is not predictable.
            reportError("put", e);
        } finally {
            if (db != null) db.endTransaction();
        }
        if (success) {
            cache(userId, key, value);
        }
    }

    public boolean getBoolean(int userId, String key, boolean defaultValue) {
        String val = getInternal(userId, key);
        return "1".equals(val) || (!"0".equals(val) && defaultValue);
    }

    public int getInt(int userId, String key, int defaultValue) {
        int ret = defaultValue;
        String val;
        try {
            if ((val = getInternal(userId, key)) != null) {
                ret = Integer.parseInt(val);
            }
        } catch (NumberFormatException ignored) {
        }
        return ret;
    }

    public long getLong(int userId, String key, long defaultValue) {
        long ret = defaultValue;
        String val;
        try {
            if ((val = getInternal(userId, key)) != null) {
                ret = Long.parseLong(val);
            }
        } catch (NumberFormatException ignored) {
        }
        return ret;
    }

    public String getString(int userId, String key, String defaultValue) {
        String val = getInternal(userId, key);
        return val != null ? val : defaultValue;
    }

    private String getInternal(int userId, String key) {
        String ret = hitOrNull(userId, key);
        if (ret != null) {
            return ret;
        }

        boolean success = false;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            if ((cursor = db.query(TABLE, new String[] { COLUMN_VALUE },
                    COLUMN_KEY + "=? AND " + COLUMN_USER + "=?",
                    new String[] { key, Integer.toString(userId) },
                    null, null, null)) != null) {
                success = (cursor.moveToFirst() && (ret = cursor.getString(0)) != null);
            }
        } catch (Exception e) {
            // Runtime exception such as SQLException could be thrown due to file system problem if
            // the software is used for system database, then it could be critical, as is not predictable.
            reportError("get", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        if (success) {
            cache(userId, key, ret);
        }
        return ret;
    }

    public void delete(int userId, String key) {
        boolean success = false;
        SQLiteDatabase db = null;
        try {
            db = mDatabaseHelper.getWritableDatabase();

            db.beginTransaction();
            db.delete(TABLE,  COLUMN_KEY + "=? AND " + COLUMN_USER + "=?",
                    new String[] { key, Integer.toString(userId) });
            db.setTransactionSuccessful();
            success = true;
        } catch (Exception e) {
            // Runtime exception such as SQLException could be thrown due to file system problem if
            // the software is used for system database, then it could be critical, as is not predictable.
            reportError("del", e);
        } finally {
            if (db != null) db.endTransaction();
        }
        if (success) {
            decache(userId, key);
        }
    }

    private String hitOrNull(int userId, String key) {
        String tag = makeTag(userId, key);
        synchronized (mCache) {
            if (mCache.containsKey(tag)) {
                LogD("hit - [ Tag : " + tag + " ]");
                return mCache.get(tag);
            }
        }
        return null;
    }

    private void cache(int userId, String key, String value) {
        String tag = makeTag(userId, key);
        synchronized (mCache) {
            LogD("cache - [ Tag : " + tag + ", Val : " + value + " ]");
            mCache.put(tag, value);
        }
    }

    private void decache(int userId, String key) {
        String tag = makeTag(userId, key);
        synchronized (mCache) {
            if (mCache.containsKey(tag)) {
                LogD("decache - [ Tag : " + tag + " ]");
                mCache.remove(tag);
            }
        }
    }

    public void preload(int userId) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            if ((cursor = db.query(TABLE,
                    new String[]{COLUMN_KEY, COLUMN_VALUE},
                    COLUMN_USER + "=?",
                    new String[]{Integer.toString(userId)},
                    null, null, null)) != null) {
                while (cursor.moveToNext()) {
                    String key = cursor.getString(0);
                    String value = cursor.getString(1);
                    putInternal(userId, key, value);
                }
            }
        } catch (Exception e) {
            // Runtime exception such as SQLException could be thrown due to file system problem if
            // the software is used for system database, then it could be critical, as is not predictable.
            reportError("preload", e);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    public void destroy(int userId) {
        boolean noError = false;
        SQLiteDatabase db = null;
        try {
            db = mDatabaseHelper.getWritableDatabase();

            db.beginTransaction();
            db.delete(TABLE, COLUMN_USER + "='" + userId + "'", null);
            db.setTransactionSuccessful();
            noError = true;
        } catch (Exception e) {
            // Runtime exception such as SQLException could be thrown due to file system problem if
            // the software is used for system database, then it could be critical, as is not predictable.
            reportError("remove", e);
        } finally {
            if (db != null) db.endTransaction();
        }
        if (!noError) {
            return;
        }
        synchronized (mCache) {
            Iterator<String> iter = mCache.keySet().iterator();
            String prefix = String.valueOf(userId);
            while (iter.hasNext()) {
                String key = iter.next(); // Key will not be null
                if (key.startsWith(prefix)) {
                    iter.remove();
                    LogD("remove - Val of key [ " + key + " ]");
                }
            }
        }
    }

    public void dump() {
        if (!DEBUG) {
            return;
        }
        synchronized (mCache) {
            LogD("");
            LogD("[ Dump Cache ]");
            LogD("");
            LogD("Cache Size : " + mCache.size());
            LogD("Cache Max  : " + MAX_CACHE_SIZE);
            LogD("---------------------------------------- START ----------------------------------------");
            /*
             * Below commented code snippet is for lower versions than Java 8.
             * Check the compileOptions from build.gradle.
             *
            for (Map.Entry<String, String> entry : mCache.entrySet()) {
                String tag = entry.getKey();
                String val = entry.getValue();
                LogD("dump - [Tag : " + tag + ", Val : " + val + "]");
            } */

            /* Java 8 and above supports lambda expressions. */
            mCache.forEach((tag, val) -> LogD("dump - [Tag : " + tag + ", Val : " + val + "]"));
            LogD("----------------------------------------- END -----------------------------------------");
        }
    }

    public void clear() {
        synchronized (mCache) {
            mCache.clear();
        }
    }

    private static String makeTag(int userId, String key) {
        return userId + "_" + key;
    }

    private static void reportError(String where, @NonNull Exception e) {
        LogE("Error in " + where + ": " + e.toString());
        e.printStackTrace();
    }

    private static void LogI(@NonNull String msg) {
        Log.i(TAG, msg);
    }

    private static void LogD(@NonNull String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    private static void LogE(@NonNull String msg) {
        Log.e(TAG, msg);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            setWriteAheadLoggingEnabled(true);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogI("DB created! : " + db.getPath());
            db.execSQL("CREATE TABLE " + TABLE + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_KEY + " TEXT," +
                    COLUMN_USER + " INTEGER," +
                    COLUMN_VALUE + " TEXT" +
                    ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            LogI("DB upgraded! : " + oldVersion + " to " + newVersion);
        }
    }
}
