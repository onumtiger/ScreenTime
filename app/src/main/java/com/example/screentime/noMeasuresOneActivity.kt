package com.example.screentime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class noMeasuresOneActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("noMeasuresOneActivity", "noMeasuresOneActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.no_measures_one)
        val infoTextView: TextView = findViewById(R.id.InfoText)
        val currentDate:String = intent.getStringExtra("currentDate")
        val startDate:String = intent.getStringExtra("startDate")
        val daysUntilStart = daysUntilStart(currentDate, startDate)
        infoTextView.text = "Currently your ScreenTime behavior is measured. \nThe actual study begins in $daysUntilStart days."
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

        return 7 - difference
    }
}