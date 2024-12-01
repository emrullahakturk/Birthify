package com.yargisoft.birthify.utils.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.saveAlarmStatus

class CancelRemindersWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Doğum günü listesi parametreden alınacak
        val birthdayIds = inputData.getStringArray("BIRTHDAY_IDS") ?: return Result.failure()

        // Tüm doğum günleri için hatırlatıcıları iptal et
        birthdayIds.forEach { birthdayId ->
            cancelBirthdayReminder(birthdayId, applicationContext)
        }

        return Result.success()
    }

    private fun cancelBirthdayReminder(birthdayId: String, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BirthdayReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            birthdayId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null){
            alarmManager.cancel(pendingIntent)
            saveAlarmStatus(birthdayId, false, context)
        }

    }
}
