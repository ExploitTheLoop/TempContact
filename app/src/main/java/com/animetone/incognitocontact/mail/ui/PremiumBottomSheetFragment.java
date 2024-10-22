package com.animetone.incognitocontact.mail.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.animetone.incognitocontact.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class PremiumBottomSheetFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_premium_bottom_sheet, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set the bottom sheet to full-screen
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dismiss button functionality
        ImageView btnDismiss = view.findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(v -> dismiss());

        LinearLayout btnTryPremium = view.findViewById(R.id.btnSubscribe);
        btnTryPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTelegramLink(getActivity(),"https://t.me/animetonee");
            }
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
}

