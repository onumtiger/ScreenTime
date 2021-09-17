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
import com.google.firebase.firestore.ktx.getField
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


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
                    val currentParticipant = snapshots.documents[0]
                    checkGroup(currentParticipant)
//                    val qOneAnswered = checkQOneAnswered(currentParticipant)
//                    if (qOneAnswered){
//                        val studyPhase = checkStudyPhase(currentParticipant, currentDate)
//
//                        when (studyPhase) {
//                            "noMeasuresOne" -> launchNoMeasuresOne()
//                            "measuresOne" -> {
//                                checkGroup(currentParticipant)
//                            }
//                            "measuresTwo" -> {
//                                when (checkQTwoAnswered(currentParticipant)) {
//                                    "false" -> launchQTwo()
//                                    "true" -> {
//                                        checkGroup(currentParticipant)
//                                    }
//                                }
//                            }
//                            "noMeasuresTwo" -> {
//                                when (checkQThreeAnswered(currentParticipant)) {
//                                    "false" -> launchQThree()
//                                    "true" -> launchNoMeasuresTwo()
//                                }
//                            }
//                            "studyIsFinished" -> launchStudyFinished()
//                        }
//                    }
//                    else {
//                        launchQOne()
//                    }
                }
                else {
                    setContentView(R.layout.activity_main)

                    // view elements
                    val inputParticipationId : EditText = findViewById(R.id.EnterParticipationID)
                    val submitButton = findViewById<Button>(R.id.StartButton)
                    val inputHintView:TextView = findViewById(R.id.InputHint)


                    submitButton.setOnClickListener{
                        if(inputParticipationId.text.toString().isEmpty()){
                            inputHintView.visibility = View.VISIBLE
                        }
                        else {
                            addDevice(inputParticipationId.text.toString(), deviceId, currentDate)
                            launchQOne()
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

    private fun launchQOne() {
        val intent = Intent(this, qOneActivity::class.java)
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

    private fun checkQOneAnswered(snapshot: DocumentSnapshot):Boolean {
        if(snapshot.get("qOne").toString() == "false"){
            return false
        }
        return true
    }

    private fun checkQTwoAnswered(snapshot: DocumentSnapshot): String {
        return snapshot.get("qTwo").toString()
    }

    private fun checkQThreeAnswered(snapshot: DocumentSnapshot): String {
        return snapshot.get("qThree").toString()
    }

    private fun checkGroup(snapshot: DocumentSnapshot) {
        when (snapshot.getString("group")) {
            "a" -> {
                val intent = Intent(this, screenTimeScoreActivity::class.java)
                startActivity(intent)
                finish()
            }
            "b" -> checkScreenTimeAnswered(snapshot)
        }
    }

    private fun checkScreenTimeAnswered(snapshot: DocumentSnapshot) {
        // TODO get screentimeAnswered
        val snapshotData = snapshot.data
        val snapshotDataJson = Gson().toJson(snapshot.data)
        Log.d("snapshotDataJson", snapshotDataJson.toString())






        val sds = snapshotData?.get("screenTimeEntries")
        Log.d("sds", sds.toString())


        val sTE: MutableMap<Int, Map<String, String>>  = snapshotData?.get("screenTimeEntries") as MutableMap<Int, Map<String, String>>
        val steJ = Gson().toJson(sTE)
        Log.d("steJ", steJ.toString())
        val entry = steJ.get(1)
        Log.d("entry", entry.toString())

        // TODO die screentime data einträge können immer noch nicht einzelnd ausgelesen werden



//        val answered = false
//        if (answered) {
//            checkEvaluation(snapshot)
//        }
//        else {
//            val intent = Intent(this, screenTimeQuestionnaireEmptyActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }
    private fun checkEvaluation(snapshot: DocumentSnapshot){
//        var screenTimeEntries = snapshot.get("screenTimeEntries")
        // TODO: 15.09.21 get screentime entries

        val evaluated = false

        var intent: Intent
        if (evaluated) {
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