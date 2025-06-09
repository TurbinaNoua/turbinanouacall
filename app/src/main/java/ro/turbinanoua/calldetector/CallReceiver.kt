package com.example.turbinanouacall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Verificăm dacă acțiunea este legată de starea telefonului
        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            // Ne interesează doar când telefonul sună (RINGING)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

                // Dacă numărul există, îl trimitem la BubbleUploader
                if (incomingNumber != null) {
                    BubbleUploader.sendPhoneNumber(context, incomingNumber)
                }
            }
        }
    }
}