package com.yargisoft.birthify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class BirthdayReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Bildirim göndermek için gerekli işlemleri burada yapacaksınız
        val birthdayId = intent.getStringExtra("birthdayId") // Doğum günü ID'sini al
        val birthdayName = intent.getStringExtra("birthdayName") // Doğum günü adını al

        // Bildirimi oluştur ve göster
        createNotification(context, birthdayId, birthdayName)
    }

    private fun createNotification(context: Context, birthdayId: String?, birthdayName: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "birthday_reminder_channel"
        val channelName = "Birthday Reminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, birthdayId.hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Birthify")
            .setContentText("It's $birthdayName's birthday today!")
            .setSmallIcon(R.drawable.ic_cake)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(birthdayId.hashCode(), notification)
    }
}
