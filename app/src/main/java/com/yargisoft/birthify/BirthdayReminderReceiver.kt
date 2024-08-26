package com.yargisoft.birthify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class BirthdayReminderReceiver(context: Context): BroadcastReceiver() {
    private lateinit var notificationText : String
    val preferences: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

    override fun onReceive(context: Context, intent: Intent) {

        val birthdayId = intent.getStringExtra("birthdayId") // Doğum günü ID'sini al
        val birthdayName = intent.getStringExtra("birthdayName") // Doğum günü adını al
        val birthdayDate = intent.getStringExtra("birthdayDate") // Doğum günü tarihini al

        createNotification(context, birthdayId, birthdayName,birthdayDate)
    }

    private fun createNotification(
        context: Context,
        birthdayId: String?,
        birthdayName: String?,
        birthdayDate: String?
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "birthday_reminder_channel"
        val channelName = "Birthday Reminder"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            birthdayId.hashCode(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Tarih formatı ve günümüz tarihi
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
        val birthday = LocalDate.parse("$birthdayDate ${LocalDate.now().year}", formatter)
        val today = LocalDate.now()

        // Tarih farkı
        val daysDifference = ChronoUnit.DAYS.between(today, birthday)



        // Notification text
        val notificationTextEnglish = when {
            daysDifference < 0 -> "${birthdayName}'s birthday has already passed, leaving us with cherished memories."
            daysDifference == 0L -> "Today is ${birthdayName}'s birthday! Time to celebrate!"
            else -> "${birthdayName}'s birthday is approaching on $birthdayDate. Mark your calendar!"
        }

        val notificationTextTurkish = when {
            daysDifference < 0 -> "${birthdayName}'nin doğum günü geçmişin tatlı hatıraları arasında yerini aldı."
            daysDifference == 0L -> "Bugün ${birthdayName}'nin doğum günü! Kutlamalar başlasın!"
            else -> "${birthdayName}'nin doğum günü ${birthdayDate} tarihinde yaklaşıyor. Tarihi takvimlerinize not edin!"
        }


        notificationText = when(preferences.getString("AppLanguage", null)){
            "en" -> notificationTextEnglish
            "tr" -> notificationTextTurkish
            else -> notificationTextEnglish
        }


        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Birthify")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_cake)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(birthdayId.hashCode(), notification)
    }
}