package ro.turbinanoua.calldetector

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class WhatsAppListenerService : NotificationListenerService() {

    private var lastNotificationTime: Long = 0
    private var lastNotificationTitle: String = ""

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn?.packageName == "com.whatsapp") {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title")
            val currentTime = System.currentTimeMillis()

            // Logic pentru a preveni înregistrările duplicate
            if (currentTime - lastNotificationTime < 2000 && title == lastNotificationTitle) {
                Log.d("WhatsAppListener", "Notificare duplicat ignorată: $title")
                return
            }

            lastNotificationTime = currentTime
            lastNotificationTitle = title ?: ""

            if (title != null && !title.contains(":") && !title.contains(" WhatsApp")) {
                // Încercăm să găsim numărul real în agendă
                val numberFromContacts = MainActivity.findPhoneNumberForContact(this, title)

                // Dacă nu îl găsim, folosim titlul notificării (care ar trebui să fie numărul)
                val numberToSend = numberFromContacts ?: title

                Log.d("WhatsAppListener", "S-a extras din notificare: '$title'. Se va trimite: '$numberToSend'")

                // #################### MODIFICARE ####################
                // Am adăugat parametrul "sursa" cu valoarea "WhatsApp"
                MainActivity.sendPhoneNumber(this, numberToSend, "WhatsApp")
                // ################## SFÂRȘIT MODIFICARE #################
            }
        }
    }
}
