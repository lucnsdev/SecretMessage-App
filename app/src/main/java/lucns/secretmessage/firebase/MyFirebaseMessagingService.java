package lucns.secretmessage.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import lucns.secretmessage.R;
import lucns.secretmessage.activities.MessageActivity;
import lucns.secretmessage.services.NotificationProvider;
import lucns.secretmessage.utils.AppPreferences;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationProvider notification;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Lucas", "onMessageReceived");
        Map<String, String> map = remoteMessage.getData();
        if (!map.isEmpty()) {
            Log.d("lucas", "Message: " + map.get("message"));
            notification.showAlert(getString(R.string.new_message), map.get("message"), null);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notification = new NotificationProvider(this);
        notification.setActivityClass(MessageActivity.class);
    }

    @Override
    public void onRegistered(@NonNull String installationId) {
        super.onRegistered(installationId);
        Log.d("lucas", "Register ID: " + installationId);
        AppPreferences.setString("fcm_register_id", installationId); // clxdQegRQga0O-niUcmWoN
    }

        /*
        FirebaseMessaging.getInstance().register().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM", "Registered via FID");
            }
        });


        FirebaseInstallations.getInstance().getId().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                showDialogBadConnection();
                return;
            }
        });
         */
}