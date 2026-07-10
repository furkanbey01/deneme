package com.example.hourlyalarm58

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.*

class AlarmSoundService : Service() {
    private var player: MediaPlayer? = null
    private var toneGenerator: ToneGenerator? = null
    private var toneThread: Thread? = null
    private var vibrator: Vibrator? = null
    override fun onBind(intent: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) { stopAlarm(true); return START_NOT_STICKY }
        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.alarmNotification(this))
        if (player == null && toneThread == null) startSound()
        startVibration()
        return START_STICKY
    }
    private fun startSound() {
        val soundId = resources.getIdentifier("alarm_sound", "raw", packageName)
        if (soundId != 0) {
            player = MediaPlayer.create(this, soundId)?.apply { isLooping = true; start() }
            if (player != null) return
        }
        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        toneThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 700)
                Thread.sleep(1000)
            }
        }.apply { start() }
    }
    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) getSystemService(VibratorManager::class.java).defaultVibrator else @Suppress("DEPRECATION") getSystemService(Vibrator::class.java)
        val effect = VibrationEffect.createWaveform(longArrayOf(0, 700, 400, 700), 0)
        vibrator?.vibrate(effect)
    }
    private fun stopAlarm(stopService: Boolean) {
        vibrator?.cancel(); vibrator = null
        toneThread?.interrupt(); toneThread = null
        toneGenerator?.release(); toneGenerator = null
        player?.let {
            runCatching { it.stop() }
            it.release()
        }
        player = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (stopService) stopSelf()
    }
    override fun onDestroy() { stopAlarm(false); super.onDestroy() }
    companion object { const val ACTION_STOP = "com.example.hourlyalarm58.STOP_ALARM" }
}
