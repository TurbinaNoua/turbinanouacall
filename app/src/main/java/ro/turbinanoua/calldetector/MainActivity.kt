package ro.turbinanoua.calldetector

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    // Mecanism pentru a cere permisiuni la rulare
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("Permissions", "Permisiunea READ_CONTACTS a fost acordată.")
            } else {
                Log.w("Permissions", "Permisiunea READ_CONTACTS a fost refuzată.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Cerem permisiunea la pornire
            LaunchedEffect(key1 = true) {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            MainScreen()
        }
    }

    companion object {
        private const val BUBBLE_APP_NAME = "turbinanouaro"
        private const val BUBBLE_API_KEY = "87739dd5547b95d421c8393082e13df0"

        private val apiService: BubbleApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://$BUBBLE_APP_NAME.bubbleapps.io/version-test/api/1.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(BubbleApiService::class.java)
        }

        // #################### MODIFICARE ####################
        // Funcția acceptă acum și un parametru "sursa"
        fun sendPhoneNumber(context: Context, phoneNumber: String, sursa: String) {
            if (phoneNumber.isBlank()) {
                Log.w("Uploader", "S-a încercat trimiterea unui număr gol. Se anulează.")
                return
            }
            Log.d("Uploader", "Se pregătește trimiterea numărului: $phoneNumber (Sursa: $sursa)")

            // Creăm obiectul BubbleClient incluzând și sursa
            val client = BubbleClient(telefon = phoneNumber, sursa = sursa)
            val authToken = "Bearer $BUBBLE_API_KEY"

            apiService.createClient(authToken, client).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("Uploader", "SUCCES! Datele pentru $phoneNumber au fost trimise la Bubble.")
                    } else {
                        Log.e("Uploader", "EROARE la trimitere. Cod: ${response.code()}. Mesaj: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Uploader", "EROARE DE REȚEA la trimitere.", t)
                }
            })
        }
        // ################## SFÂRȘIT MODIFICARE #################

        // Funcția "DETECTIV" care caută în agendă rămâne la fel
        fun findPhoneNumberForContact(context: Context, contactName: String): String? {
            Log.d("ContactSearch", "Se caută numărul pentru: '$contactName'")
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(contactName)

            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val number = cursor.getString(numberIndex)
                    Log.d("ContactSearch", "Număr găsit: $number")
                    return number
                }
            }
            Log.w("ContactSearch", "Nu s-a găsit niciun număr pentru '$contactName'.")
            return null
        }
    }
}

// Funcția MainScreen pentru UI
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Trimitere Manuală", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Introduceți numărul de telefon") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (phoneNumber.isNotBlank()) {
                // #################### MODIFICARE ####################
                // Trimitere sursa "Manual" pentru contactele adăugate din formular
                MainActivity.sendPhoneNumber(context, phoneNumber, "Manual")
                // ################## SFÂRȘIT MODIFICARE #################
                phoneNumber = ""
            }
        }) {
            Text("Trimite la Bubble")
        }
        Divider(modifier = Modifier.padding(vertical = 48.dp))
        Text("Configurare Servicii Automate", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            context.startActivity(intent)
        }) {
            Text("Activează Serviciul WhatsApp")
        }
    }
}
