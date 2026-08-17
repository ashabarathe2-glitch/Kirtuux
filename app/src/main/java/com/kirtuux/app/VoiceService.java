package com.kirtuux.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class VoiceService extends Service {
    private static final String CHANNEL = "kirtuux_voice";
    private VoiceEngine voice;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification n = new NotificationCompat.Builder(this, CHANNEL)
                .setContentTitle("Kirtuux")
                .setContentText("Voice mode active")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();
        startForeground(7, n);

        voice = new VoiceEngine(this, new VoiceEngine.Listener() {
            @Override public void onText(String text) {
                String s = text.toLowerCase();
                if (s.contains("hey kirtuux") || s.contains("kirtuux") || s.contains("हे किर्तूक्स")) {
                    voice.speak("Haan Boss, bolo.");
                }
                // SpeechRecognizer is intentionally not kept permanently open here.
                // Android may stop/restrict long-running recognition; restart logic can be
                // added with a dedicated wake-word engine for production.
            }
            @Override public void onStatus(String status) {}
            @Override public void onError(String error) {}
        });
        voice.startListening(true);
    }

    private void createChannel() {
        NotificationChannel c = new NotificationChannel(CHANNEL, "Kirtuux Voice",
                NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(c);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (voice != null) voice.destroy();
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
