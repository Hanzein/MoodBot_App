package com.farhanadi.moodbotapp.view.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.farhanadi.moodbotapp.databinding.ActivityNotificationBinding
import com.farhanadi.moodbotapp.databinding.ItemNotificationBinding
import com.farhanadi.moodbotapp.view.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Notification(
    val time: String
)
class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val notifications = mutableListOf<Notification>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        createNotificationChannel()

        // Set up RecyclerView with binding
        val itemNotificationBinding = ItemNotificationBinding.inflate(layoutInflater)
        adapter = NotificationAdapter(itemNotificationBinding, notifications) { position ->
            removeNotification(position)
        }
        binding.rvNotifcard.layoutManager = LinearLayoutManager(this)
        binding.rvNotifcard.adapter = adapter
    }

    private fun setupAction() {
        binding.btnBacknotif.setOnClickListener {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddpengingat.setOnClickListener {
            showTimePickerDialog()
        }

        binding.btnShowpengingat.setOnClickListener {
            val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

            if (notificationsEnabled) {
                disableNotifications()
                Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
            } else {
                enableNotifications()
                Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            calendar.set(Calendar.SECOND, 0)
            val timeString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)
            setReminder(calendar.timeInMillis, timeString)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun setReminder(timeInMillis: Long, timeString: String) {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        notifications.add(Notification(timeString))
        adapter.notifyItemInserted(notifications.size - 1)

        Toast.makeText(this, "Reminder Set", Toast.LENGTH_SHORT).show()
    }

    private fun removeNotification(position: Int) {
        notifications.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    private fun enableNotifications() {
        val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", true).apply()
    }

    private fun disableNotifications() {
        val prefs = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("notifications_enabled", false).apply()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("notifyReminder", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
