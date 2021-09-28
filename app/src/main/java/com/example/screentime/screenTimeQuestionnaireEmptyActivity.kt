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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


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
        var hoursProductive: String = inputProductiveHours.text.toString()
        var minutesProductive: String = inputProductiveMinutes.text.toString()

        val answersHint: TextView = findViewById(R.id.answersHint)
        val submitButton = findViewById<Button>(R.id.submitScreenTimeAnswers)
        var screenTimeEntriesList = emptyMap<Any, Any>().toMutableMap()
        var apiScreenTimeInfo = emptyMap<String, String>().toMutableMap()

        // get user from firebase
        dbParticipants.whereEqualTo("studyID", userId).get().addOnSuccessListener { snapshots ->
            if (snapshots.documents.isEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            Log.d("snapshot", snapshots.toString())
            val currentParticipant = snapshots.documents[0]
            screenTimeEntriesList = currentParticipant.data?.get("screenTimeEntries") as HashMap <Any, Any>
            val apiKey = currentParticipant.data?.get("apiKey") as String
            var productivePulse = 0
            var allProductiveDuration = ""
            var totalDuration = ""

            runBlocking {
                val job: Job = launch(context = Dispatchers.Default) {
                    val client: OkHttpClient = OkHttpClient()
                    val request: Request =
                        Request.Builder().url("https://www.rescuetime.com/anapi/daily_summary_feed?key=$apiKey")
                            .build()
                    val apiResponse = client.newCall(request).execute()
                    val apiResponseString: String = apiResponse.body!!.string()
                    val apiResponseJSONAarray = JSONArray(apiResponseString)
                    val apiResponseJSON = JSONObject(apiResponseJSONAarray[0].toString())

                    productivePulse = apiResponseJSON["productivity_pulse"] as Int
                    allProductiveDuration = apiResponseJSON["all_productive_duration_formatted"] as String
                    totalDuration = apiResponseJSON["total_duration_formatted"] as String
                    apiScreenTimeInfo = cleanScreenData(productivePulse, allProductiveDuration, totalDuration)
                }

                job.join()
            }
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
                productiveTimeQuestiontView.text = "You answered you spend ${hoursSpend}h and ${minutesSpend}min on your phone. \nHow much of that time have you been productive for?"
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
                val updateMap =  mutableMapOf(
                    "answered" to true,
                    "date" to currentDate,
                    "evaluated" to false,
                    "productiveTime" to apiScreenTimeInfo["productiveTime"],
                    "qAProductiveTime" to "$hoursProductive h $minutesProductive min",
                    "qAScore" to productivityScore,
                    "qATimeSpend" to "$hoursSpend h $minutesSpend min",
                    "score" to apiScreenTimeInfo["score"],
                    "timeSpend" to apiScreenTimeInfo["timeSpend"],
                    "group" to "b"
                )

                val entryPosition = 1 + screenTimeEntriesList.size
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

    fun activateSubmitButton(hoursProductive: String, minutesProductive: String, hoursSpend: String, minutesSpend: String, productivityScore: String, submitButton: Button){

        if(hoursSpend.isNotEmpty() && minutesSpend.isNotEmpty() && hoursProductive.isNotEmpty() && minutesProductive.isNotEmpty() && productivityScore.isNotEmpty()){
            submitButton.setBackgroundColor(resources.getColor(R.color.colorGreen))
            submitButton.setTextColor(resources.getColor(R.color.colorWhite))
            submitButton.isEnabled = true
            submitButton.isClickable = true
        }
    }

    fun cleanScreenData(productivePulse: Int, allProductiveDuration: String, totalDuration: String): MutableMap<String, String> {
        var score: String = ""
        when (true) {
            productivePulse <= 17 -> score = "f"
            productivePulse <= 33 -> score = "e"
            productivePulse <= 50 -> score = "d"
            productivePulse <= 69 -> score = "c"
            productivePulse <= 84 -> score = "b"
            productivePulse <= 100 -> score = "a"
        }

        val productiveTime = allProductiveDuration.substringBefore("m") + "min"
        val timeSpend = totalDuration.substringBefore("m") + "min"

        return mutableMapOf(
            "score" to score,
            "productiveTime" to productiveTime,
            "timeSpend" to timeSpend
        )
    }
}