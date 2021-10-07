package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class noMeasuresTwoActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("noMeasuresOneActivity", "noMeasuresOneActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.no_measures_two)
        val infoTextView: TextView = findViewById(R.id.InfoText)
        val currentDate:String = intent.getStringExtra("currentDate")
        val startDate:String = intent.getStringExtra("startDate")
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