package com.example.morslownik

import FlashController
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.plcoding.audiorecorder.playback.AndroidAudioPlayer
import com.plcoding.audiorecorder.record.AndroidAudioRecorder
import java.io.File

class TranslatorActivity : ComponentActivity() {
    private val morseCodeController = MorseCodeController()

    private lateinit var seekBar: SeekBar
    private lateinit var wpmView: TextView

    private var wpm = 10

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

        val flashController = FlashController(this)

        var captureText = CaptureActivity.textToTranslate

        if (captureText != "") {
            findViewById<EditText>(R.id.prevText).setText(captureText)
        }

        val previousText =
            findViewById<EditText>(R.id.prevText)
        val afterText = findViewById<TextView>(R.id.afterText)

        findViewById<Button>(R.id.translateButton).setOnClickListener {
            val textToTranslate = previousText.text.toString()
            val morseCode = morseCodeController.translateToMorse(textToTranslate)
            afterText.text = morseCode
        }

        val playButton: ImageButton = findViewById(R.id.soundButton)

        playButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            playButton.isEnabled = false

            GlobalScope.launch {
                morseCodeController.playSound(sentences, wpm)

                runOnUiThread {
                    // Odblokowanie seekbara
                    seekBar.isEnabled = true
                    playButton.isEnabled = true
                }
            }
        }

        val torchButton = findViewById<ImageButton>(R.id.torchButton)

        torchButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            torchButton.isEnabled = false

            lifecycleScope.launch {
                flashController.playFlashSignal(sentences, wpm)

                // Odblokowanie seekbara
                seekBar.isEnabled = true
                torchButton.isEnabled = true
            }
        }

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

        findViewById<ImageButton>(R.id.paybackButton).setOnClickListener {
            player.playFile(audioFile ?: return@setOnClickListener)
        }

        seekBar = findViewById(R.id.seekBar)
        wpmView = findViewById(R.id.wpmView)

        seekBar.max = 24
        seekBar.min = 4
        seekBar.progress = 9

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val value = progress + 1
                wpmView.text = "WPM: $value"
                wpm = value
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Puste
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Puste
            }
        })
    }
}
