package com.sofitdemo.utils.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.sofitdemo.R
import com.sofitdemo.data.repos.AppRepository
import com.sofitdemo.ui.mainclass.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class Alarm : BroadcastReceiver() {
    @Inject
    lateinit var appRepository: AppRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        //we get name, image and description using
        //appRepository.getFavouriteDrinks()
        showNotification(context,"Image","Apple Malt","aldpoakcokacpopockwpocpowkcokwocwpokcowkcpowk")
    }
    fun showNotification(context: Context?,image:String,name:String,description:String) {
        val NOTIFICATION_CHANNEL_ID = "com.myapp"
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("notification", 1)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(
                context, getTwoDigitRandomNo(), intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val defaultSoundUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val remoteCollapsedViews =
                RemoteViews(context?.applicationContext?.packageName, R.layout.notification_design)
            remoteCollapsedViews.setTextViewText(R.id.txtDrinkName, name)
            remoteCollapsedViews.setTextViewText(R.id.drinksDescription, description)
            remoteCollapsedViews.setImageViewResource(R.id.cameraImage, R.drawable.app_splash_icon)
            val notificationBuilder = NotificationCompat.Builder(context)
            notificationBuilder.setCustomBigContentView(remoteCollapsedViews)
            notificationBuilder.setSmallIcon(R.drawable.app_splash_icon)

            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setVibrate(longArrayOf(1000, 1000))
            notificationBuilder.setContentIntent(pendingIntent)

            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Custom Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000, 1000)
                assert(notificationManager != null)
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(
                1012,
                notificationBuilder.build()
            )
        }
    }
    fun getTwoDigitRandomNo(): Int {
        return Random().nextInt(90) + 10
    }

    companion object {
        var calender:Calendar? = null

        fun setAlarm(context: Context?) {

            calender = Calendar.getInstance()
            calender?.set(Calendar.HOUR_OF_DAY,2)
            calender?.set(Calendar.MINUTE,0)
            calender?.set(Calendar.SECOND,0)
            calender?.set(Calendar.MILLISECOND,0)

            val am = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(context, Alarm::class.java)
            val pi = PendingIntent.getBroadcast(context, 0, i, 0)
            am.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calender!!.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pi
            )
            Toast.makeText(context, "Alarm Set at 2 PM", Toast.LENGTH_SHORT).show()
        }
    }


/*
    @Inject
    lateinit var appRepository: AppRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        val pm = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app:SofitAlarm")
        wl.acquire(10 * 60 * 1000L *//*10 minutes*//*)

        simpleNotification(context)
        *//*var drink: List<FavouriteDrinksDb>? = appRepository.getFavouriteDrinks()

        if (drink != null) {
            if (drink.isNotEmpty()) {
                drink[0].idDrink
                drink[0].strDrinkThumb?.let {
                    drink[0].strDrink?.let { it1 ->
                        drink[0].strInstructions?.let { it2 ->
                            showNotification(context,
                                it, it1, it2
                            )
                        }
                    }
                }
            }
        }else{
            //Show Simple Notification
            simpleNotification(context)
        }*//*
        wl.release()
    }

    private fun simpleNotification(context: Context?) {
        val mBuilder = NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.app_splash_icon)
            .setContentTitle("Alert")
            .setContentText("Need some drinks open app now")
            .setAutoCancel(true)
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, getTwoDigitRandomNo(), intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        mBuilder.setContentIntent(pendingIntent)
        val mNotificationManager =
            context?.getSystemService( Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager!!.notify(10, mBuilder.build())
    }

    companion object {
        fun setAlarm(context: Context) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(context, Alarm::class.java)
            val pi = PendingIntent.getBroadcast(context, 0, i, 0)
            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                (1000 * 60 * 1).toLong(),
                pi
            ) // Millisec * Second * Minute
        }

        fun cancelAlarm(context: Context) {
            val intent = Intent(context, Alarm::class.java)
            val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
        }
    }

    *//*fun showNotification(context: Context,image:String,name:String,description:String) {
        val NOTIFICATION_CHANNEL_ID = "com.myapp"
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("notification", 1)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (intent != null) {
            val pendingIntent = PendingIntent.getActivity(
                context, getTwoDigitRandomNo(), intent,
                PendingIntent.FLAG_ONE_SHOT
            )
            val defaultSoundUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val remoteCollapsedViews =
                RemoteViews(context.applicationContext.packageName, R.layout.notification_design)
            remoteCollapsedViews.setTextViewText(R.id.txtDrinkName, name)
            remoteCollapsedViews.setTextViewText(R.id.drinksDescription, description)
            remoteCollapsedViews.setImageViewUri(R.id.cameraImage, Uri.parse(image))
            val notificationBuilder = NotificationCompat.Builder(context)
            notificationBuilder.setCustomBigContentView(remoteCollapsedViews)
            notificationBuilder.setSmallIcon(R.drawable.app_splash_icon)

            notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setVibrate(longArrayOf(1000, 1000))
            notificationBuilder.setContentIntent(pendingIntent)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Custom Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000, 1000)
                assert(notificationManager != null)
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(notificationChannel)
            }
            notificationManager.notify(
                1012,
                notificationBuilder.build()
            )
        }
    }*//*

    private fun getTime(): String {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH:mm")
        val strDate = mdformat.format(calendar.time)
        return strDate
    }

    fun getTwoDigitRandomNo(): Int {
        return Random().nextInt(90) + 10
    }*/
}