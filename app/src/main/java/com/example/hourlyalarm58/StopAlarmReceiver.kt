package com.example.hourlyalarm58

import android.content.*

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        context.startService(Intent(context, AlarmSoundService::class.java).setAction(AlarmSoundService.ACTION_STOP))
    }
}
