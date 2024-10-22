package com.animetone.incognitocontact.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.customprogresdialog;
import com.animetone.incognitocontact.databinding.FragmentHomeBinding;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView tvEmail;
    private ImageView btnChange;  // Declare the ImageView
    private TextView btnCopy;     // Declare the copy button
    private TextView btnEdit;
    private OkHttpClient client;
    private ProgressDialog progressDialog;  // Declare ProgressDialog
    private customprogresdialog SpinkitDialouge;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvEmail = binding.tvEmail;   // TextView to display email
        btnChange = binding.btnChange;  // ImageView for "Change" button
        btnCopy = binding.btnCopy;   // TextView for "Copy" button
        btnEdit = binding.btnEdit;

        client = new OkHttpClient();
        SpinkitDialouge = new customprogresdialog(getActivity());
        // Initialize ProgressDialog
       //progressDialog = new ProgressDialog(getContext());
       //progressDialog.setMessage("Fetching email...");
       //progressDialog.setCancelable(false); // Prevent closing the dialog while the task is in progress

        // Access SharedPreferences to check for saved USERNAME and DOMAIN
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("USERNAME", null);
        String savedDomain = sharedPreferences.getString("DOMAIN", null);

        // If no saved email (USERNAME or DOMAIN is null), fetch a new one
        if (savedUsername == null || savedDomain == null) {
            fetchRandomEmail();  // Fetch new email if none is saved
        } else {
            // If email exists, display it
            String savedEmail = savedUsername + "@" + savedDomain;
            tvEmail.setText(savedEmail);  // Show the saved email in the TextView
        }

        // Set OnClickListener for the change button (ImageView)
        btnChange.setOnClickListener(v -> {
            fetchRandomEmail(); // Fetch a new random email when the button is clicked
        });

        // Set OnClickListener for the copy button (TextView)
        btnCopy.setOnClickListener(v -> {
            String emailToCopy = tvEmail.getText().toString();
            if (!emailToCopy.isEmpty()) {
                copyToClipboard(emailToCopy);  // Call method to copy email to clipboard
            } else {
                Toast.makeText(getContext(), "No email to copy", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for the edit button (TextView)
        btnEdit.setOnClickListener(v -> {
            showCustomVipDialog();  // Show the custom dialog for VIP edit
        });

        return root;
    }

    // Method to fetch random email from API
    private void fetchRandomEmail() {
        //progressDialog.show();  // Show the progress dialog when fetching starts
        SpinkitDialouge.show();

        String url = "https://www.1secmail.com/api/v1/?action=genRandomMailbox&count=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> {
                  //  progressDialog.dismiss();  // Dismiss the progress dialog on failure
                    SpinkitDialouge.dismiss();
                    Toast.makeText(getContext(), "Failed to fetch email", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(jsonData);
                        String randomEmail = jsonArray.getString(0);

                        // Split the email into USERNAME and DOMAIN
                        String[] emailParts = randomEmail.split("@");
                        String username = emailParts[0];
                        String domain = emailParts[1];

                        // Save USERNAME and DOMAIN in SharedPreferences
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USERNAME", username);
                        editor.putString("DOMAIN", domain);
                        editor.apply();

                        // Update the UI on the main thread
                        getActivity().runOnUiThread(() -> {
                            tvEmail.setText(randomEmail);  // Set full email in the TextView
                            //progressDialog.dismiss();  // Dismiss the progress dialog once the email is fetched
                            SpinkitDialouge.dismiss();
                            Toast.makeText(getContext(), "Email saved!", Toast.LENGTH_SHORT).show();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Method to copy email to clipboard
    private void copyToClipboard(String email) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Email", email);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Email copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    // Method to show a custom VIP dialog
    private void showCustomVipDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_vip_alert, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomDialog);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the width and height of the dialog
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Change width to MATCH_PARENT
        }

        // Set up the YES button (LinearLayout) inside the custom layout
        LinearLayout btnYes = dialogView.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(v -> {
            // Handle YES button click
            dialog.dismiss();  // Dismiss the dialog
        });

        // Set up the NO button (LinearLayout) inside the custom layout
        LinearLayout btnNo = dialogView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> {
            // Handle NO button click
            dialog.dismiss();  // Dismiss the dialog
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
