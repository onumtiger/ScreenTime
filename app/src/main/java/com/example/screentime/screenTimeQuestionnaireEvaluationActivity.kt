package com.example.screentime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class screenTimeQuestionnaireEvaluationActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor", "LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.screentime_score)
        Log.d("screenTimeQuestionnaireEvaluationActivity", "screenTimeQuestionnaireEvaluationActivity")

    }
}