package com.animetone.incognitocontact.phone;

import java.util.List;

public class ResponseModel {
    public static class ResultItem {
        private String message;
        private String otp;
        private String time;

        public ResultItem(String message, String otp, String time) {
            this.message = message;
            this.otp = otp;
            this.time = time;
        }

        public String getMessage() {
            return message;
        }

        public String getOtp() {
            return otp;
        }

        public String getTime(){return time;}

        public void setTime(String time) {
            this.time = time;
        }
    }
}

