package com.example.otpgenerator

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private val channelId = "channelId"
    private val channelName = "channelName"
    private val notificationId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val generatedOTP = generateOTP()

        val verifyButton = findViewById<Button>(R.id.verifyButton)
        val userInputEditText = findViewById<EditText>(R.id.userInputEditText)

        verifyButton.setOnClickListener {
            val userInput = userInputEditText.text.toString()

            if (userInput.isNotEmpty() && userInput == generatedOTP) {
                Toast.makeText(this, "OTP Verified successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        val sendOTPButton = findViewById<Button>(R.id.notify)
        sendOTPButton.setOnClickListener {
            sendOTPNotification(generatedOTP)
            Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateOTP(): String {
        val otp = StringBuilder()
        val random = Random(System.currentTimeMillis())

        repeat(4) {
            val digit = random.nextInt(10)
            otp.append(digit)
        }

        return otp.toString()
    }

    @SuppressLint("MissingPermission")
    private fun sendOTPNotification(otp: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Add the flag here
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("OTP Notification")
            .setContentText("Your OTP: $otp")
            .setSmallIcon(R.drawable.baseline_emoji_emotions_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This is my notification channel"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}