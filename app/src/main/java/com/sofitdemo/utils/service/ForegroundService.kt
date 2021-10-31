package com.sofitdemo.utils.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sofitdemo.R
import com.sofitdemo.data.constant.AppConstant
import com.sofitdemo.ui.mainclass.MainActivity
import com.sofitdemo.utils.alarm.Alarm

class ForegroundService: Service() {

    override fun onBind(intent: Intent?): IBinder?  = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, AppConstant.Notification_CHANNEL_ID)
            .setContentTitle("Sofit Demo App")
            .setContentText("Sofit Running In Foreground")
            .setSmallIcon(R.drawable.ic_coins)
            .setContentIntent(pendingIntent).build()
        startForeground(AppConstant.Notification_ID, notification)
        Alarm.setAlarm(this)
        return START_STICKY
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                AppConstant.Notification_CHANNEL_ID,
                "Foreground Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }
}