package com.animetone.incognitocontact.phone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.animetone.incognitocontact.R;

import java.util.List;

public class OtpAdapter extends RecyclerView.Adapter<OtpAdapter.OtpViewHolder> {
    private List<ResponseModel.ResultItem> otpList;

    public OtpAdapter(List<ResponseModel.ResultItem> otpList) {
        this.otpList = otpList;
    }

    @NonNull
    @Override
    public OtpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_otp, parent, false);
        return new OtpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtpViewHolder holder, int position) {
        ResponseModel.ResultItem item = otpList.get(position);
        holder.messageTextView.setText(item.getMessage()); // Assuming you have a method to get message
        holder.timestamp.setText(item.getTime()); // Assuming you have a method to get message
        holder.otp.setText(item.getOtp()); // Assuming you have a method to get message
    }

    @Override
    public int getItemCount() {
        return otpList.size();
    }

    static class OtpViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView,timestamp,otp;

        public OtpViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp_text);
            otp = itemView.findViewById(R.id.otp_text);
        }
    }
}

