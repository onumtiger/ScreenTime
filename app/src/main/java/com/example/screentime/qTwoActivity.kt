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

class qTwoActivity: AppCompatActivity() {
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
        setContentView(R.layout.q_two)
        val userId:String = intent.getStringExtra("currentParticipantID").toString()
        val currentDate:String = intent.getStringExtra("currentDate").toString()
        val group:String = intent.getStringExtra("group").toString()
        submitButton = findViewById(R.id.SubmitQTwo)
        val submitQTwoHintView = findViewById<TextView>(R.id.SubmitQTwoHint)

        submitButton.setOnClickListener{
            if (Collections.frequency(questionnairesAnsweres.values, "") == 0) {
                dbParticipants.document("$userId").update("qTwoAnswers", questionnairesAnsweres)
                dbParticipants.document("$userId").update("qTwo", true)

                checkGroup(userId, group, currentDate)
            }
            else{
                submitQTwoHintView.visibility = View.VISIBLE
            }
        }
    }

    // change group to other user group
    private fun checkGroup(userId: String, group: String, currentDate: String) {
        when (group) {
            "a" -> {
                dbParticipants.document("$userId").update("group", "b")

                val intent = Intent(this, screenTimeQuestionnaireEmptyActivity::class.java)
                intent.putExtra("currentDate", currentDate)
                intent.putExtra("currentParticipantID", userId)
                startActivity(intent)
                finish()
            }
            "b" -> {
                dbParticipants.document("$userId").update("group", "a")

                val intent = Intent(this, screenTimeScoreActivity::class.java)
                intent.putExtra("currentDate", currentDate)
                intent.putExtra("currentParticipantID", userId)
                startActivity(intent)
                finish()
            }
        }
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