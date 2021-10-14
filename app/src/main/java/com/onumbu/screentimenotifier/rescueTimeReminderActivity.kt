package com.onumbu.screentimenotifier
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.*

class rescueTimeReminderActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.rescue_time_reminder)
        val swipeRefreshView: SwipeRefreshLayout = findViewById(R.id.swiperefresh)

        val sdf = SimpleDateFormat("dd.M")
        val lastUpdated = sdf.format(Date())
        swipeRefreshView.setOnRefreshListener {
            val dateNow = sdf.format(Date())
            if (lastUpdated != dateNow){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                swipeRefreshView.isRefreshing = false
            }
        }
    }
}