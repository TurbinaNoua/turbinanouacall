package ro.turbinanoua.calldetector

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WhatsAppNotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        val packageName = sbn.packageName
        if (packageName != "com.whatsapp") return

        val extras = sbn.notification.extras
        val title = extras.getString("android.title") ?: return

        Log.d("WHATSAPP", "📲 WhatsApp de la: $title")

        // Trimitem doar numărul/numele către Bubble
        Thread {
            try {
                val url = URL("https://turbinanouaro.bubbleapps.io/version-test/api/1.1/numar_apel")
                val param = "numar=" + URLEncoder.encode(title, "UTF-8")

                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    outputStream.write(param.toByteArray())
                    outputStream.flush()
                    outputStream.close()
                    val response = inputStream.bufferedReader().readText()
                    Log.d("WHATSAPP", "✅ Trimis spre Bubble: $response")
                }
            } catch (e: Exception) {
                Log.e("WHATSAPP", "❌ Eroare trimitere: ${e.message}")
            }
        }.start()
    }
}
