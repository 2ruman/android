package home.truman.demo.sdpdatabasedemo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.samsung.android.knox.sdp.SdpDatabase;
import com.samsung.android.knox.sdp.SdpUtil;
import com.samsung.android.knox.sdp.core.SdpCreationParam;
import com.samsung.android.knox.sdp.core.SdpCreationParamBuilder;
import com.samsung.android.knox.sdp.core.SdpEngine;
import com.samsung.android.knox.sdp.core.SdpEngineConstants;
import com.samsung.android.knox.sdp.core.SdpEngineInfo;
import com.samsung.android.knox.sdp.core.SdpException;
/**
 * Author  : Truman
 * Contact : truman.t.kim@gmail.com
 */
public class SdpManager {

    private static final String TAG = "SdpManager" +  AppConstants.TAG_SUFFIX;

    private static final String DEFAULT_PASSWORD = "1234";
    private static final String DEFAULT_TOKEN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234!@";
    private static final String DEFAULT_ENGINE_ALIAS = "SdpDatabaseDemo";
    private static final int DEFAULT_ENGINE_FLAGS = SdpEngineConstants.Flags.SDP_MDFPP;

    public static final String EXTRA_SDP_ENGINE_ID = SdpEngineConstants.Intent.EXTRA_SDP_ENGINE_ID;
    public static final String EXTRA_SDP_ENGINE_STATE = SdpEngineConstants.Intent.EXTRA_SDP_ENGINE_STATE;
    public static final String ACTION_SDP_STATE_CHANGED = SdpEngineConstants.Intent.ACTION_SDP_STATE_CHANGED;

    public static final int STATE_NA = 0;
    public static final int STATE_LOCKED = SdpEngineConstants.State.LOCKED;
    public static final int STATE_UNLOCKED = SdpEngineConstants.State.UNLOCKED;

    private static SdpUtil mSdpUtil;
    private static SdpEngine mSdpEngine;
    private static SdpDatabase mSdpDatabase;

    private boolean initialized = false;
    private static boolean updateDbRequired = true;

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isUpdateDbRequired() {
        return updateDbRequired;
    }

    public boolean init() {
        do {
            if (getUtil() == null) {
                // Never gonna happen...
                break;
            }
            if (getEngine() == null) {
                Log.e(TAG, "init() - Failed to init engine instance");
                break;
            }
            if (getDatabase() == null) {
                Log.e(TAG, "init() - Failed to init database instance");
                break;
            }
            Log.d(TAG, "init() - SdpManager initialized!!!");
            initialized = true;
        } while(false);

        return initialized;
    }

    public boolean prepare() {
        boolean result = false;
        if (!isInitialized()) {
            return result;
        }

        if (result = getEngine().exists(DEFAULT_ENGINE_ALIAS)) {
            Log.d(TAG, "prepare() - Engine already exist! [ " + DEFAULT_ENGINE_ALIAS + " ]");
        } else {
            Log.d(TAG, "prepare() - Engine doesn't exist... [ " + DEFAULT_ENGINE_ALIAS + " ]");
            SdpCreationParam param = new SdpCreationParamBuilder(
                    DEFAULT_ENGINE_ALIAS,
                    DEFAULT_ENGINE_FLAGS).getParam();
            try {
                getEngine().addEngine(param, DEFAULT_PASSWORD, DEFAULT_TOKEN);
                result = true;
                Log.d(TAG, "prepare() - Engine creation success!");
            } catch (SdpException e) {
                Log.e(TAG, "prepare() - Failed to prepare engine : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean lock() {
        boolean result = false;
        if (!isInitialized()) {
            return result;
        }

        SdpEngineInfo info = null;
        try {
            info = getUtil().getEngineInfo(DEFAULT_ENGINE_ALIAS);
        } catch (SdpException e) {
            Log.e(TAG, "lock() - Failed to get engine info : " + e.getMessage());
            e.printStackTrace();
        }

        if (result = (info == null && info.getState() == SdpEngineConstants.State.LOCKED)) {
            Log.d(TAG, "lock() - Engine is already unlocked");
            // return result;
        }
        try {
            getEngine().lock(DEFAULT_ENGINE_ALIAS);
            result = true;
        } catch (SdpException e) {
            Log.e(TAG, "lock() - Failed to lock engine : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public boolean unlock() {
        boolean result = false;
        if (!isInitialized()) {
            return result;
        }

        SdpEngineInfo info = null;
        try {
            info = getUtil().getEngineInfo(DEFAULT_ENGINE_ALIAS);
        } catch (SdpException e) {
            Log.e(TAG, "unlock() - Failed to get engine info : " + e.getMessage());
            e.printStackTrace();
        }

        if (result = (info == null && info.getState() == SdpEngineConstants.State.UNLOCKED)) {
            Log.d(TAG, "unlock() - Engine is already unlocked");
            // return result;
        }
        try {
            getEngine().unlock(DEFAULT_ENGINE_ALIAS, DEFAULT_PASSWORD);
            result = true;
        } catch (SdpException e) {
            Log.e(TAG, "unlock() - Failed to unlock engine : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public int getId() {
        int result = -1;
        if (!isInitialized()) {
            return result;
        }

        SdpEngineInfo info = null;
        try {
            info = getUtil().getEngineInfo(DEFAULT_ENGINE_ALIAS);
            if (info != null) {
                result = info.getId();
            }
        } catch (SdpException e) {
            Log.e(TAG, "getId() - Failed to get engine info : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public int getState() {
        int result = STATE_NA;
        if (!isInitialized()) {
            return result;
        }

        SdpEngineInfo info = null;
        try {
            info = getUtil().getEngineInfo(DEFAULT_ENGINE_ALIAS);
        } catch (SdpException e) {
            Log.e(TAG, "getState() - Failed to get engine info : " + e.getMessage());
            e.printStackTrace();
        }
        result = info == null ? STATE_NA : info.getState() == SdpEngineConstants.State.UNLOCKED ?
                STATE_UNLOCKED : STATE_LOCKED;
        return result;
    }

    public boolean updateStateToDB(SQLiteDatabase db, int state) {
        boolean result = false;
        if (!isInitialized()) {
            return result;
        }
        if (!isUpdateDbRequired()) {
            Log.e(TAG, "Updating DB is not required...");
            return result;
        }
        result = getDatabase().updateStateToDB(db, null, state);
        return result;
    }

    public static synchronized SdpDatabase getDatabase() {
        if (mSdpDatabase == null) {
            try {
                mSdpDatabase = new SdpDatabase(DEFAULT_ENGINE_ALIAS);
            } catch (SdpException e) {
                Log.e(TAG, "getDatabase() - Failed to get database : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mSdpDatabase;
    }

    public static synchronized SdpEngine getEngine() {
        if (mSdpEngine == null) {
            try {
                mSdpEngine = SdpEngine.getInstance();
            } catch (SdpException e) {
                Log.e(TAG, "getEngine() - Failed to get engine instance : " + e.getMessage());
                e.printStackTrace();
            }
        }
        return mSdpEngine;
    }

    public static synchronized SdpUtil getUtil() {
        if (mSdpUtil == null) {
            mSdpUtil = SdpUtil.getInstance();
        }
        return mSdpUtil;
    }
}
