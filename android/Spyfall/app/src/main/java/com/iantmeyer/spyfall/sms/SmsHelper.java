package com.iantmeyer.spyfall.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class SmsHelper {

    private static final String TAG = "SmsHelper";

    private final Context mContext;

    private SmsCallback mSmsCallback;

    private String PHONE = "sms_phone";
    private String ACTION_SENT = "sms_sent";
    private String ACTION_DELIVERED = "sms_delivered";

    public SmsHelper(Context context) {
        mContext = context.getApplicationContext();
        context.registerReceiver(mSmsSentReceiver, new IntentFilter(ACTION_SENT));
        context.registerReceiver(mSmsDeliveredReceiver, new IntentFilter(ACTION_DELIVERED));
    }

    public void setSmsCallback(SmsCallback smsCallback) {
        mSmsCallback = smsCallback;
    }

    public void sendSms(String phoneNumber, String message) {
        PendingIntent sentPi = getSentPi(phoneNumber);
        PendingIntent deliveredPi = getDeliveredPi(phoneNumber);
        mSmsCallback.onSmsStatusUpdate(new SmsCallback.SmsStatus(phoneNumber, SmsCallback.Status.PENDING));
        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, sentPi, deliveredPi);
    }

    private PendingIntent getSentPi(String phoneNumber) {
        Intent intent = new Intent(ACTION_SENT);
        intent.putExtra(PHONE, phoneNumber);
        return PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }

    private PendingIntent getDeliveredPi(String phoneNumber) {
        Intent intent = new Intent(ACTION_DELIVERED);
        intent.putExtra(PHONE, phoneNumber);
        return PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }

    private BroadcastReceiver mSmsSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String phoneNumber = intent.getStringExtra(PHONE);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mSmsCallback.onSmsStatusUpdate(new SmsCallback.SmsStatus(phoneNumber, SmsCallback.Status.SENT));
                    break;
                default:
                    mSmsCallback.onSmsStatusUpdate(new SmsCallback.SmsStatus(phoneNumber, SmsCallback.Status.ERROR, "Send failure."));
                    break;
            }
        }
    };

    private BroadcastReceiver mSmsDeliveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String phoneNumber = intent.getStringExtra(PHONE);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    mSmsCallback.onSmsStatusUpdate(new SmsCallback.SmsStatus(phoneNumber, SmsCallback.Status.DELIVERED));
                    break;
                default:
                    mSmsCallback.onSmsStatusUpdate(new SmsCallback.SmsStatus(phoneNumber, SmsCallback.Status.ERROR, "Delivery failure."));
                    break;
            }
        }
    };
}