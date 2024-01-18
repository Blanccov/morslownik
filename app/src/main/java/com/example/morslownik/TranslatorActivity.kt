package com.example.morslownik

import FlashController
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils.replace
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
import java.util.Locale
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.ImageView


class TranslatorActivity : ComponentActivity() {


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
    private var isRecording = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null){
            if (requestCode == 10){
                val stringArrayListExtra = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                findViewById<TextView>(R.id.prevText).setText(stringArrayListExtra!![0])
                isRecording = false
            }
        }
        else {
            Toast.makeText(applicationContext, "Failed to recognize speech!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.translatorlayout)

        val morseCodeController = MorseCodeController(this)
        val flashController = FlashController(this)

        var captureText = CaptureActivity.textToTranslate

        if (captureText != "") {
            findViewById<EditText>(R.id.prevText).setText(captureText)
        }

        val previousText =
            findViewById<EditText>(R.id.prevText)
        val afterText = findViewById<TextView>(R.id.afterText)

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        findViewById<ImageView>(R.id.button_copy).setOnClickListener{
            val textToCopy = previousText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if(textToCopy.isNotEmpty()){
            val clip = ClipData.newPlainText("Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(
                this,
                "Text copied",
                Toast.LENGTH_LONG
            )
                .show()}
        }

        findViewById<ImageView>(R.id.button_copy2).setOnClickListener{
            val textToCopy = afterText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if(textToCopy.isNotEmpty()){
                val clip = ClipData.newPlainText("Text", textToCopy)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(
                    this,
                    "Code copied",
                    Toast.LENGTH_LONG
                )
                    .show()}
        }

        var dumper = ""

        findViewById<Button>(R.id.translateButton).setOnClickListener {
            val textToTranslate = previousText.text.toString().trim().replace(Regex("\\n+"), "\n").replace(Regex(" +"), " ")
            if(textToTranslate.isNotEmpty()) {
                if(!textToTranslate.equals(dumper)) {
                    dumper = textToTranslate
                    val morseCode = morseCodeController.translateToMorse(textToTranslate)
                    afterText.text = morseCode

                    val db = DBController(this, null)
                    val plain = textToTranslate
                    val morse = morseCode

                    db.addHistory(plain, morse)

                    Toast.makeText(
                        this,
                        "translation added to database, probably...",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                else{
                    Toast.makeText(
                        this,
                        "Already translated",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
            else{
                Toast.makeText(
                    this,
                    "Cannot translate empty text",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        findViewById<Button>(R.id.clearText).setOnClickListener {
            previousText.text.clear()
            afterText.text = ""
        }

        val playButton: ImageButton = findViewById(R.id.soundButton)
        val torchButton = findViewById<ImageButton>(R.id.torchButton)

        playButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            playButton.isEnabled = false
            torchButton.isEnabled = false

            GlobalScope.launch {
                morseCodeController.playSound(sentences, wpm)

                runOnUiThread {
                    // Odblokowanie seekbara
                    seekBar.isEnabled = true
                    playButton.isEnabled = true
                    torchButton.isEnabled = true
                }
            }
        }



        torchButton.setOnClickListener {
            val sentences = afterText.text.toString().split("/").toTypedArray()

            // Zablokowanie seekbara
            seekBar.isEnabled = false
            torchButton.isEnabled = false
            playButton.isEnabled = false

            lifecycleScope.launch {
                flashController.playFlashSignal(sentences, wpm)

                // Odblokowanie seekbara
                seekBar.isEnabled = true
                torchButton.isEnabled = true
                playButton.isEnabled = true
            }
        }

        val microphoneButton = findViewById<ImageButton>(R.id.microphoneButton)

        //rozpoznawanie mowy
            microphoneButton.setOnClickListener {
                if (isRecording) {
                    recorder.stop()
                    isRecording = false
                    Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    startActivityForResult(intent, 10)

                    isRecording = true
                    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                }
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
