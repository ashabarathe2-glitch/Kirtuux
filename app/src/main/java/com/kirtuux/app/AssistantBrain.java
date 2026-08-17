package com.kirtuux.app;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AssistantBrain {
    public interface ReplyCallback {
        void onReply(String text);
        void onError(String error);
    }

    private final OkHttpClient client = new OkHttpClient();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final List<ChatMessage> history = new ArrayList<>();

    private final String[] models = {
        "gemini-2.5-flash",
        "gemini-2.0-flash",
        "gemini-flash-latest"
    };

    private static final String SYSTEM =
        "Tum Kirtuux ho — ek friendly, intelligent AI assistant. " +
        "Hindi, Marathi aur English naturally mix karke baat karo. " +
        "Warm aur clear raho. Agar sure nahi ho to honestly bolo. " +
        "User ka naam yaad rakho agar conversation mein diya gaya ho.";

    public void send(String userText, ReplyCallback cb) {
        if (BuildConfig.GEMINI_API_KEY == null || BuildConfig.GEMINI_API_KEY.isEmpty()
                || BuildConfig.GEMINI_API_KEY.startsWith("PASTE_")) {
            cb.onError("Gemini API key set nahi hai. GitHub Actions secret GEMINI_API_KEY add karo.");
            return;
        }

        history.add(new ChatMessage("user", userText));
        tryModel(0, cb);
    }

    private void tryModel(int index, ReplyCallback cb) {
        if (index >= models.length) {
            main.post(() -> cb.onError("Sabhi Gemini models abhi busy/error de rahe hain. Thodi der baad try karo."));
            return;
        }

        try {
            JSONObject body = new JSONObject();
            JSONObject system = new JSONObject();
            system.put("parts", new JSONArray().put(new JSONObject().put("text", SYSTEM)));
            body.put("system_instruction", system);

            JSONArray contents = new JSONArray();
            for (ChatMessage m : history) {
                JSONObject c = new JSONObject();
                c.put("role", m.role.equals("assistant") ? "model" : "user");
                c.put("parts", new JSONArray().put(new JSONObject().put("text", m.text)));
                contents.put(c);
            }
            body.put("contents", contents);

            Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/" +
                        models[index] + ":generateContent?key=" + BuildConfig.GEMINI_API_KEY)
                .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    tryModel(index + 1, cb);
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    String raw = response.body() == null ? "" : response.body().string();
                    if (!response.isSuccessful()) {
                        if (response.code() == 429 || response.code() == 503 || response.code() == 500) {
                            tryModel(index + 1, cb);
                        } else {
                            history.remove(history.size() - 1);
                            main.post(() -> cb.onError("Gemini error " + response.code() + ": " + raw));
                        }
                        return;
                    }

                    try {
                        JSONObject root = new JSONObject(raw);
                        String text = root.getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                        history.add(new ChatMessage("assistant", text));
                        main.post(() -> cb.onReply(text));
                    } catch (Exception ex) {
                        tryModel(index + 1, cb);
                    }
                }
            });
        } catch (Exception e) {
            tryModel(index + 1, cb);
        }
    }

    public void clearHistory() {
        history.clear();
    }
}
