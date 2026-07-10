package com.example.hourlyalarm58

import android.Manifest
import android.app.AlarmManager
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = AppPreferences(this)
        val scheduler = AlarmScheduler(this)
        setContent { AlarmApp(prefs, scheduler, ::requestPermissions, ::toggleAlarm, ::testAlarm) }
    }
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (!AlarmScheduler(this).canScheduleExactAlarms() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName")))
        }
    }
    private fun toggleAlarm(enabled: Boolean) = lifecycleScope.launch {
        val prefs = AppPreferences(this@MainActivity)
        prefs.setAlarmEnabled(enabled)
        if (enabled && AlarmScheduler(this@MainActivity).canScheduleExactAlarms()) prefs.setNextAlarmTime(AlarmScheduler(this@MainActivity).scheduleNext())
        else { AlarmScheduler(this@MainActivity).cancel(); prefs.setNextAlarmTime(0L) }
    }
    private fun testAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(Intent(this, AlarmSoundService::class.java)) else startService(Intent(this, AlarmSoundService::class.java))
    }
}

@Composable
fun AlarmApp(prefs: AppPreferences, scheduler: AlarmScheduler, requestPermissions: () -> Unit, toggleAlarm: (Boolean) -> Unit, testAlarm: () -> Unit) {
    val enabled by prefs.alarmEnabled.collectAsState(initial = false)
    val nextTime by prefs.nextAlarmTime.collectAsState(initial = 0L)
    val context = LocalContext.current
    var exactAllowed by remember { mutableStateOf(scheduler.canScheduleExactAlarms()) }
    var notificationsAllowed by remember { mutableStateOf(Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) }
    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF9C6BFF), background = Color(0xFF08080A), surface = Color(0xFF17171C))) {
        Surface(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(24.dp))
                Text("Saat 58 Alarm", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Her saat 58 geçe çalar", color = Color(0xFFB9B9C3))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) { Text(if (enabled) "Alarm aktif" else "Alarm pasif", color = Color.White, fontSize = 20.sp); Text("Açıkken her saat tek alarm kurulur", color = Color(0xFF9A9AA4)) }
                            Switch(checked = enabled, onCheckedChange = toggleAlarm, enabled = exactAllowed)
                        }
                        Text("Sonraki alarm: ${formatTime(nextTime)}", color = Color.White, fontSize = 18.sp)
                    }
                }
                StatusCard("Bildirim izni", if (notificationsAllowed) "Açık" else "Kapalı")
                StatusCard("Exact alarm izni", if (exactAllowed) "Açık" else "Kapalı")
                Button(onClick = { requestPermissions(); exactAllowed = scheduler.canScheduleExactAlarms(); notificationsAllowed = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED }, modifier = Modifier.fillMaxWidth()) { Text("İzinleri Aç") }
                OutlinedButton(onClick = testAlarm, modifier = Modifier.fillMaxWidth()) { Text("Test Alarmı Çal") }
            }
        }
    }
}

@Composable fun StatusCard(title: String, value: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF17171C)), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(title, color = Color(0xFFB9B9C3)); Text(value, color = Color.White) } } }
fun formatTime(time: Long): String = if (time <= 0L) "-" else SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
