package ro.turbinanoua.calldetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        Log.d("DEBUG", "📞 Apel detectat - Stare: $state, Număr: $incomingNumber")

        if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {
            Log.d("WHATSAPP", "📲 WhatsApp de la: TurbinaNoua ro CallCenter")
            trimiteNumarApel(incomingNumber)
        }
    }

    // ✅ Metodă publică apelabilă din MainActivity pentru test
    fun testNumarApel(context: Context, numarTelefon: String) {
        Log.d("DEBUG", "🔁 Test manual trimitere $numarTelefon")
        trimiteNumarApel(numarTelefon)
    }

    private fun trimiteNumarApel(numarTelefon: String) {
        val client = OkHttpClient()

        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = """
        {
            "telefon": "$numarTelefon",
            "sursa": "WhatsApp"
        }
        """.trimIndent()

        Log.d("DEBUG", "📤 Pornesc trimiterea către Bubble...")
        Log.d("DEBUG", "➡️ Payload JSON: $jsonBody")

        val requestBody = jsonBody.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("https://turbinanouaro.bubbleapps.io/version-test/api/1.1/wf/numar_apel")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DEBUG", "❌ Eroare de rețea sau conexiune: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("DEBUG", "📥 Cod răspuns Bubble: ${response.code}")
                Log.d("DEBUG", "📦 Body răspuns: $responseBody")

                if (response.isSuccessful) {
                    Log.d("DEBUG", "✅ Trimitere cu succes pentru numărul: $numarTelefon")
                } else {
                    Log.e("DEBUG", "❌ Eroare răspuns server: ${response.code} / ${response.message}")
                }
            }
        })
    }
}
