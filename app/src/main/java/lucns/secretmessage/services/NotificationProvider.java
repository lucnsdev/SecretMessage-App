package lucns.secretmessage.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;

import lucns.secretmessage.R;

public class NotificationProvider {

    private final int NOTIFICATION_CODE = 1234;
    private final String silent = "Silent";
    private final String alert = "Alert";
    private final Context context;
    private final NotificationManager notificationManager;
    private Notification notification;
    private boolean isShowing;
    private Class<?> activityClass;

    public NotificationProvider(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannels();
    }

    private void createChannels() {
        NotificationChannel builderChannel = new NotificationChannel(silent, "Silent", NotificationManager.IMPORTANCE_DEFAULT);
        builderChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        builderChannel.enableLights(false);
        builderChannel.enableVibration(false);
        builderChannel.setSound(null, null);
        notificationManager.createNotificationChannel(builderChannel);

        AudioAttributes.Builder audioAttributes = new AudioAttributes.Builder();
        audioAttributes.setAllowedCapturePolicy(AudioAttributes.ALLOW_CAPTURE_BY_ALL);
        audioAttributes.setLegacyStreamType(AudioManager.STREAM_NOTIFICATION);
        audioAttributes.setUsage(AudioAttributes.USAGE_NOTIFICATION);

        builderChannel = new NotificationChannel(alert, context.getString(R.string.notification_alert), NotificationManager.IMPORTANCE_HIGH);
        builderChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        builderChannel.enableLights(true);
        builderChannel.enableVibration(true);
        builderChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.music_box), audioAttributes.build());
        builderChannel.setLightColor(Color.argb(255, 255, 255, 255));
        builderChannel.setVibrationPattern(new long[]{250, 250, 250, 250});
        notificationManager.createNotificationChannel(builderChannel);
    }

    public int getNotificationCode() {
        return NOTIFICATION_CODE;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setActivityClass(Class<?> activityToOpen) {
        activityClass = activityToOpen;
    }

    public void showAlert(int title, int text, int sub) {
        show(context.getString(title), context.getString(text), context.getString(sub));
    }

    public void showAlert(String title, String text, String sub) {
        isShowing = true;
        PendingIntent pendingIntent = null;
        if (activityClass != null) {
            Intent resultIntent = new Intent(context, activityClass);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }

        Notification.Builder builder = new Notification.Builder(context, alert);
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setShowWhen(true);
        builder.setColorized(false);
        //builder.setColor(context.getColor(R.color.accent));
        builder.setTicker(title);
        builder.setContentTitle(title);
        if (text != null) builder.setContentText(text);
        if (sub != null) builder.setSubText(sub);
        builder.setSmallIcon(Icon.createWithResource(context, R.drawable.icon_secret_message));
        builder.setCategory(Notification.CATEGORY_SERVICE);
        if (pendingIntent != null) builder.setContentIntent(pendingIntent);
        notification = builder.build();
        notificationManager.notify(NOTIFICATION_CODE, notification);
    }

    public void show(int title, int text, int sub) {
        show(context.getString(title), context.getString(text), context.getString(sub));
    }

    public void show(String title, String text, String sub) {
        isShowing = true;
        PendingIntent pendingIntent = null;
        if (activityClass != null) {
            Intent resultIntent = new Intent(context, activityClass);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(resultIntent);
            pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        Notification.Builder builder = new Notification.Builder(context, silent);
        builder.setAutoCancel(true);
        builder.setOngoing(false);
        builder.setShowWhen(true);
        builder.setColorized(false);
        //builder.setColor(context.getColor(R.color.accent));
        builder.setTicker(title);
        builder.setContentTitle(title);
        if (text != null) builder.setContentText(text);
        if (sub != null) builder.setSubText(sub);
        if (pendingIntent != null) builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(Icon.createWithResource(context, R.drawable.icon_secret_message));
        builder.setCategory(Notification.CATEGORY_SERVICE);
        notification = builder.build();
        notificationManager.notify(NOTIFICATION_CODE, notification);
    }

    public void hide() {
        isShowing = false;
        notificationManager.cancelAll();
    }

    public boolean isShowing() {
        return isShowing;
    }
}
