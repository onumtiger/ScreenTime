package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class qOneActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference
    var questionOneAnswer = ""
    var questionTwoAnswer = ""
    var questionThreeAnswer = ""
    var questionFourAnswer = ""
    var questionFiveAnswer = ""
    var questionSixAnswer = ""
    lateinit var questionnairesAnsweres: MutableMap<String, String>
    lateinit var submitButton: Button

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        setContentView(R.layout.q_one)
        val userId:String = intent.getStringExtra("currentParticipantID")
        val currentDate:String = intent.getStringExtra("currentDate")
        val startDate:String = intent.getStringExtra("startDate")
        submitButton = findViewById(R.id.SubmitQOne)
        val submitQOneHintView = findViewById<TextView>(R.id.SubmitQOneHint)

        submitButton.setOnClickListener{
            if (Collections.frequency(questionnairesAnsweres.values, "") == 0) {
                dbParticipants.document("$userId").update("qOneAnswers", questionnairesAnsweres)
                dbParticipants.document("$userId").update("qOne", true)

                launchNoMeasuresOne(userId, currentDate, startDate)
            }
            else{
                submitQOneHintView.visibility = View.VISIBLE
            }
        }
    }

    private fun launchNoMeasuresOne(currentParticipantID: String, currentDate: String, startDate: String){
        val intent = Intent(this, noMeasuresOneActivity::class.java)
        intent.putExtra("currentParticipantID", currentParticipantID)
        intent.putExtra("currentDate", currentDate)
        intent.putExtra("startDate", startDate)
        startActivity(intent)
        finish()
    }

    fun activateSubmitButton(){
        this.questionnairesAnsweres = mutableMapOf(
            "1" to this.questionOneAnswer,
            "2" to this.questionTwoAnswer,
            "3" to this.questionThreeAnswer,
            "4" to this.questionFourAnswer,
            "5" to this.questionFiveAnswer,
            "6" to this.questionSixAnswer
        )

        if (Collections.frequency(questionnairesAnsweres.values, "") == 0){
            submitButton.setBackgroundColor(resources.getColor(R.color.colorGreen))
            submitButton.setTextColor(resources.getColor(R.color.colorWhite))
            submitButton.isEnabled = true
            submitButton.isClickable = true
        }
    }

    fun questionOneClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionOneAnswerOne ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionOneAnswerTwo ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionOneAnswerThree ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionOneAnswerFour ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionOneAnswerFive ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionOneAnswerSix ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }

    fun questionTwoClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionTwoAnswerOne ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionTwoAnswerTwo ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionTwoAnswerThree ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionTwoAnswerFour ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionTwoAnswerFive ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionTwoAnswerSix ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }

    fun questionThreeClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionThreeAnswerOne ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionThreeAnswerTwo ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionThreeAnswerThree ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionThreeAnswerFour ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionThreeAnswerFive ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionThreeAnswerSix ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }

    fun questionFourClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionFourAnswerOne ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionFourAnswerTwo ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionFourAnswerThree ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionFourAnswerFour ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionFourAnswerFive ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionFourAnswerSix ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }

    fun questionFiveClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionFiveAnswerOne ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionFiveAnswerTwo ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionFiveAnswerThree ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionFiveAnswerFour ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionFiveAnswerFive ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionFiveAnswerSix ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }

    fun questionSixClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.questionSixAnswerOne ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.stronglyDisagree)
                    }
                R.id.questionSixAnswerTwo ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.disagree)
                    }
                R.id.questionSixAnswerThree ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.slightlyDisagree)
                    }
                R.id.questionSixAnswerFour ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.slightlyAgree)
                    }
                R.id.questionSixAnswerFive ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.agree)
                    }
                R.id.questionSixAnswerSix ->
                    if (checked) {
                        this.questionSixAnswer = resources.getString(R.string.stronglyAgree)
                    }
            }
        }
        activateSubmitButton()
    }
}