package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText

import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import android.content.ContentValues.TAG
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        Log.d("AppCompatActivity", "AppCompatActivity")

        // firebase collection
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")

        // get device ID
        val deviceId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // get FCM token
        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result.toString()
        })

        // calculate current Date
        val sdf = SimpleDateFormat("dd.M")
        val currentDate = sdf.format(Date())

        // check if user already participates in study
        dbParticipants
            .whereEqualTo("deviceId", deviceId)
            .get().addOnSuccessListener { snapshots ->
                if (snapshots.documents.isNotEmpty()) {
                    val currentParticipant = snapshots.documents[0]
                    val currentParticipantID = currentParticipant.get("studyID").toString()
                    val startDate = currentParticipant.getString("startDate")!!
                    val group = currentParticipant.getString("group")!!

                    // update token to make sure it is always up to date
                    this.dbParticipants.document(currentParticipantID).update("token", token)

                    if(!checkRescueTimeActive(currentParticipant)){
                        val intent = Intent(this, rescueTimeReminderActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        checkGroup(currentParticipant, currentParticipantID, currentDate)
                        when (checkQOneAnswered(currentParticipant)) {
                            true -> {
                                when (checkStudyPhase(currentDate, startDate)) {
                                    "noMeasuresOne" -> {
                                        launchNoMeasuresOne(currentParticipantID, currentDate, currentParticipant.getString("startDate")!!)
                                    }
                                    "measuresOne" -> {
                                        checkGroup(currentParticipant, currentParticipantID, currentDate)
                                    }
                                    "measuresTwo" -> {
                                        when (checkQTwoAnswered(currentParticipant)) {
                                            false -> launchQTwo(currentParticipantID, currentDate, group)
                                            true -> {
                                                checkGroup(currentParticipant, currentParticipantID, currentDate)
                                            }
                                        }
                                    }
                                    "noMeasuresTwo" -> {
                                        when (checkQTwoAnswered(currentParticipant)){
                                            false -> launchQTwo(currentParticipantID, currentDate, group)
                                            true -> {
                                                when (checkQThreeAnswered(currentParticipant)) {
                                                    false -> launchQThree(currentParticipantID, currentDate, startDate)
                                                    true -> launchNoMeasuresTwo(currentParticipantID, currentDate, currentParticipant.getString("startDate")!!)
                                                }
                                            }
                                        }
                                    }
                                    "studyIsFinished" -> {
                                        when (checkQTwoAnswered(currentParticipant)){
                                            false -> launchQTwo(currentParticipantID, currentDate, group)
                                            true -> {
                                                when (checkQThreeAnswered(currentParticipant)) {
                                                    false -> launchQThree(currentParticipantID, currentDate, startDate)
                                                    true -> launchNoMeasuresTwo(currentParticipantID, currentDate, currentParticipant.getString("startDate")!!)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            false -> launchQOne(currentParticipantID, currentDate, startDate )
                        }
                    }
                }
                else {
                    setContentView(R.layout.activity_main)

                    // view elements
                    val inputParticipationId : EditText = findViewById(R.id.EnterParticipationID)
                    val inputApiKey : EditText = findViewById(R.id.EnterApiKey)
                    val submitButton = findViewById<Button>(R.id.StartButton)
                    val inputHintView:TextView = findViewById(R.id.InputHint)

                    submitButton.setOnClickListener{
                        val currentParticipantID = inputParticipationId.text.toString()
                        val currentParticipantApiKey = inputApiKey.text.toString()
                        if(currentParticipantID.isEmpty() || currentParticipantApiKey.isEmpty()){
                            inputHintView.visibility = View.VISIBLE
                        }
                        else {
                            addDevice(currentParticipantID, deviceId, currentDate, currentParticipantApiKey, token)
                            launchQOne(currentParticipantID, currentDate, currentDate)
                        }
                    }
                }
            }
    }

    private fun addDevice(userId: String, deviceId: String, currentDate: String, currentParticipantApiKey:String, token: String) {
        Log.d("firebase", "add device")
        this.dbParticipants.document(userId).update("deviceId", deviceId)
        this.dbParticipants.document(userId).update("startDate", currentDate)
        this.dbParticipants.document(userId).update("apiKey", currentParticipantApiKey)
        this.dbParticipants.document(userId).update("token", token)
    }

    private fun launchQOne(currentParticipantID: String, currentDate: String, startDate: String) {
        val intent = Intent(this, qOneActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    private fun launchQTwo(currentParticipantID: String, currentDate: String, group: String) {
        val intent = Intent(this, qTwoActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("group", group)
        startActivity(intent)
        finish()
    }

    private fun launchQThree(currentParticipantID: String, currentDate: String, startDate: String) {
        val intent = Intent(this, qThreeActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresOne(currentParticipantID: String, currentDate: String, startDate: String){
        val intent = Intent(this, noMeasuresOneActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresTwo(currentParticipantID: String, currentDate: String, startDate: String){
        val intent = Intent(this, noMeasuresTwoActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    private fun checkQOneAnswered(currentParticipant: DocumentSnapshot):Boolean {
        if(currentParticipant.get("qOne").toString() == "false"){
            return false
        }
        return true
    }

    private fun checkQTwoAnswered(currentParticipant: DocumentSnapshot): Boolean {
        if(currentParticipant.get("qTwo").toString() == "false"){
            return false
        }
        return true
    }

    private fun checkQThreeAnswered(currentParticipant: DocumentSnapshot): Boolean {
        if(currentParticipant.get("qThree").toString() == "false"){
            return false
        }
        return true
    }

    private fun checkGroup(currentParticipant: DocumentSnapshot, currentParticipantID: String, currentDate: String) {
        when (currentParticipant.getString("group")) {
            "a" -> {
                val intent = Intent(this, screenTimeScoreActivity::class.java)
                intent.putExtra("currentDate", currentDate)
                intent.putExtra("currentParticipantID", currentParticipantID)
                startActivity(intent)
                finish()
            }
            "b" -> checkScreenTimeAnswered(currentParticipant, currentParticipantID, currentDate)
        }
    }

    private fun checkScreenTimeAnswered(currentParticipant: DocumentSnapshot, currentParticipantID: String, currentDate: String) {
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>
        val answeredQuestionnaire: Boolean = lastScreenTimeEntry["answered"] as Boolean
        val evaluatedResult: Boolean = lastScreenTimeEntry["evaluated"] as Boolean
        var intent: Intent

        if (answeredQuestionnaire && evaluatedResult) {
            intent = Intent(this, screenTimeQuestionnaireActivity::class.java)

        } else if (answeredQuestionnaire && !evaluatedResult){
            intent = Intent(this, screenTimeQuestionnaireEvaluationActivity::class.java)
        } else {
            intent = Intent(this, screenTimeQuestionnaireEmptyActivity::class.java)
        }

        intent.putExtra("currentDate", currentDate)
        intent.putExtra("currentParticipantID", currentParticipantID)
        startActivity(intent)
        finish()
    }


    private fun checkStudyPhase(currentDate: String, startDate: String): String{
        val startDay = startDate!!.substringBefore(".")
        val startMonth = startDate!!.substringAfter(".")

        val currentDay = currentDate.substringBefore(".")
        val currentMonth = currentDate.substringAfter(".")

        var difference: Number

        if (startMonth == currentMonth){
            difference = currentDay.toInt() - startDay.toInt()
        }
        else {
            difference = 30 - startDay.toInt() + currentDay.toInt()
        }

        return when {
            difference >= 28 -> "studyIsFinished"
            difference >= 21 -> "noMeasuresTwo"
            difference >= 14 -> "measuresTwo"
            difference >= 7 -> "measuresOne"
            else -> "noMeasuresOne"
        }
    }

    private fun checkRescueTimeActive(currentParticipant: DocumentSnapshot): Boolean{
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>

        val sdfPoint = SimpleDateFormat("dd.M.")
        val dateYesterday = sdfPoint.format(Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24)))

        return lastScreenTimeEntry["date"].toString() == dateYesterday
    }
}
