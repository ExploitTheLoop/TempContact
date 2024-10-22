package com.animetone.incognitocontact.mail.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.animetone.incognitocontact.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MessageDetailsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_FROM = "arg_from";
    private static final String ARG_BODY = "arg_body";

    // Factory method to create a new instance of MessageDetailsBottomSheet with the message details
    public static MessageDetailsBottomSheet newInstance(String from, String body) {
        MessageDetailsBottomSheet fragment = new MessageDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_FROM, from);
        args.putString(ARG_BODY, body);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        TextView tvFrom = view.findViewById(R.id.tvFromDetail);
        TextView tvBody = view.findViewById(R.id.tvBodyDetail);
        ImageButton btnClose = view.findViewById(R.id.btnClose); // Initialize the close button

        // Retrieve arguments passed to the fragment
        if (getArguments() != null) {
            String from = getArguments().getString(ARG_FROM);
            String body = getArguments().getString(ARG_BODY);

            // Set the values to the TextViews
            tvFrom.setText(from);
            tvBody.setText(body);
        }

        // Set the click listener for the close button to dismiss the bottom sheet
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Close the bottom sheet when the cross button is clicked
            }
        });

        return view;
    }
}
