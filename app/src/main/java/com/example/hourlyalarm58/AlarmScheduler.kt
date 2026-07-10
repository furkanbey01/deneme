package com.example.hourlyalarm58

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun canScheduleExactAlarms(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }

    fun scheduleNext(): Long {
        val triggerAtMillis = nextMinute58Millis()
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAtMillis, openAppIntent()),
            alarmIntent(),
        )
        return triggerAtMillis
    }

    fun cancel() {
        alarmManager.cancel(alarmIntent())
    }

    private fun alarmIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            REQUEST_ALARM,
            Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun openAppIntent(): PendingIntent {
        return PendingIntent.getActivity(
            context,
            REQUEST_OPEN_APP,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private const val REQUEST_ALARM = 5800
        private const val REQUEST_OPEN_APP = 5801

        fun nextMinute58Millis(nowMillis: Long = System.currentTimeMillis()): Long {
            return Calendar.getInstance().apply {
                timeInMillis = nowMillis
                set(Calendar.MINUTE, 58)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= nowMillis) add(Calendar.HOUR_OF_DAY, 1)
            }.timeInMillis
        }
    }
}
