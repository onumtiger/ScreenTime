package com.example.screentime
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class rescueTimeReminderActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.rescue_time_reminder)
    }
}