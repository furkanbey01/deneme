package com.example.hourlyalarm58

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AlarmSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var fallbackTone: ToneGenerator? = null
    private var fallbackToneThread: Thread? = null
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAlarm(stopService = false)
            return START_NOT_STICKY
        }

        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.alarmNotification(this))
        startSoundIfNeeded()
        startVibration()
        return START_STICKY
    }

    override fun onDestroy() {
        stopAlarm(stopService = false)
        super.onDestroy()
    }

    private fun startSoundIfNeeded() {
        if (mediaPlayer != null || fallbackToneThread != null) return

        val rawSoundId = resources.getIdentifier("alarm_sound", "raw", packageName)
        if (rawSoundId != 0) {
            mediaPlayer = MediaPlayer.create(this, rawSoundId)?.apply {
                isLooping = true
                start()
            }
            if (mediaPlayer != null) return
        }

        fallbackTone = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        fallbackToneThread = Thread {
            while (!Thread.currentThread().isInterrupted) {
                fallbackTone?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 700)
                Thread.sleep(1_000)
            }
        }.apply { start() }
    }

    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Vibrator::class.java)
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 700, 400, 700), 0))
    }

    private fun stopAlarm(stopService: Boolean) {
        vibrator?.cancel()
        vibrator = null
        fallbackToneThread?.interrupt()
        fallbackToneThread = null
        fallbackTone?.release()
        fallbackTone = null
        mediaPlayer?.let { player ->
            runCatching { player.stop() }
            player.release()
        }
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (stopService) stopSelf()
    }

    companion object {
        const val ACTION_STOP = "com.example.hourlyalarm58.action.STOP_ALARM"
    }
}
