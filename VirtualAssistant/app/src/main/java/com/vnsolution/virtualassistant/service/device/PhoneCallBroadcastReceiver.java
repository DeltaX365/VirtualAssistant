package com.vnsolution.virtualassistant.service.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.vnsolution.virtualassistant.common.DeviceOptions;

import java.util.Date;

public class PhoneCallBroadcastReceiver extends BroadcastReceiver {
    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedPhoneNumber; //because the passed incoming is only valid in ringing

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            // We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                savedPhoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            } else {
//                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
//                switch (telephonyManager.getCallState()) {
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
//                        Toast.makeText(context, "RINGING: " + phoneNumber, Toast.LENGTH_LONG).show();
//                        break;
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                        Toast.makeText(context, "INCOMING ACCEPT", Toast.LENGTH_LONG).show();
//                        break;
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        Toast.makeText(context, "INCOMING IDLE", Toast.LENGTH_LONG).show();
//                        break;
//                }

//                String callState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//                String phoneNumber = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                int state = 0;
//                if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    state = TelephonyManager.CALL_STATE_RINGING;
//                } else if (callState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//                    state = TelephonyManager.CALL_STATE_OFFHOOK;
//                } else if (callState.equals(TelephonyManager.CALL_STATE_IDLE)) {
//                    state = TelephonyManager.CALL_STATE_IDLE;
//                }
//                handleCallStateChanged(context, state, phoneNumber);

                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephonyManager.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        handleCallStateChanged(context, state, phoneNumber);
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
            }
        }
    }

    private void handleCallStateChanged(Context context, int state, String phoneNumber) {
        if (lastState == state) {
            // No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedPhoneNumber = phoneNumber;
                Toast.makeText(context, "INCOMING CALL STARTED", Toast.LENGTH_LONG).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // Transition of ringing->offhook are pickups of incoming calls. Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    Toast.makeText(context, "OUTGOING CALL STARTED", Toast.LENGTH_LONG).show();
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                // Went to idle - this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // Ring but no pickup - a miss
                    Toast.makeText(context, "A MISSED CALL", Toast.LENGTH_LONG).show();
                } else if (isIncoming) {
                    Toast.makeText(context, "INCOMING CALL ENDED", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "OUTGOING CALL ENDED", Toast.LENGTH_LONG).show();
                }
                syncPhoneCalls(context);
                break;
        }
        lastState = state;
    }

    private void syncPhoneCalls(Context context) {
        Intent serviceIntent = new Intent(context, DeviceService.class);
        serviceIntent.setAction(DeviceOptions.INTENT_ACTION_SYNC_PHONE_CALL);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
