package com.example.phonecallnotifier;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class phoneBR extends BroadcastReceiver {

    private Context context;
    String incoming_nr;
    private int prev_state;
    TelephonyManager telephony;
    String mPhoneNumber="";
    private static final String TAG = "debuggy";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("debuggy", " receiver called ");
        this.context = context;
        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager
        Bundle bundle = intent.getExtras();
        String phoneNr = bundle.getString("incoming_number");
        Log.v(TAG, "phoneNr: " + phoneNr);
    }

    /* Custom PhoneStateListener */
    public class CustomPhoneStateListener extends PhoneStateListener {

        private static final String TAG = "debuggy";

        @SuppressLint("MissingPermission")
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (incomingNumber != null && incomingNumber.length() > 0) incoming_nr = incomingNumber;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "CALL_STATE_RINGING");
                    prev_state = state;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "CALL_STATE_OFFHOOK");
                    prev_state = state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "CALL_STATE_IDLE==>" + incoming_nr);
                    if ((prev_state == TelephonyManager.CALL_STATE_OFFHOOK)) {
                        prev_state = state;
                        Log.d(TAG, "missed call from " + incomingNumber);
                        //Answered Call which is ended
                        Toast.makeText(context, "answered call end", Toast.LENGTH_SHORT).show();
                    }
                    if ((prev_state == TelephonyManager.CALL_STATE_RINGING)) {
                        prev_state = state;
                        Log.d(TAG, "missed call from " + incomingNumber + " and thus sending sending sms");
                        //Rejected or Missed call
                        Toast.makeText(context, " missed call end " + incomingNumber, Toast.LENGTH_SHORT).show();
                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Permission not granted ", Toast.LENGTH_SHORT).show();
                        return ;
                    }else{
                        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                        SendSMSTo(mPhoneNumber,"You got a miss call from "+incomingNumber);
                    }
                    break;
            }
        }
    }

    public void SendSMSTo(String fromNumber, String message) {
        Log.d(TAG," callback linked and sending message ");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mPhoneNumber, null, message, null, null);
    }
}
