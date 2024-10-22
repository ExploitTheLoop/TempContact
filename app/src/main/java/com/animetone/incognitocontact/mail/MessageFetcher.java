package com.animetone.incognitocontact.mail;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.animetone.incognitocontact.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageFetcher {
    private Context context;
    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private Handler handler;
    private List<Integer> existingMessageIds = new ArrayList<>();
    private static final int POLLING_INTERVAL = 5000; // 30 seconds
    private Runnable fetchMessagesRunnable;

    public MessageFetcher(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
        this.handler = new Handler();

        fetchMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages();
                handler.postDelayed(this, POLLING_INTERVAL); // Re-run after polling interval
            }
        };
    }

    public void startFetchingMessages() {
        handler.post(fetchMessagesRunnable);
    }

    public void stopFetchingMessages() {
        handler.removeCallbacks(fetchMessagesRunnable);
    }

    private void fetchMessages() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", null);
        String domain = sharedPreferences.getString("DOMAIN", null);

        if (username == null || domain == null) {
            Toast.makeText(context, "No email found, please generate one first", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://www.1secmail.com/api/v1/?action=getMessages&login=" + username + "&domain=" + domain;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                ((Activity) context).runOnUiThread(() -> {
                    Toast.makeText(context, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        List<Integer> newMessageIds = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject message = jsonArray.getJSONObject(i);
                            int messageId = message.getInt("id");
                            newMessageIds.add(messageId);

                            // Check if the message is new
                            if (!existingMessageIds.contains(messageId)) {
                                existingMessageIds.add(messageId);
                                sendNewMessageNotification(message.getString("subject")); // Send notification for new message
                            }
                        }

                        // Update the list of known message IDs
                        existingMessageIds.clear();
                        existingMessageIds.addAll(newMessageIds);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ((Activity) context).runOnUiThread(() -> {

                            Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    ((Activity) context).runOnUiThread(() -> {

                        Toast.makeText(context, "Failed to fetch messages", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void sendNewMessageNotification(String messageSubject) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the channel is already created (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("NewMessages", "New Messages", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NewMessages")
                .setSmallIcon(R.drawable.baseline_mark_email_unread_24)  // Add a small icon here
                .setContentTitle("New Message")
                .setContentText("Subject: " + messageSubject)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}
