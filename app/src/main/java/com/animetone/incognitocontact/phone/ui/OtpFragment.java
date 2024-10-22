package com.animetone.incognitocontact.phone.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.customprogresdialog;
import com.animetone.incognitocontact.phone.OtpAdapter;
import com.animetone.incognitocontact.phone.ResponseModel;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OtpFragment extends Fragment {
    private RecyclerView recyclerView;
    private OtpAdapter otpAdapter;
    private OkHttpClient client;
    private TextView messageTextView;
    private TextView copyTextView;
    private LinearLayout messagetetxtbackground;
    private customprogresdialog SpinkitDialouge;
    private String country;
    private String phoneNumber;
    private String source;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ResponseModel.ResultItem> previousResults = new ArrayList<>(); // Store previous results

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageTextView = view.findViewById(R.id.messageTextView);
        messagetetxtbackground = view.findViewById(R.id.messagetextbackground);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        copyTextView = view.findViewById(R.id.copyTextView);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPreferences", getContext().MODE_PRIVATE);
        country = sharedPreferences.getString("primary_country", null);
        phoneNumber = sharedPreferences.getString("primary_number", null);
        source = sharedPreferences.getString("primary_source", null);

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            messageTextView.setText("Primary number is not set");
            messageTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
            messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
            messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
            return view; // Exit early if the number is not set
        }

        messageTextView.setText("Number : "+phoneNumber);
        messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
        messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
        client = new OkHttpClient();
        SpinkitDialouge = new customprogresdialog(getActivity());
        SpinkitDialouge.show();

        fetchData(country, phoneNumber, source );
        // Set up swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchData(country, phoneNumber, source );
        });

        copyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyToClipboard(phoneNumber);
            }
        });

        // Start fetching data every few seconds
        startPeriodicFetch();

        return view;
    }

    private void startPeriodicFetch() {
        // Schedule periodic fetching (e.g., every 5 seconds)
        final Handler handler = new Handler();
        final int delay = 30000; // 5 seconds

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchData(country, phoneNumber, source );
                handler.postDelayed(this, delay); // Repeat this runnable again every 5 seconds
            }
        }, delay);
    }

    private void fetchData(String country, String phoneNumber, String source) {
        String url = String.format("https://otp-api.shelex.dev/api/%s/%s?since=1&source=%s", country, phoneNumber, source);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    if (SpinkitDialouge.isShowing()) {
                        SpinkitDialouge.dismiss();
                    }
                    messageTextView.setText("Request Timeout");
                    messageTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                    messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                    swipeRefreshLayout.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (SpinkitDialouge.isShowing()) {
                    SpinkitDialouge.dismiss();
                }

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    parseJsonData(jsonData);
                } else {
                    getActivity().runOnUiThread(() -> {
                        messageTextView.setText("Response not successful");
                        messageTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                        messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                        messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        });
    }

    private void parseJsonData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            List<ResponseModel.ResultItem> newResults = new ArrayList<>();

            if (resultsArray.length() == 0) {
                getActivity().runOnUiThread(() -> {
                    messageTextView.setText("No messages");
                    messageTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                    messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                    messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                    messageTextView.setVisibility(View.VISIBLE);
                    messagetetxtbackground.setVisibility(View.VISIBLE);
                   // showEmptyInboxDialog();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject item = resultsArray.getJSONObject(i);
                String agoText = item.optString("agoText", "No data");
                String message = item.optString("message", "No message");
                String otp = item.optString("otp", "No OTP");

                newResults.add(new ResponseModel.ResultItem(message, otp, agoText));
            }

            // Check for new OTPs
            boolean hasNewOtp = false;
            for (ResponseModel.ResultItem newResultItem : newResults) {
                boolean found = false;
                for (ResponseModel.ResultItem previousResultItem : previousResults) {
                    if (newResultItem.getOtp().equals(previousResultItem.getOtp())) {
                        found = true; // Existing OTP found
                        break;
                    }
                }
                if (!found) {
                    hasNewOtp = true; // New OTP detected
                    break; // No need to check further
                }
            }

            // Update the previous results list
            previousResults.clear();
            previousResults.addAll(newResults); // Update previousResults to the new results

            // Handle updates based on whether new OTPs were detected
            if (hasNewOtp) {
                getActivity().runOnUiThread(() -> {
                    otpAdapter = new OtpAdapter(newResults);
                    recyclerView.setAdapter(otpAdapter);
                  //  messagetetxtbackground.setVisibility(View.GONE);
                   // messageTextView.setVisibility(View.GONE);
                });
            }

            swipeRefreshLayout.setRefreshing(false);

        } catch (JSONException e) {
            e.printStackTrace();
            getActivity().runOnUiThread(() -> {
                messageTextView.setText("Error parsing JSON");
                messageTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                messageTextView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                messagetetxtbackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.navbarbackcolor));
                swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void copyToClipboard(String Number) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Number", Number);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Number copied to clipboard", Toast.LENGTH_SHORT).show();
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
}
