package com.example.screentime

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.text.Editable

import android.text.TextWatcher
import android.util.Log


class MainActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        Log.d("build", "build")
1
        // View elements
        val inputParticipationId : EditText = findViewById(R.id.EnterParticipationID)
        val submitButton = findViewById<Button>(R.id.StartButton)
        inputParticipationId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateButton(p0.toString(), submitButton)
                Log.d("before", "hello")
                Log.d("before", p0.toString())
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateButton(p0.toString(), submitButton)
                Log.d("on", "hi")
                Log.d("on", p0.toString())
            }

            override fun afterTextChanged(s: Editable) {
                updateButton(s.toString(), submitButton)
                Log.d("after", "bye")
                Log.d("after", s.toString())
            }
        })
    }

    // funktioniert noch nicht
    @SuppressLint("ResourceAsColor")
    fun updateButton(input: String, button: Button) {
        Log.d("input", input)
        if(input.isEmpty()){
            button.isEnabled = false
            button.isClickable = false
            button.setBackgroundColor(R.color.colorGrey)
        }
        Log.d("check", "true")
        button.isEnabled = true
        button.isClickable = true
        button.setBackgroundColor(R.color.colorBlue)
    }
}