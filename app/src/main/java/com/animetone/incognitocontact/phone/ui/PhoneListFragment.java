package com.animetone.incognitocontact.phone.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.animetone.incognitocontact.phone.Phone;
import com.animetone.incognitocontact.phone.PhoneAdapter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhoneListFragment extends Fragment {
    private RecyclerView recyclerView;
    private PhoneAdapter phoneAdapter;
    private List<Phone> phoneList;
    private String countryName;
    private String countrySource;
    private ProgressDialog progressDialog;  // ProgressDialog reference
    private customprogresdialog SpinkitDialouge;
    // Factory method to create a new instance of the fragment with arguments
    public static PhoneListFragment newInstance(String countryName, String countrySource) {
        PhoneListFragment fragment = new PhoneListFragment();
        Bundle args = new Bundle();
        args.putString("country_name", countryName);
        args.putString("country_source", countrySource);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneList = new ArrayList<>();
        SpinkitDialouge = new customprogresdialog(getActivity());
        // Initialize ProgressDialog
       // progressDialog = new ProgressDialog(getContext());
       // progressDialog.setMessage("Loading Numbers...");
       // progressDialog.setCancelable(false);

        // Retrieve the countryName from the arguments
        if (getArguments() != null) {
            countryName = getArguments().getString("country_name");
            countrySource = getArguments().getString("country_source");

            // Show ProgressDialog before fetching data
            SpinkitDialouge.show();
            fetchPhoneNumbers(countryName); // Fetch phone numbers based on country
        }
    }

    private void fetchPhoneNumbers(String countryName) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://otp-api.shelex.dev/api/list/" + countryName;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    // Dismiss ProgressDialog on failure
                    getActivity().runOnUiThread(() -> SpinkitDialouge.dismiss());
                    
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    parsePhoneNumbers(responseData);
                } else {
                    if (getActivity() != null) {
                        // Dismiss ProgressDialog on failure
                        getActivity().runOnUiThread(() -> SpinkitDialouge.dismiss());
                    }
                }
            }
        });
    }

    private void parsePhoneNumbers(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray phonesArray = jsonObject.getJSONArray("phones");

            for (int i = 0; i < phonesArray.length(); i++) {
                JSONObject phoneObject = phonesArray.getJSONObject(i);
                String value = phoneObject.getString("value");
                String Csource = phoneObject.getString("source");
                Phone phone = new Phone(value, countryName, Csource); // Pass countryName in Phone constructor
                phoneList.add(phone);
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    phoneAdapter.notifyDataSetChanged();  // Notify adapter of data change
                    SpinkitDialouge.dismiss();  // Dismiss ProgressDialog after data is loaded
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null) {
                // Dismiss ProgressDialog if parsing fails
                getActivity().runOnUiThread(() -> SpinkitDialouge.dismiss());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        phoneAdapter = new PhoneAdapter(phoneList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(phoneAdapter);
        return view;
    }
}
