package com.kirtuux.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceEngine {
    public interface Listener {
        void onText(String text);
        void onStatus(String status);
        void onError(String error);
    }

    private final Context context;
    private final Listener listener;
    private SpeechRecognizer recognizer;
    private TextToSpeech tts;

    public VoiceEngine(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("hi", "IN"));
                tts.setSpeechRate(0.98f);
            }
        });
    }

    public void startListening(boolean wakeMode) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            listener.onError("Is device par Speech Recognition available nahi hai.");
            return;
        }
        stopListening();
        recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) { listener.onStatus("LISTENING"); }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() { listener.onStatus("PROCESSING"); }
            @Override public void onResults(Bundle results) {
                ArrayList<String> r = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (r != null && !r.isEmpty()) listener.onText(r.get(0));
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
            @Override public void onError(int error) { listener.onStatus("READY"); }
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizer.startListening(intent);
    }

    public void stopListening() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.destroy();
            recognizer = null;
        }
        listener.onStatus("READY");
    }

    public void speak(String text) {
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kirtuux_reply");
    }

    public void destroy() {
        stopListening();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
