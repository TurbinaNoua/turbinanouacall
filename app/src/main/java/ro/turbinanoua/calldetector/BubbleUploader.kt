package com.example.turbinanouacall

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BubbleUploader {

    // !!! ATENȚIE: MODIFICĂ OBLIGATORIU URMĂTOARELE 2 LINII !!!
    private const val BUBBLE_APP_NAME = "numele-tau-de-app-bubble" // Scrie aici numele unic al aplicației tale Bubble
    private const val BUBBLE_API_KEY = "cheia-ta-privata-din-bubble" // Scrie aici cheia API privată din setările Bubble

    private val apiService: BubbleApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://$BUBBLE_APP_NAME.bubbleapps.io/version-test/api/1.1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun sendPhoneNumber(context: Context, phoneNumber: String) {
        // Verificăm dacă numărul nu este gol
        if (phoneNumber.isBlank()) {
            Log.w("BubbleUploader", "S-a încercat trimiterea unui număr gol. Se anulează.")
            return
        }

        Log.d("BubbleUploader", "Se pregătește trimiterea numărului: $phoneNumber")

        val client = BubbleClient(telefon = phoneNumber)
        val authToken = "Bearer $BUBBLE_API_KEY"

        apiService.createClient(authToken, client).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("BubbleUploader", "SUCCES! Numărul $phoneNumber a fost trimis la Bubble.")
                } else {
                    Log.e("BubbleUploader", "EROARE la trimitere. Cod: ${response.code()}. Mesaj: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BubbleUploader", "EROARE DE REȚEA la trimitere.", t)
            }
        })
    }
}