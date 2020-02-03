package com.truman.demo.userchecker;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.Locale;

public class UserChecker {

    public static final String EXTRA_USER_ID = "user_id";

    private static final int UID_BOUNDARY = 100000;

    private UserManager mUserManager;
    private UserHandle mUserHandle;
    private Context mContext;
    private int mUserId;

    public UserChecker(Context context, int userId) {
        mContext = context;
        mUserManager = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
        mUserId = userId;
        mUserHandle = UserHandle.getUserHandleForUid(mUserId * UID_BOUNDARY);
    }

    public String isRunning() throws RuntimeException {
        return String.format(Locale.US,
                "User %d is running? %b",
                mUserId,
                mUserManager.isUserRunning(mUserHandle));
    }

    public String isUserRunningOrStopping() throws RuntimeException {
        return String.format(Locale.US,
                "User %d is running or stopping? %b",
                mUserId,
                mUserManager.isUserRunningOrStopping(mUserHandle));
    }

    public String isUserUnlocked() throws RuntimeException {
        return String.format(Locale.US,
                "User %d is unlocked? %b",
                mUserId,
                mUserManager.isUserUnlocked(mUserHandle));
    }

    public static int getMyUserId() {
        return android.os.Process.myUid() / UID_BOUNDARY;
    }

}
