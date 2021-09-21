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
                    checkGroup(currentParticipant)
                    val qOneAnswered = checkQOneAnswered(currentParticipant)
                    if (qOneAnswered){
                        val studyPhase = checkStudyPhase(currentParticipant, currentDate)

                        when (studyPhase) {
                            "noMeasuresOne" -> launchNoMeasuresOne()
                            "measuresOne" -> {
                                checkGroup(currentParticipant)
                            }
                            "measuresTwo" -> {
                                when (checkQTwoAnswered(currentParticipant)) {
                                    "false" -> launchQTwo()
                                    "true" -> {
                                        checkGroup(currentParticipant)
                                    }
                                }
                            }
                            "noMeasuresTwo" -> {
                                when (checkQThreeAnswered(currentParticipant)) {
                                    "false" -> launchQThree()
                                    "true" -> launchNoMeasuresTwo()
                                }
                            }
                            "studyIsFinished" -> launchStudyFinished()
                        }
                    }
                    else {
                        launchQOne(currentParticipant.get("studyID").toString())
                    }
                }
                else {
                    setContentView(R.layout.activity_main)

                    // view elements
                    val inputParticipationId : EditText = findViewById(R.id.EnterParticipationID)
                    val submitButton = findViewById<Button>(R.id.StartButton)
                    val inputHintView:TextView = findViewById(R.id.InputHint)

                    val currentParticipantID = inputParticipationId.text.toString()


                    submitButton.setOnClickListener{
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

    private fun launchQTwo() {
        val intent = Intent(this, qTwoActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchQThree() {
        val intent = Intent(this, qThreeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchStudyFinished(){
        val intent = Intent(this, studyFinishedActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresOne(){
        val intent = Intent(this, noMeasuresOneActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchNoMeasuresTwo(){
        val intent = Intent(this, noMeasuresTwoActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkQOneAnswered(currentParticipant: DocumentSnapshot):Boolean {
        if(currentParticipant.get("qOne").toString() == "false"){
            return false
        }
        return true
    }

    private fun checkQTwoAnswered(currentParticipant: DocumentSnapshot): String {
        return currentParticipant.get("qTwo").toString()
    }

    private fun checkQThreeAnswered(currentParticipant: DocumentSnapshot): String {
        return currentParticipant.get("qThree").toString()
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

    private fun checkStudyPhase(snapshot: DocumentSnapshot, currentDate: String): String{
        val startDate = snapshot.getString("startDate")
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

        return when {
            difference >= 28 -> "studyIsFinished"
            difference >= 21 -> "noMeasuresTwo"
            difference >= 14 -> "measuresTwo"
            difference >= 7 -> "measuresOne"
            else -> "noMeasuresOne"
        }
    }
}
