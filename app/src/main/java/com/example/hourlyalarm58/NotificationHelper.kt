package com.example.hourlyalarm58

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    const val CHANNEL_ID = "alarm_channel"
    const val NOTIFICATION_ID = 5800
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Alarm", NotificationManager.IMPORTANCE_HIGH)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    fun alarmNotification(context: Context): Notification {
        ensureChannel(context)
        val stopIntent = PendingIntent.getBroadcast(context, 60, Intent(context, StopAlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val contentIntent = PendingIntent.getActivity(context, 61, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarm çalıyor")
            .setContentText("Kapatmak için dokun")
            .setContentIntent(contentIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Kapat", stopIntent)
            .build()
    }
}
