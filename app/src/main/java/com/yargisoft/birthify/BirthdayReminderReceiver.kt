package com.yargisoft.birthify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class BirthdayReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val birthdayName = intent.getStringExtra("BIRTHDAY_NAME")
        val birthdayId = intent.getStringExtra("BIRTHDAY_ID")

        // Bildirimi göster
        showNotification(context, birthdayName)
    }

    private fun showNotification(context: Context, birthdayName: String?) {
        val notificationBuilder = NotificationCompat.Builder(context, "birthday_channel")
            .setSmallIcon(R.drawable.ic_cake)
            .setContentTitle("Birthday Reminder")
            .setContentText("Don't forget to wish $birthdayName a Happy Birthday!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
