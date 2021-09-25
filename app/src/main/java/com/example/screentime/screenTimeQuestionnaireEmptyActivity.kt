package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.screentime_questionnaire_empty.*
import android.text.Editable

import android.text.TextWatcher
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class screenTimeQuestionnaireEmptyActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("screenTimeQuestionnaireEmptyActivity", "screenTimeQuestionnaireEmptyActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.screentime_questionnaire_empty)
        val userId:String = intent.getStringExtra("currentParticipantID")
        val currentDate: String = intent.getStringExtra("currentDate")

        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")

        // view elements
        val scoreButtonA = findViewById<Button>(R.id.productivityScoreButtonA)
        val scoreButtonB = findViewById<Button>(R.id.productivityScoreButtonB)
        val scoreButtonC = findViewById<Button>(R.id.productivityScoreButtonC)
        val scoreButtonD = findViewById<Button>(R.id.productivityScoreButtonD)
        val scoreButtonE = findViewById<Button>(R.id.productivityScoreButtonE)
        val scoreButtonF = findViewById<Button>(R.id.productivityScoreButtonF)
        var productivityScore: String = ""

        val inputHoursSpend : EditText = findViewById(R.id.EnterGeneralTimeInputHours)
        val inputMinutesSpend : EditText = findViewById(R.id.EnterGeneralTimeInputMinutes)
        val productiveTimeQuestiontView: TextView = findViewById(R.id.productiveTimeQuestion)
        var hoursSpend: String = inputHoursSpend.text.toString()
        var minutesSpend: String = inputMinutesSpend.text.toString()

        val inputProductiveHours : EditText = findViewById(R.id.EnterProductiveTimeInputHours)
        val inputProductiveMinutes : EditText = findViewById(R.id.EnterProductiveTimeInputMinutes)

        val answersHint: TextView = findViewById(R.id.answersHint)
        val submitButton = findViewById<Button>(R.id.submitScreenTimeAnswers)
        var screenTimeEntriesSize = "0"

        // get user from firebase
        dbParticipants.whereEqualTo("studyID", userId).get().addOnSuccessListener { snapshots ->
            if (snapshots.documents.isEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            val currentParticipant = snapshots.documents[0]
            val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
            screenTimeEntriesSize = (1 + screenTimeEntriesList.size).toString()
        }

        inputHoursSpend.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
                hoursSpend = s.toString()
                productiveTimeQuestiontView.text = "You answered you spend ${hoursSpend}h and ${minutesSpend}min on your phone. \nHow much of that time have you been productive for?"
            }
        })

        inputMinutesSpend.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
                minutesSpend = s.toString()
                productiveTimeQuestiontView.text = "You answered you spend ${hoursSpend}h and ${minutesSpend}min on your phone. \nHow much of that time have you been productive for?"
            }
        })

        submitButton.setOnClickListener{
            val hoursProductive: String = inputProductiveHours.text.toString()
            val minutesProductive: String = inputProductiveMinutes.text.toString()
            if(hoursSpend.isEmpty() || minutesSpend.isEmpty() || hoursProductive.isEmpty() || minutesProductive.isEmpty() || productivityScore.isEmpty()){
                answersHint.visibility = View.VISIBLE
            }
            else {
                // TODO save answers – launch next activity
                    val updateMap = mutableMapOf(
                screenTimeEntriesSize to mutableMapOf(
                    "answered" to false,
                    "date" to currentDate,
                    "evaluated" to false,
                    "productiveTime" to "",
                    "qAProductiveTime" to "$hoursProductive h $minutesProductive min",
                    "qAScore" to productivityScore,
                    "qATimeSpend" to "$hoursSpend h $minutesSpend min",
                    "score" to "",
                    "timeSpend" to ""
                )
                    )

                // update entry or add new value
                this.dbParticipants.document(userId).collection("screenTimeEntries").add(updateMap)
            }
        }

        scoreButtonA.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGreen))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "a"
        }

        scoreButtonB.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGreen))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "b"
        }

        scoreButtonC.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorOrange))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "c"
        }

        scoreButtonD.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorOrange))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "d"
        }

        scoreButtonE.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorRed))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "e"
        }

        scoreButtonF.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorRed))

            productivityScore = "f"
        }

    }
}