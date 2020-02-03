package com.truman.demo.userchecker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class NotiManager {

    private static final String TAG = "NotiManager" +  AppMain.getSuffix();

    private static final int MAX_LINES_INBOX = 5;
    private static final int NOTIFICATION_ID = 1;
    private static NotiManager sInstance;
    private Context mContext;
    private String mChannelId; // = NotificationChannel.DEFAULT_CHANNEL_ID;
    private String mChannelName;
    private final Object NB_LOCK = new Object(); // Locker for Notification Build

    private NotificationManager mNotificationManager;
    private Notification.Builder mNotiBuilder;

    private NotiManager() {
        mContext = AppMain.getAppContext();
        mChannelId = mContext.getPackageName() + ".NotiManager";
        mChannelName = AppMain.getName();

        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        prepareChannel();
        init(null, null); // Initialize mNotiBuilder so as to prevent NPE!
    }

    public static synchronized NotiManager getInstance() {
        if (sInstance == null) {
            sInstance = new NotiManager();
        }
        return sInstance;
    }

    private void prepareChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O /* 26 */) {
            NotificationChannel channel = new NotificationChannel(
                    mChannelId,
                    mChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    public NotiManager init(String title, String content) {
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(),
                R.mipmap.ic_launcher);
        int smallIcon = R.mipmap.ic_launcher_round;

        synchronized (NB_LOCK) {
            mNotiBuilder = new Notification.Builder(mContext, mChannelId)
                    .setContentTitle(nullSafe(title))
                    .setContentText(nullSafe(content))
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(smallIcon)
                    //.setTicker("Tick")
                    .setShowWhen(true)
                    //.setWhen(System.currentTimeMillis())
                    //.setAutoCancel(true);
                    //.setNumber(1);
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent);
        }
        return this;
    }

    public void show() {
        synchronized (NB_LOCK) {
            mNotificationManager.notify(NOTIFICATION_ID, mNotiBuilder.build());
        }
    }

    public Notification get() {
        synchronized (NB_LOCK) {
            return mNotiBuilder.build();
        }
    }

    public NotiManager setTitle(String title) {
        synchronized (NB_LOCK) {
            mNotiBuilder.setContentTitle(nullSafe(title));
        }
        return this;
    }

    public NotiManager setContents(String ...contents) {
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();

        int effectiveCnt = 0;
        if (contents != null) {
            for (int i = 0 ; i < contents.length ; i++) {
                if (contents[i] != null && (effectiveCnt++ < MAX_LINES_INBOX)) {
                    inboxStyle.addLine(contents[i]);
                }
            }
        }
        if (effectiveCnt > MAX_LINES_INBOX) {
            inboxStyle.setSummaryText("+" + MAX_LINES_INBOX);
        }
        synchronized (NB_LOCK) {
            mNotiBuilder
                    .setWhen(System.currentTimeMillis())
                    .setStyle(inboxStyle);

        }
        return this;
    }

    public static int getId() {
        return NOTIFICATION_ID;
    }

    public static String nullSafe(String string) {
        if (string == null) {
            return "null";
        }
        return string;
    }
}
