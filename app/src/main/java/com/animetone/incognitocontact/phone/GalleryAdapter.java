package com.animetone.incognitocontact.phone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.animetone.incognitocontact.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<Country> countryList;

    public GalleryAdapter(List<Country> countryList) {
        this.countryList = new ArrayList<>();
        HashSet<String> uniqueCountries = new HashSet<>();

        for (Country country : countryList) {
            // Check for duplicates based on the country name
            if (country.getCount() > 0 && uniqueCountries.add(country.getName().toLowerCase())) {
                this.countryList.add(country);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = countryList.get(position);

        String countryCode = getCountryCodeFromName(country.getName());
        String flagUrl = "https://flagcdn.com/w320/" + countryCode.toLowerCase() + ".png";

        // Use Glide to load the flag image
        Glide.with(holder.itemView.getContext())
                .load(flagUrl)
                .placeholder(R.drawable.loading) // Placeholder image while loading
                .error(R.drawable.mark) // Error image if loading fails
                .centerCrop()
                .into(holder.countryflag);

        // Set a click listener on the entire item view
        holder.itemView.setOnClickListener(v -> {
            // Create a bundle with the selected country name
            Bundle bundle = new Bundle();
            bundle.putString("country_name", country.getName());
            bundle.putString("country_source", country.getSource());

            // Find the NavController from the hosting Activity or Fragment
            NavController navController = Navigation.findNavController((AppCompatActivity) v.getContext(), R.id.nav_host_fragment_content_main);

            // Navigate to PhoneListFragment using the NavController
            navController.navigate(R.id.phoneFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView countryflag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryflag = itemView.findViewById(R.id.ivCountryFlag);
        }
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
