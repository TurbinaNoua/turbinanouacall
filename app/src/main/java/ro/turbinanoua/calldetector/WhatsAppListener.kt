package com.example.turbinanouacall

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class WhatsAppListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Verificăm că notificarea nu este nulă și că este de la WhatsApp
        if (sbn?.packageName == "com.whatsapp") {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title")

            // Ignorăm notificările de grup (care conțin ':') sau notificările generale ale WhatsApp
            if (title != null && !title.contains(":") && !title.contains(" WhatsApp")) {
                val potentialPhoneNumber = title
                Log.d("WhatsAppListener", "S-a extras din notificare: $potentialPhoneNumber")
                BubbleUploader.sendPhoneNumber(this, potentialPhoneNumber)
            }
        }
    }
}