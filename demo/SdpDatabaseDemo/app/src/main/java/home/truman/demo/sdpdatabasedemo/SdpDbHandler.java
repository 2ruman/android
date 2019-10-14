package home.truman.demo.sdpdatabasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.samsung.android.knox.sdp.core.SdpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class SdpDbHandler {

    private static final String TAG = "SdpManager" +  AppConstants.TAG_SUFFIX;

    private static final String DATABASE_NAME = "sdp.db";
    private static final String TABLE_NAME = "s_table";
    private static final String N_COL1 = "n_col1";
    private static final String N_COL2 = "n_col2";
    private static final String S_COL3 = "s_col3";
    private static final String CREATE_STMT =
            "create table if not exists "+TABLE_NAME
            + " (_id integer primary key autoincrement, "
            + N_COL1 + " text not null , "
            + N_COL2 + " text not null , "
            + S_COL3 + " text not null );";
    private static final int DATABASE_VERSION = 1;
    private static final List<String> S_COLS = Arrays.asList(S_COL3);
    private static final List<String> ALL_COLS = Arrays.asList(N_COL1, N_COL2, S_COL3);
    private static final String N_COL1_DATA = "Column 1 has normal data";
    private static final String N_COL2_DATA = "Column 2 has normal data";
    private static final String S_COL3_DATA = "Column 3 has sensitive data";
    private static final int MAX_SELECT_CNT = 200;

    public static SQLiteDatabase mDB;
    private SdpDbHelper mSdpDbHelper;

    private class SdpDbHelper extends SQLiteOpenHelper {

        public SdpDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "onCreate()");
            db.execSQL(CREATE_STMT);
            Log.d(TAG, "onCreate() - is sensitive db? " +  setSensitive(db));
        }

        public void onOpen(SQLiteDatabase db) {
            Log.d(TAG, "onOpen() - is sensitive db? " +  isSensitive(db));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade()");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

        private boolean setSensitive(SQLiteDatabase db) {
            boolean result = false;
            try {
                result = SdpManager.getDatabase().setSensitive(db, null, TABLE_NAME, S_COLS);
            } catch (SdpException e) {
                Log.e(TAG, "Failed to set db sensitive");
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected failure while set db sensitive");
                e.printStackTrace();
            }
            return result;
        }

        private boolean isSensitive(SQLiteDatabase db) {
            boolean result = false;
            try {
                for (String COL : ALL_COLS) {
                    if (SdpManager.getDatabase().isSensitive(db, null, TABLE_NAME, COL)) {
                        Log.d(TAG, "isSensitive() - " + COL + " is sensitive!");
                        result = true;
                    } else {
                        Log.d(TAG, "isSensitive() - " + COL + " is not sensitive...");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Unexpected failure while check db sensitive");
                e.printStackTrace();
            }
            return result;
        }
    }

    public SdpDbHandler(Context context) {
        mSdpDbHelper = new SdpDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SdpDbHandler open() throws SQLException {
        mDB = mSdpDbHelper.getWritableDatabase();

        return this;
    }

    public SQLiteDatabase getDB() {
        if (mDB == null) {
            mDB = mSdpDbHelper.getWritableDatabase();
        }
        return mDB;
    }

    public void insert() {
        mDB = mSdpDbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(N_COL1, N_COL1_DATA);
        value.put(N_COL2, N_COL2_DATA);
        value.put(S_COL3, S_COL3_DATA);

        mDB.insert(TABLE_NAME, null, value);
    }

    public void inserts(int reps) {

        mDB = mSdpDbHelper.getWritableDatabase();

        for (int i = 0 ; i < reps ; i++) {
            ContentValues value = new ContentValues();
            value.put(N_COL1, N_COL1_DATA);
            value.put(N_COL2, N_COL2_DATA);
            value.put(S_COL3, S_COL3_DATA);

            mDB.insert(TABLE_NAME, null, value);
        }
    }

    public int select() {
        mDB = mSdpDbHelper.getWritableDatabase();
        Cursor iCursor = mDB.query(TABLE_NAME, null, null, null, null, null, null);
        int i = 0;
        while (iCursor.moveToNext()) {
            String n_col1 = iCursor.getString(iCursor.getColumnIndex(N_COL1));
            String n_col2 = iCursor.getString(iCursor.getColumnIndex(N_COL2));
            String s_col3 = iCursor.getString(iCursor.getColumnIndex(S_COL3));
            if (i++ <= MAX_SELECT_CNT) {
                Log.d(TAG, String.format("select(%d) - %s : %s, %s : %s, %s : %s",
                        i, N_COL1, n_col1, N_COL2, n_col2, S_COL3, s_col3));
            }
        }
        return i;
    }

    public int delete() {
        mDB = mSdpDbHelper.getWritableDatabase();
        int result = mDB.delete(TABLE_NAME, null, null);
        return result;
    }

    public void close() {
        if (mDB != null) {
            mDB.close();
        }
    }

    public void export() {
        try {
            File externalDir = Environment.getExternalStorageDirectory();
            File dataDir = Environment.getDataDirectory();
            String packageName = AppUtils.getPackageName();
            String databasesPath = "//user//"+AppUtils.getUserId()+"//"+packageName+"//databases";

            if (externalDir.canWrite()) {

                File databasesDir = new File(dataDir, databasesPath);
                Log.d(TAG, "export() - databases path :" + databasesDir.getAbsolutePath());

                if (databasesDir.exists()) {
                    File targetDir = new File(externalDir, AppUtils.getCurrentDateSeq() + "_databases");
                    if (targetDir.exists()) {
                        Log.e(TAG, "target dir already exist...");
                        return;
                    } else if (!targetDir.mkdir()) {
                        Log.e(TAG, "Unable to create target dir...");
                       return;
                    }

                    for (File origFile : databasesDir.listFiles()) {
                        if (origFile.exists()) {
                            String fileName = origFile.getName();
                            File targetFile = new File(targetDir, fileName);
                            Log.d(TAG, "export() - src : " + origFile.getAbsolutePath());
                            Log.d(TAG, "export() - dst : " + targetFile.getAbsolutePath());
                            FileChannel src = new FileInputStream(origFile).getChannel();
                            FileChannel dst = new FileOutputStream(targetFile).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                        }
                    }
                } else {
                    Log.d(TAG, "Databases dir not found... " + databasesDir.getAbsolutePath());
                }
            } else {
                Log.d(TAG, "export() - external storage is not writable...");
            }
        } catch (Exception e) {
            Log.e(TAG, "export() - Failed... " + e.getMessage());
            e.printStackTrace();
        }
    }
}

