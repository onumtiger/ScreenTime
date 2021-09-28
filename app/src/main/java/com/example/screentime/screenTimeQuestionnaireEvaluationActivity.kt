package com.example.screentime

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
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

        var screenTimeEntriesList = emptyMap<Any, Any>().toMutableMap()

        // get user from firebase
        dbParticipants.whereEqualTo("studyID", userId).get().addOnSuccessListener { snapshots ->
            if (snapshots.documents.isEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            val currentParticipant = snapshots.documents[0]
            screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <Any, Any>
            val screenTimeInfo = getScreenTimeEntry(currentParticipant)


            dateView.text = "${screenTimeInfo["date"]}.21"
            scoreInfoText.text = "You rated your productivity with an ${screenTimeInfo["scoreInfoText"]}. \nActually your productivity score is: "
            generalTimeQuestion.text = "You said you spend ${screenTimeInfo["qATimeSpend"]} on your phone. \nActually you spend on your phone:"
            generalTimeSpend.text = screenTimeInfo["timeSpend"]
            productiveTimeQuestion.text = "You said you’ve been productive for ${screenTimeInfo["qAProductiveTime"]}. \nActually you’ve been less productive."
            productiveTime.text = screenTimeInfo["productiveTime"]

            when (true){
                screenTimeInfo["score"] == "a" -> scoreButtonA.setTextColor(resources.getColor(R.color.colorGreen))
                screenTimeInfo["score"] == "b" -> scoreButtonB.setTextColor(resources.getColor(R.color.colorGreen))
                screenTimeInfo["score"] == "c" -> scoreButtonC.setTextColor(resources.getColor(R.color.colorOrange))
                screenTimeInfo["score"] == "d" -> scoreButtonD.setTextColor(resources.getColor(R.color.colorOrange))
                screenTimeInfo["score"] == "e" -> scoreButtonE.setTextColor(resources.getColor(R.color.colorRed))
                screenTimeInfo["score"] == "f" -> scoreButtonF.setTextColor(resources.getColor(R.color.colorRed))
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

    fun getScreenTimeEntry(currentParticipant: DocumentSnapshot): MutableMap<String, String>{
        val screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <*, *>
        val lastScreenTimeEntry = screenTimeEntriesList[screenTimeEntriesList.size.toString()] as HashMap<*,*>

        return mutableMapOf(
            "timeSpend" to lastScreenTimeEntry["timeSpend"] as String,
            "score" to lastScreenTimeEntry["score"] as String,
            "productiveTime" to lastScreenTimeEntry["productiveTime"] as String,
            "qAProductiveTime" to lastScreenTimeEntry["qAProductiveTime"] as String,
            "qAScore" to lastScreenTimeEntry["qAScore"] as String,
            "qATimeSpend" to lastScreenTimeEntry["qATimeSpend"] as String,
            "date" to lastScreenTimeEntry["date"] as String,
        )
    }
}