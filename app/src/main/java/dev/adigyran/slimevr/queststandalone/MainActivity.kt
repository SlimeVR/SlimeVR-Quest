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

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = provideViewBinding(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun provideViewBinding(layoutInflater: LayoutInflater): ActivityMainBinding  = ActivityMainBinding.inflate(layoutInflater)


    override fun onDestroy() {
        super.onDestroy()

    }
}