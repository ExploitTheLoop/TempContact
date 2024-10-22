package com.animetone.incognitocontact.mail.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.customprogresdialog;
import com.animetone.incognitocontact.mail.MessagesAdapter;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessagesAdapter messagesAdapter;
    private List<JSONObject> messagesList = new ArrayList<>();
    private OkHttpClient client;
    private TextView textViewNoMessages;
    private customprogresdialog SpinkitDialouge;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler(); // Handler for periodic updates
    private Runnable fetchMessagesRunnable; // Runnable for fetching messages
    private Set<String> existingMessageIds = new HashSet<>(); // Set to track existing message IDs

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        textViewNoMessages = view.findViewById(R.id.textViewNoMessages);
        messagesAdapter = new MessagesAdapter(this, messagesList);
        recyclerView.setAdapter(messagesAdapter);

        client = new OkHttpClient();
        SpinkitDialouge = new customprogresdialog(getActivity());

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutMessages);
        swipeRefreshLayout.setOnRefreshListener(this::fetchMessages);
        SpinkitDialouge.show();

        fetchMessages(); // Initial fetch
        startPeriodicFetch(); // Start periodic fetching

        return view;
    }

    private void startPeriodicFetch() {
        fetchMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages();
                handler.postDelayed(this, 5000); // Fetch messages every 5 seconds
            }
        };
        handler.post(fetchMessagesRunnable); // Start the runnable
    }

    private void fetchMessages() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", null);
        String domain = sharedPreferences.getString("DOMAIN", null);

        if (username == null || domain == null) {
            textViewNoMessages.setText("No email found, please generate one first");
            textViewNoMessages.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
            textViewNoMessages.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String url = "https://www.1secmail.com/api/v1/?action=getMessages&login=" + username + "&domain=" + domain;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                    SpinkitDialouge.dismiss();
                    textViewNoMessages.setText("Failed to fetch messages");
                    textViewNoMessages.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
                    textViewNoMessages.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        List<JSONObject> newMessagesList = new ArrayList<>();
                        boolean newMessagesFound = false;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject message = jsonArray.getJSONObject(i);
                            String messageId = message.getString("id");
                            if (!existingMessageIds.contains(messageId)) {
                                newMessagesFound = true; // New message found
                                existingMessageIds.add(messageId); // Add new message ID to the set
                                newMessagesList.add(message);
                            }
                        }

                        if (newMessagesFound) {
                            // Add new messages to the front of the existing list
                            messagesList.addAll(0, newMessagesList);
                            // Optionally reverse the list if needed
                           // Collections.reverse(messagesList);
                        }

                        getActivity().runOnUiThread(() -> {
                            messagesAdapter.notifyDataSetChanged();
                            SpinkitDialouge.dismiss();
                            swipeRefreshLayout.setRefreshing(false);

                            if (messagesList.isEmpty()) {
                                textViewNoMessages.setText("No Messages");
                                textViewNoMessages.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
                                textViewNoMessages.setVisibility(View.VISIBLE);
                            } else {
                                textViewNoMessages.setVisibility(View.GONE); // Hide "No messages" TextView
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> {
                            SpinkitDialouge.dismiss();
                            textViewNoMessages.setText("Error parsing response");
                            textViewNoMessages.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
                            textViewNoMessages.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                        });
                    }
                } else {
                    getActivity().runOnUiThread(() -> {
                        SpinkitDialouge.dismiss();
                        textViewNoMessages.setText("Failed to fetch messages");
                        textViewNoMessages.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.black));
                        textViewNoMessages.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }


    private void showEmptyInboxDialog() {
        new AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.INFO)
                .setTitle("Inbox Empty")
                .setMessage("Your inbox is currently empty. Please check back later for new messages.")
                .setCancelable(false)
                .setDarkMode(false)
                .setGravity(Gravity.CENTER)
                .setAnimation(DialogAnimation.SHRINK)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(AestheticDialog.Builder dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(fetchMessagesRunnable); // Stop periodic fetching
    }
}
