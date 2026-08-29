import android.os.Bundle;
package com.kirtuux.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.os.Build;
private static final int MIC_REQ = 100;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private static final int MIC_REQ = 100;
    private LinearLayout chat;
    private ScrollView scroll;
    private TextView status;
    private EditText input;
    private AssistantBrain brain;
    private VoiceEngine voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat = findViewById(R.id.chatContainer);
        scroll = findViewById(R.id.scroll);
        status = findViewById(R.id.status);
        input = findViewById(R.id.input);
        Button send = findViewById(R.id.send);
        ImageButton mic = findViewById(R.id.mic);
        Button startWake = findViewById(R.id.startWake);
        Button stopWake = findViewById(R.id.stopWake);

        brain = new AssistantBrain();
        voice = new VoiceEngine(this, new VoiceEngine.Listener() {
            @Override public void onText(String text) {
                input.setText(text);
                sendMessage(text);
            }
            @Override public void onStatus(String s) { runOnUiThread(() -> status.setText(s)); }
            @Override public void onError(String e) { addMessage("Kirtuux", e, false); }
        });

        addMessage("Kirtuux", "Namaste Boss 👋 Main ready hoon. Mujhse Hindi, Marathi ya English mein baat karo.", false);

        send.setOnClickListener(v -> sendMessage(input.getText().toString().trim()));
private static final int PERMISSION_REQ = 200;
        mic.setOnClickListener(v -> {
            if (checkMic()) voice.startListening(false);
        });

        startWake.setOnClickListener(v -> {
            if (checkMic()) {
                Intent i = new Intent(this, VoiceService.class);
                ContextCompat.startForegroundService(this, i);
                status.setText("WAKE MODE");
            }
        });

        stopWake.setOnClickListener(v -> {
            stopService(new Intent(this, VoiceService.class));
            status.setText("READY");
        });
    }

    private boolean checkMic() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, MIC_REQ);
            return false;
        }
        return true;
    }

    private void sendMessage(String text) {
        if (text.isEmpty()) return;
        addMessage("You", text, true);
        input.setText("");
        status.setText("THINKING...");
        brain.send(text, new AssistantBrain.ReplyCallback() {
            @Override public void onReply(String reply) {
                addMessage("Kirtuux", reply, false);
                status.setText("READY");
                voice.speak(reply);
            }
            @Override public void onError(String error) {
                addMessage("Kirtuux", error, false);
                status.setText("ERROR");
            }
        });
    }

    private void addMessage(String who, String text, boolean user) {
        runOnUiThread(() -> {
            TextView tv = new TextView(this);
            tv.setText(who + "\n\n" + text);
            tv.setTextSize(16);
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(18, 14, 18, 14);
            tv.setGravity(Gravity.START);
            tv.setBackgroundResource(user ? R.drawable.bg_user_message : R.drawable.bg_message);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 8, 0, 8);
            tv.setLayoutParams(lp);
            chat.addView(tv);
            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    @Override protected void onDestroy() {
        voice.destroy();
        super.onDestroy();
    }
}
addMessage("Kirtuux", "Namaste Boss 👋 Main ready hoon. Mujhse Hindi, Marathi ya English mein baat karo.", false);
requestRuntimePermissions();
private void requestRuntimePermissions() {
    java.util.ArrayList<String> permissions = new java.util.ArrayList<>();

    // Camera
    permissions.add(Manifest.permission.CAMERA);

    // Microphone
    permissions.add(Manifest.permission.RECORD_AUDIO);

    // Calendar
    permissions.add(Manifest.permission.READ_CALENDAR);
    permissions.add(Manifest.permission.WRITE_CALENDAR);

    // Contacts
    permissions.add(Manifest.permission.READ_CONTACTS);
    permissions.add(Manifest.permission.WRITE_CONTACTS);

    // Location
    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

    // Phone
    permissions.add(Manifest.permission.CALL_PHONE);
    permissions.add(Manifest.permission.READ_PHONE_STATE);
    permissions.add(Manifest.permission.READ_PHONE_NUMBERS);
    permissions.add(Manifest.permission.ANSWER_PHONE_CALLS);

    // Call log
    permissions.add(Manifest.permission.READ_CALL_LOG);

    // SMS
    permissions.add(Manifest.permission.READ_SMS);
    permissions.add(Manifest.permission.SEND_SMS);
    permissions.add(Manifest.permission.RECEIVE_SMS);

    // Notifications - Android 13+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
        permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
    }

    // Bluetooth - Android 12+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        permissions.add(Manifest.permission.BLUETOOTH_SCAN);
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
    }

    java.util.ArrayList<String> needRequest = new java.util.ArrayList<>();

    for (String permission : permissions) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            needRequest.add(permission);
        }
    }

    if (!needRequest.isEmpty()) {
        ActivityCompat.requestPermissions(
                this,
                needRequest.toArray(new String[0]),
                PERMISSION_REQ
        );
    }
}

