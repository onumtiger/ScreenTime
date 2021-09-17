package com.example.screentime

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class qTwoActivity: AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.blocked_overview)
        Log.d("qTwoActivity", "qTwoActivity")
    }

    // Todo change group after answers are send!
}