package com.example.morslownik

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class TranslatorActivity : ComponentActivity() {
    private val morseCodeController = MorseCodeController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translatorlayout)

        findViewById<TextView>(R.id.prevText).text = CaptureActivity.textToTranslate

        val textToTranslate = findViewById<TextView>(R.id.prevText).text.toString()
//         textToTranslate = CaptureActivity.textToTranslate // Assuming you have the text to translate stored in CaptureActivity

        // Translate text to Morse code
        val morseCode = morseCodeController.translateToMorse(textToTranslate)


        findViewById<TextView>(R.id.afterText).text = morseCode
    }
}
