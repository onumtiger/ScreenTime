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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

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

//            checkScreenTimeEntries(currentParticipant, currentDate)
            val screenTimeInfo = getScreenTimeEntry(currentParticipant)

            setContentView(R.layout.screentime_score)
            val timeSpendView: TextView = findViewById(R.id.timeSpend)
            val scoreView: TextView = findViewById(R.id.productivityScore)
            val productiveTimeView: TextView = findViewById(R.id.ProductiveTime)

            timeSpendView.text = screenTimeInfo["timeSpend"]
            scoreView.text = screenTimeInfo["score"].toString().toUpperCase()
            productiveTimeView.text = screenTimeInfo["productiveTime"]
        }
    }

    fun checkScreenTimeEntries(currentParticipant: DocumentSnapshot, currentDate: String){
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>
        val lastScreenTimeEntryDate: String = lastScreenTimeEntry["date"] as String

        val newEntryNeeded:Boolean = lastScreenTimeEntryDate == currentDate

        if (newEntryNeeded){
            getScreenTimeFromApi(currentParticipant, currentDate)
            addScreenTimeEntry()
        }
        else {
            updateScreenTime()
        }
    }

    // TODO: calculateScreenTime and add it to userprofile then display it
    fun getScreenTimeFromApi(currentParticipant: DocumentSnapshot, currentDate: String) {
        val apiKey = currentParticipant.data?.get("apiKey") as String
        val currentDay = currentDate.substringBefore(".")
        val currentMonth = currentDate.substringAfter(".")

        val client: OkHttpClient = OkHttpClient()

        val request: Request = Request.Builder().url("https://www.rescuetime.com/anapi/data?key=$apiKey&format=json&perspective=interval&restrict_kind=activity&interval=hour&restrict_begin=2021-$currentMonth-$currentDay").build()

        client.newCall(request).execute().use { response -> response = response.body()!! }
        }

    // TODO: calculateScreenTime and add it to userprofile then display it
    fun addScreenTimeEntry() {}

    // TODO: get Screentime info from rescue api and update current entry
    fun updateScreenTime() {}

    fun getScreenTimeEntry(currentParticipant: DocumentSnapshot): MutableMap<String, String>{
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>

        return mutableMapOf(
            "timeSpend" to lastScreenTimeEntry["timeSpend"] as String,
            "score" to lastScreenTimeEntry["score"] as String,
            "productiveTime" to lastScreenTimeEntry["productiveTime"] as String,
        )
    }
}