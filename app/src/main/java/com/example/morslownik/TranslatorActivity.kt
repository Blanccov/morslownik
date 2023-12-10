package com.example.morslownik

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity

class TranslatorActivity : ComponentActivity() {
    private val morseCodeController = MorseCodeController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translatorlayout)

        var captureText = CaptureActivity.textToTranslate

        if (captureText != "") {
            findViewById<EditText>(R.id.prevText).setText(captureText)
        }

        val previousText = findViewById<EditText>(R.id.prevText) // Zmiana na EditText, aby użytkownik mógł wprowadzać tekst
        val afterText = findViewById<TextView>(R.id.afterText)

        findViewById<Button>(R.id.translateButton).setOnClickListener{
            val textToTranslate = previousText.text.toString() // Pobranie wprowadzonego tekstu
            val morseCode = morseCodeController.translateToMorse(textToTranslate)
            afterText.text = morseCode
        }
    }
}
