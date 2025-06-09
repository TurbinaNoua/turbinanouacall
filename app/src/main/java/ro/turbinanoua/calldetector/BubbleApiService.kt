package ro.turbinanoua.calldetector

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface BubbleApiService {
    // AICI ESTE MODIFICAREA:
    @POST("wf/creare_client_inteligent")
    fun createClient(
        @Header("Authorization") token: String,
        @Body clientData: BubbleClient
    ): Call<Void>
}