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
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("noMeasuresOneActivity", "noMeasuresOneActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        val userId:String = intent.getStringExtra("currentParticipantID")
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

        // get user from firebase
        dbParticipants.whereEqualTo("studyID", userId).get().addOnSuccessListener { snapshots ->
            if (snapshots.documents.isEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            val currentParticipant = snapshots.documents[0]

            getScreenTimeFromApi(currentParticipant, currentDate, userId)
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

    fun getScreenTimeFromApi(currentParticipant: DocumentSnapshot, currentDate: String, userId: String) {
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
        addScreenTimeEntry(currentParticipant, cleanedScreenTimeData, currentDate, userId)
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

    fun addScreenTimeEntry(currentParticipant: DocumentSnapshot, cleanedScreenTimeData: MutableMap<String, String>, currentDate: String, userId: String) {
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
    }
}