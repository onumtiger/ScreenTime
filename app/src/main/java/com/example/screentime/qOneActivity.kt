package com.example.screentime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class qOneActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference
    var questionOneAnswer = ""

    // TODO https://developer.android.com/guide/topics/ui/controls/radiobutton
    // TODO make radion button arrangement inline and question above like in this link

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")
        setContentView(R.layout.q_one)
        val userId:String = intent.getStringExtra("currentParticipantID")
        val submitButton = findViewById<Button>(R.id.SubmitQOne)


        // TODO check wether all questions are answered
        submitButton.setOnClickListener{
            val questionnairesAnsweres = mutableMapOf("1" to questionOneAnswer, "2" to "y", "3" to "zz")
            // TODO update firebase so dass userid nicht mehr verändert werden muss
            dbParticipants.document("0$userId").update("qOneAnswers", questionnairesAnsweres)
            dbParticipants.document("0$userId").update("qOne", true)
            // TODO: start next intent e.g. main activity to evaluate where to go next
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.questionOneAnswerOne ->
                    if (checked) {
                        this.questionOneAnswer = "questionOneAnswerOne"
                    }
                R.id.questionOneAnswerTwo ->
                    if (checked) {
                        this.questionOneAnswer = "questionOneAnswerTwo"
                    }
                R.id.questionOneAnswerThree ->
                    if (checked) {
                        this.questionOneAnswer = "questionOneAnswerThree"
                    }
            }
            Log.d("questionOneAnswer", this.questionOneAnswer)
        }
    }


}