package com.animetone.incognitocontact;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class customprogresdialog extends Dialog {
    public customprogresdialog(@NonNull Context context) {
        super(context);
        WindowManager.LayoutParams Params = getWindow().getAttributes();
        Params.gravity = Gravity.CENTER_HORIZONTAL;
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_layout,null);
        setContentView(view);
    }
}
