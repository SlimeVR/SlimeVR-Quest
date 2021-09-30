package dev.adigyran.slimevr.queststandalone.testservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.adigyran.slimevr.queststandalone.standalone.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import java.util.concurrent.TimeUnit

class TestForegroundService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null
    private val disposable = CompositeDisposable()
    private var timer:Long = 0L
    override fun onCreate() {
        instance = this
    }



    var on_death: Runnable? = Runnable {
        stopSelf()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TestForegroundService::class.java.name, "onStartCommand: ")
        showForegroundNotification()
        disposable.add(Observable.interval(1000,TimeUnit.MILLISECONDS)
            .doOnNext { Log.d(TestForegroundService::class.java.name, "onStartCommand timer: $it")
                timer++
                sendTimerValue()
            }
            .subscribe())
        var tag = "aditrack::BackgroundTrackingSync"
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag)
        wakeLock?.acquire(10*60*1000L /*10 minutes*/)
        return START_STICKY
    }

    private fun sendTimerValue()
    {
        val timerIntent= Intent("TIMR")
        timerIntent.putExtra("VLU","%d".format(timer))
        LocalBroadcastManager.getInstance(this).sendBroadcast(timerIntent)
    }

    private val localBinder: IBinder = TrackingBinder()

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (on_death != null) on_death!!.run()
        }
    }

    override fun onDestroy() {
        instance = null
        disposable.clear()
        wakeLock?.release()
        unregisterReceiver(broadcastReceiver)
    }

    private fun showForegroundNotification()
    {
        val channelId = getString(R.string.default_notification_channel_id)
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(
                    "NOTIFICATION_CHANNEL_ID",
                    "namee",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        registerReceiver(broadcastReceiver, IntentFilter("kill-ze-service"))
        val intent = Intent("kill-ze-service")
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "NOTIFICATION_CHANNEL_ID")
            .setContentTitle("SlimeVRForegroundTest")
            .setTicker("SlimeVRForegroundTest")
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(0, "Stop", pendingIntent)
            .setOngoing(true).build()
        startForeground(1001, notification)

    }

    inner class TrackingBinder : Binder() {
        val service: TestForegroundService
            get() = this@TestForegroundService
    }

    companion object {
        private var instance: TestForegroundService? = null
        val isInstanceCreated: Boolean
            get() = instance != null
    }
}