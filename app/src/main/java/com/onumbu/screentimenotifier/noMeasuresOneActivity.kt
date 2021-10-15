package com.onumbu.screentimenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class noMeasuresOneActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        val userId: String = intent.getStringExtra("currentParticipantID").toString()
        setContentView(R.layout.no_measures_one)
        val swipeRefreshView: SwipeRefreshLayout = findViewById(R.id.swiperefresh)
        val infoTextView: TextView = findViewById(R.id.InfoText)
        val currentDate:String = intent.getStringExtra("currentDate").toString()
        val startDate:String = intent.getStringExtra("startDate").toString()


        // get user from firebase
        dbParticipants.document(userId).get().addOnSuccessListener { currentParticipant ->
            if (currentParticipant.data?.isNullOrEmpty() == true){
                Toast.makeText(this, "Error! Please contact the study supervisor!",
                    Toast.LENGTH_LONG).show();
            }

            val group = currentParticipant.getString("group")!!

            val daysUntilStart = daysUntilStart(currentDate, startDate, userId, group)
            infoTextView.text = "Currently your ScreenTime behavior is measured. \nThe actual study begins in $daysUntilStart days."
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

    fun daysUntilStart(currentDate: String, startDate: String, userId: String, group: String): Int {
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

        val daysUntilStart = 7 - difference

        if (daysUntilStart <= 0) {
            checkGroup(userId, group, currentDate)
        }

        return daysUntilStart
    }

    private fun checkGroup(userId: String, group: String, currentDate: String) {
        when (group) {
            "b" -> {
                val intent = Intent(this, screenTimeQuestionnaireEmptyActivity::class.java)
                intent.putExtra("currentDate", currentDate)
                intent.putExtra("currentParticipantID", userId)
                startActivity(intent)
                finish()
            }
            "a" -> {
                val intent = Intent(this, screenTimeScoreActivity::class.java)
                intent.putExtra("currentDate", currentDate)
                intent.putExtra("currentParticipantID", userId)
                startActivity(intent)
                finish()
            }
        }
    }
}