package com.animetone.incognitocontact;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.mail.MessageFetcher;
import com.animetone.incognitocontact.mail.ui.PremiumBottomSheetFragment;
import com.animetone.incognitocontact.security.Authenticator;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.animetone.incognitocontact.databinding.ActivityMainBinding;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView PhoneBottomNavigationView;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;
    private MessageFetcher messageFetcher;

    private Handler handler = new Handler();
    private Runnable checkInternetRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navbarbackcolor));
        bottomNavigationView = binding.appBarMain.bottomNavigationView;

        // Add the gallery BottomNavigationView
        PhoneBottomNavigationView = findViewById(R.id.galleryBottomNavigationView);

        setupToolbarAndNavigation();
        setupBottomNavigation();
        setupToolbarImage();
        setupToolbarImageListener();
        setupGalleryNavigation();

        // Start checking for internet connection
        startCheckingInternet();

        // Initialize MessageFetcher
        messageFetcher = new MessageFetcher(this);

        // Check for notification permission
        checkNotificationPermission();

        // Set up NavController and handle fragment destination changes
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_gallery || destination.getId() == R.id.phoneFragment || destination.getId() == R.id.otpFragment) {
                    // Hide the main BottomNavigationView and show the gallery one
                    bottomNavigationView.setVisibility(View.GONE);
                    PhoneBottomNavigationView.setVisibility(View.VISIBLE);
                }else if(destination.getId() == R.id.nav_slideshow){
                    bottomNavigationView.setVisibility(View.GONE);
                    PhoneBottomNavigationView.setVisibility(View.GONE);
                } else {
                    // Show the main BottomNavigationView and hide the gallery one
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    PhoneBottomNavigationView.setVisibility(View.GONE);
                }
            }
        });
    }


    private void checkNotificationPermission() {
        // Android 13 and above requires POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission for notifications
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission is already granted, start fetching messages
                messageFetcher.startFetchingMessages();
            }
        } else {
            // For Android versions below 13, start fetching directly (no need for permission)
            messageFetcher.startFetchingMessages();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // Check if permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                messageFetcher.startFetchingMessages();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Notification permission is required to receive message notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupToolbarAndNavigation() {
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Configure AppBar with navigation destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.messagesFragment,R.id.phoneFragment,R.id.otpFragment)
                .setOpenableLayout(drawer)
                .build();

        navigationView.setItemIconTintList(null); // Allow default colors for icons
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setupBottomNavigation() {
        // Set up BottomNavigationView with NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Handle item clicks for bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_email) {
                    navController.navigate(R.id.nav_home);
                    return true;
                } else if (item.getItemId() == R.id.menu_inbox) {
                    navController.navigate(R.id.messagesFragment);
                    return true;
                } else if (item.getItemId() == R.id.menu_switch_email) {
                    navController.navigate(R.id.nav_home);
                    showCustomVipDialogforHome();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupGalleryNavigation() {
        // Set up BottomNavigationView with NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(PhoneBottomNavigationView, navController);

        // Handle item clicks for bottom navigation
        PhoneBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.inventory) {
                    navController.navigate(R.id.nav_gallery);
                    return true;
                } else if (item.getItemId() == R.id.ibox) {
                    navController.navigate(R.id.otpFragment);
                    return true;
                } else if (item.getItemId() == R.id.editnumber) {
                    navController.navigate(R.id.nav_gallery);
                    showCustomVipDialogforGallery();
                    return true;
                }
                return false;
            }
        });
    }



    private void setupToolbarImage() {
        ImageView toolbarImage = binding.appBarMain.toolbar.findViewById(R.id.toolbarImage);

        // Load an image into the ImageView using Glide
        Glide.with(this)
                .load("https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExd2N2Mmh3cTU5cDczOGRzcm92bTg0azRua3o3cnFndWJ5OWRmNDkxbSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/CTuI03HYoB1SrpAN26/giphy.gif") // Replace with your image URL
                .into(toolbarImage);
    }

    private void setupToolbarImageListener() {
        ImageView toolbarImage = binding.appBarMain.toolbar.findViewById(R.id.toolbarImage);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show the ExampleBottomSheetFragment when the toolbar image is clicked
                PremiumBottomSheetFragment bottomSheetFragment = new PremiumBottomSheetFragment();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });
    }

    private void showCustomVipDialogforHome() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_vip_alert, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
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
            openTelegramLink(this,"https://t.me/animetonee");
            // Handle YES button click
            dialog.dismiss();  // Dismiss the dialog
            bottomNavigationView.setSelectedItemId(R.id.menu_email);
        });

        // Set up the NO button (LinearLayout) inside the custom layout
        LinearLayout btnNo = dialogView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            bottomNavigationView.setSelectedItemId(R.id.menu_email);

        });
    }

    private void showCustomVipDialogforGallery() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_vip_alert, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
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
            openTelegramLink(this,"https://t.me/animetonee");
            // Handle YES button click
            dialog.dismiss();  // Dismiss the dialog
            PhoneBottomNavigationView.setSelectedItemId(R.id.inventory);
        });

        // Set up the NO button (LinearLayout) inside the custom layout
        LinearLayout btnNo = dialogView.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            PhoneBottomNavigationView.setSelectedItemId(R.id.inventory);

        });
    }

    public void openTelegramLink(Context context, String telegramLink) {
        Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
        telegramIntent.setData(Uri.parse(telegramLink));
        telegramIntent.setPackage("org.telegram.messenger");

        try {
            context.startActivity(telegramIntent);
        } catch (ActivityNotFoundException e) {
            // Telegram app not installed, fallback to browser
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(telegramLink)));
        }
    }

    private void startCheckingInternet() {
        checkInternetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isNetworkAvailable() || !Authenticator.isAuthenticated()) {
                    ShowNoInternet();
                }
                // Check again after 5 seconds
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(checkInternetRunnable);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShowNoInternet() {
        new AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
                .setTitle("No Internet Connection")
                .setMessage("Please check your network connection and try again.")
                .setCancelable(false)
                .setDarkMode(false)
                .setGravity(Gravity.CENTER)
                .setAnimation(DialogAnimation.SHRINK)
                .setOnClickListener(new OnDialogClickListener() {
                    @Override
                    public void onClick(AestheticDialog.Builder dialog) {
                        finishAffinity();
                    }
                })
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop fetching messages when the activity is destroyed
        if (messageFetcher != null) {
            messageFetcher.stopFetchingMessages();
        }
        handler.removeCallbacks(checkInternetRunnable);
    }
}
