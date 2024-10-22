package com.animetone.incognitocontact.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.mail.ui.MessageDetailsBottomSheet;
import com.animetone.incognitocontact.mail.ui.MessagesFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<JSONObject> messagesList;
    private MessagesFragment fragment; // Hold reference to the MessagesFragment

    // Constructor to pass the messages list and MessagesFragment
    public MessagesAdapter(MessagesFragment fragment, List<JSONObject> messagesList) {
        this.fragment = fragment; // Initialize fragment
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        try {
            JSONObject message = messagesList.get(position);
            String id = message.getString("id");
            String from = message.getString("from");
            String subject = message.getString("subject").isEmpty() ? "No Subject" : message.getString("subject");
            String date = message.getString("date");

            holder.tvFrom.setText(from);
            holder.tvSubject.setText(subject);
            holder.tvDate.setText(date);

            // Set an OnClickListener for the ViewHolder
            holder.itemView.setOnClickListener(v -> {
                fetchEmailData(id); // Call fetchEmailData when an item is clicked
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    // ViewHolder class for RecyclerView
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvFrom, tvSubject, tvDate;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    // Method to fetch email data
    private void fetchEmailData(String emailId) {
        // Access SharedPreferences for login and domain
        SharedPreferences sharedPreferences = fragment.getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String login = sharedPreferences.getString("USERNAME", null);
        String domain = sharedPreferences.getString("DOMAIN", null);

        // Check if login and domain are available
        if (login != null && domain != null) {
            String url = "https://www.1secmail.com/api/v1/?action=readMessage&login=" + login + "&domain=" + domain + "&id=" + emailId;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    fragment.requireActivity().runOnUiThread(() -> showDialog("Error", "Failed to fetch email data."));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        fragment.requireActivity().runOnUiThread(() -> showEmailBottomSheet(responseBody));
                    } else {
                        fragment.requireActivity().runOnUiThread(() -> showDialog("Error", "Failed to retrieve email."));
                    }
                }
            });
        } else {
            // Show dialog if login or domain is missing
            fragment.requireActivity().runOnUiThread(() -> showDialog("Error", "Login or domain is missing."));
        }
    }

    // Method to show email dialog
    private void showEmailDialog(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            String from = jsonResponse.getString("from");
            String body = jsonResponse.getString("textBody"); // Using textBody to display the plain text

            showDialog(from, body);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to show dialog
    private void showDialog(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(fragment.getActivity());
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        dialogBuilder.create().show();
    }

    // Method to show the email details in BottomSheetDialogFragment
    private void showEmailBottomSheet(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            String from = jsonResponse.getString("from");
            String body = jsonResponse.getString("textBody"); // Using textBody to display plain text

            MessageDetailsBottomSheet bottomSheet = MessageDetailsBottomSheet.newInstance(from, body);
            bottomSheet.show(fragment.getParentFragmentManager(), "MessageDetailsBottomSheet");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
