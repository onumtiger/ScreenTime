package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.screentime_score.*

class screenTimeQuestionnaireEvaluationActivity: AppCompatActivity() {
    lateinit var dbParticipants: CollectionReference
    @SuppressLint("ResourceAsColor", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("screenTimeQuestionnaireEvaluationActivity", "screenTimeQuestionnaireEvaluationActivity")
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.screentime_questionnaire_evaluation)
        val userId:String = intent.getStringExtra("currentParticipantID")
        val currentDate:String = intent.getStringExtra("currentDate")

        // firebase
        dbParticipants = FirebaseFirestore.getInstance().collection("participants")

        // view elements
        val scoreButtonA = findViewById<Button>(R.id.productivityScoreButtonA)
        val scoreButtonB = findViewById<Button>(R.id.productivityScoreButtonB)
        val scoreButtonC = findViewById<Button>(R.id.productivityScoreButtonC)
        val scoreButtonD = findViewById<Button>(R.id.productivityScoreButtonD)
        val scoreButtonE = findViewById<Button>(R.id.productivityScoreButtonE)
        val scoreButtonF = findViewById<Button>(R.id.productivityScoreButtonF)

        val dateView: TextView = findViewById(R.id.date)
        val scoreInfoText: TextView = findViewById(R.id.scoreInfoText)
        val generalTimeQuestion: TextView = findViewById(R.id.generalTimeQuestion)
        val generalTimeSpend: TextView = findViewById(R.id.generalTimeSpend)
        val productiveTimeQuestion: TextView = findViewById(R.id.productiveTimeQuestion)
        val productiveTime: TextView = findViewById(R.id.productiveTime)

        val satisfactionButtonYes = findViewById<Button>(R.id.satisfactionButtonYes)
        val satisfactionButtonNo = findViewById<Button>(R.id.satisfactionButtonNo)

        // get user from firebase
        dbParticipants.document(userId).get().addOnSuccessListener { currentParticipant ->
            if (currentParticipant.data?.isNullOrEmpty() == true){
                Toast.makeText(this, "Error! Please contact the study supervisor!",
                    Toast.LENGTH_LONG).show();
            }

            val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <Any, Any>
            val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap <Any, Any>


            dateView.text = "${currentDate}.21"
            scoreInfoText.text = "You rated your productivity with an ${lastScreenTimeEntry["score"]}. \nActually your productivity score is: "
            generalTimeQuestion.text = "You said you spend ${lastScreenTimeEntry["qATimeSpend"]} on your phone. \nActually you spend on your phone:"
            generalTimeSpend.text = lastScreenTimeEntry["timeSpend"].toString()
            productiveTimeQuestion.text = "You said you’ve been productive for ${lastScreenTimeEntry["qAProductiveTime"]}. \nActually you’ve been less productive."
            productiveTime.text = lastScreenTimeEntry["productiveTime"].toString()

            when (true){
                lastScreenTimeEntry["score"] == "a" -> scoreButtonA.setTextColor(resources.getColor(R.color.colorGreen))
                lastScreenTimeEntry["score"] == "b" -> scoreButtonB.setTextColor(resources.getColor(R.color.colorGreen))
                lastScreenTimeEntry["score"] == "c" -> scoreButtonC.setTextColor(resources.getColor(R.color.colorOrange))
                lastScreenTimeEntry["score"] == "d" -> scoreButtonD.setTextColor(resources.getColor(R.color.colorOrange))
                lastScreenTimeEntry["score"] == "e" -> scoreButtonE.setTextColor(resources.getColor(R.color.colorRed))
                lastScreenTimeEntry["score"] == "f" -> scoreButtonF.setTextColor(resources.getColor(R.color.colorRed))
            }

            satisfactionButtonNo.setOnClickListener{
                val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<Any, Any>
                lastScreenTimeEntry["evaluation"] = "no"
                lastScreenTimeEntry["evaluated"] = true

                screenTimeEntriesList[screenTimeEntriesList.size.toString()] = lastScreenTimeEntry
                this.dbParticipants.document(userId).update("screenTimeEntries", screenTimeEntriesList)

                val intent = Intent(this, screenTimeQuestionnaireActivity::class.java)
                intent.putExtra("currentParticipantID", userId)
                intent.putExtra("currentDate", currentDate)
                startActivity(intent)
            }

            satisfactionButtonYes.setOnClickListener{
                val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<Any, Any>
                lastScreenTimeEntry["evaluation"] = "yes"
                lastScreenTimeEntry["evaluated"] = true

                screenTimeEntriesList[screenTimeEntriesList.size.toString()] = lastScreenTimeEntry
                this.dbParticipants.document(userId).update("screenTimeEntries", screenTimeEntriesList)

                val intent = Intent(this, screenTimeQuestionnaireActivity::class.java)
                intent.putExtra("currentParticipantID", userId)
                intent.putExtra("currentDate", currentDate)
                startActivity(intent)
            }
        }


    }
}