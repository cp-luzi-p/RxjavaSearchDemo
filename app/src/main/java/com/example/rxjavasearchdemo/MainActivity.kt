package com.example.rxjavasearchdemo

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.rxjavasearchdemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            initView(it)
        }
    }

    private fun initView(binding: ActivityMainBinding){
        setSupportActionBar(binding.toolbar)

        binding.contentLayout.btnLocalSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, LocalSearchActivity::class.java))
        }

        binding.contentLayout.btnRemoteSearch.setOnClickListener {
            startActivity(Intent(this@MainActivity, RemoteSearchActivity::class.java))
        }

        whiteNotificationBar(binding.toolbar)

    }

    private fun whiteNotificationBar(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = toolbar.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            toolbar.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }
}