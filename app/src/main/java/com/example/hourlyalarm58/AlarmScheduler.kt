package com.example.hourlyalarm58

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    fun canScheduleExactAlarms(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    fun scheduleNext(): Long {
        val triggerAt = calculateNextAlarmTime()
        val intent = PendingIntent.getBroadcast(context, 58, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val showIntent = PendingIntent.getActivity(context, 59, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAt, showIntent), intent)
        return triggerAt
    }
    fun cancel() {
        val intent = PendingIntent.getBroadcast(context, 58, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(intent)
    }
    companion object {
        fun calculateNextAlarmTime(now: Long = System.currentTimeMillis()): Long {
            val cal = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.MINUTE, 58); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= now) add(Calendar.HOUR_OF_DAY, 1)
            }
            return cal.timeInMillis
        }
    }
}
