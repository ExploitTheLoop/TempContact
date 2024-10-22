package com.animetone.incognitocontact.phone;

import static androidx.core.content.ContextCompat.getSystemService;
import static java.security.AccessController.getContext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.animetone.incognitocontact.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {
    private List<Phone> phoneList;

    public PhoneAdapter(List<Phone> phoneList) {
        this.phoneList = phoneList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Phone phone = phoneList.get(position);

        // Check if the phone number starts with '+', and if not, add it
        String phoneNumber = phone.getNumber();
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+" + phoneNumber;
        }

        holder.tvPhoneNumber.setText(phoneNumber);
        
        holder.tvCountryName.setText(phone.getCountryName());

        String countryCode = getCountryCodeFromName(phone.getCountryName());
        String flagUrl = "https://flagcdn.com/w320/" + countryCode.toLowerCase() + ".png";

        Glide.with(holder.itemView.getContext())
                .load(flagUrl)
                .placeholder(R.drawable.loading) // Placeholder image while loading
                .error(R.drawable.mark) // Error image if loading fails
                .centerCrop()
                .into(holder.FlagImg);

        holder.ivMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(view.getContext(), holder.ivMoreOptions);
                popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu()); // Inflate the menu XML

                // Handle menu item clicks
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menu_set_primary) {
                            SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("MyAppPreferences", view.getContext().MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            String filteredPhoneNumber = phone.getNumber().replace("+", "");

                            // Store the phone number and country name in SharedPreferences
                            editor.putString("primary_number", filteredPhoneNumber);
                            editor.putString("primary_country", phone.getCountryName()); // Assuming you have a method to get country name
                            editor.putString("primary_source", phone.getSource());
                            // Apply the changes
                            editor.apply();
                            Toast.makeText(view.getContext(), "Set " + phone.getNumber() + " as Primary Number", Toast.LENGTH_SHORT).show();
                            return true;
                        }else if( item.getItemId() == R.id.menu_copy){

                            copyToClipboard(view.getContext(),phone.getNumber());  // Call the copy method
                            Toast.makeText(view.getContext(), "Phone number copied to clipboard", Toast.LENGTH_SHORT).show();

                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show(); // Show the popup menu
            }
        });


    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPhoneNumber, tvCountryName;
        ImageView FlagImg,ivMoreOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvCountryName = itemView.findViewById(R.id.tvCountryName);
            FlagImg = itemView.findViewById(R.id.ivCountryIcon);
            ivMoreOptions = itemView.findViewById(R.id.ivMoreOptions);
        }
    }

    private void copyToClipboard(Context context, String phoneNumber) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Phone Number", phoneNumber);
        clipboard.setPrimaryClip(clip);
    }



    private String getCountryCodeFromName(String countryName) {
        switch (countryName.toLowerCase()) {
            case "uk": return "gb";
            case "usa": return "us";
            case "southkorea": return "KR";
            case "southafrica": return "za";
            case "srilanka": return "lk";
            case "hongkong": return "HK";
            case "czechrepublic": return "cz";
            case "afghanistan": return "af";
            case "albania": return "al";
            case "algeria": return "dz";
            case "andorra": return "ad";
            case "angola": return "ao";
            case "argentina": return "ar";
            case "armenia": return "am";
            case "australia": return "au";
            case "austria": return "at";
            case "azerbaijan": return "az";
            case "bahamas": return "bs";
            case "bahrain": return "bh";
            case "bangladesh": return "bd";
            case "barbados": return "bb";
            case "belarus": return "by";
            case "belgium": return "be";
            case "belize": return "bz";
            case "benin": return "bj";
            case "bhutan": return "bt";
            case "bolivia": return "bo";
            case "bosnia and herzegovina": return "ba";
            case "botswana": return "bw";
            case "brazil": return "br";
            case "brunei": return "bn";
            case "bulgaria": return "bg";
            case "burkina faso": return "bf";
            case "burundi": return "bi";
            case "cabo verde": return "cv";
            case "cambodia": return "kh";
            case "cameroon": return "cm";
            case "canada": return "ca";
            case "central african republic": return "cf";
            case "chad": return "td";
            case "chile": return "cl";
            case "china": return "cn";
            case "colombia": return "co";
            case "comoros": return "km";
            case "congo, democratic republic of the": return "cd";
            case "congo, republic of the": return "cg";
            case "costa rica": return "cr";
            case "croatia": return "hr";
            case "cuba": return "cu";
            case "cyprus": return "cy";
            case "czech republic": return "cz";
            case "denmark": return "dk";
            case "djibouti": return "dj";
            case "dominica": return "dm";
            case "dominican republic": return "do";
            case "ecuador": return "ec";
            case "egypt": return "eg";
            case "el salvador": return "sv";
            case "equatorial guinea": return "gq";
            case "eritrea": return "er";
            case "estonia": return "ee";
            case "eswatini": return "sz";
            case "ethiopia": return "et";
            case "fiji": return "fj";
            case "finland": return "fi";
            case "france": return "fr";
            case "gabon": return "ga";
            case "gambia": return "gm";
            case "georgia": return "ge";
            case "germany": return "de";
            case "ghana": return "gh";
            case "greece": return "gr";
            case "grenada": return "gd";
            case "guatemala": return "gt";
            case "guinea": return "gn";
            case "guinea-bissau": return "gw";
            case "guyana": return "gy";
            case "haiti": return "ht";
            case "honduras": return "hn";
            case "hungary": return "hu";
            case "iceland": return "is";
            case "india": return "in";
            case "indonesia": return "id";
            case "iran": return "ir";
            case "iraq": return "iq";
            case "ireland": return "ie";
            case "israel": return "il";
            case "italy": return "it";
            case "jamaica": return "jm";
            case "japan": return "jp";
            case "jordan": return "jo";
            case "kazakhstan": return "kz";
            case "kenya": return "ke";
            case "kiribati": return "ki";
            case "korea, north": return "kp";
            case "korea, south": return "kr";
            case "kuwait": return "kw";
            case "kyrgyzstan": return "kg";
            case "laos": return "la";
            case "latvia": return "lv";
            case "lebanon": return "lb";
            case "lesotho": return "ls";
            case "liberia": return "lr";
            case "libya": return "ly";
            case "liechtenstein": return "li";
            case "lithuania": return "lt";
            case "luxembourg": return "lu";
            case "madagascar": return "mg";
            case "malawi": return "mw";
            case "malaysia": return "my";
            case "maldives": return "mv";
            case "mali": return "ml";
            case "malta": return "mt";
            case "marshall islands": return "mh";
            case "mauritania": return "mr";
            case "mauritius": return "mu";
            case "mexico": return "mx";
            case "micronesia": return "fm";
            case "moldova": return "md";
            case "monaco": return "mc";
            case "mongolia": return "mn";
            case "montenegro": return "me";
            case "morocco": return "ma";
            case "mozambique": return "mz";
            case "myanmar": return "mm";
            case "namibia": return "na";
            case "nauru": return "nr";
            case "nepal": return "np";
            case "netherlands": return "nl";
            case "new zealand": return "nz";
            case "nicaragua": return "ni";
            case "niger": return "ne";
            case "nigeria": return "ng";
            case "north macedonia": return "mk";
            case "norway": return "no";
            case "oman": return "om";
            case "pakistan": return "pk";
            case "palau": return "pw";
            case "palestine": return "ps";
            case "panama": return "pa";
            case "papua new guinea": return "pg";
            case "paraguay": return "py";
            case "peru": return "pe";
            case "philippines": return "ph";
            case "poland": return "pl";
            case "portugal": return "pt";
            case "qatar": return "qa";
            case "romania": return "ro";
            case "russia": return "ru";
            case "rwanda": return "rw";
            case "st. kitts and nevis": return "kn";
            case "st. lucia": return "lc";
            case "st. vincent and the grenadines": return "vc";
            case "samoa": return "ws";
            case "san marino": return "sm";
            case "saudi arabia": return "sa";
            case "senegal": return "sn";
            case "serbia": return "rs";
            case "seychelles": return "sc";
            case "sierra leone": return "sl";
            case "singapore": return "sg";
            case "slovakia": return "sk";
            case "slovenia": return "si";
            case "solomon islands": return "sb";
            case "somalia": return "so";
            case "south africa": return "za";
            case "south sudan": return "ss";
            case "spain": return "es";
            case "sri lanka": return "lk";
            case "sudan": return "sd";
            case "suriname": return "sr";
            case "sweden": return "se";
            case "switzerland": return "ch";
            case "syria": return "sy";
            case "taiwan": return "tw";
            case "tajikistan": return "tj";
            case "tanzania": return "tz";
            case "thailand": return "th";
            case "togo": return "tg";
            case "tonga": return "to";
            case "trinidad and tobago": return "tt";
            case "tunisia": return "tn";
            case "turkey": return "tr";
            case "turkmenistan": return "tm";
            case "tuvalu": return "tv";
            case "uganda": return "ug";
            case "ukraine": return "ua";
            case "united arab emirates": return "ae";
            case "united kingdom": return "gb";
            case "united states": return "us";
            case "uruguay": return "uy";
            case "uzbekistan": return "uz";
            case "vanuatu": return "vu";
            case "vatican city": return "va";
            case "venezuela": return "ve";
            case "vietnam": return "vn";
            case "yemen": return "ye";
            case "zambia": return "zm";
            case "zimbabwe": return "zw";
            default: return ""; // Return an empty string if the country is not found
        }
    }
}

