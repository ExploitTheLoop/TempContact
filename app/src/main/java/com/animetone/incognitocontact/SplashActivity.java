package com.animetone.incognitocontact;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.animetone.incognitocontact.api.KeyAuth;
import com.animetone.incognitocontact.security.Authenticator;
import com.saadahmedev.popupdialog.PopupDialog;

public class SplashActivity extends AppCompatActivity {

    private TextView splashText, loadingText;
    private int progress = 1;
    private Handler handler = new Handler();
    private int initProgress = 0;
    private boolean isInitializing = false;

    private static final String URL = "https://keyauth.win/api/1.2/";
    private static final String OWNERID = "XQFamIACDQ"; // Set your owner ID
    private static final String APPNAME = "nulbytes"; // Set your app name
    private static final String VERSION = "1.1"; // Set your app version

    private KeyAuth keyAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        // Initialize both TextViews
        splashText = findViewById(R.id.splashText);
        loadingText = findViewById(R.id.loadingText);

        keyAuth = new KeyAuth(APPNAME, OWNERID, VERSION, URL);

        // Start the percentage counter
        startCounting();
    }

    // Separate method for counting progress
    private void startCounting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progress <= 100 && !isInitializing) {
                    splashText.setText(progress + "%"); // Show only the percentage
                    progress++;
                    handler.postDelayed(this, 50); // Update every 50ms until 100%
                } else if (!isInitializing) {
                    // Once progress reaches 100%, start KeyAuth initialization with simulated progress
                    startInitializationProgress();
                }
            }
        }, 50); // Initial delay of 50 milliseconds
    }

    // Method to handle initialization progress display
    private void startInitializationProgress() {
        isInitializing = true;  // Flag to stop the percentage counter
        initProgress = 1;       // Reset init progress

        loadingText.setText("Initializing..."); // Display initialization status here
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (initProgress <= 99) {
                    splashText.setText(initProgress + "%"); // Update percentage during initialization
                    initProgress += 10;  // Increment by 10% (can be adjusted)
                    handler.postDelayed(this, 200); // Delay for the next progress update
                } else {
                    // When initialization progress reaches 100%, proceed with actual initialization
                    initializeKeyAuth();
                }
            }
        }, 200); // Start initialization progress
    }

    // Method to handle KeyAuth initialization
    private void initializeKeyAuth() {
        keyAuth.init(new KeyAuth.InitCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    splashText.setText("100%"); // Final progress update
                    loadingText.setText("Initialization Successful!"); // Indicate success in loadingText
                    // Navigate to the next activity or proceed with other actions
                    Authenticator.setAuthenticated(true);
                    // Start MainActivity after initialization is successful
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close SplashActivity to prevent going back to it

                });
            }

            @Override
            public void onFailure(String message) {
                runOnUiThread(() -> {
                    splashText.setText("Initialization Failed");
                    loadingText.setText("Initialization Failed: " + message); // Show failure message here
                   // Toast.makeText(SplashActivity.this, "Initialization Failed: " + message, Toast.LENGTH_SHORT).show();

                    PopupDialog.getInstance(SplashActivity.this)
                            .statusDialogBuilder()
                            .createErrorDialog()
                            .setHeading("Uh-Oh")
                            .setDescription("Unexpected error occurred. Try again later.")
                            .build(dialog -> {
                                // Finish the activity when the dialog is dismissed
                                finish();
                            })
                            .show();

                });
            }
        });
    }
}
