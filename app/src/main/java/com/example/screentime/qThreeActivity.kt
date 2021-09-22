package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class qThreeActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference
    var questionOneAnswer = ""
    var questionTwoAnswer = ""
    var questionThreeAnswer = ""
    var questionFourAnswer = ""
    var questionFiveAnswer = ""
    lateinit var questionnairesAnsweres: MutableMap<String, String>
    lateinit var submitButton: Button

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        setContentView(R.layout.q_three)
        val userId:String = intent.getStringExtra("currentParticipantID")
        submitButton = findViewById(R.id.SubmitQThree)
        val submitQThreeHintView = findViewById<TextView>(R.id.SubmitQThreeHint)

        submitButton.setOnClickListener{
            if (Collections.frequency(questionnairesAnsweres.values, "") == 0) {
                dbParticipants.document("$userId").update("qThreeAnswers", questionnairesAnsweres)
                dbParticipants.document("$userId").update("qThree", true)

                startMainActivity()
            }
            else{
                submitQThreeHintView.visibility = View.VISIBLE
            }
        }
    }

    fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun activateSubmitButton(){
        this.questionnairesAnsweres = mutableMapOf(
            "1" to this.questionOneAnswer,
            "2" to this.questionTwoAnswer,
            "3" to this.questionThreeAnswer,
            "4" to this.questionFourAnswer,
            "5" to this.questionFiveAnswer
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
                        this.questionOneAnswer = resources.getString(R.string.questionOneAnswerOne)
                    }
                R.id.questionOneAnswerTwo ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.questionOneAnswerTwo)
                    }
                R.id.questionOneAnswerThree ->
                    if (checked) {
                        this.questionOneAnswer = resources.getString(R.string.questionOneAnswerThree)
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
                        this.questionTwoAnswer = resources.getString(R.string.questionTwoAnswerOne)
                    }
                R.id.questionTwoAnswerTwo ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.questionTwoAnswerTwo)
                    }
                R.id.questionTwoAnswerThree ->
                    if (checked) {
                        this.questionTwoAnswer = resources.getString(R.string.questionTwoAnswerThree)
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
                        this.questionThreeAnswer = resources.getString(R.string.questionThreeAnswerOne)
                    }
                R.id.questionThreeAnswerTwo ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.questionThreeAnswerTwo)
                    }
                R.id.questionThreeAnswerThree ->
                    if (checked) {
                        this.questionThreeAnswer = resources.getString(R.string.questionThreeAnswerThree)
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
                        this.questionFourAnswer = resources.getString(R.string.questionFourAnswerOne)
                    }
                R.id.questionFourAnswerTwo ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.questionFourAnswerTwo)
                    }
                R.id.questionFourAnswerThree ->
                    if (checked) {
                        this.questionFourAnswer = resources.getString(R.string.questionFourAnswerThree)
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
                        this.questionFiveAnswer = resources.getString(R.string.questionFiveAnswerOne)
                    }
                R.id.questionFiveAnswerTwo ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.questionFiveAnswerTwo)
                    }
                R.id.questionFiveAnswerThree ->
                    if (checked) {
                        this.questionFiveAnswer = resources.getString(R.string.questionFiveAnswerThree)
                    }
            }
        }
        activateSubmitButton()
    }
}