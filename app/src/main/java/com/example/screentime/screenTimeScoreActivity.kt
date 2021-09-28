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




class screenTimeScoreActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("screenTimeScoreActivity", "screenTimeScoreActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        val userId:String = intent.getStringExtra("currentParticipantID")
        val currentDate: String = intent.getStringExtra("currentDate")

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

            checkScreenTimeEntries(currentParticipant, currentDate, views, userId)
        }
    }

    fun setViewData (views: MutableMap<String, TextView>, content: MutableMap<String, String>){
        views["timeSpendView"]!!.text = content["timeSpend"]
        views["scoreView"]!!.text = content["score"].toString().uppercase()
        views["productiveTimeView"]!!.text = content["productiveTime"]
        views["dateView"]!!.text = "${content["date"]}.21"
    }

    fun checkScreenTimeEntries(currentParticipant: DocumentSnapshot, currentDate: String, views: MutableMap<String, TextView>, userId: String){
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>
        val lastScreenTimeEntryDate: String = lastScreenTimeEntry["date"] as String

        val newEntryNeeded:Boolean = lastScreenTimeEntryDate != currentDate

        if (newEntryNeeded){
            getScreenTimeFromApi(currentParticipant, views, currentDate, userId)
        }
        else {
            val screenTimeInfo = getScreenTimeEntry(currentParticipant)
            setViewData(
                views,
                screenTimeInfo
            )
        }
    }

    fun getScreenTimeFromApi(currentParticipant: DocumentSnapshot, views: MutableMap<String, TextView>, currentDate: String, userId: String) {
        val apiKey = currentParticipant.data?.get("apiKey") as String
        var productivePulse = 0
        var allProductiveDuration = ""
        var totalDuration = ""

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val client: OkHttpClient = OkHttpClient()
                val request: Request =
                    Request.Builder().url("https://www.rescuetime.com/anapi/daily_summary_feed?key=$apiKey")
                        .build()
                val apiResponse = client.newCall(request).execute()
                val apiResponseString: String = apiResponse.body!!.string()
                val apiResponseJSONAarray = JSONArray(apiResponseString)
                val apiResponseJSON = JSONObject(apiResponseJSONAarray[0].toString())

                productivePulse = apiResponseJSON["productivity_pulse"] as Int
                allProductiveDuration = apiResponseJSON["all_productive_duration_formatted"] as String
                totalDuration = apiResponseJSON["total_duration_formatted"] as String
            }

            job.join()
        }

        val cleanedScreenTimeData = cleanScreenData(productivePulse, allProductiveDuration, totalDuration)
        addScreenTimeEntry(currentParticipant, cleanedScreenTimeData, views, currentDate, userId)
    }

    fun addScreenTimeEntry(currentParticipant: DocumentSnapshot, cleanedScreenTimeData: MutableMap<String, String>, views: MutableMap<String, TextView>, currentDate: String, userId: String) {
        val screenTimeData = mutableMapOf(
            "date" to currentDate,
            "score" to cleanedScreenTimeData["score"],
            "timeSpend" to cleanedScreenTimeData["timeSpend"],
            "productiveTime" to cleanedScreenTimeData["productiveTime"],
            "answered" to false,
            "evaluated" to false,
            "group" to "a"
        )

        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <Any, Any>
        val entryPosition = 1 + screenTimeEntriesList.size
        screenTimeEntriesList["$entryPosition"] = screenTimeData
        this.dbParticipants.document(userId).update("screenTimeEntries", screenTimeEntriesList)

        cleanedScreenTimeData["date"] = currentDate
        setViewData(views, cleanedScreenTimeData)
    }

    fun cleanScreenData(productivePulse: Int, allProductiveDuration: String, totalDuration: String): MutableMap<String, String> {
        var score: String = ""
        when (true) {
            productivePulse <= 17 -> score = "f"
            productivePulse <= 33 -> score = "e"
            productivePulse <= 50 -> score = "d"
            productivePulse <= 69 -> score = "c"
            productivePulse <= 84 -> score = "b"
            productivePulse <= 100 -> score = "a"
        }

        val productiveTime = allProductiveDuration.substringBefore("m") + "min"
        val timeSpend = totalDuration.substringBefore("m") + "min"

        return mutableMapOf(
            "score" to score,
            "productiveTime" to productiveTime,
            "timeSpend" to timeSpend
        )
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
}