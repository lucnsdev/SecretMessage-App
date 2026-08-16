package lucns.secretmessage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import lucns.secretmessage.R;

public class MessageViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_message_view);

        Intent intent = getIntent();
        ((TextView) findViewById(R.id.textMessage)).setText(intent.getStringExtra("message"));
        ((TextView) findViewById(R.id.textDateTime)).setText(intent.getStringExtra("datetime"));
    }
}
