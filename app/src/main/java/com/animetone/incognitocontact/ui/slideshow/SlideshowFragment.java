package com.animetone.incognitocontact.ui.slideshow;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.animetone.incognitocontact.R;
import com.animetone.incognitocontact.databinding.FragmentSlideshowBinding;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Create and set up the AboutPage view
        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.tmpclogo) // Replace with your actual drawable resource
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .setDescription("TempMail & TempNumber is your ultimate solution for online privacy and security. With our app, you can generate temporary email addresses and mobile numbers to receive One-Time Passwords (OTPs) without revealing your personal information.")
                .addGroup("Connect with us")
                .addEmail("sayan8013cocacc@gmail.com")
                .addWebsite("https://t.me/animetonee")
                .addFacebook("https://t.me/animetonee")
                .addTwitter("https://t.me/animetonee")
                .addYoutube("https://t.me/animetonee")
                .addPlayStore("https://t.me/animetonee")
                .addInstagram("https://t.me/animetonee")
                .addGitHub("ExploitTheLoop")
                .addItem(getCopyRightsElement())
                .create();

        return aboutPage;
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.baseline_image_24); // Replace with your actual drawable resource
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color); // Ensure this color resource exists
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
