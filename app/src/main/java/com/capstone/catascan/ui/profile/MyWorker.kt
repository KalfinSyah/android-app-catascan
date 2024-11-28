package com.capstone.catascan.ui.profile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.capstone.catascan.MainActivity

class MyWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result = getReminder()

    private fun getReminder(): Result {
        val notifDetailIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(notifDetailIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(com.capstone.catascan.R.drawable.ic_notification)
            .setContentTitle("Catascan")
            .setContentText("Are you worried about your eye health? Scan your eye for free now!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification.build())

        return Result.success()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_1"
        const val CHANNEL_NAME = "catascan_channel"
    }
}