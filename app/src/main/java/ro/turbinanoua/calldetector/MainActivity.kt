package ro.turbinanoua.calldetector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ro.turbinanoua.calldetector.ui.theme.TurbinaNouaCallTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Permisiuni esențiale
        checkPermissions()

        // 🔔 Deschide ecranul pentru permisiunea de notificări
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

        Toast.makeText(
            this,
            "✅ Activează permisiunea de notificări pentru aplicație",
            Toast.LENGTH_LONG
        ).show()

        // 🧱 Interfață Compose
        enableEdgeToEdge()
        setContent {
            TurbinaNouaCallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private fun checkPermissions() {
        val phoneStatePermission = Manifest.permission.READ_PHONE_STATE
        val callLogPermission = Manifest.permission.READ_CALL_LOG
        val sendSmsPermission = Manifest.permission.SEND_SMS

        if (ContextCompat.checkSelfPermission(this, phoneStatePermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, callLogPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, sendSmsPermission) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(phoneStatePermission, callLogPermission, sendSmsPermission),
                100
            )
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TurbinaNouaCall este activă",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Această aplicație detectează apelurile și afișează cine te sună.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            // 🔁 Apelează metoda de test cu număr fictiv
            CallReceiver().testNumarApel(context, "+40712345678")
        }) {
            Text("Trimite număr test")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TurbinaNouaCallTheme {
        MainScreen()
    }
}
