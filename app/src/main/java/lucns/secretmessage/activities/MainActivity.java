package lucns.secretmessage.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

import lucns.secretmessage.R;
import lucns.secretmessage.firebase.FirebaseMessagingSender;
import lucns.secretmessage.utils.AppPreferences;
import lucns.secretmessage.utils.Utils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseMessagingSender sender = new FirebaseMessagingSender(this, new FirebaseMessagingSender.Callback() {
            @Override
            public void onFinish(int responseCode, String responseMessage) {

            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrate();
                Log.d("Lucas", "register id: " + AppPreferences.getString("fcm_register_id"));
                sender.setDestineToken(AppPreferences.getString("fcm_register_id"));
                try {
                    sender.put(new JSONObject().put("content", "Lucas321@ - " + Instant.now().getEpochSecond()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}