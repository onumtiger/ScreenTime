package com.onumbu.screentimenotifier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable

import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.HashMap


class screenTimeQuestionnaireEmptyActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.screentime_questionnaire_empty)
        val userId:String = intent.getStringExtra("currentParticipantID").toString()
        val currentDate: String = intent.getStringExtra("currentDate").toString()

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
        var hoursProductive: String
        var minutesProductive: String

        val answersHint: TextView = findViewById(R.id.answersHint)
        val submitButton = findViewById<Button>(R.id.submitScreenTimeAnswers)
        var screenTimeEntriesList = emptyMap<Any, Any>().toMutableMap()
        var lastScreenTimeEntry = emptyMap<Any, Any>().toMutableMap()

        // get user from firebase
        dbParticipants.document(userId).get().addOnSuccessListener { currentParticipant ->
            if (currentParticipant.data?.isNullOrEmpty() == true){
                Toast.makeText(this, "Error! Please contact the study supervisor!",
                    Toast.LENGTH_LONG).show();
            }

            screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <Any, Any>
            lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap <Any, Any>
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
                productiveTimeQuestiontView.text = "So you spend ${hoursSpend}h and ${minutesSpend}min on your phone."
                activateSubmitButton(
                    inputProductiveHours.text.toString(),
                    inputProductiveMinutes.text.toString(),
                    inputHoursSpend.text.toString(),
                    inputMinutesSpend.text.toString(),
                    productivityScore,
                    submitButton
                )
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
                productiveTimeQuestiontView.text = "So you spend ${hoursSpend}h and ${minutesSpend}min on your phone."
                activateSubmitButton(
                    inputProductiveHours.text.toString(),
                    inputProductiveMinutes.text.toString(),
                    inputHoursSpend.text.toString(),
                    inputMinutesSpend.text.toString(),
                    productivityScore,
                    submitButton
                )
            }
        })

        inputProductiveHours.addTextChangedListener(object : TextWatcher {
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
                activateSubmitButton(
                    inputProductiveHours.text.toString(),
                    inputProductiveMinutes.text.toString(),
                    inputHoursSpend.text.toString(),
                    inputMinutesSpend.text.toString(),
                    productivityScore,
                    submitButton
                )
            }
        })

        inputProductiveMinutes.addTextChangedListener(object : TextWatcher {
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
                activateSubmitButton(
                    inputProductiveHours.text.toString(),
                    inputProductiveMinutes.text.toString(),
                    inputHoursSpend.text.toString(),
                    inputMinutesSpend.text.toString(),
                    productivityScore,
                    submitButton
                )
            }
        })

        submitButton.setOnClickListener{
            hoursProductive = inputProductiveHours.text.toString()
            minutesProductive = inputProductiveMinutes.text.toString()
            hoursSpend = inputHoursSpend.text.toString()
            minutesSpend = inputMinutesSpend.text.toString()

            if(hoursSpend.isEmpty() || minutesSpend.isEmpty() || hoursProductive.isEmpty() || minutesProductive.isEmpty() || productivityScore.isEmpty()){
                answersHint.visibility = View.VISIBLE
            }
            else {

                val updateMap =  lastScreenTimeEntry
                updateMap["answered"] = true
                updateMap["qAProductiveTime"] = "${hoursProductive}h ${minutesProductive}min"
                updateMap["qAScore"] = productivityScore
                updateMap["qATimeSpend"] = "${hoursSpend}h ${minutesSpend}min"

                val entryPosition = screenTimeEntriesList.size
                screenTimeEntriesList["$entryPosition"] = updateMap
                this.dbParticipants.document(userId).update("screenTimeEntries", screenTimeEntriesList)

                val intent = Intent(this, screenTimeQuestionnaireEvaluationActivity::class.java)
                intent.putExtra("currentParticipantID", userId)
                intent.putExtra("currentDate", currentDate)
                startActivity(intent)
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

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }

        scoreButtonB.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGreen))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "b"

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }

        scoreButtonC.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorOrange))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "c"

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }

        scoreButtonD.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorOrange))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "d"

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }

        scoreButtonE.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorRed))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorGrey))

            productivityScore = "e"

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }

        scoreButtonF.setOnClickListener{
            scoreButtonA.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonB.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonC.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonD.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonE.setTextColor(resources.getColor(R.color.colorGrey))
            scoreButtonF.setTextColor(resources.getColor(R.color.colorRed))

            productivityScore = "f"

            activateSubmitButton(
                inputProductiveHours.text.toString(),
                inputProductiveMinutes.text.toString(),
                inputHoursSpend.text.toString(),
                inputMinutesSpend.text.toString(),
                productivityScore,
                submitButton
            )
        }
    }

    private fun activateSubmitButton(hoursProductive: String, minutesProductive: String, hoursSpend: String, minutesSpend: String, productivityScore: String, submitButton: Button){
        if(hoursSpend.isNotEmpty() && minutesSpend.isNotEmpty() && hoursProductive.isNotEmpty() && minutesProductive.isNotEmpty() && productivityScore.isNotEmpty()){
            submitButton.setBackgroundColor(resources.getColor(R.color.colorGreen))
            submitButton.setTextColor(resources.getColor(R.color.colorWhite))
            submitButton.isEnabled = true
            submitButton.isClickable = true
        }
    }
}