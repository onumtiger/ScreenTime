package com.onumbu.screentimenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.*


class noMeasuresTwoActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.no_measures_two)
        val swipeRefreshView: SwipeRefreshLayout = findViewById(R.id.swiperefresh)
        val infoTextView: TextView = findViewById(R.id.InfoText)
        val currentDate:String = intent.getStringExtra("currentDate").toString()
        val startDate:String = intent.getStringExtra("startDate").toString()
        val daysUntilStart = daysUntilStart(currentDate, startDate)
        if (daysUntilStart > 0) {
            infoTextView.text = "Currently your ScreenTime behavior is measured. \nThe actual study ends in $daysUntilStart days."
        } else {
            infoTextView.text = "The study has ended. Thanks for participating!"
        }

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

    fun daysUntilStart(currentDate: String, startDate: String): Int {
        val startDay = startDate!!.substringBefore(".")
        val startMonth = startDate!!.substringAfter(".")

        val currentDay = currentDate.substringBefore(".")
        val currentMonth = currentDate.substringAfter(".")

        var difference: Number

        if (startMonth == currentMonth) {
            difference = currentDay.toInt() - startDay.toInt()
        } else {
            difference = 30 - startDay.toInt() + currentDay.toInt()
        }

        return 28 - difference
    }
}