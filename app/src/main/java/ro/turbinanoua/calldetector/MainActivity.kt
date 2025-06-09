package com.example.turbinanouacall

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main.xml)

        // Logica pentru butonul de trimitere manuală
        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)
        val buttonSend = findViewById<Button>(R.id.buttonSend)

        buttonSend.setOnClickListener {
            val phoneNumber = editTextPhone.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                BubbleUploader.sendPhoneNumber(this, phoneNumber)
                editTextPhone.text.clear()
            }
        }

        // Logica pentru butonul de activare a serviciului de notificări
        val buttonEnableNotifications = findViewById<Button>(R.id.buttonEnableNotifications)
        buttonEnableNotifications.setOnClickListener {
            // Creează o intenție de a deschide setările sistemului pentru "Notification Listener"
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
    }
}