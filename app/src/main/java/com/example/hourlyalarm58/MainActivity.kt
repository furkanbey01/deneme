package com.example.hourlyalarm58

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val scope = MainScope()
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = AppPreferences(this)
        val scheduler = AlarmScheduler(this)
        setContent {
            AlarmScreen(
                preferences = preferences,
                scheduler = scheduler,
                onPermissionsClick = ::openPermissions,
                onEnabledChange = ::setAlarmEnabled,
                onTestClick = ::startTestAlarm,
            )
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun setAlarmEnabled(enabled: Boolean) {
        scope.launch {
            val scheduler = AlarmScheduler(this@MainActivity)
            val preferences = AppPreferences(this@MainActivity)
            preferences.setAlarmEnabled(enabled)
            if (enabled && scheduler.canScheduleExactAlarms()) {
                preferences.setNextAlarmTime(scheduler.scheduleNext())
            } else {
                scheduler.cancel()
                preferences.setNextAlarmTime(0L)
            }
        }
    }

    private fun openPermissions() {
        if (Build.VERSION.SDK_INT >= 33 && !hasNotificationPermission()) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !AlarmScheduler(this).canScheduleExactAlarms()) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
        }
    }

    private fun startTestAlarm() {
        val intent = Intent(this, AlarmSoundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent) else startService(intent)
    }

    private fun hasNotificationPermission(): Boolean {
        return Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
private fun AlarmScreen(
    preferences: AppPreferences,
    scheduler: AlarmScheduler,
    onPermissionsClick: () -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onTestClick: () -> Unit,
) {
    val context = LocalContext.current
    val enabled by preferences.alarmEnabled.collectAsState(initial = false)
    val nextAlarmTime by preferences.nextAlarmTime.collectAsState(initial = 0L)
    var exactAllowed by remember { mutableStateOf(scheduler.canScheduleExactAlarms()) }
    var notificationAllowed by remember {
        mutableStateOf(Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF9E7BFF),
            background = Color(0xFF08080B),
            surface = Color(0xFF17171D),
        ),
    ) {
        Surface(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Spacer(Modifier.height(28.dp))
                Text("Saat 58 Alarm", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Text("Her saat 58 geçe çalar", color = Color(0xFFB9B9C5))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(if (enabled) "Alarm aktif" else "Alarm pasif", color = Color.White, fontSize = 20.sp)
                                Text("Tek alarm kurulur, tetiklenince yenilenir", color = Color(0xFF9A9AA4))
                            }
                            Switch(checked = enabled, onCheckedChange = onEnabledChange, enabled = exactAllowed)
                        }
                        Text("Sonraki alarm: ${formatAlarmTime(nextAlarmTime)}", color = Color.White, fontSize = 18.sp)
                    }
                }

                StatusCard("Bildirim izni", if (notificationAllowed) "Açık" else "Kapalı")
                StatusCard("Exact alarm izni", if (exactAllowed) "Açık" else "Kapalı")

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onPermissionsClick()
                        exactAllowed = scheduler.canScheduleExactAlarms()
                        notificationAllowed = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    },
                ) { Text("İzinleri Aç") }

                OutlinedButton(modifier = Modifier.fillMaxWidth(), onClick = onTestClick) {
                    Text("Test Alarmı Çal")
                }
            }
        }
    }
}

@Composable
private fun StatusCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF17171D))) {
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color(0xFFB9B9C5))
            Text(value, color = Color.White)
        }
    }
}

private fun formatAlarmTime(epochMillis: Long): String {
    return if (epochMillis <= 0L) "-" else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(epochMillis))
}
