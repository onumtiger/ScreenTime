package com.example.screentime
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class rescueTimeReminderActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("rescueTimeReminderActivity", "rescueTimeReminderActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.rescue_time_reminder)
    }
}