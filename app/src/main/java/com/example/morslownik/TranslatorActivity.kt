package com.example.morslownik

import FlashController
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Application
import android.content.ContentValues.TAG
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.plcoding.audiorecorder.playback.AndroidAudioPlayer
import com.plcoding.audiorecorder.record.AndroidAudioRecorder
import java.io.File
import java.io.IOException

class TranslatorActivity : ComponentActivity() {
    private val morseCodeController = MorseCodeController()

    private val flashController = FlashController(this)

    private val recorder by lazy {
        AndroidAudioRecorder(applicationContext)
    }

    private val player by lazy {
        AndroidAudioPlayer(applicationContext)
    }

    private var audioFile: File? = null
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



        //dzwiek do morse
        val playButton: ImageButton = findViewById(R.id.soundButton) // Przykładowy przycisk w twojej aplikacji

        playButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie przycisku
            playButton.isEnabled = false

            // Wywołanie funkcji playSound w tle
            GlobalScope.launch {
                morseCodeController.playSound(sentences, 10)

                runOnUiThread {
                    playButton.isEnabled =
                        true // To jest tylko przykład, gdzie odblokowujesz przycisk
                }
            }
        }

        //latarka do morse
        findViewById<ImageButton>(R.id.torchButton).setOnClickListener{

            val sentences = afterText.text.toString().split("/").toTypedArray()

            flashController.playFlashSignal(sentences, 10)
        }

        //mikorofon
        val microphoneButton = findViewById<ImageButton>(R.id.microphoneButton)
        var isRecording = false

        microphoneButton.setOnClickListener {
            if (isRecording) {
                recorder.stop()
                isRecording = false
                Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
            } else {
                File(cacheDir, "audio.mp3").also { file ->
                    recorder.start(file)
                    audioFile = file
                }
                isRecording = true
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageButton>(R.id.paybackButton).setOnClickListener Button@{
            player.playFile(audioFile ?: return@Button)

        }
    }
}


