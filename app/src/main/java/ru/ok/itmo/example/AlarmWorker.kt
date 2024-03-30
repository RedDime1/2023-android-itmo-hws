package ru.ok.itmo.example

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class AlarmWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val channel = NotificationChannel("id", "alarm", NotificationManager.IMPORTANCE_DEFAULT)
            .apply {
                description = "mda"
            }
        applicationContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(applicationContext, "id")
            .setContentTitle("Good night")
            .setContentText("mda... Wake up")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_MAX)
        NotificationManagerCompat.from(applicationContext).notify(566, notification.build())
        return Result.success()
    }

    companion object {

    fun constructAlarm(context: Context, timeInMillis: Long) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .setRequiresStorageNotLow(false)
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()

        WorkManager.getInstance(context).enqueue(
            OneTimeWorkRequest.Builder(AlarmWorker::class.java)
                .setConstraints(constraints)
                .setInputData(Data.Builder().putLong("time", timeInMillis).build())
                .setInitialDelay(timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addTag("tag")
                .build()
        )
    }
}
}