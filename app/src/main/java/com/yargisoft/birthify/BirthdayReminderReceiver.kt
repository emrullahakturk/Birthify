package com.yargisoft.birthify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class BirthdayReminderReceiver : BroadcastReceiver() {
    private lateinit var notificationText : String
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



        // Bildirim metni
        val notificationTextEnglish = when {
            daysDifference < 0 -> "${birthdayName}'s birthday has passed."
            daysDifference == 0L -> "It's $birthdayName's birthday today!"
            else -> "$birthdayName's birthday is coming up on $birthdayDate."
        }

        val notificationTextTurkish= when {
            daysDifference < 0 -> "${birthdayName}'s birthday has passed."
            daysDifference == 0L -> "It's $birthdayName's birthday today!"
            else -> "$birthdayName's birthday is coming up on $birthdayDate."
        }

        notificationText = when(Locale.getDefault().language){
            "English" -> notificationTextEnglish
            "Türkçe" -> notificationTextTurkish
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