package ro.turbinanoua.calldetector

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.regex.Pattern

class WhatsAppListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val packageName = sbn?.packageName ?: return

        if (packageName == "com.whatsapp") {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getString("android.text") ?: ""

            val numarExtras = extractPhoneNumber("$title $text")

            if (numarExtras != null) {
                Log.d("WHATSAPP", "📲 WhatsApp de la: $numarExtras")
                trimiteNumarBubble(numarExtras)
            } else {
                Log.d("WHATSAPP", "❌ Nu am găsit număr în notificare")
            }
        }
    }

    private fun extractPhoneNumber(input: String): String? {
        val pattern = Pattern.compile("\\+4\\d{9,}")
        val matcher = pattern.matcher(input)
        return if (matcher.find()) matcher.group() else null
    }

    private fun trimiteNumarBubble(numarTelefon: String) {
        val client = OkHttpClient()

        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val json = """
            {
                "telefon": "$numarTelefon",
                "sursa": "WhatsApp"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("https://turbinanouaro.bubbleapps.io/version-test/api/1.1/wf/numar_apel")
            .post(json)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WHATSAPP", "❌ Eroare trimitere: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("WHATSAPP", "✅ Cod răspuns Bubble: ${response.code}")
            }
        })
    }
}
