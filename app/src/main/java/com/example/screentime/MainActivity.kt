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
import com.google.firebase.firestore.auth.User


// TODO 22.09 group b + screentime calculation


class MainActivity : AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        Log.d("AppCompatActivity", "AppCompatActivity")

        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")

        // get device ID
        val deviceId: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // calculate current Date
        val sdf = SimpleDateFormat("dd.M")
        val currentDate = sdf.format(Date())

        // check if user already participates in study
        dbParticipants
            .whereEqualTo("deviceId", deviceId)
            .get().addOnSuccessListener { snapshots ->
                if (snapshots.documents.isNotEmpty()) {
                    Log.d("snapshots", snapshots.toString())
                    val currentParticipant = snapshots.documents[0]
                    val currentParticipantID = currentParticipant.get("studyID").toString()
                    checkGroup(currentParticipant)
                    when (checkQOneAnswered(currentParticipant)) {
                        true -> {
                            when (checkStudyPhase(currentParticipant, currentDate)) {
                                "noMeasuresOne" -> launchNoMeasuresOne(currentDate, currentParticipant.getString("startDate")!!)
                                "measuresOne" -> {
                                    checkGroup(currentParticipant)
                                }
                                "measuresTwo" -> {
                                    when (checkQTwoAnswered(currentParticipant)) {
                                        false -> launchQTwo(currentParticipantID)
                                        true -> {
                                            checkGroup(currentParticipant)
                                        }
                                    }
                                }
                                "noMeasuresTwo" -> {
                                    when (checkQTwoAnswered(currentParticipant)){
                                        false -> launchQTwo(currentParticipantID)
                                        true -> {
                                            when (checkQThreeAnswered(currentParticipant)) {
                                                false -> launchQThree(currentParticipantID)
                                                true -> launchNoMeasuresTwo(currentDate, currentParticipant.getString("startDate")!!)
                                            }
                                        }
                                    }
                                }
                                "studyIsFinished" -> {
                                    when (checkQTwoAnswered(currentParticipant)){
                                        false -> launchQTwo(currentParticipantID)
                                        true -> {
                                            when (checkQThreeAnswered(currentParticipant)) {
                                                false -> launchQThree(currentParticipantID)
                                                true -> launchNoMeasuresTwo(currentDate, currentParticipant.getString("startDate")!!)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        false -> launchQOne(currentParticipantID)
                    }
                }
                else {
                    setContentView(R.layout.activity_main)

                    // view elements
                    val inputParticipationId : EditText = findViewById(R.id.EnterParticipationID)
                    val submitButton = findViewById<Button>(R.id.StartButton)
                    val inputHintView:TextView = findViewById(R.id.InputHint)

                    submitButton.setOnClickListener{
                        val currentParticipantID = inputParticipationId.text.toString()
                        if(currentParticipantID.isEmpty()){
                            inputHintView.visibility = View.VISIBLE
                        }
                        else {
                            addDevice(currentParticipantID, deviceId, currentDate)
                            launchQOne(currentParticipantID)
                        }
                    }
                }
            }
    }

    private fun addDevice(userId: String, deviceId: String, currentDate: String) {
        Log.d("firebase", "add device")
        this.dbParticipants.document(userId).update("deviceId", deviceId)
        this.dbParticipants.document(userId).update("startDate", currentDate)
    }

    private fun launchQOne(currentParticipantID: String) {
        val intent = Intent(this, qOneActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        startActivity(intent)
        finish()
    }

    private fun launchQTwo(currentParticipantID: String) {
        val intent = Intent(this, qTwoActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        startActivity(intent)
        finish()
    }

    private fun launchQThree(currentParticipantID: String) {
        val intent = Intent(this, qThreeActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresOne(currentDate: String, startDate: String){
        val intent = Intent(this, noMeasuresOneActivity::class.java)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresTwo(currentDate: String, startDate: String){
        val intent = Intent(this, noMeasuresTwoActivity::class.java)
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

    private fun checkGroup(currentParticipant: DocumentSnapshot) {
        when (currentParticipant.getString("group")) {
            "a" -> {
                val intent = Intent(this, screenTimeScoreActivity::class.java)
                startActivity(intent)
                finish()
            }
            "b" -> checkScreenTimeAnswered(currentParticipant)
        }
    }

    private fun checkScreenTimeAnswered(currentParticipant: DocumentSnapshot) {
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>
        val answeredQuestionnaire: Boolean = lastScreenTimeEntry["answered"] as Boolean
        val evaluatedResult: Boolean = lastScreenTimeEntry["evaluated"] as Boolean

        if (answeredQuestionnaire) {
            checkEvaluation(evaluatedResult)
        }
        else {
            val intent = Intent(this, screenTimeQuestionnaireEmptyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun checkEvaluation(evaluatedResult: Boolean){
        var intent: Intent
        if (evaluatedResult) {
            intent = Intent(this, screenTimeQuestionnaireActivity::class.java)
        }
        else {
            intent = Intent(this, screenTimeQuestionnaireEvaluationActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    private fun checkStudyPhase(currentParticipant: DocumentSnapshot, currentDate: String): String{
        val startDate = currentParticipant.getString("startDate")
        Log.d("startDate", startDate)
        Log.d("currentDate", currentDate)
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

        Log.d("difference", difference.toString())

        return when {
            difference >= 28 -> "studyIsFinished"
            difference >= 21 -> "noMeasuresTwo"
            difference >= 14 -> "measuresTwo"
            difference >= 7 -> "measuresOne"
            else -> "noMeasuresOne"
        }
    }
}
