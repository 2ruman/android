package truman.android.example.sob_service.service;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import truman.android.example.sob_service.R;

public class MyServiceNotification {

   private final static int DEFAULT_SMALL_ICON_ID = R.mipmap.ic_launcher_round;

   private final Context context;
   private final int notificationId;
   private final String notificationChannelId;
   private final String notificationChannelName;

   private Class<?> linkedActivityClass;

   public MyServiceNotification(Context context, Class<?> serviceClass) {
      this.context = context;
      notificationId = 1;
      notificationChannelId = serviceClass.getName();
      notificationChannelName = serviceClass.getSimpleName();

      prepareChannel();
   }

   private void prepareChannel() {
      NotificationChannel channel = new NotificationChannel(
              notificationChannelId, notificationChannelName, IMPORTANCE_DEFAULT);
      channel.setShowBadge(false);

      NotificationManager notificationManager = (NotificationManager) context
              .getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.createNotificationChannel(channel);
   }

   public int getNotificationId() {
      return notificationId;
   }

   public Notification getNotification(String title, String text) {
      return getNotification(title, text, DEFAULT_SMALL_ICON_ID);
   }

   public Notification getNotification(String title, String text, int smallIcon) {
      Notification.Builder builder = getNotificatoinBuilder(title, text, smallIcon);
      if (linkedActivityClass != null) {
         Intent intent = new Intent(context, linkedActivityClass);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                 PendingIntent.FLAG_IMMUTABLE);
         builder.setContentIntent(pendingIntent);
      }
      return builder.build();
   }

   private Notification.Builder getNotificatoinBuilder(String title, String text, int smallIcon) {
      return new Notification.Builder(context, notificationChannelId)
              .setContentTitle(title)
              .setContentText(text)
              .setSmallIcon(smallIcon)
              .setShowWhen(true)
              .setWhen(System.currentTimeMillis());
   }

   public void setLinkedActivityClass(Class<?> linkedActivityClass) {
      this.linkedActivityClass = linkedActivityClass;
   }
}
