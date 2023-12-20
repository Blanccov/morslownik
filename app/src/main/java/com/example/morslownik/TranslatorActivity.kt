package com.example.morslownik

import FlashController
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.SeekBar
import android.widget.Toast
import com.plcoding.audiorecorder.playback.AndroidAudioPlayer
import com.plcoding.audiorecorder.record.AndroidAudioRecorder
import java.io.File

class TranslatorActivity : ComponentActivity() {
    private val morseCodeController = MorseCodeController()

    private val flashController = FlashController(this)

    private lateinit var seekBar: SeekBar
    private lateinit var wpmView: TextView

    private var wpm = 10;

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

        val previousText =
            findViewById<EditText>(R.id.prevText) // Zmiana na EditText, aby użytkownik mógł wprowadzać tekst
        val afterText = findViewById<TextView>(R.id.afterText)

        findViewById<Button>(R.id.translateButton).setOnClickListener {
            val textToTranslate = previousText.text.toString() // Pobranie wprowadzonego tekstu
            val morseCode = morseCodeController.translateToMorse(textToTranslate)
            afterText.text = morseCode
        }


        //dzwiek do morse
        val playButton: ImageButton =
            findViewById(R.id.soundButton) // Przykładowy przycisk w twojej aplikacji

        playButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie przycisku
            playButton.isEnabled = false

            // Wywołanie funkcji playSound w tle
            GlobalScope.launch {
                morseCodeController.playSound(sentences, wpm)

                runOnUiThread {
                    playButton.isEnabled =
                        true // To jest tylko przykład, gdzie odblokowujesz przycisk
                }
            }
        }

        val torchButton = findViewById<ImageButton>(R.id.torchButton)

        //latarka do morse
        torchButton.setOnClickListener {

            val sentences = afterText.text.toString().split("/").toTypedArray()

            torchButton.isEnabled = false

                flashController.playFlashSignal(sentences, wpm)

                runOnUiThread {
                    torchButton.isEnabled =
                        true // To jest tylko przykład, gdzie odblokowujesz przycisk
                }
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

        //wnp seekbar

        seekBar = findViewById(R.id.seekBar)
        wpmView = findViewById(R.id.wpmView)

        seekBar.max = 24
        seekBar.min = 4
        seekBar.progress = 9

        // Dodaj listener do seekbara
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                wpmView.text = "WPM: $value"
                wpm = value;
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Puste, jeśli nie potrzebujesz obsługi w momencie rozpoczęcia przesuwania
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Puste, jeśli nie potrzebujesz obsługi w momencie zakończenia przesuwania
            }
    })
}
}

