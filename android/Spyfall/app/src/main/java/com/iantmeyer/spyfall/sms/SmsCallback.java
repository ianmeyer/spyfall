package com.iantmeyer.spyfall.sms;

public interface SmsCallback {

    enum Status {
        PENDING,
        SENT,
        DELIVERED,
        ERROR
    }

    void onSmsStatusUpdate(SmsStatus status);

    class SmsStatus {
        public final String phoneNumber;
        public final Status status;
        public final String errorMessage;

        public SmsStatus(String phoneNumber, Status status) {
            this(phoneNumber, status, "");
        }

        public SmsStatus(String phoneNumber, Status status, String errorMessage) {
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.errorMessage = errorMessage;
        }
    }
}