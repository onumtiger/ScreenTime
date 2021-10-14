package com.example.screentime

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class noMeasuresTwoActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.no_measures_two)
        val infoTextView: TextView = findViewById(R.id.InfoText)
        val currentDate:String = intent.getStringExtra("currentDate").toString()
        val startDate:String = intent.getStringExtra("startDate").toString()
        val daysUntilStart = daysUntilStart(currentDate, startDate)
        if (daysUntilStart > 0) {
            infoTextView.text = "Currently your ScreenTime behavior is measured. \nThe actual study ends in $daysUntilStart days."
        } else {
            infoTextView.text = "The study has ended. Thanks for participating!"
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