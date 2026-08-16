package lucns.secretmessage.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import lucns.secretmessage.R;
import lucns.secretmessage.utils.Utils;

public class MessageActivity extends Activity {

    private TextView textStatus;
    private ListView listView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textStatus = findViewById(R.id.textStatus);
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Utils.vibrate();
                return false;
            }
        });

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFinishing()) return;
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.hasInternetConnection()) {
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userRef = database.child("secret_message");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        List<Message> messages = new ArrayList<>();
                        while (iterator.hasNext()) {
                            DataSnapshot message = iterator.next();
                            messages.add(new Message(message.getValue(String.class), getTimestamp(message.getKey())));
                        }
                        messages.sort(new Comparator<Message>() {
                            @Override
                            public int compare(Message o1, Message o2) {
                                return Long.compare(o2.timestamp, o1.timestamp);
                            }
                        });
                        updateList(messages.toArray(new Message[0]));
                    } else {
                        textStatus.setText(R.string.no_messages);
                        listView.setVisibility(View.INVISIBLE);
                        textStatus.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    textStatus.setText(R.string.error_connection);
                    listView.setVisibility(View.INVISIBLE);
                    textStatus.setVisibility(View.VISIBLE);
                }
            });
        } else {
            textStatus.setText(R.string.no_connection);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void updateList(Message[] messages) {
        if (messages.length == 0) {
            textStatus.setText(R.string.no_messages);
            listView.setVisibility(View.INVISIBLE);
            textStatus.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        textStatus.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(this);
        View[] viewsHolder = new View[messages.length];
        listView.setAdapter(new ArrayAdapter<Message>(this, R.layout.list_item_message, messages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (viewsHolder[position] == null) {
                    viewsHolder[position] = inflater.inflate(R.layout.list_item_message, null, false);
                    ((TextView) viewsHolder[position].findViewById(R.id.textMessage)).setText(messages[position].text);
                    ((TextView) viewsHolder[position].findViewById(R.id.textDateTime)).setText(getDateTime(messages[position].timestamp));
                }
                return viewsHolder[position];
            }
        });
        listView.setVisibility(View.VISIBLE);
    }

    private String getDateTime(long timestamp) {
        long difference = Instant.now().getEpochSecond() - timestamp;
        if (difference < 60) return getString(R.string.few_seconds);
        else if (difference < 3600) return String.format(Locale.getDefault(), getString(R.string.format_minutes), difference / 60, difference / 60 == 1 ? "" : "s");
        else if (difference < 86400) return String.format(Locale.getDefault(), getString(R.string.format_hours), difference / 3600, difference / 3600 == 1 ? "" : "s");
        else return String.format(Locale.getDefault(), getString(R.string.format_days), difference / 86400, difference / 86400 == 1 ? "" : "s");
    }

    private long getTimestamp(String datetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(datetime, formatter);
        return dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    private static class Message {
        public String text;
        public long timestamp;

        public Message(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
