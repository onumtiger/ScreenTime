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
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.lang.Exception
import org.json.JSONArray

import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class screenTimeScoreActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("screenTimeScoreActivity", "screenTimeScoreActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        val userId:String = intent.getStringExtra("currentParticipantID")

        // get user from firebase
        dbParticipants.whereEqualTo("studyID", userId).get().addOnSuccessListener { snapshots ->
            if (snapshots.documents.isEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            val currentParticipant = snapshots.documents[0]

            setContentView(R.layout.screentime_score)
            val timeSpendView: TextView = findViewById(R.id.timeSpend)
            val scoreView: TextView = findViewById(R.id.productivityScore)
            val productiveTimeView: TextView = findViewById(R.id.ProductiveTime)
            val dateView: TextView = findViewById(R.id.date)
            val views = mutableMapOf("timeSpendView" to timeSpendView, "scoreView" to scoreView, "productiveTimeView" to productiveTimeView, "dateView" to dateView)

            val screenTimeInfo = getScreenTimeEntry(currentParticipant)
            Log.d("here!", "here!")
            setViewData(
                views,
                screenTimeInfo
            )
        }
    }

    fun setViewData (views: MutableMap<String, TextView>, content: MutableMap<String, String>){
        views["timeSpendView"]!!.text = content["timeSpend"]
        views["scoreView"]!!.text = content["score"].toString().uppercase()
        views["productiveTimeView"]!!.text = content["productiveTime"]
        views["dateView"]!!.text = "${getDay()}, ${content["date"]}21"
    }


    fun getScreenTimeEntry(currentParticipant: DocumentSnapshot): MutableMap<String, String>{
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>

        return mutableMapOf(
            "timeSpend" to lastScreenTimeEntry["timeSpend"] as String,
            "score" to lastScreenTimeEntry["score"] as String,
            "productiveTime" to lastScreenTimeEntry["productiveTime"] as String,
            "date" to lastScreenTimeEntry["date"] as String,
        )
    }

    private fun getDay(): String{
        val c: Calendar = Calendar.getInstance()
        c.time = Date()

        return when (c.get(Calendar.DAY_OF_WEEK)) {
            1 -> "Saturday"
            2 -> "Sunday"
            3 -> "Monday"
            4 -> "Tuesday"
            5 -> "Wednesday"
            6 -> "Thursday"
            else -> "Friday"
        }
    }
}