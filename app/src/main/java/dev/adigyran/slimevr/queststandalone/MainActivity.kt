package dev.adigyran.slimevr.queststandalone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.adigyran.slimevr.queststandalone.standalone.databinding.ActivityMainBinding
import dev.adigyran.slimevr.queststandalone.testservice.TestForegroundService

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = provideViewBinding(layoutInflater)
        val view = binding.root
        setContentView(view)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(logReceiver, IntentFilter("TIMR"))
        startTestService()


    }

    private fun provideViewBinding(layoutInflater: LayoutInflater): ActivityMainBinding  = ActivityMainBinding.inflate(layoutInflater)

    private fun startTestService()
    {

        val testServiceIntent = Intent(this, TestForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(testServiceIntent)
        }
        else
        {
            this.startService(testServiceIntent)
        }
    }

    private val logReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "TIMR" -> {
                    val timerValue = intent.getStringExtra("VLU")
                    runOnUiThread { binding.numbr.text = timerValue }


                  return
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logReceiver)
        val intent = Intent("kill-ze-service")
        this.sendBroadcast(intent)
        super.onDestroy()

    }
}