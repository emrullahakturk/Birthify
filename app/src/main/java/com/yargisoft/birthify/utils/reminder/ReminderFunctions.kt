package com.yargisoft.birthify.utils.reminder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.yargisoft.birthify.data.models.Birthday
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object ReminderFunctions {



    fun updateBirthdayReminder(birthday: Birthday, context: Context) {
        // Mevcut alarmı iptal et
        cancelBirthdayReminder(
            birthday.id,
            birthday.name,
            birthday.birthdayDate,
            context
        )

        // Yeni hatırlatıcıyı ayarla
        scheduleBirthdayReminder(
            birthday.id,
            birthday.name,
            birthday.birthdayDate,
            birthday.notifyDate,
            context
        )
    }
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleBirthdayReminder(birthdayId: String, birthdayName: String, birthdayDate: String, notifyDate: String, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BirthdayReminderReceiver::class.java).apply {
            putExtra("birthdayId", birthdayId)
            putExtra("birthdayName", birthdayName)
            putExtra("birthdayDate", birthdayDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            birthdayId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val reminderTime = calculateReminderTime(birthdayDate, notifyDate)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
        saveAlarmStatus(birthdayId, true, context)

    }

    private fun calculateReminderTime(birthdayDate: String, notifyDate: String): Long {
        // Tarih formatını tanımla
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)



        // Geçerli yılı al
        val currentYear = LocalDate.now().year


        // Doğum gününü geçerli yıl ile parse et
        val parsedBirthdayDate: LocalDate
        try {
            parsedBirthdayDate = LocalDate.parse("$birthdayDate $currentYear", formatter)
        } catch (e: DateTimeParseException) {
            // Hata durumunda loglama yap
            e.printStackTrace()
            throw IllegalArgumentException("Invalid date format for birthdayDate: $birthdayDate")
        }

        // Hatırlatıcı tarihini hesapla
        val reminderDateTime = when (notifyDate) {
            "On the day" -> parsedBirthdayDate.atTime(0, 0) // Doğum günü tarihinde saat 12:00'de
            "1 day ago" -> parsedBirthdayDate.minusDays(1).atTime(0, 0) // 1 gün önce saat 00:00'da
            "1 week ago" -> parsedBirthdayDate.minusWeeks(1).atTime(0, 0) // 1 hafta önce saat 00:00'da
            "1 month ago" -> parsedBirthdayDate.minusMonths(1).atTime(0, 0) // 1 ay önce saat 00:00'da
            else -> throw IllegalArgumentException("Invalid Notifiy Date")
        }

        // Zamanı Epoch millisaniye cinsinden döndür
        return reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }


    fun cancelBirthdayReminder(birthdayId: String, birthdayName:String, birthdayDate:String, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BirthdayReminderReceiver::class.java).apply {
            putExtra("birthdayId", birthdayId)
            putExtra("birthdayName", birthdayName)
            putExtra("birthdayDate", birthdayDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            birthdayId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null){
            alarmManager.cancel(pendingIntent)
            saveAlarmStatus(birthdayId, false, context)
        }

    }


    fun requestExactAlarmPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        activity.startActivity(intent)
    }


    fun saveAlarmStatus(birthdayId: String, isScheduled: Boolean, context: Context) {
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(birthdayId, isScheduled)
        editor.apply()
    }

    fun isAlarmScheduled(birthdayId: String, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("AlarmPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(birthdayId, false)
    }

}