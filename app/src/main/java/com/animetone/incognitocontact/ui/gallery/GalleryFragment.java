package com.animetone.incognitocontact.ui.gallery;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.customprogresdialog;
import com.animetone.incognitocontact.phone.Country;
import com.animetone.incognitocontact.phone.GalleryAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thecode.aestheticdialogs.AestheticDialog;
import com.thecode.aestheticdialogs.DialogAnimation;
import com.thecode.aestheticdialogs.DialogStyle;
import com.thecode.aestheticdialogs.DialogType;
import com.thecode.aestheticdialogs.OnDialogClickListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter adapter;
    private List<Country> countryList = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
   // private ProgressDialog progressDialog;
    private customprogresdialog SpinkitDialouge;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SpinkitDialouge = new customprogresdialog(getActivity());
        // Initialize and show ProgressDialog
      // progressDialog = new ProgressDialog(getContext());
      // progressDialog.setMessage("Loading countries...");
      // progressDialog.setCancelable(false);
      // progressDialog.show();
        SpinkitDialouge.show();

        fetchCountries();

        return view;
    }

    private void fetchCountries() {
        Request request = new Request.Builder()
                .url("https://otp-api.shelex.dev/api/countries")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace(); // Handle failure

                if (getActivity() != null) {

                    getActivity().runOnUiThread(() -> SpinkitDialouge.dismiss());

                    // Dismiss ProgressDialog on failure
                    new AestheticDialog.Builder(getActivity(), DialogStyle.FLAT, DialogType.ERROR) // Using ERROR type
                            .setTitle("Error Occurred")
                            .setMessage("Failed to fetch data. Please try again.")
                            .setCancelable(false)
                            .setDarkMode(true)
                            .setGravity(Gravity.CENTER)
                            .setAnimation(DialogAnimation.SHRINK)
                            .setOnClickListener(new OnDialogClickListener() {
                                @Override
                                public void onClick(AestheticDialog.Builder dialog) {
                                    fetchCountries();
                                    dialog.dismiss();
                                }
                            })
                            .show();

                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();

                    // Parse JSON response to List of Country objects
                    Gson gson = new Gson();
                    Type countryListType = new TypeToken<ArrayList<Country>>() {}.getType();
                    countryList = gson.fromJson(jsonResponse, countryListType);

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            adapter = new GalleryAdapter(countryList);
                            recyclerView.setAdapter(adapter);

                            // Dismiss ProgressDialog after data is loaded
                            SpinkitDialouge.dismiss();
                        });
                    }
                } else {
                    if (getActivity() != null) {

                        // Dismiss ProgressDialog if response is not successful
                        getActivity().runOnUiThread(() -> SpinkitDialouge.dismiss());

                        // Dismiss ProgressDialog on failure
                        new AestheticDialog.Builder(getActivity(), DialogStyle.FLAT, DialogType.ERROR) // Using ERROR type
                                .setTitle("Error Occurred")
                                .setMessage("Failed to fetch data. Please try again.")
                                .setCancelable(false)
                                .setDarkMode(true)
                                .setGravity(Gravity.CENTER)
                                .setAnimation(DialogAnimation.SHRINK)
                                .setOnClickListener(new OnDialogClickListener() {
                                    @Override
                                    public void onClick(AestheticDialog.Builder dialog) {
                                        fetchCountries();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        });
    }
}
